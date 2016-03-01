package com.services.spotify.extension.mongodb;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;

import com.services.spotify.extension.mongodb.api.DeployMongoDb;
import com.services.spotify.extension.mongodb.api.MongoClient;
import com.services.spotify.extension.mongodb.api.UnDeployMongoDb;
import com.services.spotify.extension.mongodb.api.impl.MongoDbActions;

/**
 * MongoDbExecutor
 *
 * @author <a href="mailto:hellgate75@gmail.com">Fabrizio Torelli</a>
 * @version $Revision: $
 */
public class MongoDbExecutor {

	private void execute(Object object) {
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
