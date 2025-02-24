package com.telegram_bot_interactive.repository;

import com.telegram_bot_interactive.common.wrappers.SerializationWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class StoreProcedureRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper mapper;

    public Optional<String> Execute(String procedureName, HashMap<String, Object> request) {

        MapSqlParameterSource inputParams;
        try {
            inputParams = new MapSqlParameterSource()
                    .addValue("IN_REQUEST", SerializationWrapper.serialize(request));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName(procedureName);

        var result = simpleJdbcCall.execute(inputParams);

        var outResult = result.getOrDefault("OUT_RESULT", null);

        if ( outResult == null || outResult == "{}") {
            return Optional.empty();
        }
        return Optional.of(outResult.toString());
    }
}
