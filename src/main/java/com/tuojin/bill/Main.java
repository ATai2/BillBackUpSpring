package com.tuojin.bill;

import com.tuojin.bill.utils.Context;
import com.tuojin.bill.utils.DBHelper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/12.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("任务开始执行……");
        Long beginTime=new Date().getTime();
        IOperator operator = new DBHelper();
//        检查表，表是否存在
        operator.checkTable();
//        检查表结构是否改变
        operator.checkTableUpdate();
//        图片文件移动：暂时是本机的文件移动，（服务器上传）
//        operator.tiffMove();
//        表数据迁移
        operator.dataTransfer();

        operator.close();
        System.out.println("任务结束。查看日志请移步日志记录……");
        long endTime=new Date().getTime();
        System.out.println("共计用时： "+(endTime-beginTime)/1000+"s.");
    }
}
