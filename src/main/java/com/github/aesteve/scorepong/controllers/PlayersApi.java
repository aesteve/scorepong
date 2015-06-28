package com.github.aesteve.scorepong.controllers;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.github.aesteve.scorepong.model.Player;
import com.github.aesteve.scorepong.services.MongoDAO;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@Controller("/api/players/")
@ContentType("application/json")
public class PlayersApi {

	@Service("mongo")
	private MongoDAO mongo;
	
	@GET
	public void getAllPlayers(RoutingContext context, Payload<Set<Player>> payload) {
		mongo.getMatches(res -> {
			if (res.failed()) {
				context.fail(res.cause());
				return;
			}
			List<JsonObject> matches = res.result();
			final Set<Player> players = new LinkedHashSet<>();
			matches.forEach(match -> {
				Player player1 = getOrCreate(match.getString("player1"), players.stream());
				Player player2 = getOrCreate(match.getString("player2"), players.stream());
				if (!players.contains(player1)) {
					players.add(player1);
				}
				if (!players.contains(player2)) {
					players.add(player2);
				}
				player1.update(match);
				player2.update(match);
			});
			payload.set(players);
			context.next();
		});
	}
	
	
	private static Player getOrCreate(String name, Stream<Player> stream) {
		return stream.filter(player -> {
			return player.name.equals(name);
		}).findFirst().orElse(new Player(name));
	}
}
