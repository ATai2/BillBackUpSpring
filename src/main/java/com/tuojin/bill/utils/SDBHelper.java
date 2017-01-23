package com.tuojin.bill.utils;


import com.mysql.jdbc.*;
import com.tuojin.bill.IOperator;
import com.tuojin.bill.bean.MySqlStructure;
import com.tuojin.bill.bean.SqlServerStructure;
import com.tuojin.bill.bean.TiffBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;
import java.util.Date;


/**
 * 表创建不添加外键约束
 * 设计：整个类多为方法叠加，面向对象编程的思想有点少，后期抽象。
 * Created by Administrator on 2017/1/12.
 */

public class SDBHelper {

    private String hospitalid;
    private String targetDir;
    private Logger logger = Logger.getLogger(DBHelper.class);
    private final Properties prop;
    private ArrayList<String> mTables;
    @Autowired
    @Qualifier("jdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("jdbcTemplateMy")
    JdbcTemplate jdbcTemplateMy;


    @Autowired
    @Qualifier("mysql")
    DataSourceTransactionManager tmMysql;

//    任务方法：   表结构的复制  表数据的复制 原表数据的删除  tiff文件的复制删除

    /**
     * 初始化配置数据
     */
//    @SuppressWarnings("Since15")
    public SDBHelper() {
        logger.info("初始化DBHelper");
        prop = new Properties();
        try {
            logger.info("读取配置文件");
            InputStream in = new BufferedInputStream(new FileInputStream("E:\\project\\IDEA\\BillBackUpSpring\\src\\main\\resources\\init.properties"));
//            InputStream in = new BufferedInputStream(new FileInputStream("init.properties"));
            prop.load(in);
            hospitalid = prop.getProperty("hospitalid");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("无法找到配置文件，请检查配置……");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("配置文件读取出错，请检查配置……");
        }
    }


    /**
     * 获得控制表中的所有迁移表
     *
     * @param
     * @return
     */
    @Transactional("sqlserver")
    private List<String> getListFromControllTable() {
        String controlTable = "ty_FillTableList";//放入配置文件中
        String sql = "select sTableName from " + controlTable;
        mTables = new ArrayList<String>();
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> map :
                maps) {
            mTables.add(map.get("sTableName").toString());
        }
        return mTables;
    }

    @Transactional("mysql")
    public List<String> getMyTableList() {
        List<String> strings = jdbcTemplateMy.queryForList("show tables;", String.class);

        for (int i = 0; i < strings.size(); i++) {
            strings.get(i);
            System.out.println(strings.get(i));
        }

        return strings;
    }

    public void  readFromTables(){
        List<String> myTableList = getMyTableList();
        for (String s :
                myTableList) {
            getDataByTableName(s);
        }
    }

    public void getDataByTableName(String tableName){
        List<Map<String, Object>> maps = jdbcTemplateMy.queryForList("select count(*) from " + tableName);
        for (int i = 0; i < maps.size(); i++) {
            Object o = maps.get(i).get("count(*)");
            System.out.println(o);
        }
    }

    @Transactional("mysql")
    public void delMyTableData() {
        try {
            jdbcTemplateMy.update("DELETE FROM ty_fillinfo1 WHERE  lIndex=?", new Object[]{76});
            jdbcTemplateMy.update("DELETE FROM ty_fillinfo1 WHERE  lIndex=?", new Object[]{77});
            jdbcTemplateMy.update("DELETE FROM ty_fillinfo1 WHERE  lIndex=?", new Object[]{78});
            jdbcTemplateMy.update("DELETE FROM ty_fillinfo1 WHERE  lIndex=?", new Object[]{0});
            throw new Exception("slkdjf");
        } catch (Exception e) {

        }
    }
}
