package com.moople.gitpals.MainApplication;

import com.mongodb.MongoClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
		MongoClient mongoClient = new MongoClient("mongodb://danmoop_admin:hackingtheinternet@ds215759.mlab.com:15759/gitpals", 15759);
	}

}
