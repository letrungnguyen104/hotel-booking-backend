package com.project.hotel.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LogFolderInitializer {
    @PostConstruct
    public void init() {
        String logPath = System.getProperty("user.dir") + File.separator + "logging";
        File folder = new File(logPath);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
                System.out.println("Created log directory: " + logPath);
            }
        }
    }
}
