package com.tagit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(AppConfig.class)
public class TagItApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TagItApp.class, args);
        AppConfig config = context.getBean(AppConfig.class);
        TagItGUI.setApplicationContext(context);
        
        javafx.application.Application.launch(TagItGUI.class, args);
    }
}