package com.services.spotify.annotations.mongo.embedded;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author <a href="mailto:hellgate75@gmail.com">Fabrizio Torelli</a>
 * @version $Revision: $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MongoClient {
	public String host() default "localhost";
	public int port() default 27017;
	public boolean journaled() default true;
	public boolean lazy() default true;
}
