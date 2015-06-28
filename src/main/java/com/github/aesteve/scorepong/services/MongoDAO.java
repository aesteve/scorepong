package com.github.aesteve.scorepong.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

import com.github.aesteve.scorepong.verticles.EmbeddedMongo;
import com.github.aesteve.vertx.nubes.services.Service;

public class MongoDAO implements Service {

	private MongoClient mongo;
	private JsonObject config;
	
	public MongoDAO(JsonObject config) {
		this.config = config;
		if (config.getBoolean("embed", false)) {
			config.put("host", "localhost");
			config.put("port", EmbeddedMongo.MONGO_PORT);
		}
		config.put("db_name", "scorepong");
	}
	
	@Override
	public void init(Vertx vertx) {
		mongo = MongoClient.createShared(vertx, config);
	}

	@Override
	public void start(Future<Void> future) {
		future.complete();
	}

	@Override
	public void stop(Future<Void> future) {
		mongo.close();
		future.complete();
	}

	public void getMatches(Handler<AsyncResult<List<JsonObject>>> handler) {
		mongo.find("match", new JsonObject(), handler);
	}

	public void getMatch(String id, Handler<AsyncResult<JsonObject>> handler) {
		JsonObject idQuery = new JsonObject();
		idQuery.put("_id", id);
		mongo.findOne("match", idQuery, null, handler);
	}

	public void createMatch(JsonObject match, Handler<AsyncResult<JsonObject>> handler) {
		mongo.insert("match", match, res -> {
			if (res.failed()) {
				handler.handle(Future.failedFuture(res.cause()));
				return;
			}
			match.put("_id", res.result());
			handler.handle(Future.succeededFuture(match));
		});
	}

	public void updateMatch(JsonObject newMatch, List<String> unset, Handler<AsyncResult<Void>> handler) {
		JsonObject query = new JsonObject();
		query.put("_id", newMatch.getString("_id"));
		JsonObject updateQuery = new JsonObject();
		updateQuery.put("$set", newMatch);
		if (unset != null) {
			JsonObject unsetQuery = new JsonObject();
			unset.forEach(field -> {
				newMatch.remove(field);
				unsetQuery.put(field, "");
			});
			updateQuery.put("$unset", unsetQuery);
		}
		mongo.update("match", query, updateQuery, handler);
	}

	public void delete(String id, Handler<AsyncResult<Void>> handler) {
		JsonObject query = new JsonObject();
		query.put("_id", id);
		mongo.remove("match", query, handler);
	}
}
