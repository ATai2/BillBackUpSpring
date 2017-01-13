package com.tuojin.bill.old;


import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class DataBaseUtil {
    private Logger log= Logger.getLogger(Sqlserver_To_Mysql.class);

    public static void main(String[] srg) {
        new DataBaseUtil().getConn();
//        getConn();
    }

    private void getConn() {
        String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";   //����JDBC����
        String dbURL = "jdbc:sqlserver://192.168.1.197:1433; DatabaseName=Enuo63305330";   //���ӷ����������ݿ�sample
        String userName = "sa";   //Ĭ���û���
        String userPwd = "63305330";   //����
        Connection dbConn;
        try {
            Class.forName(driverName);
            dbConn = DriverManager.getConnection(dbURL, userName, userPwd);
            System.out.println("Connection Successful!");   //������ӳɹ� ����̨���Connection Successful!

            log.debug("getConn");
            log.debug("getConn");
            log.debug("getConn");
            log.debug("getConn");
            log.debug("getConn");
            log.debug("getConn");
            log.debug("getConn");
            log.debug("getConn");
            log.debug("getConn");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
