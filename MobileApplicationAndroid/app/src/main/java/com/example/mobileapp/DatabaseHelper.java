package com.example.mobileapp;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public class DatabaseHelper {
    private String connectionString = "mongodb://localhost:27017";
    private MongoClientURI uri = new MongoClientURI(connectionString);
    private MongoClient mongoClient = new MongoClient(uri);
    private MongoDatabase mongoDatabase = mongoClient.getDatabase("homestays_management");

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }
}
