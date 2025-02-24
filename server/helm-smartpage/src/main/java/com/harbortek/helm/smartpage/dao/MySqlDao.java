package com.harbortek.helm.smartpage.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MySqlDao extends BaseSqlDao{
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}
