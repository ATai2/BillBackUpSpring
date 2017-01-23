package com.tuojin.bill;

import com.tuojin.bill.utils.SDBHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by Administrator on 2017/1/19.
 */
public class SpringMain {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        SDBHelper sd = (SDBHelper) context.getBean("sdbHelper");
//        sd.getMyTableList();
        sd.delMyTableData();
    }
}
