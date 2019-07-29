package com.moople.gitpals.MainApplication;

import com.moople.gitpals.MainApplication.Service.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainApplication
{

	public static void main(String[] args)
	{
		// Start app
		SpringApplication.run(MainApplication.class, args);

		// Init data for later importing and using
		Data.initTechnologiesMap();
	}

}
