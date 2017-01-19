package com.tuojin.bill;

/**
 * Created by Administrator on 2017/1/12.
 */
public interface IOperator {
    //tif
    boolean initTable();
    //表结构检查,新建
    boolean checkTable();
//    表结构改变
    boolean checkTableUpdate();
//表数据复制，旧表数据删除
    boolean dataTransfer();
    // 图片移动
    boolean tiffMove();

    void close();
}
