package com.github.aesteve.scorepong.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

import com.github.aesteve.scorepong.services.MongoDAO;
import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnClose;
import com.github.aesteve.vertx.nubes.annotations.sockjs.OnMessage;
import com.github.aesteve.vertx.nubes.annotations.sockjs.SockJS;
import com.github.aesteve.vertx.nubes.utils.DateUtils;

@SockJS("/sockjs/*")
public class Socket {

	private final Logger log = LoggerFactory.getLogger(Socket.class);

	@Service("mongo")
	private MongoDAO mongo;
	private Map<SockJSSocket, String> matches;

	public Socket() {
		matches = new HashMap<>();
	}

	@OnMessage
	public void onMessage(Buffer msg, SockJSSocket emitter) {
		JsonObject json = new JsonObject(msg.toString());
		String action = json.getString("action");
		switch (action) {
			case "connect":
				String gameId = json.getString("game");
				connect(emitter, gameId);
				break;
			case "start":
				this.startGame(emitter);
				break;
			case "point":
				Integer player = json.getInteger("player");
				this.scorePoint(emitter, player);
				break;
		}
	}

	@OnClose
	public void closed(SockJSSocket socket) {
		matches.remove(socket);
	}

	private void connect(SockJSSocket emitter, String gameId) {
		matches.put(emitter, gameId);
		mongo.getMatch(gameId, res -> {
			if (res.failed()) {
				log.error("Could not find game " + gameId, res.cause());
				return;
			}
			matches.put(emitter, gameId);
			emitter.write(Buffer.buffer(res.result().toString()));
		});
	}

	private void scorePoint(SockJSSocket emitter, Integer player) {
		String id = matches.get(emitter);
		if (id == null) {
			emitter.close();
		}
		mongo.getMatch(id, res -> {
			if (res.failed()) {
				log.error("Could not get game", res.cause());
				return;
			}
			JsonObject game = res.result();
			if (game.getBoolean("endDate") != null) {
				return;
			}
			if (game.getInteger("toss") == null) {
				game.put("toss", player);
				game.put("scorePlayer1", 0);
				game.put("scorePlayer2", 0);
			} else {
				String scoreNode = "scorePlayer" + player;
				Integer oldScore = game.getInteger(scoreNode, 0);
				game.put(scoreNode, ++oldScore);
				updateHistory(game, player);
				endIfNeeded(game);
			}
			mongo.updateMatch(id, game, updateRes -> {
				if (updateRes.succeeded()) {
					emitter.write(Buffer.buffer(game.toString()));
				} else {
					log.error("Could not update game", updateRes.cause());
				}
			});
		});
	}

	private void startGame(SockJSSocket emitter) {
		String id = matches.get(emitter);
		if (id == null) {
			emitter.close();
		}
		mongo.getMatch(id, res -> {
			if (res.failed()) {
				log.error("Could not get game", res.cause());
				return;
			}
			JsonObject game = res.result();
			game.put("startDate", DateUtils.INSTANCE.formatIso8601(new Date()));
			mongo.updateMatch(id, game, updateRes -> {
				if (updateRes.succeeded()) {
					matches.put(emitter, id);
					emitter.write(Buffer.buffer(game.toString()));
				} else {
					log.error("Could not update game", updateRes.cause());
				}
			});
		});
	}

	private static void endIfNeeded(JsonObject game) {
		int score1 = game.getInteger("scorePlayer1", 0);
		int score2 = game.getInteger("scorePlayer2", 0);
		boolean ended = false;
		if (score1 >= 21 && score1 >= score2 + 2) {
			// player1 wins
			game.put("winnerId", 1);
			game.put("winner", game.getString("player1"));
			ended = true;
		} else if (score2 > 21 && score2 >= score1 + 2) {
			// player2 wins
			game.put("winnerId", 1);
			game.put("winner", game.getString("player1"));
			ended = true;
		}
		if (ended) {
			game.put("endDate", DateUtils.INSTANCE.formatIso8601(new Date()));
		}
	}

	private static void updateHistory(JsonObject game, Integer player) {
		JsonArray history = game.getJsonArray("history", new JsonArray());
		history.add(player);
		game.put("history", history);
	}

}
