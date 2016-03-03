package com.services.spotify.extension.mongodb.annotations.spi;

import java.io.IOException;
import java.net.UnknownHostException;

import com.services.spotify.extension.mongodb.annotations.api.MongoExecutable;
import com.services.spotify.extension.mongodb.api.MongoClient;
import com.services.spotify.extension.mongodb.mongoservice.MongoDbClient;
import com.services.spotify.extension.mongodb.mongoservice.TestService;

public class MongoDbActions {
	
	private static MongoDbActions instance=null;
	
	private MongoDbActions() {
		super();
	}

	public void startMongo(MongoExecutable annotation) throws UnknownHostException, IOException  {
		TestService.getInstance().connectMongoDeamon(annotation.host(),annotation.port(), annotation.master(), annotation.enableAuth(),
													 annotation.useNoJournal(), annotation.enableTextSearch(), annotation.verbose());
	}

	public void stopMongo() {
		TestService.getInstance().disconnectMongoDeamon();
	}

	public MongoDbClient getClient(MongoClient annotation) throws Exception {
		return TestService.getInstance().provideClient(annotation.host(), annotation.port(), annotation.journaled(), annotation.lazy());
	}
	
	public static MongoDbActions getInstance() {
		if (instance == null)
			instance = new MongoDbActions();
		return instance;
	}

}
