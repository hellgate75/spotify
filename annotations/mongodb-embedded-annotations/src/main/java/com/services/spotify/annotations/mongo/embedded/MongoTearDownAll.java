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
@Target(ElementType.TYPE)
public @interface MongoTearDownAll {
}
