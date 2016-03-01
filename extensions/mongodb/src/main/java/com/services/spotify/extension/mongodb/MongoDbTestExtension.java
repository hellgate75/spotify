package com.services.spotify.extension.mongodb;

import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * MongoDbExtension
 *
 * @author <a href="mailto:hellgate75@gmail.com">Fabrizio Torelli</a>
 * @version $Revision: $
 */
public class MongoDbTestExtension implements LoadableExtension {

	@Override
	   public void register(ExtensionBuilder builder)
	   {
	      builder.observer(MongoDbTestExecutor.class);
	   }

}
