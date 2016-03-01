package com.services.spotify.extension.mongodb.runner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.services.spotify.extension.mongodb.api.DeployMongoDb;
import com.services.spotify.extension.mongodb.api.MongoClient;
import com.services.spotify.extension.mongodb.api.UnDeployMongoDb;
import com.services.spotify.extension.mongodb.api.impl.MongoDbActions;

public class MongoDbRunner extends BlockJUnit4ClassRunner {
	private List<FrameworkMethod> testMethodList = new ArrayList<FrameworkMethod>(0);
	private List<Method> beforeMethodList = new ArrayList<Method>(0);
	private List<Method> afterMethodList = new ArrayList<Method>(0);
	private List<Method> beforeClassMethodList = new ArrayList<Method>(0);
	private List<Method> afterClassMethodList = new ArrayList<Method>(0);
	private Class<?> currentTestClass = null;
	private Object test = null;

	public MongoDbRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		this.currentTestClass = testClass;
		for (FrameworkMethod m: super.getChildren()) {
			if (m.getAnnotation(Test.class)!=null) {
				testMethodList.add(m);
			}
		}
		for (Method m: testClass.getDeclaredMethods()) {
			if (!testMethodList.contains(m) && m.getReturnType().getName().equals("void") && m.getTypeParameters().length==0) {
				if (m.getAnnotation(BeforeClass.class)!=null && m.getModifiers()==Modifier.PUBLIC) {
					beforeClassMethodList.add(m);
				}
				else if (m.getAnnotation(AfterClass.class)!=null && m.getModifiers()==Modifier.PUBLIC+Modifier.STATIC) {
					afterClassMethodList.add(m);
				}
				else if (m.getAnnotation(Before.class)!=null && m.getModifiers()==Modifier.PUBLIC+Modifier.STATIC) {
					beforeMethodList.add(m);
				}
				else if (m.getAnnotation(After.class)!=null && m.getModifiers()==Modifier.PUBLIC) {
					afterMethodList.add(m);
				}
			}
		}
	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription(this.currentTestClass.getName(),
				this.currentTestClass.getAnnotations());
	}



	
	@Override
	protected String testName(FrameworkMethod method) {
		// TODO Auto-generated method stub
		return super.testName(method);
	}

	@Override
	protected Object createTest() throws Exception {
		return this.test;
	}

	@Override
	public void run(RunNotifier notifier) { 
		for(Method method: beforeClassMethodList) {
			executeTestAnnotations(method);
			try {
				method.invoke(null);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		try {
			this.test =  super.createTest();
			applyFieldAnnotations(this.test);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		for(Method beforemethod: beforeMethodList) {
			executeTestAnnotations(beforemethod);
			try {
				beforemethod.invoke(this.test);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		for (FrameworkMethod method: testMethodList) {
			executeTestAnnotations(method.getMethod());
			super.runChild(method, notifier);
		}
		for(Method aftermethod: afterMethodList) {
			executeTestAnnotations(aftermethod);
			try {
				aftermethod.invoke(this.test);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
		for(Method method: afterClassMethodList) {
			executeTestAnnotations(method);
			try {
				method.invoke(null);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void executeTestAnnotations(Method method) {
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

	private void applyFieldAnnotations(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			MongoClient clientAnnotation = field.getAnnotation(MongoClient.class);
			if (clientAnnotation != null) {
				try {
					if (field.getType().isAssignableFrom(com.services.spotify.extension.mongodb.mongoservice.MongoDbClient.class)) {
						field.setAccessible(true);
						field.set(object, MongoDbActions.getInstance().getClient(clientAnnotation));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
