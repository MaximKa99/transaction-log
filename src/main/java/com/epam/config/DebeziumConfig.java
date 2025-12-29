package com.epam.config;

import com.epam.service.CdcService;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.embedded.Connect;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class DebeziumConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebeziumConfig.class);
    
    @Value("${spring.datasource.host}")
    private String dbHost;
    
    @Value("${spring.datasource.port}")
    private int dbPort;
    
    @Value("${spring.datasource.database}")
    private String dbName;
    
    @Value("${spring.datasource.username}")
    private String dbUser;
    
    @Value("${spring.datasource.password}")
    private String dbPassword;
    
    @Value("${debezium.slot.name}")
    private String slotName;
    
    @Value("${debezium.publication.name}")
    private String publicationName;
    
    private DebeziumEngine<ChangeEvent<SourceRecord, SourceRecord>> engine;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final CdcService cdcService;
    
    public DebeziumConfig(CdcService cdcService) {
        this.cdcService = cdcService;
    }
    
    private DebeziumEngine<ChangeEvent<SourceRecord, SourceRecord>> createDebeziumEngine() {
        Properties props = new Properties();
        props.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
        props.setProperty("offset.flush.interval.ms", "60000");
        props.setProperty("name", "formula-connector");
        props.setProperty("database.hostname", dbHost);
        props.setProperty("database.port", String.valueOf(dbPort));
        props.setProperty("database.user", dbUser);
        props.setProperty("database.password", dbPassword);
        props.setProperty("database.dbname", dbName);
        props.setProperty("database.server.name", "formula-server");
        props.setProperty("topic.prefix", "formula-server");
        props.setProperty("table.include.list", "public.formulas");
        props.setProperty("plugin.name", "pgoutput");
        props.setProperty("slot.name", slotName);
        props.setProperty("publication.name", publicationName);
        props.setProperty("publication.autocreate.mode", "filtered");
        props.setProperty("schema.history.internal", "io.debezium.storage.memory.MemorySchemaHistory");
        
        return DebeziumEngine.create(Connect.class)
                .using(props)
                .notifying(cdcService::handleChangeEvent)
                .build();
    }
    
    @PostConstruct
    public void start() {
        LOGGER.info("Starting Debezium engine...");
        engine = createDebeziumEngine();
        executor.execute(engine);
        LOGGER.info("Debezium engine started successfully");
    }
    
    @PreDestroy
    public void stop() {
        if (engine != null) {
            LOGGER.info("Stopping Debezium engine...");
            try {
                engine.close();
            } catch (IOException e) {
                LOGGER.error("Error stopping Debezium engine", e);
            }
            LOGGER.info("Debezium engine stopped");
        }
    }
}

