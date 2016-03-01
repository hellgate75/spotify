package com.services.spotify.extension.mongodb.api.impl;

import java.io.IOException;
import java.net.UnknownHostException;

import com.services.spotify.extension.mongodb.api.DeployMongoDb;
import com.services.spotify.extension.mongodb.api.UnDeployMongoDb;
import com.services.spotify.extension.mongodb.mongoservice.MongoDbClient;

public interface IMongoDbActions {
	public void startMongo(DeployMongoDb annotation) throws UnknownHostException, IOException ;
	public void stopMongo(UnDeployMongoDb annotation);
	public MongoDbClient getClient(com.services.spotify.extension.mongodb.api.MongoClient annotation) throws Exception;
}
