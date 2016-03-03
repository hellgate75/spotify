package com.services.spotify.extension.mongodb.annotations.test.spi;

import java.util.List;

import org.bson.Document;

import com.services.spotify.extension.mongodb.annotations.api.MongoExecutable;
import com.services.spotify.extension.mongodb.annotations.spi.MongoDbExecutable;
import com.services.spotify.extension.mongodb.api.MongoClient;
import com.services.spotify.extension.mongodb.mongoservice.MongoDbClient;

@MongoExecutable(host="localhost", port=27017)
public class MongoDbSpi extends MongoDbExecutable {

	@MongoClient(lazy=true, host="localhost", port=27017) public MongoDbClient mongoClient;
	
	public MongoDbSpi() throws Exception {
		super();
	}

	public void initialize() throws Exception {
		//INFO: Lazy state need a first connect to work ....
		mongoClient.reconnect();
		mongoClient.database("access");
		mongoClient.collection("objects");
	}
	
	public void insert() throws Exception {
		for (int i=0; i<100; i++) {
			Document document = Document.parse("{\"name\": \"My Object "+(10456+i)+"\", \"code\":"+(10456+i)+"}");
			document.append("_id", MongoDbClient.createObjectId());
			mongoClient.insert(document);
		}
	}
	
	public long count() throws Exception {
		return mongoClient.count();
	}
	
	public void insertNew(int index) throws Exception {
		Document document = Document.parse("{\"name\": \"My Object "+(10656+index)+"\", \"code\":"+(10656+index)+"}");
		document.append("_id", MongoDbClient.createObjectId());
		mongoClient.insert(document);
	}
	
	public void shift() throws Exception {
		Document document =  mongoClient.all().get(0);
		Document filter = new Document();
		filter.append("_id", document.get("_id"));
		mongoClient.delete(filter, true);
	}
	
	public void pop() throws Exception {
		List<Document> docs = mongoClient.all();
		Document document =  docs.get(docs.size()-1);
		Document filter = new Document();
		filter.append("_id", document.get("_id"));
		mongoClient.delete(filter, true);
	}
	
	public void release() throws Exception {
		this.mongoClient.disconnect();
	}

	public MongoDbClient getMongoClient() {
		return mongoClient;
	}
	
	

}
