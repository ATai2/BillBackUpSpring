package com.tuojin.bill;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2017/1/20.
 */
public class BillJobLauncher  {
    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("batch.xml");





    }
}
