package org.wikitolearn.EasyLinkAPI.utils;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
	private MongoClient mongoClient;
	private static final MongoConnection INSTANCE = new MongoConnection();
	
	private MongoConnection(){
		this.mongoClient = new MongoClient( "localhost" );
	}
	
	public static MongoConnection getInstance(){
		return INSTANCE;
	}
	
	public MongoCollection connect(){
		MongoCollection collection = mongoClient.getDatabase("local").getCollection("annotations");
		return collection;
	}
	
	public void disconnect(){
		mongoClient.close();
	}
}