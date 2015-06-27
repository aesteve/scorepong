package com.github.aesteve.scorepong.controllers;

import io.vertx.ext.web.RoutingContext;

import com.github.aesteve.scorepong.Server;
import com.github.aesteve.scorepong.services.MongoDAO;
import com.github.aesteve.vertx.nubes.annotations.Controller;
import com.github.aesteve.vertx.nubes.annotations.File;
import com.github.aesteve.vertx.nubes.annotations.params.Param;
import com.github.aesteve.vertx.nubes.annotations.routing.http.GET;
import com.github.aesteve.vertx.nubes.annotations.services.Service;

@Controller("/")
public class Views {

	@Service(Server.MONGO_SERVICE)
	private MongoDAO mongo;

	@GET
	@File("web/assets/index.html")
	public void index(RoutingContext context) {
		context.next();
	}

	@GET("match/:id")
	@File("web/assets/match.html")
	public void renderMatch(RoutingContext context, @Param("id") String id) {
		context.next();
	}
}
