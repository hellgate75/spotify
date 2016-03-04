package com.services.spotify.annotations.mongo.embedded.executor;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.services.spotify.annotations.mongo.embedded.client.MongoDbClient;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ExtractedArtifactStoreBuilder;
import de.flapdoodle.embed.mongo.config.IMongoCmdOptions;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongoCmdOptionsBuilder;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.extract.UserTempNaming;
import de.flapdoodle.embed.process.runtime.Network;

public class MongoDbService {
	private static MongoDbService instance = null;
	private Map<String, List<MongoInstance>> mongodInstanceList = new ConcurrentHashMap<String, List<MongoInstance>>(0);
	private boolean tearingDown = false;
	
	private MongoDbService() {
		super();
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				MongoDbService.this.shutdownDeamons();
			}
			
		});
	}
	
	public MongoDbClient provideClient(String host, int port, boolean journaled, boolean lazy) throws Exception {
		return new MongoDbClient(host, port, journaled, lazy);
	}
	
	public MongoDbClient provideClient(List<ServerAddress> addresses, List<MongoCredential> credentials, boolean journaled) throws Exception {
		return new MongoDbClient(addresses, credentials, journaled);
	}
	
	public void connectMongoDeamon(String hostname, int port, boolean master, boolean enableAuth, boolean useNoJournal, boolean enableTextSearch, boolean verbose) throws UnknownHostException, IOException, InstantiationException, Exception  {
		if (isActiveMongodInstance(hostname, port))
			throw new InstantiationException("Mongo Deamon is still working for hostname : " + hostname + ":"+port+"!!");
		if (tearingDown) 
			throw new InstantiationException("Mongo Deamon is tearing down and seamon on hostname : " + hostname + ":"+port+" is not processable!!");
		MongodExecutable mongodExe;
		MongodProcess mongod;
        try {
			IRuntimeConfig _runtimeConfig=new RuntimeConfigBuilder()
			.defaults(Command.MongoD)
			.artifactStore(new ExtractedArtifactStoreBuilder()
					.defaults(Command.MongoD)
					.executableNaming(new UserTempNaming())
					.build())
			.build();
			MongoCmdOptionsBuilder cmdBuilder = new MongoCmdOptionsBuilder()
			.master(master)
			.enableAuth(enableAuth)
			.useNoJournal(useNoJournal)
			.useNoPrealloc(false)
			.enableTextSearch(enableTextSearch)
			.verbose(verbose);
			IMongoCmdOptions cmdOptions = cmdBuilder.build();
			
			IMongodConfig mongoConfig = new MongodConfigBuilder()
			.net(new Net(port, Network.localhostIsIPv6()))
			.cmdOptions(cmdOptions)
			.version(Version.Main.PRODUCTION)
			.withLaunchArgument("--bind_ip", hostname)
			.build();

			mongodExe = MongodStarter.getInstance(_runtimeConfig).prepare(mongoConfig);
			File file = mongodExe.getFile().executable();
			if (file.exists())
				file.delete();
			System.out.println("File : " + file.getAbsolutePath());
			mongodExe = MongodStarter.getInstance(_runtimeConfig).prepare(mongoConfig);
			mongod = mongodExe.start();
			while(!mongod.isProcessRunning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			MongoInstance instance = new MongoInstance(hostname, port, mongodExe, mongod);
			addActiveMongodInstance(hostname, instance);
		} catch (Exception e) {
			System.out.println("Error in mongod starter ...");
			throw e;
		}
        
	}
	
	private synchronized void addActiveMongodInstance(String hostname, MongoDbService.MongoInstance instance) {
		List<MongoDbService.MongoInstance> mongoInstances = mongodInstanceList.get(hostname);
		if (mongoInstances!=null) {
			mongoInstances.add(instance);
			mongodInstanceList.put(hostname, mongoInstances);
		}
		else {
			mongoInstances = new ArrayList<MongoDbService.MongoInstance>(0);
			mongoInstances.add(instance);
			mongodInstanceList.put(hostname, mongoInstances);
		}
	}
	private synchronized void removeActiveMongodInstance(String hostname, MongoDbService.MongoInstance instance) {
		List<MongoDbService.MongoInstance> mongoInstances = mongodInstanceList.get(hostname);
		if (mongoInstances!=null) {
			mongoInstances.remove(instance);
			if (mongoInstances.size()==0) {
				mongodInstanceList.remove(hostname);
			}
			else {
				mongodInstanceList.put(hostname, mongoInstances);
			}
		}
	}

	private synchronized boolean isActiveMongodInstance(String hostname, Integer port) {
		return getActiveMongodInstance(hostname, port)!=null;
	}
	
	private synchronized MongoDbService.MongoInstance getActiveMongodInstance(String hostname, Integer port) {
			List<MongoDbService.MongoInstance> mongoInstances = mongodInstanceList.get(hostname);
			if (mongoInstances!=null) {
				for (MongoDbService.MongoInstance instance: mongoInstances) {
					if (instance.match(hostname, port))
						return instance;
				}
			}
			return null;
	}
	
	public synchronized void disconnectMongoDeamon(String hostname, Integer port) {
		 MongoDbService.MongoInstance instance =  getActiveMongodInstance(hostname, port);
		if (instance!=null) {
			instance.teardown();
			removeActiveMongodInstance(hostname, instance);
		}
	}
	
	public final synchronized void shutdownDeamons() {
		tearingDown = true;
		Set<String> hostnames = mongodInstanceList.keySet();
		for(String hostname: hostnames) {
			List<MongoDbService.MongoInstance> mongoInstances = mongodInstanceList.get(hostname);
			if (mongoInstances!=null) {
				for(MongoDbService.MongoInstance instance: mongoInstances) {
					if (instance!=null) {
						instance.teardown();
						removeActiveMongodInstance(hostname, instance);
					}
				}
			}
		}
		tearingDown = false;
	}
	
	public static MongoDbService getInstance() {
		if (instance == null)
			instance = new MongoDbService();
		return instance;
	}
	
	private final class MongoInstance {
		private String hostname;
		private Integer port;
		private MongodExecutable mongodExe;
		private MongodProcess mongod;
		private boolean running = true;
		public MongoInstance(String hostname, Integer port,
				MongodExecutable mongodExe, MongodProcess mongod) {
			super();
			if (hostname==null || hostname.length()==0)
				throw new NullPointerException("Mongo Deamon Hostname cannot be null.");
			this.hostname = hostname;
			if (port==null)
				throw new NullPointerException("Mongo Deamon Port cannot be null.");
			this.port = port;
			if (mongodExe==null)
				throw new NullPointerException("Mongo Deamon invalid exe definition.");
			this.mongodExe = mongodExe;
			if (mongod==null)
				throw new NullPointerException("Mongo Deamon invalid deamon definition.");
			this.mongod = mongod;
		}
		public boolean match(String hostname, Integer port) {
			return this.hostname.equals(hostname) && this.port.equals(port);
		}
		public void teardown() {
			if (!running)
				return;
			mongod.stop();
			mongodExe.stop();
			while(mongod.isProcessRunning()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			mongod = null;
			mongodExe = null;
			hostname = null;
			port = null;
			running = false;
		}
	}
}
