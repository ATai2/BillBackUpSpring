package com.tuojin.bill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/1/19.
 */
public class SpringDemo {
//    @Qualifier("jdbcTemplate")

    @Autowired
    @Qualifier("jdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcTemplateMy")
    JdbcTemplate jdbcTemplateMy;

    public void testsqlserver() {
//        SpringDemo springDemo = new SpringDemo();
        JdbcTemplate jdbcTemplate = this.jdbcTemplate;
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT * FROM ty_MakeStat");
        System.out.println("");
    }

    public void testMysql() {
//        SpringDemo springDemo = new SpringDemo();
        JdbcTemplate jdbcTemplate = this.jdbcTemplate;
        jdbcTemplate.execute("show tables;");
        System.out.println("123456798");
    }
}
