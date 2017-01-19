package com.tuojin.bill.old;


import com.tuojin.bill.utils.Constant;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 将Sql Server的脚本及其中的内容部分逐行的读入到Mysql中的类
 *
 * @author Administrator
 */
public class Sqlserver_To_Mysql {
    // sqlserver 的链接属性
    private Connection mysqlConn = null; // mysql
    private Connection conn = null; // sql server
    private Statement stat = null; // sql server
    private Statement mysqlStat = null; // mysql
    private Logger log = Logger.getLogger(Sqlserver_To_Mysql.class);

    /**
     * 链接sqlserver数据库
     *
     * @return
     */
    public Connection getConn() {
        // String url="jdbc:jtds:sqlserver://192.168.1.197/Enuo63305330";
        String url = "jdbc:sqlserver://192.168.1.197:1433; DatabaseName=Enuo63305330";
        String user = "sa";
        String password = "63305330";
        // String url="jdbc:jtds:sqlserver://113.105.231.103/innosystem_db";
        // String url="jdbc:sqlserver://113.105.231.103:1433; DatabaseName=innosystem_db";
        // String user="sa";
        // String password="Inno2010";
        try {
            // Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, user, password);
            log.debug("getConn");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("weilianjianshang conn");
        }
        return conn;
    }

    /**
     * 链接Mysql数据库
     *
     * @return
     */
    public Connection getMysqlConn() {
        // String
        // url="jdbc:mysql://192.168.1.110/innosystem_db?useUnicode=true&amp;characterEncoding=utf-8";
//        String url = "jdbc:mysql://localhost:3306/ssm?useUnicode=true&amp;characterEncoding=utf-8";
        String url = "jdbc:mysql://192.168.1.243:3306/Enuo63305330?useUnicode=true&amp;characterEncoding=utf-8";
        String user = "root";
        String password = "root";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            mysqlConn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mysqlConn;
    }

    /**
     * sqlserver的statement
     *
     * @return
     */
    public Statement getStatement() {
        try {
            conn = this.getConn();
            if (conn != null)
                stat = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stat;
    }

    public Statement getmysqlStatement() {
        try {
            mysqlConn = this.getMysqlConn();
            if (mysqlConn != null)
                mysqlStat = mysqlConn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mysqlStat;
    }

    /**
     * 关闭sqlserver的数据库
     */
    public void close() {
        try {
            if (stat != null)
                stat.close();
            stat = null;
            if (mysqlStat != null)
                mysqlStat.close();
            mysqlStat = null;
        } catch (Exception e) {

        }
        try {
            if (conn != null)
                conn.close();
            conn = null;
            if (mysqlStat != null)
                mysqlStat.close();
            mysqlStat = null;
        } catch (Exception e) {

        }

    }

    public static void main(String[] args) {
        Sqlserver_To_Mysql sql = new Sqlserver_To_Mysql();

        try {
            sql.getTableData(); // 批量处理数据表
            // sql.getTableData("announcement"); //单个处理数据表
            // sql.testTableData("guest"); //单个处理数据表的乱码问题
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 批量的执行mysql 表插入数据的操作

    /**
     *
      * @throws SQLException
     */
    public void getTableData() throws SQLException {
//        获得sqlserver的数据库元数据
        DatabaseMetaData meta = this.getConn().getMetaData();
        //        获得mysql的数据库元数据
        DatabaseMetaData meta_mysql = this.getMysqlConn().getMetaData();

        ResultSet rs2 = meta.getTables(null, null, null, new String[]{"TABLE"}); // 依次取得数据库中的 表名
        while (rs2.next()) {
            String tableName = rs2.getString(3);
            System.out.println("表名：" + rs2.getString(3)); // 表名
            System.out.println("------------------------------");
            String sql = "select * from " + tableName;
            // HashMap hashMap=null;
            // ArrayList result=new ArrayList();
            ResultSet rs;
            try {
                rs = this.getStatement().executeQuery(sql); // 执行sql server中的语句，得到ResultSet值
                // ResultSetMetaData可用于获取关于 ResultSet 对象中列的类型和属性信息的对象
                // 使用元数据获取一个表字段的总数
                ResultSetMetaData rsmd = rs.getMetaData();
                int coulum = rsmd.getColumnCount(); // 统计在rs中有多少列
                // 在mysql已有的数据库表中插入数据

                int count = 1;
                while (rs.next()) {
                    String mysql = "insert into " + tableName + "";
                    String mysql1 = "(";// 用以处理字段名
                    String mysql2 = "values(";// 用以处理字段的值
                    // hashMap=new HashMap(); //存取表中字段和其值
                    // String[] array=new String[coulum]; //用以存储rs中的不同类型的字段值
                    for (int i = 0; i < coulum; i++) {
                        String columName = rsmd.getColumnName(i + 1); // 获取指定列的名称。
                        if (i == (coulum - 1)) {
                            mysql1 = mysql1 + columName + ")";
                        } else {
                            mysql1 = mysql1 + columName + ",";
                        }
                        // 分情况Date，int，float，double，varchar的不同类型的字段
                        int columType = rsmd.getColumnType(i + 1); // 获取指定列的 SQL 类型。
                        if (columType == java.sql.Types.INTEGER) {
                            rs.getInt(i + 1);
                            rs.wasNull();
                            // System.out.print("列"+(i+1)+"名:"+columName+" , 字段值为:"+rs.getInt(i+1)+"  ");
                            if (count == 1) {
                                if (i == 0) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                                } else if (i == (coulum - 1)) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ");";
                                    else
                                        mysql2 = mysql2 + "" + rs.getInt(i + 1) + ");";
                                } else {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                                }

                            }
                            // hashMap.put(columName, rs.getInt(i+1)); //获取指定列的数据值
                        } else if (columType == java.sql.Types.BIGINT) {
                            rs.getInt(i + 1);
                            rs.wasNull();
                            if (count == 1) {
                                if (i == 0) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                                } else if (i == (coulum - 1)) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ");";
                                    else
                                        mysql2 = mysql2 + "" + rs.getInt(i + 1) + ");";
                                } else {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                                }
                            }

                            // hashMap.put(columName, rs.getInt(i+1));
                        } else if (columType == java.sql.Types.DOUBLE) {
                            rs.getDouble(i + 1);
                            rs.wasNull();
                            if (count == 1) {
                                if (i == 0) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getDouble(i + 1) + ",";
                                } else if (i == (coulum - 1)) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ");";
                                    else
                                        mysql2 = mysql2 + "" + rs.getDouble(i + 1) + ");";
                                } else {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getDouble(i + 1) + ",";
                                }
                            }

                            // hashMap.put(columName, rs.getDouble(i+1));
                        } else if (columType == java.sql.Types.FLOAT) {
                            rs.getFloat(i + 1);
                            rs.wasNull();
                            if (count == 1) {
                                if (i == 0) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getFloat(i + 1) + ",";
                                } else if (i == (coulum - 1)) {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ");";
                                    else
                                        mysql2 = mysql2 + "" + rs.getFloat(i + 1) + ");";
                                } else {
                                    if (rs.wasNull())
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "" + rs.getFloat(i + 1) + ",";
                                }
                            }

                            // hashMap.put(columName, rs.getFloat(i+1));
                        } else if (columType == java.sql.Types.DATE) {
                            rs.getDate(i + 1);
                            if (count == 1) {
                                if (i == 0) {
                                    if (rs.getDate(i + 1) == null)
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getDate(i + 1) + "',";
                                } else if (i == (coulum - 1)) {
                                    if (rs.getDate(i + 1) == null)
                                        mysql2 = mysql2 + null + ");";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getDate(i + 1) + "');";
                                } else {
                                    if (rs.getDate(i + 1) == null)
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getDate(i + 1) + "',";
                                }
                            }

                            // hashMap.put(columName, rs.getString(i+1));
                        } else if (columType == java.sql.Types.BOOLEAN) {
                            rs.getBoolean(i + 1);
                            if (count == 1) {
                                if (i == 0) {
                                    mysql2 = mysql2 + "'" + rs.getBoolean(i + 1) + "',";
                                } else if (i == (coulum - 1)) {
                                    mysql2 = mysql2 + "'" + rs.getBoolean(i + 1) + "');";
                                } else {
                                    mysql2 = mysql2 + "'" + rs.getBoolean(i + 1) + "',";
                                }
                            }
                        } else if (columType == java.sql.Types.CHAR) {

                            rs.getString(i + 1);
                            if (count == 1) {
                                if (i == 0) {
                                    if (rs.getString(i + 1) == null)
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                                } else if (i == (coulum - 1)) {
                                    if (rs.getString(i + 1) == null)
                                        mysql2 = mysql2 + null + ");";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getString(i + 1) + "');";
                                } else {
                                    if (rs.getString(i + 1) == null)
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                                }
                            }
                            // System.out.print("列"+(i+1)+"名:"+columName+" , 字段值为:"+rs.getBoolean(i+1)+"  ");

                            // hashMap.put(columName, rs.getBoolean(i+1));
                        } else {
                            rs.getString(i + 1);
                            if (count == 1) {
                                if (i == 0) {
                                    if (rs.getString(i + 1) == null)
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                                } else if (i == (coulum - 1)) {
                                    if (rs.getString(i + 1) == null)
                                        mysql2 = mysql2 + null + ");";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getString(i + 1) + "');";
                                } else {
                                    if (rs.getString(i + 1) == null)
                                        mysql2 = mysql2 + null + ",";
                                    else
                                        mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                                }
                            }
                            // System.out.print("列"+(i+1)+"名:"+columName+" , 字段值为:"+rs.getString(i+1)+"  ");

                            // hashMap.put(columName, rs.getString(i+1));
                        }
                        // System.out.println();

                    }
                    mysql = mysql + mysql1 + mysql2;
                    // System.out.println("建立表的语句:"+mysql);
                    this.getmysqlStatement().executeUpdate(mysql);

                }
                rs.close();

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        rs2.close();
        this.close();
    }

    // 单个的执行mysql 表插入数据的操作
    public void getTableData(String tableName) throws SQLException {
        String sql = "select * from " + tableName;
        ResultSet rs;
        try {
            System.out.println("表名1:" + tableName);
            rs = this.getStatement().executeQuery(sql); // 执行sql server中的语句，得到ResultSet值
            // ResultSetMetaData可用于获取关于 ResultSet 对象中列的类型和属性信息的对象
            // 使用元数据获取一个表字段的总数
            System.out.println("表名2:" + tableName);
            ResultSetMetaData rsmd = rs.getMetaData();
            int coulum = rsmd.getColumnCount(); // 统计在rs中有多少列
            // 在mysql已有的数据库表中插入数据
            int count = 1;
            while (rs.next()) {
                String mysql = "insert into " + tableName + "";
                String mysql1 = "(";// 用以处理字段名
                String mysql2 = "values(";// 用以处理字段的值

                // String[] array=new String[coulum]; //用以存储rs中的不同类型的字段值
                for (int i = 0; i < coulum; i++) {
                    String columName = rsmd.getColumnName(i + 1); // 获取指定列的名称。sql中从1开始
                    if (i == (coulum - 1)) {

                        mysql1 = mysql1 + columName + ")";

                    } else {
                        mysql1 = mysql1 + columName + ",";
                    }
                    // 分情况Date，int，float，double，varchar的不同类型的字段
                    int columType = rsmd.getColumnType(i + 1); // 获取指定列的 SQL 类型。
                    if (columType == java.sql.Types.INTEGER) {
                        // 一下三行都是检验其是否为null的
                        int v = rs.getInt(i + 1);
                        rs.wasNull();
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + (rs.wasNull() ? null : v) + "  ");
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "" + rs.getInt(i + 1) + ");";
                            } else {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                            }

                        }

                    } else if (columType == java.sql.Types.BIGINT) {
                        rs.getInt(i + 1);
                        rs.wasNull();
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "" + rs.getInt(i + 1) + ");";
                            } else {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getInt(i + 1) + ",";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getInt(i + 1) + "  ");

                    } else if (columType == java.sql.Types.DOUBLE) {
                        rs.getDouble(i + 1);
                        rs.wasNull();
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getDouble(i + 1) + ",";
                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "" + rs.getDouble(i + 1) + ");";
                            } else {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getDouble(i + 1) + ",";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getDouble(i + 1) + "  ");

                    } else if (columType == java.sql.Types.FLOAT) {
                        rs.getFloat(i + 1);
                        rs.wasNull();
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getFloat(i + 1) + ",";
                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "" + rs.getFloat(i + 1) + ");";
                            } else {
                                if (rs.wasNull())
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "" + rs.getFloat(i + 1) + ",";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getFloat(i + 1) + "  ");

                    } else if (columType == java.sql.Types.DATE) {
                        rs.getDate(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getDate(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getDate(i + 1) + "',";
                            } else if (i == (coulum - 1)) {
                                if (rs.getDate(i + 1) == null)
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "'" + rs.getDate(i + 1) + "');";
                            } else {
                                if (rs.getDate(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getDate(i + 1) + "',";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getString(i + 1) + "  ");

                    } else if (columType == java.sql.Types.BOOLEAN) {
                        rs.getBoolean(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                mysql2 = mysql2 + "'" + rs.getBoolean(i + 1) + "',";
                            } else if (i == (coulum - 1)) {
                                mysql2 = mysql2 + "'" + rs.getBoolean(i + 1) + "');";
                            } else {
                                mysql2 = mysql2 + "'" + rs.getBoolean(i + 1) + "',";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getBoolean(i + 1) + "  ");

                    } else if (columType == java.sql.Types.LONGVARCHAR) {
                        rs.getString(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                            } else if (i == (coulum - 1)) {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "');";
                            } else {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getBoolean(i + 1) + "  ");

                    } else if (columType == java.sql.Types.CHAR) {
                        rs.getString(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                            } else if (i == (coulum - 1)) {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "');";
                            } else {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getBoolean(i + 1) + "  ");
                    } else {
                        rs.getString(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                            } else if (i == (coulum - 1)) {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ");";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "');";
                            } else {
                                if (rs.getString(i + 1) == null)
                                    mysql2 = mysql2 + null + ",";
                                else
                                    mysql2 = mysql2 + "'" + rs.getString(i + 1) + "',";
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getString(i + 1) + "  ");

                    }
                    System.out.println();

                }
                mysql = mysql + mysql1 + mysql2;
                System.out.println("建立表的语句:" + mysql);
                this.getmysqlStatement().executeUpdate(mysql);

            }
            rs.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.close();
    }

    /**
     * 处理特殊字符：如果多行文本域中有单引号这些特殊符号怎么用代码进行处理
     */
    public void testTableData(String tableName) throws SQLException {
        String sql = "select * from " + tableName;

        ResultSet rs;
        try {
            rs = this.getStatement().executeQuery(sql); // 执行sql server中的语句，得到ResultSet值
            ResultSet rss = this.getStatement().executeQuery(sql);

            // ResultSetMetaData可用于获取关于 ResultSet 对象中列的类型和属性信息的对象
            // 使用元数据获取一个表字段的总数
            ResultSetMetaData rsmd = rs.getMetaData();
            int coulum = rsmd.getColumnCount(); // 统计在rs中有多少列

            String valuesquest = "insert into " + tableName + "(";
            while (rss.next()) {
                for (int j = 0; j < coulum; j++) {
                    String columName = rsmd.getColumnName(j + 1); // 获取指定列的名称。
                    if (j == (coulum - 1))
                        valuesquest = valuesquest + columName + ")";
                    else
                        valuesquest = valuesquest + columName + ",";
                }
                break;
            }
            valuesquest = valuesquest + "values(";
            System.out.println("列的数目是:" + coulum);
            // 在mysql已有的数据库表中插入数据

            for (int k = 0; k < coulum; k++) {
                if (k == coulum - 1)
                    valuesquest = valuesquest + "?);";
                else
                    valuesquest = valuesquest + "?,";
            }
            System.out.println("values(是:" + valuesquest);
            int count = 1;
            while (rs.next()) {
                PreparedStatement prep;

                prep = this.getMysqlConn().prepareStatement(valuesquest);
                // //这样无论有什么特殊字符都自动搞掂，无需转换
                // prep.setString(1, request.getParameter( "text "));
                // prep.executeUpdate();
                // String mysql="insert into "+tableName+"";
                // String mysql1 = "(";//用以处理字段名
                // String mysql2 = "values(";//用以处理字段的值
                // [] array=new String[coulum]; //用以存储rs中的不同类型的字段值
                for (int i = 0; i < coulum; i++) {
                    String columName = rsmd.getColumnName(i + 1); // 获取指定列的名称。

                    // 分情况Date，int，float，double，varchar的不同类型的字段
                    int columType = rsmd.getColumnType(i + 1); // 获取指定列的 SQL 类型。
                    if (columType == java.sql.Types.INTEGER) {
                        // 一下三行都是检验其是否为null的
                        int v = rs.getInt(i + 1);
                        rs.wasNull();
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + (rs.wasNull() ? null : v) + "  ");
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    prep.setInt((i + 1), 0);
                                else
                                    prep.setInt((i + 1), rs.getInt(i + 1));

                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    prep.setInt((i + 1), 0);
                                else
                                    prep.setInt((i + 1), rs.getInt(i + 1));
                            } else {
                                if (rs.wasNull())
                                    prep.setInt((i + 1), 0);
                                else
                                    prep.setInt((i + 1), rs.getInt(i + 1));
                            }

                        }
                        // hashMap.put(columName, rs.getInt(i+1)); //获取指定列的数据值
                    } else if (columType == java.sql.Types.BIGINT) {
                        rs.getInt(i + 1);
                        rs.wasNull();
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    prep.setInt((i + 1), (Integer) null);
                                else
                                    prep.setInt((i + 1), rs.getInt(i + 1));
                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    prep.setInt((i + 1), (Integer) null);
                                else
                                    prep.setInt((i + 1), rs.getInt(i + 1));
                            } else {
                                if (rs.wasNull())
                                    prep.setInt((i + 1), (Integer) null);
                                else
                                    prep.setInt((i + 1), rs.getInt(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getInt(i + 1) + "  ");

                        // hashMap.put(columName, rs.getInt(i+1));
                    } else if (columType == java.sql.Types.DOUBLE) {
                        rs.getDouble(i + 1);
                        rs.wasNull();
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    prep.setDouble((i + 1), (Double) null);
                                else
                                    prep.setDouble((i + 1), rs.getDouble(i + 1));
                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    prep.setDouble((i + 1), (Double) null);
                                else
                                    prep.setDouble((i + 1), rs.getDouble(i + 1));
                            } else {
                                if (rs.wasNull())
                                    prep.setDouble((i + 1), (Double) null);
                                else
                                    prep.setDouble((i + 1), rs.getDouble(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getDouble(i + 1) + "  ");

                        // hashMap.put(columName, rs.getDouble(i+1));
                    } else if (columType == java.sql.Types.FLOAT) {
                        rs.getFloat(i + 1);
                        rs.wasNull();
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.wasNull())
                                    prep.setFloat((i + 1), (Float) null);
                                else
                                    prep.setFloat((i + 1), rs.getFloat(i + 1));
                            } else if (i == (coulum - 1)) {
                                if (rs.wasNull())
                                    prep.setFloat((i + 1), (Float) null);
                                else
                                    prep.setFloat((i + 1), rs.getFloat(i + 1));
                            } else {
                                if (rs.wasNull())
                                    prep.setFloat((i + 1), (Float) null);
                                else
                                    prep.setFloat((i + 1), rs.getFloat(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getFloat(i + 1) + "  ");

                        // hashMap.put(columName, rs.getFloat(i+1));
                    } else if (columType == java.sql.Types.DATE) {
                        rs.getDate(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getDate(i + 1) == null)
                                    prep.setDate((i + 1), null);
                                else
                                    prep.setDate((i + 1), rs.getDate(i + 1));
                            } else if (i == (coulum - 1)) {
                                if (rs.getDate(i + 1) == null)
                                    prep.setDate((i + 1), null);
                                else
                                    prep.setDate((i + 1), rs.getDate(i + 1));
                            } else {
                                if (rs.getDate(i + 1) == null)
                                    prep.setDate((i + 1), null);
                                else
                                    prep.setDate((i + 1), rs.getDate(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getString(i + 1) + "  ");

                        // hashMap.put(columName, rs.getString(i+1));
                    } else if (columType == java.sql.Types.BOOLEAN) {
                        rs.getBoolean(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                prep.setBoolean((i + 1), rs.getBoolean(i + 1));
                                // mysql2 = mysql2 +"'"+ rs.getBoolean(i+1) +"',";
                            } else if (i == (coulum - 1)) {
                                prep.setBoolean((i + 1), rs.getBoolean(i + 1));
                            } else {
                                prep.setBoolean((i + 1), rs.getBoolean(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getBoolean(i + 1) + "  ");

                        // hashMap.put(columName, rs.getBoolean(i+1));
                    } else if (columType == java.sql.Types.LONGVARCHAR) {
                        rs.getString(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            } else if (i == (coulum - 1)) {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            } else {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getBoolean(i + 1) + "  ");

                        // hashMap.put(columName, rs.getBoolean(i+1));
                    } else if (columType == java.sql.Types.CHAR) {
                        rs.getString(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            } else if (i == (coulum - 1)) {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            } else {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getBoolean(i + 1) + "  ");
                    } else {
                        rs.getString(i + 1);
                        if (count == 1) {
                            if (i == 0) {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            } else if (i == (coulum - 1)) {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            } else {
                                if (rs.getString(i + 1) == null)
                                    prep.setString((i + 1), null);
                                else
                                    prep.setString((i + 1), rs.getString(i + 1));
                            }
                        }
                        System.out.print("列" + (i + 1) + "名:" + columName + " , 字段值为:" + rs.getString(i + 1) + "  ");

                        // hashMap.put(columName, rs.getString(i+1));
                    }
                    System.out.println();

                }
                prep.executeUpdate();
                prep.close();
                // mysql = mysql + mysql1 + mysql2;
                // System.out.println("建立表的语句:"+mysql);
                // this.getmysqlStatement().executeUpdate(mysql);

            }
            rs.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.close();
    }
}
