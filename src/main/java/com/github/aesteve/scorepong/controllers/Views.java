package com.github.aesteve.scorepong.controllers;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.scorepong.Server;
import com.github.aesteve.scorepong.services.MongoDAO;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.View;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.services.Service;

@Controller("/")
public class Views {

	@Service(Server.MONGO_SERVICE)
	private MongoDAO mongo;

	@GET
	@View("index.hbs")
	public void index(RoutingContext context) {
		mongo.getMatches(res -> {
			if (res.failed()) {
				context.fail(res.cause());
			}
			context.put("matches", res.result());
			context.next();
		});
	}

	@GET("match/:id")
	@View("match.hbs")
	public void renderMatch(RoutingContext context, @Param("id") String id) {
		mongo.getMatch(id, res -> {
			if (res.failed()) {
				context.fail(res.cause());
			}
			context.put("match", res.result());
			context.next();
		});
	}
}
