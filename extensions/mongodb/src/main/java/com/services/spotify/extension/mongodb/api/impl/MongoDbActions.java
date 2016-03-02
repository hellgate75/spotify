package com.services.spotify.extension.mongodb.api.impl;

import java.io.IOException;
import java.net.UnknownHostException;

import com.services.spotify.extension.mongodb.api.DeployMongoDb;
import com.services.spotify.extension.mongodb.api.UnDeployMongoDb;
import com.services.spotify.extension.mongodb.mongoservice.MongoDbClient;
import com.services.spotify.extension.mongodb.mongoservice.TestService;

public class MongoDbActions implements IMongoDbActions {
	
	private static MongoDbActions instance=null;
	
	private MongoDbActions() {
		super();
	}

	@Override
	public void startMongo(DeployMongoDb annotation) throws UnknownHostException, IOException  {
		TestService.getInstance().connectMongoDeamon(annotation.host(),annotation.port(), annotation.master(), annotation.enableAuth(),
													 annotation.useNoJournal(), annotation.enableTextSearch(), annotation.verbose());
	}

	@Override
	public void stopMongo(UnDeployMongoDb annotation) {
		TestService.getInstance().disconnectMongoDeamon();
	}

	@Override
	public MongoDbClient getClient(com.services.spotify.extension.mongodb.api.MongoClient annotation) throws Exception {
		return TestService.getInstance().provideClient(annotation.host(), annotation.port(), annotation.journaled(), annotation.lazy());
	}
	
	public static MongoDbActions getInstance() {
		if (instance == null)
			instance = new MongoDbActions();
		return instance;
	}

}
