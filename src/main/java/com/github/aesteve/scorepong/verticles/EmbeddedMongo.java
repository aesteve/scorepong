package com.github.aesteve.scorepong.verticles;

import java.io.IOException;

import com.github.aesteve.vertx.nubes.annotations.services.Verticle;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.AbstractVerticle;

@Verticle(worker=true)
public class EmbeddedMongo extends AbstractVerticle {
	
	public static final int MONGO_PORT = 8888;
	
	private MongodExecutable mongod;
	
	@Override
	public void start() throws IOException {
		MongodStarter starter = MongodStarter.getDefaultInstance();
		int port = MONGO_PORT;
		MongodConfigBuilder builder = new MongodConfigBuilder();
		builder.version(Version.Main.PRODUCTION);
		builder.net(new Net(port, Network.localhostIsIPv6()));
		mongod = starter.prepare(builder.build());
		mongod.start();
	}
	
	@Override
	public void stop() {
		if (mongod != null) {
			mongod.stop();
		}
	}
	
}
