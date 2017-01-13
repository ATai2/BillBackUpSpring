package com.tuojin.bill.utils;

import com.tuojin.bill.bean.MySqlStructure;
import com.tuojin.bill.bean.SqlServerStructure;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.omg.CORBA.Environment;


import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * 表创建不添加外键约束=====
 * Created by Administrator on 2017/1/12.
 */
public class DBHelper {

    private String sqlserver_url;
    private String sqlserver_user;
    private String sqlserver_password;
    private String mysql_url;
    private String mysql_user;
    private String mysql_password;
    // sqlserver 的链接属性
    private Connection mysqlConn = null; // mysql
    private Connection conn = null; // sql server
    private Statement stat = null; // sql server
    private Statement mysqlStat = null; // mysql

    private String hospitalID = "xinhua";


    private Logger logger = Logger.getLogger(DBHelper.class);
    private final Properties prop;


    public static void main(String[] args) {
        DBHelper dbHelper = new DBHelper();
//        Connection conn = dbHelper.getConn();
        Connection conn = dbHelper.getMysqlConn();
        String tableName = "user";
        String tablestructure = "select * from information_schema.columns where table_name='ty_SysConfig'";
        String renamesql = "rename table " + tableName + " to " + tableName + "_" + DateFormatUtils.format(new Date(), "yyyyMMDD");
        try {
            Statement statement = new DBHelper().getmysqlStatement();
            System.out.println(statement.execute(renamesql));
//            ResultSet resultSet = statement.executeQuery(renamesql);
//            if (resultSet != null) {
//                while (resultSet.next()) {
//                    System.out.println(resultSet.getString(1));
//                }
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void createTabeFromSqlServer(String tableName) throws SQLException {
        //               获得sqlserver的数据库元数据
        DatabaseMetaData meta = this.getConn().getMetaData();
        //        获得mysql的数据库元数据
        DatabaseMetaData meta_mysql = this.getMysqlConn().getMetaData();

        List<SqlServerStructure> sqlServerStructures = new ArrayList<SqlServerStructure>();
        List<MySqlStructure> mySqlStructures = new ArrayList<MySqlStructure>();
        List<String> sqlCols = new ArrayList<String>();
        List<String> mysqlCols = new ArrayList<String>();


        String mysql = "desc " + tableName;
        String sqlserver = "select * from information_schema.columns where table_name='" + tableName + "'";
//        1获得sqlserver的表结构
        ResultSet resultSet = this.getStatement().executeQuery(sqlserver);
        while (resultSet.next()) {
            SqlServerStructure sql = new SqlServerStructure(
                    resultSet.getString(Constant.TABLE_CATALOG),
                    resultSet.getString(Constant.TABLE_SCHEMA),
                    resultSet.getString(Constant.TABLE_NAME),
                    resultSet.getString(Constant.COLUMN_NAME),
                    resultSet.getInt(Constant.ORDINAL_POSITION),
                    resultSet.getString(Constant.COLUMN_DEFAULT),
                    resultSet.getString(Constant.IS_NULLABLE),
                    resultSet.getString(Constant.DATA_TYPE),
                    resultSet.getString(Constant.CHARACTER_MAXIMUM_LENGTH),
                    resultSet.getString(Constant.CHARACTER_OCTET_LENGTH),
                    resultSet.getString(Constant.NUMERIC_PRECISION),
                    resultSet.getString(Constant.NUMERIC_SCALE),
                    resultSet.getString(Constant.CHARACTER_SET_NAME),
                    resultSet.getString(Constant.COLLATION_NAME),
                    resultSet.getString(Constant.COLUMN_TYPE),
                    resultSet.getString(Constant.COLUMN_KEY),
                    resultSet.getString(Constant.EXTRA),
                    resultSet.getString(Constant.PRIVILEGES),
                    resultSet.getString(Constant.COLUMN_COMMENT)
            );
            sqlCols.add(resultSet.getString(Constant.COLUMN_NAME));
            sqlServerStructures.add(sql);
        }
//        获得mysql的表结构
        ResultSet resmy = this.getmysqlStatement().executeQuery(mysql);
        while (resmy.next()) {
            MySqlStructure sql = new MySqlStructure(
                    resmy.getString(Constant.Field),
                    resmy.getString(Constant.Type),
                    resmy.getString(Constant.Null),
                    resmy.getString(Constant.Key),
                    resmy.getString(Constant.Default),
                    resmy.getString(Constant.Extra)
            );
            mySqlStructures.add(sql);
            mysqlCols.add(resmy.getString(Constant.Field));
        }

        boolean isChanged=false;
        for (int i = 0; i < sqlCols.size(); i++) {
            if (!mysqlCols.contains(sqlCols.get(i))) {
                isChanged = true;
            }
        }

        if (isChanged) {
            //        旧表重命名，创建新表
            String renamesql = "rename table " + tableName + " to " + tableName + "_" + DateFormatUtils.format(new Date(), "yyyyMMDD");
//        更新表结构，创建新表
            this.getStatement().execute(renamesql);
//        对比MySQL数据库的表  1.表不存在创建，
            String primaryKey="";

            StringBuilder col=new StringBuilder("CREATE TABLE `");
            for (int i = 0; i < sqlServerStructures.size(); i++) {
                SqlServerStructure s=sqlServerStructures.get(i);
               col.append(s.getCOLUMN_NAME()).append("'  ");
                switch (s.getDATA_TYPE()) {
                    case "varchar":
                        col.append("varchar("+(int)(Double.parseDouble(s.getCHARACTER_MAXIMUM_LENGTH())*1.5+1)+")  ");
                        break;
                    case "char":
                        col.append("char("+(int)(Double.parseDouble(s.getCHARACTER_MAXIMUM_LENGTH())*1.5+1)+")  ");
                        break;
                    case "int":
                        col.append("int(11)  ");
                        break;
                    default:
                        break;
                }
                if (s.getIS_NULLABLE().equals("YES")){
                    col.append("NULL  ");
                }else{
                    col.append("NOT NULL");
                }
//                if (i != sqlServerStructures.size()-1) {
//                    col.append(", ");
//                }
//                if (s.getP)  未添加主键
            }

            ResultSet primarykeys = this.getStatement().executeQuery("select b.column_name\n" +
                    "from information_schema.table_constraints a\n" +
                    "inner join information_schema.constraint_column_usage b\n" +
                    "on a.constraint_name = b.constraint_name\n" +
                    "where a.constraint_type = 'PRIMARY KEY' and a.table_name = '" + tableName + "'");
            if (primarykeys != null) {
                while (primarykeys.next()) {
                    col.append("")
                }
            }


            col.append(")\n" +
                    "ENGINE=InnoDB\n" +
                    "DEFAULT CHARACTER SET=utf8\n" +
                    ";\n");
//        2表存在看字段在MySQL中是否存在：2.1存在，不操作；2.2不存在，将旧表重命名，创建新表；
        }


    }

    /**
     * 初始化配置数据
     */
    @SuppressWarnings("Since15")
    public DBHelper() {
        logger.info("初始化DBHelper");
        prop = new Properties();

        try {
//            InputStream in=new BufferedInputStream(new FileInputStream("init.properties"));
            InputStream in = new BufferedInputStream(new FileInputStream("E:\\project\\IDEA\\BillBackUpSpring\\src\\main\\resources\\init.properties"));
            prop.load(in);
            Iterator<String> it = prop.stringPropertyNames().iterator();
            sqlserver_url = prop.getProperty("sqlserver_url");
            sqlserver_user = prop.getProperty("sqlserver_user");
            sqlserver_password = prop.getProperty("sqlserver_password");

            mysql_url = prop.getProperty("mysql_url");
            mysql_user = prop.getProperty("mysql_user");
            mysql_password = prop.getProperty("mysql_password");

//            while (it.hasNext()) {
//                String key=it.next();
//                System.out.println(key);
//                System.out.println(prop.getProperty(key));
//
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("无法找到配置文件，请检查配置……");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("配置文件读取出错，请检查配置……");
        }
    }

    /**
     * 链接sqlserver数据库
     *
     * @return
     */
    public Connection getConn() {
        // String url="jdbc:jtds:sqlserver://192.168.1.197/Enuo63305330";
        String url = sqlserver_url;
        String user = sqlserver_user;
        String password = sqlserver_password;
        // String url="jdbc:jtds:sqlserver://113.105.231.103/innosystem_db";
        // String url="jdbc:sqlserver://113.105.231.103:1433; DatabaseName=innosystem_db";
        // String user="sa";
        // String password="Inno2010";
        try {
            // Class.forName("net.sourceforge.jtds.jdbc.Driver");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, user, password);
            logger.debug("getConn");
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
        String url = mysql_url;
        String user = mysql_user;
        String password = mysql_password;
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
//    public static void main(String[] args) {
//        Sqlserver_To_Mysql sql = new Sqlserver_To_Mysql();
//
//        try {
//            sql.getTableData(); // 批量处理数据表
//            // sql.getTableData("announcement"); //单个处理数据表
//            // sql.testTableData("guest"); //单个处理数据表的乱码问题
//        } catch (SQLException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    public boolean initTable() {

        return false;
    }

    // 批量的执行mysql 表插入数据的操作
    public void getTableData() throws SQLException {
//               获得sqlserver的数据库元数据
        DatabaseMetaData meta = this.getConn().getMetaData();
        //        获得mysql的数据库元数据
        DatabaseMetaData meta_mysql = this.getMysqlConn().getMetaData();

        ResultSet rs2 = meta.getTables(null, null, null, new String[]{"TABLE"}); // 依次取得数据库中的 表名


//        遍历每一张表
        while (rs2.next()) {


            String tableName = rs2.getString(3);
            System.out.println("表名：" + rs2.getString(3)); // 表名
            System.out.println("------------------------------");
//            大数据量的情况下的数据
            String sql = "select * from " + tableName;
            // HashMap hashMap=null;
            // ArrayList result=new ArrayList();
//            搜索所有数据=====该游标，同时
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
                    String columName = rsmd.getColumnName(i + 1); // 获取指定列的名称。
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
