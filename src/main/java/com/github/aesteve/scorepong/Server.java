package com.github.aesteve.scorepong;

import com.github.aesteve.scorepong.services.MongoDAO;
import com.github.aesteve.vertx.nubes.VertxNubes;
import com.github.aesteve.vertx.nubes.exceptions.MissingConfigurationException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxException;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;

public class Server extends AbstractVerticle {

	public final static String MONGO_SERVICE = "mongo";

	private HttpServer server;
	private HttpServerOptions options;
	private VertxNubes nubes;

	@Override
	public void init(Vertx vertx, Context context) {
		super.init(vertx, context);
		JsonObject config = context.config();
		options = new HttpServerOptions();
		options.setHost(config.getString("host", "localhost"));
		options.setPort(config.getInteger("port", 9000));
		JsonObject nubesConfig = config.getJsonObject("nubes");
		JsonObject mongoConfig = config.getJsonObject("mongo");
		try {
			nubes = new VertxNubes(vertx, nubesConfig);
			nubes.registerService(MONGO_SERVICE, new MongoDAO(mongoConfig));
		} catch (MissingConfigurationException me) {
			throw new VertxException(me);
		}
	}

	@Override
	public void start(Future<Void> future) {
		server = vertx.createHttpServer(options);
		nubes.bootstrap(res -> {
			if (res.failed()) {
				future.fail(res.cause());
				return;
			}
			server.requestHandler(res.result()::accept);
			startServer(future);
		});
	}

	@Override
	public void stop(Future<Void> future) {
		nubes.stop(nubesRes -> {
			closeServer(future);
		});
	}

	private void startServer(Future<Void> future) {
		server.listen(res -> {
			if (res.succeeded()) {
				future.complete();
			} else {
				future.fail(res.cause());
			}
		});
	}

	private void closeServer(Future<Void> future) {
		if (server != null) {
			server.close(res -> {
				if (res.failed()) {
					future.fail(res.cause());
				} else {
					future.complete();
				}
			});
		} else {
			future.complete();
		}
	}
}
