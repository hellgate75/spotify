package com.services.spotify.extension.mongodb.test;

import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import com.services.spotify.extension.mongodb.api.DeployMongoDb;
import com.services.spotify.extension.mongodb.api.MongoClient;
import com.services.spotify.extension.mongodb.api.UnDeployMongoDb;
import com.services.spotify.extension.mongodb.mongoservice.MongoDbClient;
import com.services.spotify.extension.mongodb.runner.MongoDbRunner;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(MongoDbRunner.class)
public class TestMongoDbRunner {
	@MongoClient(host="localhost", port=27017) public MongoDbClient mongoClient;
	
	@BeforeClass
	@DeployMongoDb(host="localhost", port=27017)
	public static void beforeClass() {
		System.out.println("beforeClass ....");
	}

	@Before
	public void createMongoData() throws Exception {
		System.out.println("createMongoData");
		mongoClient.database("access");
		mongoClient.collection("objects");
		for (int i=0; i<100; i++) {
			Document document = Document.parse("{\"name\": \"My Object "+(10456+i)+"\", \"code\":"+(10456+i)+"}");
			document.append("_id", MongoDbClient.createObjectId());
			mongoClient.insert(document);
		}
	}
	
	@After
	public void deleteMongoData() throws Exception {
		System.out.println("deleteMongoData");
		mongoClient.disconnect();
	}
	
	@Test
	public void testAShouldBeMongoDb() {
		System.out.println("testAShouldBeMongoDb");
		assertNotNull(mongoClient);
	}

	@Test
	public void testBShouldBePresentOneUndredObjects() throws Exception {
		System.out.println("testBShouldBePresentOneUndredObjects");
		assertEquals(100L, mongoClient.count());
	}

	@Test
	public void testCShouldBeAbleToInsertANewObject() throws Exception {
		System.out.println("testCShouldBeAbleToInsertANewObject");
		Document document = Document.parse("{\"name\": \"My Object "+(10656)+"\", \"code\":"+(10656)+"}");
		document.append("_id", MongoDbClient.createObjectId());
		mongoClient.insert(document);
		assertEquals(101L, mongoClient.count());
	}
	
	@Test
	@UnDeployMongoDb
	public void testXShouldMongoDbHaveBeenClosed() throws Exception {
		System.out.println("testCShouldBeAbleToInsertANewObject");
		assertTrue(!mongoClient.isConnected());
	}

	@Test
	public void testDhouldBeAbleToDeleteAnObject() throws Exception {
		System.out.println("testDhouldBeAbleToDeleteAnObject");
		Document document =  mongoClient.all().get(0);
		Document filter = new Document();
		filter.append("_id", document.get("_id"));
		mongoClient.delete(filter, true);
		assertEquals(100L, mongoClient.count());
	}
	
}
