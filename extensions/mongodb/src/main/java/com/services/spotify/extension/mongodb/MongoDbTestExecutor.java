package com.services.spotify.extension.mongodb;

import java.lang.reflect.Method;

import org.jboss.arquillian.container.spi.event.container.AfterUnDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.container.spi.event.container.BeforeUnDeploy;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

/**
 * MongoDbExecutor
 *
 * @author <a href="mailto:hellgate75@gmail.com">Fabrizio Torelli</a>
 * @version $Revision: $
 */
public class MongoDbTestExecutor {

	   public void executeBeforeClass(@Observes BeforeClass event, TestClass testClass)
	   {
	      execute(
	            testClass.getMethods(
	                  com.services.spotify.extension.mongodb.api.DeployMongoDb.class)
	      );
	   }

	   public void executeAfterClass(@Observes AfterClass event, TestClass testClass)
	   {
	      execute(
	            testClass.getMethods(
	                  com.services.spotify.extension.mongodb.api.UnDeployMongoDb.class)
	      );
	   }

	   public void executeBeforeDeploy(@Observes BeforeDeploy event, TestClass testClass)
	   {
	      execute(
	            testClass.getMethods(
	                  com.services.spotify.extension.mongodb.api.DeployMongoDb.class)
	      );
	   }

	   public void executeAfterUnDeploy(@Observes AfterUnDeploy event, TestClass testClass)
	   {
	      execute(
	            testClass.getMethods(
	                  com.services.spotify.extension.mongodb.api.UnDeployMongoDb.class)
	      );
	   }


	   private void execute(Method[] methods)
	   {
	      if(methods == null)
	      {
	         return;
	      }
	      for(Method method : methods)
	      {
	         try
	         {
	            method.invoke(null);
	         }
	         catch (Exception e) 
	         {
	            throw new RuntimeException("Could not execute @BeforeDeploy method: " + method, e);
	         }
	      }
	   }
}
