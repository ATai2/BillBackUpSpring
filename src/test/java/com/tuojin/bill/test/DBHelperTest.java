package com.tuojin.bill.test;

import com.tuojin.bill.utils.DBHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

/**
 * Created by Administrator on 2017/1/12.
 */
public class DBHelperTest {
    DBHelper dbHelper=new DBHelper();



//    @Test
//    public void testRenameTable(){
//        dbHelper.validateTableStructure("ty_sysrun");
//    }
//
//    @Test
//    public void testString(){
//        String s="123456789 and ";
//        int and = s.lastIndexOf("and");
//        System.out.println(s.substring(0,and));
//
//    }
//    @Test
//    public void testGBK2UTF(){
//
////        StringUtils.toEncodedString()
//        File file=new File("E:\\project\\IDEA\\BillBackUpSpring\\src\\main\\java\\test");
//        try {
//            String s = FileUtils.readFileToString(file);
//            System.out.println(s);
//           String strUni= new String(s.getBytes("gbk"),"utf-8");
//            System.out.println(strUni);
////           String str2= new String(strUni.getBytes("utf-8"));
////            System.out.println(str2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    @Test
//    public void testGetTiffFile(){
//        dbHelper.getTiffFile().forEach(tiffBean -> {
//            System.out.println(tiffBean.getsFilePahtName());
//        });
//    }
//    @Test
//    public void testGetPKname(){
//        dbHelper.getPKName("ty_VoucherFile");
//        dbHelper.getPKName("ty_SysRun");
//    }
//    @Test
//    public void testGetIdName(){
//        String tableName="ty_VoucherFile";
////        String tableName="ty_FillInfo1";
////        dbHelper.getIdName(dbHelper.getConn(),);
//        String sql = "select * from " + tableName;
//        ResultSet rs;
//        try {
//            rs = dbHelper.getStatement().executeQuery(sql); // 执行sql server中的语句，得到ResultSet值
//
////            dbHelper.getIdName(rs);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Test
//    public void testCopyTiffFile(){
//        dbHelper.copyTiffFiles(dbHelper.getTiffFile());
//    }
//    @Test
//    public void testStringBuilder(){
//        StringBuilder col=new StringBuilder("jlajkdladlafd,");
//        if (col.lastIndexOf(",") == col.length()-1) {
//            col.replace(col.length() - 1, col.length(), "");
//        }
//        col.append(")");
//        System.out.println(col.toString());
//    }
//    @Test
//    public void testCreatTable(){
////            dbHelper.createTableByName("ty_SysRun");
////            dbHelper.createTableByName("ty_AdvertList");
////            dbHelper.createTableByName("ty_FillInfo1");
//            dbHelper.createTableByName("ty_FillTableList");
////            dbHelper.createTableByName("ty_SysConfig");
////            dbHelper.createTableByName("ty_VoucherFile");
//
//    }
//
//    @Test
//    public void testCopyTableDate(){
//
//            dbHelper.copyTableData("ty_SysConfig");
//            dbHelper.copyTableData("ty_FillTableList");
//            dbHelper.copyTableData("ty_FillInfo1");
//            dbHelper.copyTableData("ty_AdvertList");
//            dbHelper.copyTableData("ty_SysRun");
//            dbHelper.copyTableData("ty_VoucherFile");
//
//    }
//
//    @Test
//    public void testInitTables(){
////        List<String> ty_fillTableList = dbHelper.initTable("ty_FillTableList");
////        ty_fillTableList.forEach(s -> System.out.println(s));
//    }
//
//    @org.junit.Test
//    public void getConn() throws Exception {
//        DBHelper dbHelper=new DBHelper();
//    }
//
//    @org.junit.Test
//    public void getMysqlConn() throws Exception {
//
//    }
//
//    @org.junit.Test
//    public void getStatement() throws Exception {
//
//    }
//
//    @org.junit.Test
//    public void getmysqlStatement() throws Exception {
//
//    }
//
//    @org.junit.Test
//    public void close() throws Exception {
//
//    }
//
//    @org.junit.Test
//    public void getTableData() throws Exception {
//        DBHelper dbHelper=new DBHelper();
////        dbHelper.getTableData();
//        DatabaseMetaData meta = dbHelper.getConn().getMetaData();
//        //        获得mysql的数据库元数据
//        DatabaseMetaData meta_mysql = dbHelper.getMysqlConn().getMetaData();
//
//        ResultSet rs2 = meta.getTables(null, null, null, new String[]{"TABLE"}); // 依次取得数据库中的 表名
////        遍历每一张表
//        while (rs2.next()) {
//            String tableName = rs2.getString(3);
//            if (tableName.equals("sysdiagrams")) continue;
//            if (tableName.equals("ty_AdvertList")) continue;
//            dbHelper.createTableByName(tableName);
//        }
//    }
//
//    @org.junit.Test
//    public void getTableData1() throws Exception {
//
//    }
//
//    @org.junit.Test
//    public void testTableData() throws Exception {
//
//    }
}