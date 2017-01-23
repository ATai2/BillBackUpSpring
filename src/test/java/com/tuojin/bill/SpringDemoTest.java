package com.tuojin.bill;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/1/19.
 */
public class SpringDemoTest {
    @Autowired
    SpringDemo springDemo;

    @Test
    public void testsqlserver() throws Exception {
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
        SpringDemo sd= (SpringDemo) context.getBean("springDemo");
        springDemo.testsqlserver();
    }
}