package com.computerscience.filesystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileSystemApplication {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemApplication.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        SpringApplication.run(FileSystemApplication.class, args);
    }

}


