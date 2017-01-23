package com.tuojin.bill.utils;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/1/20.
 */
public class SDBHelperTest {


    private SDBHelper sdbHelper;

    @Before
    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        sdbHelper = (SDBHelper) context.getBean("sdbHelper");
    }

    @Test
    public void getMyTableList() throws Exception {
        sdbHelper.getMyTableList();
    }

    @Test
    public void delMyTableData() throws Exception {
        sdbHelper.delMyTableData();
    }

    @Test
    public void readFromTables() throws Exception {
        sdbHelper.readFromTables();
    }
}