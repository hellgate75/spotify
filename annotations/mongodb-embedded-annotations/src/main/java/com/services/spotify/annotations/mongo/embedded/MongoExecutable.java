package com.services.spotify.annotations.mongo.embedded;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author <a href="mailto:hellgate75@gmail.com">Fabrizio Torelli</a>
 * @version $Revision: $
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MongoExecutable {
	public String host() default "localhost";
	public int port() default 27017;
	public boolean master() default true;
	public boolean enableAuth() default false;
	public boolean useNoJournal() default false;
	public boolean enableTextSearch() default false;
	public boolean verbose() default true;
}
