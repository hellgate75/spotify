package com.services.spotify.annotations.mongo.embedded.processor;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MongoDbAnnotationProcessor extends AbstractProcessor {

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		Set<String> annotationsByFullName = new HashSet<String>(0);
		annotationsByFullName.add("com.services.spotify.annotations.mongo.embedded.MongoClient");
		annotationsByFullName.add("com.services.spotify.annotations.mongo.embedded.MongoExecutable");
		annotationsByFullName.add("com.services.spotify.annotations.mongo.embedded.MongoStartUp");
		annotationsByFullName.add("com.services.spotify.annotations.mongo.embedded.MongoTearDown");
		annotationsByFullName.add("com.services.spotify.annotations.mongo.embedded.MongoTearDownAll");
		return annotationsByFullName;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		System.out.println("MongoDbAnnotationProcessor - process() - annotations : " + annotations.size());
		for(TypeElement type : annotations) {
			/*if (type.getKind().isClass()) {
				
			}
			else if (type.getKind().isField()) {
				
			}*/
			System.out.println("type : " + type.getQualifiedName());
			System.out.println("is class : " + type.getKind().isClass());
			System.out.println("is field : " + type.getKind().isField());
			System.out.println("enclosing type : " + type.getEnclosingElement().getSimpleName());
			System.out.println("is class : " + type.getEnclosingElement().getKind().isClass());
			System.out.println("is field : " + type.getEnclosingElement().getKind().isField());
		}
		return false;
	}

}
