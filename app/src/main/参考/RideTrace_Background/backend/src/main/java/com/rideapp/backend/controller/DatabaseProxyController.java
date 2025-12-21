package com.rideapp.backend.controller;

import com.rideapp.backend.dto.ApiResponse;
import com.rideapp.backend.dto.DbBatchUpdateRequest;
import com.rideapp.backend.dto.DbInsertRequest;
import com.rideapp.backend.dto.DbQueryRequest;
import com.rideapp.backend.dto.DbUpdateRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/db")
public class DatabaseProxyController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseProxyController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/query")
    public ApiResponse<List<Map<String, Object>>> query(@RequestBody DbQueryRequest req) {
        String sql = req.getSql();
        if (sql == null || sql.isBlank()) {
            return ApiResponse.error(2001, "sql is empty");
        }
        List<Object> params = req.getParams();
        Object[] args = params == null ? new Object[0] : params.toArray();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, args);
        return ApiResponse.success(rows);
    }

    @PostMapping("/update")
    public ApiResponse<Integer> update(@RequestBody DbUpdateRequest req) {
        String sql = req.getSql();
        if (sql == null || sql.isBlank()) {
            return ApiResponse.error(2001, "sql is empty");
        }
        List<Object> params = req.getParams();
        Object[] args = params == null ? new Object[0] : params.toArray();
        int affected = jdbcTemplate.update(sql, args);
        return ApiResponse.success(affected);
    }

    @PostMapping("/insert")
    public ApiResponse<Long> insert(@RequestBody DbInsertRequest req) {
        String sql = req.getSql();
        if (sql == null || sql.isBlank()) {
            return ApiResponse.error(2001, "sql is empty");
        }
        List<Object> params = req.getParams();
        Object[] args = params == null ? new Object[0] : params.toArray();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            return ApiResponse.error(2002, "no generated key");
        }
        return ApiResponse.success(key.longValue());
    }

    @PostMapping("/batch-update")
    public ApiResponse<int[]> batchUpdate(@RequestBody DbBatchUpdateRequest req) {
        String sql = req.getSql();
        if (sql == null || sql.isBlank()) {
            return ApiResponse.error(2001, "sql is empty");
        }
        List<List<Object>> paramsList = req.getParamsList();
        if (paramsList == null || paramsList.isEmpty()) {
            return ApiResponse.success(new int[0]);
        }
        int[][] result = jdbcTemplate.batchUpdate(sql, paramsList, paramsList.size(), (ps, args) -> {
            for (int i = 0; i < args.size(); i++) {
                ps.setObject(i + 1, args.get(i));
            }
        });
        int[] flat = new int[result.length];
        for (int i = 0; i < result.length; i++) {
            flat[i] = result[i].length > 0 ? result[i][0] : 0;
        }
        return ApiResponse.success(flat);
    }
}

