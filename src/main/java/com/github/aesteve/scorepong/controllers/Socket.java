package com.github.aesteve.scorepong.controllers;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.handler.sockjs.SockJSSocket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		if ("connect".equals(action)) {
			String gameId = json.getString("game");
			connect(emitter, gameId);
			return;
		}
		checkAccess(emitter, game -> {
			switch (action) {
				case "start":
					this.startGame(emitter, game);
					break;
				case "point":
					Integer player = json.getInteger("player");
					this.scorePoint(emitter, game, player);
					break;
				case "undo":
					this.undo(emitter, game);
					break;
			}
		});
	}

	@OnClose
	public void closed(SockJSSocket socket) {
		matches.remove(socket);
	}

	private void checkAccess(SockJSSocket emitter, Handler<JsonObject> handler) {
		String id = matches.get(emitter);
		if (id == null) {
			emitter.close();
			return;
		}
		mongo.getMatch(id, res -> {
			if (res.failed()) {
				log.error("Could not find game " + id, res.cause());
				return;
			}
			handler.handle(res.result());
		});
	}

	private void connect(SockJSSocket emitter, String gameId) {
		mongo.getMatch(gameId, res -> {
			if (res.failed()) {
				log.error("Could not find game " + gameId, res.cause());
				return;
			}
			matches.put(emitter, gameId);
			notifyAll(res.result());
		});
	}

	private void scorePoint(SockJSSocket emitter, JsonObject game, Integer player) {
		if (game.getString("endDate") != null) {
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
		mongo.updateMatch(game, null, updateRes -> {
			if (updateRes.succeeded()) {
				notifyAll(game);
			} else {
				log.error("Could not update game", updateRes.cause());
			}
		});
	}

	private void undo(SockJSSocket emitter, JsonObject game) {
		JsonArray history = game.getJsonArray("history");
		if (history == null) {
			return;
		}
		List<String> removeFields = new ArrayList<>();
		if (history.size() == 0) {
			// reset
			removeFields.add("toss");
			removeFields.add("startDate");
		} else {
			// undo last point
			removeFields.add("endDate");
			removeFields.add("winnerId");
			removeFields.add("winner");
			Integer last = (Integer) history.remove(history.size() - 1);
			game.put("scorePlayer" + last, game.getInteger("scorePlayer" + last) - 1);
		}
		mongo.updateMatch(game, removeFields, res -> {
			if (res.failed()) {
				log.error("Could not update", res.cause());
				return;
			}
			notifyAll(game);
		});
	}

	private void startGame(SockJSSocket emitter, JsonObject game) {
		String id = game.getString("_id");
		game.put("startDate", DateUtils.INSTANCE.formatIso8601(new Date()));
		mongo.updateMatch(game, null, updateRes -> {
			if (updateRes.succeeded()) {
				matches.put(emitter, id);
				notifyAll(game);
			} else {
				log.error("Could not update game", updateRes.cause());
			}
		});
	}

	private void notifyAll(JsonObject game) {
		matches.entrySet().stream().filter(entry -> {
			return entry.getValue().equals(game.getString("_id"));
		}).forEach(entry -> {
			entry.getKey().write(Buffer.buffer(game.toString()));
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
		} else if (score2 >= 21 && score2 >= score1 + 2) {
			// player2 wins
			game.put("winnerId", 2);
			game.put("winner", game.getString("player2"));
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
