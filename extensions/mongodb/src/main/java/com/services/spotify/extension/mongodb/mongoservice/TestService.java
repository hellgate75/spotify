package com.services.spotify.extension.mongodb.mongoservice;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.jboss.arquillian.core.spi.InvocationException;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

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

public class TestService {
	private static TestService instance = null;
	private MongodExecutable mongodExe;
	private MongodProcess mongod;
	private boolean started = false;
	
	private TestService() {
		
	}
	
	public MongoDbClient provideClient(String host, int port, boolean journaled) throws Exception {
		return new MongoDbClient(host, port, journaled);
	}
	
	public MongoDbClient provideClient(List<ServerAddress> addresses, List<MongoCredential> credentials, boolean journaled) throws Exception {
		return new MongoDbClient(addresses, credentials, journaled);
	}
	
	public void connectMongoDeamon(String hostname, int port, boolean master, boolean enableAuth, boolean useNoJournal, boolean enableTextSearch, boolean verbose) throws UnknownHostException, IOException  {
		if (started)
			throw new InvocationException(new RuntimeException("Mongo Deamon is still working ..."));
//		logger.info("setup() - creating MongoDb embedded listener ....");
		started = true;
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
			.enableTextSearch(enableTextSearch)
			.verbose(verbose);
			IMongoCmdOptions cmdOptions = cmdBuilder.build();
			
			MongodConfigBuilder mongoBuilder = new MongodConfigBuilder()
			.net(new Net(port, Network.localhostIsIPv6()))
			.cmdOptions(cmdOptions)
			.version(Version.Main.PRODUCTION);
			if (hostname!=null)
				mongoBuilder = mongoBuilder.withLaunchArgument("bind_ip", hostname);
			IMongodConfig mongoConfig = mongoBuilder.build();

//		logger.info("setup() - starting MongoDb embedded listener ....");
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			started = false;
			System.out.println("Error in mongod starter ...");
			throw e;
		}
        
	}
	
	public void disconnectMongoDeamon() {
		if (started) {
			mongod.stop();
			mongodExe.stop();
			mongod = null;
			mongodExe = null;
			started = false;
		}
	}
	
	public static TestService getInstance() {
		if (instance == null)
			instance = new TestService();
		return instance;
	}
}
