package com.services.spotify.extension.mongodb.annotations.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.services.spotify.extension.mongodb.annotations.test.spi.MongoDbSpi;
import com.services.spotify.extension.mongodb.api.UnDeployMongoDb;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestMongoDbSpi {
	private static MongoDbSpi spi = null;
	
	public static void initializeMongo() throws Exception {
		System.out.println("initializeMongo ....");
		spi = new MongoDbSpi();
		spi.initialize();
		spi.insert();
	}

	public static void releaseMongo() throws Exception {
		System.out.println("releaseMongo");
		spi.release();
	}
	
	@Test
	public void testAShouldBeMongoDb() throws Exception {
		initializeMongo();
		assertNotNull(spi.getMongoClient());
	}

	@Test
	public void testBShouldBePresentOneUndredObjects() throws Exception {
		System.out.println("testBShouldBePresentOneUndredObjects");
		assertEquals(100L, spi.count());
	}

	@Test
	public void testCShouldBeAbleToInsertANewObject() throws Exception {
		spi.insertNew(1);
		assertEquals(101L, spi.count());
	}
	
	@Test
	@UnDeployMongoDb
	public void testXShouldMongoDbHaveBeenClosed() throws Exception {
		System.out.println("testCShouldBeAbleToInsertANewObject");
		releaseMongo();
		assertTrue(!spi.getMongoClient().isConnected());
	}

	@Test
	public void testDhouldBeAbleToDeleteAnObject() throws Exception {
		System.out.println("testDhouldBeAbleToDeleteAnObject");
		spi.shift();
		assertEquals(100L, spi.count());
	}
	
}
