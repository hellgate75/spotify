package com.services.spotify.annotations.mongo.embedded.client;

import java.util.Map;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MyDocument extends Document {

	private static final long serialVersionUID = -9215172859114658327L;

	public MyDocument() {
		super();
	}

	public MyDocument(Map<String, Object> map) {
		super(map);
	}

	public MyDocument(String key, Object value) {
		super(key, value);
	}
	
	public MyDocument(String json) throws Exception {
		super();
		Document d = Document.parse(json);
		for(String key: d.keySet()) {
			this.put(key, d.get(key));
		}
	}

	@Override
	public String toString() {
		return "MyDocument [json=" + toJson() + "]";
	}

	public JSONObject getJSONObject() throws Exception {
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(this.toJson());
	}
	
	public static Document fromJSON(String json) throws Exception {
		return fromJSON(json, null);
	}
	public static Document fromJSON(String json, String idField) throws Exception {
		Document doc = Document.parse(json);
		if (idField!=null && doc!=null && doc.containsKey(idField)) {
			String id = doc.getString(idField);
			doc.put(idField, MongoDbClient.convertObjectId(id));
		}
		else if (idField!=null && doc!=null && !doc.containsKey(idField)) {
			doc.put(idField, MongoDbClient.createObjectId());
		}
		else if (doc!=null ) {
			doc.put(idField, MongoDbClient.createObjectId());
		}
		return doc;
	}
	
}
