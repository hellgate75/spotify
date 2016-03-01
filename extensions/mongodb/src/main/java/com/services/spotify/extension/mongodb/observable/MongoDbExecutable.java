package com.services.spotify.extension.mongodb.observable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.services.spotify.extension.mongodb.api.DeployMongoDb;
import com.services.spotify.extension.mongodb.api.MongoClient;
import com.services.spotify.extension.mongodb.api.UnDeployMongoDb;
import com.services.spotify.extension.mongodb.api.impl.MongoDbActions;

public class MongoDbExecutable {

	public MongoDbExecutable() {
		super();
		executeAnnotations(this);
	}
	

	private static void executeAnnotations(Object object) {
		Method[] methods = object.getClass().getMethods();
		Field[] fields = object.getClass().getFields();

		for (Method method : methods) {
			DeployMongoDb deployAnnotation = method.getAnnotation(DeployMongoDb.class);
			if (deployAnnotation != null) {
				try {
					MongoDbActions.getInstance().startMongo(deployAnnotation);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			UnDeployMongoDb undeployAnnotation = method.getAnnotation(UnDeployMongoDb.class);
			if (undeployAnnotation != null) {
				try {
					MongoDbActions.getInstance().stopMongo(undeployAnnotation);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		for (Field field : fields) {
			MongoClient clientAnnotation = field.getAnnotation(MongoClient.class);
			if (clientAnnotation != null) {
				try {
					if (field.getClass().isAssignableFrom(com.services.spotify.extension.mongodb.mongoservice.MongoDbClient.class))
						field.set(object, MongoDbActions.getInstance().getClient(clientAnnotation));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
