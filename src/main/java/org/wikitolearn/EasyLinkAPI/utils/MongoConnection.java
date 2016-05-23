package org.wikitolearn.EasyLinkAPI.utils;

import com.mongodb.MongoClient;
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
	
	public MongoDatabase connect(){
		MongoDatabase db = mongoClient.getDatabase("local");
		return db;
	}
	
	public void disconnect(){
		mongoClient.close();
	}
}