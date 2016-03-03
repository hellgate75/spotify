package com.services.spotify.extension.mongodb.annotations.spi;

import java.lang.reflect.Field;

import com.services.spotify.extension.mongodb.annotations.api.MongoExecutable;
import com.services.spotify.extension.mongodb.api.MongoClient;

public class MongoDbExecutable {

	private boolean mongoStarted = false;
	
	public MongoDbExecutable() throws Exception {
		super();
		this.startup();
		applyFieldAnnotations(this);
	}
	
	private final void startup() throws Exception {
		if (executeStartup(this))
			mongoStarted=true;
		else
			mongoStarted = false;
	}

	@Override
	protected void finalize() throws Throwable {
		if (this.mongoStarted)
			this.teardown();
		super.finalize();
	}

	private final void teardown() throws Exception {
		executeTeardown();
	}
	
	private static boolean executeStartup(MongoDbExecutable executable) throws Exception {
		boolean executed = false;
		MongoExecutable me = executable.getClass().getAnnotation(MongoExecutable.class);
		if (me!=null) {
			MongoDbActions.getInstance().startMongo(me);
			executed = true;
		}
		return executed;
	}
	private static void executeTeardown() throws Exception {
		MongoDbActions.getInstance().stopMongo();
	}
	
	private static void applyFieldAnnotations(MongoDbExecutable executable) {
		Field[] fields = executable.getClass().getDeclaredFields();
		for (Field field : fields) {
			MongoClient clientAnnotation = field.getAnnotation(MongoClient.class);
			if (clientAnnotation != null) {
				try {
					if (field.getType().isAssignableFrom(com.services.spotify.extension.mongodb.mongoservice.MongoDbClient.class)) {
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
