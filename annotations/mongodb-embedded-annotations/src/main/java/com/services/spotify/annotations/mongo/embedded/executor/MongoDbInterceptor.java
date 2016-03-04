package com.services.spotify.annotations.mongo.embedded.executor;

import java.lang.reflect.Field;

import com.services.spotify.annotations.mongo.embedded.MongoClient;
import com.services.spotify.annotations.mongo.embedded.MongoExecutable;
import com.services.spotify.annotations.mongo.embedded.MongoStartUp;
import com.services.spotify.annotations.mongo.embedded.MongoTearDown;
import com.services.spotify.annotations.mongo.embedded.MongoTearDownAll;
import com.services.spotify.annotations.mongo.embedded.client.MongoDbClient;


public class MongoDbInterceptor {

	public static void decorateMongoDbAnnotations(Object source) throws Throwable {
		applyFieldAnnotations(source);
		applyClassAnnotations(source);
	}
	
	private static void applyClassAnnotations(Object executable) {
		MongoExecutable executableAnnotation = executable.getClass().getAnnotation(MongoExecutable.class);
		if (executableAnnotation != null) {
			try {
					MongoDbActions.getInstance().startMongod(executableAnnotation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MongoStartUp startUpAnnotation = executable.getClass().getAnnotation(MongoStartUp.class);
		if (startUpAnnotation != null) {
			try {
					MongoDbActions.getInstance().startMongod(startUpAnnotation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MongoTearDown tearDownUpAnnotation = executable.getClass().getAnnotation(MongoTearDown.class);
		if (tearDownUpAnnotation != null) {
			try {
					MongoDbActions.getInstance().stopMongod(tearDownUpAnnotation);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		MongoTearDownAll tearDownUpAllAnnotation = executable.getClass().getAnnotation(MongoTearDownAll.class);
		if (tearDownUpAllAnnotation != null) {
			try {
					MongoDbActions.getInstance().teardownAllMongod();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void applyFieldAnnotations(Object executable) {
		Field[] fields = executable.getClass().getDeclaredFields();
		for (Field field : fields) {
			MongoClient clientAnnotation = field.getAnnotation(MongoClient.class);
			if (clientAnnotation != null) {
				try {
					if (field.getType().isAssignableFrom(MongoDbClient.class)) {
						field.setAccessible(true);
						field.set(executable, MongoDbActions.getInstance().getClient(clientAnnotation));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
