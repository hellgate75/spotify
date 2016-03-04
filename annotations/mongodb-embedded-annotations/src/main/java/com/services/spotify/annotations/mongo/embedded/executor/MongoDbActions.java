package com.services.spotify.annotations.mongo.embedded.executor;

import java.io.IOException;
import java.net.UnknownHostException;

import com.services.spotify.annotations.mongo.embedded.MongoClient;
import com.services.spotify.annotations.mongo.embedded.MongoExecutable;
import com.services.spotify.annotations.mongo.embedded.MongoStartUp;
import com.services.spotify.annotations.mongo.embedded.MongoTearDown;
import com.services.spotify.annotations.mongo.embedded.client.MongoDbClient;

public class MongoDbActions {
	
	private static MongoDbActions instance=null;
	
	private MongoDbActions() {
		super();
	}

	public void startMongod(MongoExecutable annotation) throws UnknownHostException, IOException, Exception  {
		MongoDbService.getInstance().connectMongoDeamon(annotation.host(),annotation.port(), annotation.master(), annotation.enableAuth(),
													 annotation.useNoJournal(), annotation.enableTextSearch(), annotation.verbose());
	}

	public void startMongod(MongoStartUp annotation) throws UnknownHostException, IOException, Exception  {
		MongoDbService.getInstance().connectMongoDeamon(annotation.host(),annotation.port(), annotation.master(), annotation.enableAuth(),
													 annotation.useNoJournal(), annotation.enableTextSearch(), annotation.verbose());
	}

	public void stopMongod(MongoExecutable annotation) {
		MongoDbService.getInstance().disconnectMongoDeamon(annotation.host(), annotation.port());
	}

	public void stopMongod(MongoTearDown annotation) {
		MongoDbService.getInstance().disconnectMongoDeamon(annotation.host(), annotation.port());
	}

	public void teardownAllMongod() {
		MongoDbService.getInstance().shutdownDeamons();
	}

	public MongoDbClient getClient(MongoClient annotation) throws Exception {
		return MongoDbService.getInstance().provideClient(annotation.host(), annotation.port(), annotation.journaled(), annotation.lazy());
	}
	
	public static MongoDbActions getInstance() {
		if (instance == null)
			instance = new MongoDbActions();
		return instance;
	}

}
