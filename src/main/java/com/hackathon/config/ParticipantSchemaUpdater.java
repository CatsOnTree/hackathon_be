package com.hackathon.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ParticipantSchemaUpdater {

    private static final Logger log = LoggerFactory.getLogger(ParticipantSchemaUpdater.class);

    private final JdbcTemplate jdbcTemplate;

    public ParticipantSchemaUpdater(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void widenGeneratedResumeColumns() {
        log.info("SchemaUpdate phase=start table=participants columns=skills,resume_analysis_json");
        jdbcTemplate.execute("alter table if exists participants alter column skills type TEXT");
        jdbcTemplate.execute("alter table if exists participants alter column resume_analysis_json type TEXT");
        log.info("SchemaUpdate phase=done table=participants columns=skills,resume_analysis_json");
    }
}
