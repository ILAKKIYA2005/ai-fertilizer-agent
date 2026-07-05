package com.fertilizer.agent.config;

import org.springframework.context.annotation.Configuration;

/**
 * MongoDB configuration.
 * Uses the URI supplied via the MONGODB_URI environment variable
 * (spring.data.mongodb.uri in application-render.properties).
 * No embedded / Flapdoodle MongoDB is started in this configuration.
 */
@Configuration
public class MongoConfig {
    // Spring Data MongoDB auto-configures the MongoClient from
    // spring.data.mongodb.uri, so no bean definitions are needed here.
}
