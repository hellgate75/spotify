package com.services.spotify.extension.mongodb.mongoservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;

public class MongoDbClient {
	private String host = "localhost";
	private int port = 27017;
	private List<ServerAddress> addresses = null;
	private List<MongoCredential> credentials = null;
	private boolean journaled = true;
	private String dbName = null;
	private String collectionName = null;
	private MongoClient client = null;
	private MongoDatabase database = null;
	private MongoCollection<Document> collection = null;

	public MongoDbClient() {
		super();
	}
	public MongoDbClient(String host, int port, boolean journaled, boolean lazy) throws Exception {
		super();
		this.host = host;
		this.port = port;
		this.journaled = journaled;
		if (!lazy) {
			this.client = new MongoClient(this.host, this.port);
			if (this.journaled)
				this.client.setWriteConcern(WriteConcern.JOURNALED);
			this.client.setReadPreference(ReadPreference.primary());
			if (this.client.isLocked())
				this.client.unlock();
		}
	}

	public MongoDbClient(List<ServerAddress> addresses, List<MongoCredential> credentials, boolean journaled) throws Exception {
		super();
		this.addresses = addresses;
		this.credentials = credentials;
		this.journaled = journaled;
		this.client = new MongoClient(this.addresses, this.credentials);
		if (this.journaled)
			this.client.setWriteConcern(WriteConcern.JOURNALED);
		this.client.setReadPreference(ReadPreference.primary());
		if (this.client.isLocked())
			this.client.unlock();
	}

	public void reconnect() throws Exception {
		if (!this.isConnected()) {
			if (addresses == null)
				this.client = new MongoClient(this.host, this.port);
			else
				this.client = new MongoClient(this.addresses, this.credentials);
			if (this.journaled)
				this.client.setWriteConcern(WriteConcern.JOURNALED);
			this.client.setReadPreference(ReadPreference.primary());
			if (this.client.isLocked())
				this.client.unlock();
		}
	}

	public void reconnect(String host, int port, boolean journaled) throws Exception {
		if (this.isConnected()) {
			this.client.close();
			this.client = null;
			this.addresses = null;
			this.credentials = null;
			this.dbName = null;
			this.journaled = false;
			this.collectionName = null;
			this.database = null;
			this.collection = null;
		}
		this.host = host;
		this.port = port;
		this.journaled = journaled;
		this.reconnect();
	}

	public void reconnect(List<ServerAddress> addresses, List<MongoCredential> credentials, boolean journaled) throws Exception {
		if (this.isConnected()) {
			this.client.close();
			this.client = null;
			this.addresses = null;
			this.credentials = null;
			this.dbName = null;
			this.journaled = false;
			this.collectionName = null;
			this.database = null;
			this.collection = null;
		}
		this.addresses = addresses;
		this.credentials = credentials;
		this.journaled = journaled;
		this.reconnect();
	}

	public void disconnect() throws Exception {
		if (this.isConnected()) {
			this.client.close();
			this.client = null;
			this.dbName = null;
			this.journaled = false;
			this.collectionName = null;
			this.database = null;
			this.collection = null;
		}
	}

	public boolean isConnected() {
		return client!=null;
	}

	public List<String> databases() throws Exception {
		try {
			List<String> databaseNames = new ArrayList<String>(0);
			MongoIterable<String> mongoDatabases = this.client.listDatabaseNames();
			for(String collectionName: mongoDatabases) {
				databaseNames.add(collectionName);
			}
			return databaseNames;
		} catch (Exception e) {
			throw e;
		}
	}

	public void database(String databaseName) throws Exception {
		try {
			this.database = this.client.getDatabase(databaseName);
			this.dbName = databaseName;
		} catch (Exception e) {
			this.dbName = null;
			this.collectionName = null;
			this.database = null;
			this.collection = null;
			throw e;
		}
	}

	public MongoCollection<Document> collection(String collectionName) throws Exception {
		try {
			this.collection = this.database.getCollection(collectionName);
			this.collectionName = collectionName;
			return this.collection;
		} catch (Exception e) {
			collectionName = null;
			collection = null;
			throw e;
		}
	}

	public MongoCollection<Document> createCollection(String collectionName) throws Exception {
		try {
			Document command = new Document();
			command.put("create", collectionName);
			this.database.runCommand(command);
			return this.collection(collectionName);
		} catch (Exception e) {
		}
		return null;
	}

	public List<String> collections() throws Exception {
		try {
			List<String> collectionNames = new ArrayList<String>(0);
			MongoIterable<String> mongoCollections = this.database.listCollectionNames();
			for(String collectionName: mongoCollections) {
				collectionNames.add(collectionName);
			}
			return collectionNames;
		} catch (Exception e) {
			throw e;
		}
	}


	public List<Document> all() throws Exception {
		List<Document> results = new ArrayList<Document>(0);
		MongoCursor<Document> cursor = this.cursor(null);
		while(cursor.hasNext()) {
			results.add(cursor.next());
		}
		return results;
	}

	public List<Document> filter(Bson filter) throws Exception {
		List<Document> results = new ArrayList<Document>(0);
		MongoCursor<Document> cursor = this.cursor(filter);
		while(cursor.hasNext()) {
			results.add(cursor.next());
		}
		return results;
	}

	public MongoCursor<Document> cursor(Bson filter) throws Exception {
		FindIterable<Document> iter = filter!=null ? this.collection.find(filter) : this.collection.find();
		return iter.iterator();
	}

	public FindIterable<Document> iterator(Bson filter) throws Exception {
		return filter!=null ? this.collection.find(filter) : this.collection.find();
	}

	public long count() throws Exception {
		return collection.count();
	}

	public void insert(Document document) throws Exception {
		collection.insertOne(document);
	}

	public void insert(List<Document> documents) throws Exception {
		collection.insertMany(documents);
	}

	public void insert(List<Document> documents, InsertManyOptions opts) throws Exception {
		collection.insertMany(documents, opts);
	}

	public DeleteResult delete(Bson filter, boolean onlyOne) throws Exception {
		if (onlyOne)
			return collection.deleteOne(filter);
		else
			return collection.deleteMany(filter);
	}

	public UpdateResult update(Bson filter, Bson update, UpdateOptions updateOptions, boolean onlyOne) throws Exception {
		if (Document.class.isAssignableFrom(update.getClass())) {
			if(((Document)update).get("$set")==null)
				update = new Document("$set",  update);
		}
		if (onlyOne) {
			if (updateOptions==null) {
				return collection.updateOne(filter, update);
			}
			else {
				return collection.updateOne(filter, update, updateOptions);
			}
		}
		else {
			if (updateOptions==null) {
				return collection.updateMany(filter, update);
			}
			else {
				return collection.updateMany(filter, update, updateOptions);
			}
		}
	}

	public UpdateResult replace(Bson filter, Document replacement, UpdateOptions updateOptions) throws Exception {
		if (updateOptions==null) {
			return collection.replaceOne(filter, replacement);
		}
		else {
			return collection.replaceOne(filter, replacement, updateOptions);
		}
	}

	public UpdateOptions createUpdateOption(boolean updateWhenNoMatchFound) {
		UpdateOptions options = new UpdateOptions();
		options.upsert(updateWhenNoMatchFound);
		return options;
	}


	public InsertManyOptions createInsertManyOptions(boolean isOrdered) {
		InsertManyOptions options = new InsertManyOptions();
		options.ordered(isOrdered);
		return options;
	}


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setHost(String host) {
		this.host = host;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setJournaled(boolean journaled) {
		this.journaled = journaled;
	}
	public List<ServerAddress> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<ServerAddress> addresses) {
		this.addresses = addresses;
	}
	public List<MongoCredential> getCredentials() {
		return credentials;
	}
	public void setCredentials(List<MongoCredential> credentials) {
		this.credentials = credentials;
	}
	public boolean isJournaled() {
		return journaled;
	}

	public String getDbName() {
		return dbName;
	}

	public String getCollectionName() {
		return collectionName;
	}

	@Override
	protected void finalize() throws Throwable {
		this.disconnect();
		super.finalize();
	}

	public static ObjectId createObjectId() {
		return new ObjectId(new Date(System.currentTimeMillis()));
	}

	public static ObjectId convertObjectId(String id) {
		return new ObjectId(id);
	}

}
