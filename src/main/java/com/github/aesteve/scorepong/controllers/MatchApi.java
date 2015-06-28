package com.github.aesteve.scorepong.controllers;

import com.github.aesteve.scorepong.Server;
import com.github.aesteve.scorepong.services.MongoDAO;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.mixins.ContentType;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.params.RequestBody;
import com.github.aesteve.vertx.nubes.annotations.routing.http.DELETE;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PATCH;
import com.github.aesteve.vertx.nubes.annotations.routing.http.POST;
import com.github.aesteve.vertx.nubes.annotations.routing.http.PUT;
import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.marshallers.Payload;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

@Controller("/api/match")
@ContentType("application/json")
public class MatchApi {

	@Service(Server.MONGO_SERVICE)
	private MongoDAO mongo;

	@GET
	public void getMatches(RoutingContext context, Payload<JsonArray> matches) {
		mongo.getMatches(res -> {
			if (res.failed()) {
				context.fail(res.cause());
				return;
			}
			matches.set(new JsonArray(res.result()));
			context.next();
		});
	}

	@GET("/:id")
	public void getMatch(RoutingContext context, @Param("id") String matchId, Payload<JsonObject> match) {
		mongo.getMatch(matchId, res -> {
			if (res.failed()) {
				context.fail(res.cause());
				return;
			}
			match.set(res.result());
			context.next();
		});
	}

	@POST
	public void createMatch(RoutingContext context, @RequestBody JsonObject match, Payload<JsonObject> result) {
		mongo.createMatch(match, res -> {
			if (res.failed()) {
				context.fail(res.cause());
				return;
			}
			result.set(res.result());
			context.next();
		});
	}

	@PUT("/:id")
	@PATCH("/:id")
	public void updateMatch(RoutingContext context, @Param("id") String id, @RequestBody JsonObject match, Payload<JsonObject> result) {
		mongo.updateMatch(match.put("_id", id), null, res -> {
			if (res.failed()) {
				context.fail(res.cause());
				return;
			}
			result.set(match);
			context.next();
		});
	}

	@DELETE("/:id")
	public void deleteMatch(RoutingContext context, @Param("id") String id) {
		mongo.delete(id, res -> {
			if (res.failed()) {
				context.fail(res.cause());
				return;
			}
			context.response().setStatusCode(204);
			context.response().end();
		});
	}
}
