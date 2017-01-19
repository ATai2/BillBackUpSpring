package com.tuojin.bill.utils;

import com.mysql.jdbc.*;
import com.tuojin.bill.IOperator;
import com.tuojin.bill.bean.MySqlStructure;
import com.tuojin.bill.bean.SqlServerStructure;
import com.tuojin.bill.bean.TiffBean;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.*;
import java.util.Date;

/**
 * 表创建不添加外键约束
 * 设计：整个类多为方法叠加，面向对象编程的思想有点少，后期抽象。
 * Created by Administrator on 2017/1/12.
 */
public class DBHelper implements IOperator {

    private String hospitalid;
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
    private String targetDir;
    private Logger logger = Logger.getLogger(DBHelper.class);
    private final Properties prop;
    private ArrayList<String> mTables;

//    任务方法：   表结构的复制  表数据的复制 原表数据的删除  tiff文件的复制删除

    /**
     * 初始化配置数据
     */
    @SuppressWarnings("Since15")
    public DBHelper() {
        logger.info("初始化DBHelper");
        prop = new Properties();
        try {
            logger.info("读取配置文件");
            InputStream in = new BufferedInputStream(new FileInputStream("E:\\project\\IDEA\\BillBackUpSpring\\src\\main\\resources\\init.properties"));
//            InputStream in = new BufferedInputStream(new FileInputStream("init.properties"));
            prop.load(in);
            Iterator<String> it = prop.stringPropertyNames().iterator();
            sqlserver_url = prop.getProperty("sqlserver_url");
            sqlserver_user = prop.getProperty("sqlserver_user");
            sqlserver_password = prop.getProperty("sqlserver_password");
            mysql_url = prop.getProperty("mysql_url");
            mysql_user = prop.getProperty("mysql_user");
            mysql_password = prop.getProperty("mysql_password");
            targetDir = prop.getProperty("server_dir");

            hospitalid = prop.getProperty("hospitalid");
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
    private Connection getConn() {
        String url = sqlserver_url;
        String user = sqlserver_user;
        String password = sqlserver_password;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("weilianjianshang conn");
            logger.error("无法连接sqlserver数据库，请检查配置文件！");
        }
        return conn;
    }

    /**
     * 链接Mysql数据库
     *
     * @return
     */
    private Connection getMysqlConn() {
        String url = mysql_url;
        String user = mysql_user;
        String password = mysql_password;

        Properties prop = new java.util.Properties();
        prop.put(" charSet ", "utf8");
        prop.put(" user ", user);
        prop.put(" password ", password);

        try {
            Class.forName("com.mysql.jdbc.Driver");
//            mysqlConn = DriverManager.getConnection(url, prop);
            mysqlConn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("mysql数据连接错误，请检查配置");
        }
        return mysqlConn;
    }

    /**
     * sqlserver的statement
     *
     * @return
     */
    private Statement getStatement() {
        try {
            conn = this.getConn();
            if (conn != null)
                stat = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("无法获得sqlserver的statement");
        }
        return stat;
    }

    private Statement getmysqlStatement() {
        try {
            mysqlConn = this.getMysqlConn();
            if (mysqlConn != null)
                mysqlStat = mysqlConn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("无法获得mysql的statement");
        }
        return mysqlStat;
    }

    /**
     * 关闭sqlserver的数据库
     */
    @Override
    public void close() {
        try {
            if (stat != null)
                stat.close();
            stat = null;
            if (mysqlStat != null)
                mysqlStat.close();
            mysqlStat = null;
            logger.info("sqlserver连接关闭成功");
        } catch (Exception e) {
            logger.error("sqlserver连接关闭错误");
        }
        try {
            if (conn != null)
                conn.close();
            conn = null;
            if (mysqlStat != null)
                mysqlStat.close();
            mysqlStat = null;
            logger.info("mysql连接关闭成功");
        } catch (Exception e) {
            logger.error("mysql连接关闭错误");
        }

    }

    /**
     * 读取需要复制的表内容
     * 此处应为控制表中罗列所有需要复制的数据，从此表的list进行数据操作。但是，数据不完整，采取全部遍历的方式
     *
     * @param
     * @return
     */
    private List<String> initsqlserverTable() {
        return getListFromControllTable();
    }

    /**
     * 获得控制表中的所有迁移表
     *
     * @param
     * @return
     */
    private List<String> getListFromControllTable() {
        String controlTable = "ty_FillTableList";//放入配置文件中
        String sql = "select sTableName from " + controlTable;
        mTables = new ArrayList<String>();
        try {
            ResultSet resultSet = this.getStatement().executeQuery(sql);
            while (resultSet.next()) {
                String str = resultSet.getString(1);
                mTables.add(str);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("无法获取迁移表");
        }
        return mTables;
    }

    /**
     * 临时方法
     * 获得数据库中的所有数据表
     *
     * @return
     */
    @Deprecated
    private List<String> getListFromAll() {
        String sql = " SELECT Name FROM SysObjects Where XType='U' ORDER BY Name";
        mTables = new ArrayList<String>();
        try {
            ResultSet resultSet = this.getStatement().executeQuery(sql);
            while (resultSet.next()) {
                String str = resultSet.getString(1);
                if (str.equals("sysdiagrams")) continue;
                mTables.add(str);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("无法获得sqlserver中的数据表");
        }
        return mTables;
    }

    /**
     * 读取Mysql数据库中的表
     *
     * @param
     * @return
     */
    private List<String> initMyTable() {
        String sql = "show tables";
        List<String> list = new ArrayList<String>();
        try {
            ResultSet resultSet = this.getmysqlStatement().executeQuery(sql);
            while (resultSet.next()) {
                String str = resultSet.getString(1);
                list.add(str);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("无法获得mysql中的数据表");
        }
        return list;
    }

    /**
     * 验证数据库中不存在的表
     *
     * @param listsqlserver sqlserver中的table列表
     * @param listmysql     mysql中的table列表
     * @return
     */
    private List<String> validTables(List<String> listsqlserver, List<String> listmysql) {
        List<String> list = new ArrayList<String>();
        for (String s :
                listsqlserver) {
            if (!listmysql.contains(s)) {
//            if (!listmysql.contains(s.toLowerCase())) {
                logger.info("目标数据库中不存在数据表：" + s);
                list.add(s);
            }
        }
        return list;
    }

    /**
     * 根据表名复制表结构
     *
     * @param tableName 要复制的表
     * @throws SQLException
     */
    private void validateTableStructure(String tableName) {
        //               获得sqlserver的数据库元数据
        try {
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
                        resultSet.getString(Constant.IS_NULLABLE),
                        resultSet.getString(Constant.DATA_TYPE),
                        resultSet.getString(Constant.CHARACTER_MAXIMUM_LENGTH),
                        resultSet.getString(Constant.CHARACTER_OCTET_LENGTH),
                        resultSet.getString(Constant.NUMERIC_PRECISION),
                        resultSet.getString(Constant.CHARACTER_SET_NAME),
                        resultSet.getString(Constant.COLLATION_NAME)
                );
                sqlCols.add(resultSet.getString(Constant.COLUMN_NAME));
                sqlServerStructures.add(sql);
            }
//        获得mysql的表结构
            try {
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
            } catch (Exception e) {
                logger.error("本地数据库中不存在！");
            }
            boolean isChanged = false;
            for (int i = 0; i < sqlCols.size(); i++) {
                if (!mysqlCols.contains(sqlCols.get(i))) {
                    isChanged = true;
                }
            }
            if (isChanged) {
//            数据表改变，重命名，
                String renamesql = "rename table " + tableName + " to " + tableName + "_" + DateFormatUtils.format(new Date(), "yyyyMMDD");
                try {
                    this.getmysqlStatement().execute(renamesql);
                } catch (SQLException e) {
                    e.printStackTrace();
                    logger.error(tableName + "无法重命名");
                }
                // 新建表
                createTableByName(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据表名新建表
     *
     * @param tableName
     * @throws SQLException
     */
    private void createTableByName(String tableName) {
        //               获得sqlserver的数据库元数据
        try {
            DatabaseMetaData meta = this.getConn().getMetaData();
            List<SqlServerStructure> sqlServerStructures = new ArrayList<SqlServerStructure>();
            List<String> sqlCols = new ArrayList<String>();
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
                        resultSet.getString(Constant.IS_NULLABLE),
                        resultSet.getString(Constant.DATA_TYPE),
                        resultSet.getString(Constant.CHARACTER_MAXIMUM_LENGTH),
                        resultSet.getString(Constant.CHARACTER_OCTET_LENGTH),
                        resultSet.getString(Constant.NUMERIC_PRECISION),
                        resultSet.getString(Constant.CHARACTER_SET_NAME),
                        resultSet.getString(Constant.COLLATION_NAME)
                );
                sqlCols.add(resultSet.getString(Constant.COLUMN_NAME));
                sqlServerStructures.add(sql);
            }
            StringBuilder col = new StringBuilder("CREATE TABLE " + tableName + "(");
//            StringBuilder col = new StringBuilder("DROP TABLE IF EXISTS " + tableName + ";CREATE TABLE " + tableName + "(");
            for (int i = 0; i < sqlServerStructures.size(); i++) {
                SqlServerStructure s = sqlServerStructures.get(i);
                col.append("`").append(s.getCOLUMN_NAME()).append("`");
                if (s.getDATA_TYPE().equals("varchar")) {
                    if (Integer.parseInt(s.getCHARACTER_MAXIMUM_LENGTH()) > 0) {
                        col.append("varchar(" + (int) (Double.parseDouble(s.getCHARACTER_MAXIMUM_LENGTH()) * 1.5 + 1) + ")  ");
                    } else {
//                            varchar(max)   最大长度为-1
                        col.append("text ");
                    }
                } else if (s.getDATA_TYPE().equals("char")) {
                    col.append("char(" + (int) (Double.parseDouble(s.getCHARACTER_MAXIMUM_LENGTH()) * 1.5 + 1) + ")  ");
                } else if (s.getDATA_TYPE().equals("sysname")) {
                    col.append("varchar(32)  ");
                } else if (s.getDATA_TYPE().equals("varbinary")) {
                    col.append("int(11)  ");
                } else if (s.getDATA_TYPE().equals("int")) {
                    col.append("int(11)  ");
                } else if (s.getDATA_TYPE().equals("longblob")) {
                    col.append("longblob(255)  ");
                } else if (s.getDATA_TYPE().equals("bigint")) {
                    col.append("bigint  ");
                }

                if (s.getIS_NULLABLE().equals("YES")) {
                    col.append("NULL  ");
                } else {
                    col.append("NOT NULL");
                }
                col.append(",");

            }
            if (tableName.equals("ty_FillInfo1")) {
                col.append("`" + Constant.TIFFDEST + "` varchar(520) null,");
            }
            col.append("`" + Constant.HOSPITAL_ID + "` varchar(32) null,")
                    .append("`" + Constant.BACK_DATE + "` varchar(32) null,");

            ResultSet primarykeys = this.getStatement().executeQuery("select b.column_name " +
                    "from information_schema.table_constraints  a " +
                    "inner join information_schema.constraint_column_usage  b " +
                    "on a.constraint_name = b.constraint_name " +
                    "where a.constraint_type = 'PRIMARY KEY' and a.table_name ='" + tableName + "'");
//            添加主键
            if (primarykeys != null) {
                col.append(" PRIMARY KEY (");
                while (primarykeys.next()) {
                    col.append("`" + primarykeys.getString("column_name") + "`,");
                }
                if (col.lastIndexOf(",") == col.length() - 1) {
                    col.replace(col.length() - 1, col.length(), "");
                }
                col.append(")");
            }
//            /添加主键
            col.append(")\n" +
                    "ENGINE=InnoDB\n" +
                    "DEFAULT CHARACTER SET=utf8\n" +
                    ";\n");
            String sql = col.toString();
            try {
                this.getmysqlStatement().execute(sql);
            } catch (SQLException e) {
//                e.printStackTrace();
                logger.info("创建新表：" + tableName + "失败！");
            }
            logger.info("创建新表：" + tableName);
//        2表存在看字段在MySQL中是否存在：2.1存在，不操作；2.2不存在，将旧表重命名，创建新表；
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得tiff文件列表，用于文件复制
     *
     * @return
     */
    private List<TiffBean> getTiffFile() {
        String sql = "select sVoucherKey, sVoucherNo, sFilePathName from ty_VoucherFile";
        List<TiffBean> list = new ArrayList<TiffBean>();
        try {
            ResultSet resultSet = this.getStatement().executeQuery(sql);
            while (resultSet.next()) {
                TiffBean bean = new TiffBean();
                bean.setsVoucherKey(resultSet.getString("sVoucherKey"));
                bean.setsUoucherNo(resultSet.getString("sVoucherNo"));
                bean.setsFilePahtName(resultSet.getString("sFilePathName"));
                //目标文件路径+日期+文件名
                String dest = targetDir + "/" + DateFormatUtils.format(new Date(), "yyyy-MM-DD") + resultSet.getString("sFilePathName").substring(2);
                bean.setDestination(dest);
                list.add(bean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将文件复制到目标文件夹
     *
     * @param list
     */
    private void copyTiffFiles(List<TiffBean> list) {
//        复制文件
        for (int i = 0; i < list.size(); i++) {
            TiffBean tiffBean = list.get(i);
            try {
                File destFile = new File(tiffBean.getDestination());
                File srcFile = new File(tiffBean.getsFilePahtName());
                if (!srcFile.exists()) {
                    logger.error("主键为：" + tiffBean.getsVoucherKey() + "、" + tiffBean.getsUoucherNo() + "的tiff文件未能找到！");
                    throw new FileNotFoundException("目标文件未能找到！");
//                    continue;
                }

                if (destFile.exists()) {
                    FileUtils.deleteQuietly(destFile);
                }
                FileUtils.moveFile(srcFile, destFile);
//                if (!destFile.exists()) {
//                    destFile.createNewFile();
//                }
//
//                FileUtils.copyFile(srcFile, destFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                logger.error("主键为：" + tiffBean.getsVoucherKey() + "、" + tiffBean.getsUoucherNo() + "的tiff文件未能找到！");
            } catch (IOException e) {
                logger.error("主键为：" + tiffBean.getsVoucherKey() + "、" + tiffBean.getsUoucherNo() + "的tiff文件迁移出错！");
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("主键为：" + tiffBean.getsVoucherKey() + "、" + tiffBean.getsUoucherNo() + "的tiff文件迁移出错！");
            }
        }
    }

    /**
     * 验证文件是否复制成功，成功：源文件删除，不成功：添加log；
     *
     * @param list
     */
    private void invalidCopyTiffFiles(List<TiffBean> list) {

        for (TiffBean tiffBean :
                list) {
            File source = new File(tiffBean.getsFilePahtName());
            File dest = new File(tiffBean.getDestination());
            if (source.length() == dest.length()) {
                if (FileUtils.deleteQuietly(source)) {
                    logger.info("主键为：" + tiffBean.getsVoucherKey() + "、" + tiffBean.getsUoucherNo() + "的tiff文件删除成功");
                } else {
                    logger.error("主键为：" + tiffBean.getsVoucherKey() + "、" + tiffBean.getsUoucherNo() + "的tiff文件删除失败！");

                }
            }
        }
    }


    // 批量的执行mysql 表插入数据的操作
    private void copyTableData(String tableName) {
//            createTableByName(tableName);
        System.out.println("表名：" + tableName); // 表名
        System.out.println("------------------------------");
//            大数据量的情况下的数据
        String sql = "select * from " + tableName;
        ResultSet rs;
        try {
            copyColumnMy(tableName, sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 批量的执行mysql 表插入数据的操作
    private void getTableData() throws SQLException {
//               获得sqlserver的数据库元数据
        DatabaseMetaData meta = this.getConn().getMetaData();
        //        获得mysql的数据库元数据
        DatabaseMetaData meta_mysql = this.getMysqlConn().getMetaData();

        ResultSet rs2 = meta.getTables(null, null, null, new String[]{"TABLE"}); // 依次取得数据库中的 表名
//        遍历每一张表
        while (rs2.next()) {

            String tableName = rs2.getString(3);
            if (tableName.equals("sysdiagrams")) continue;

            createTableByName(tableName);


            System.out.println("表名：" + rs2.getString(3)); // 表名
            System.out.println("------------------------------");
//            大数据量的情况下的数据
            String sql = "select * from " + tableName;
            ResultSet rs;
            try {
                copyColumnMy(tableName, sql);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        rs2.close();
        this.close();
    }

    private void copyColumnMy(String tableName, String sql) throws SQLException, UnsupportedEncodingException {
        ResultSet rs;
        rs = this.getStatement().executeQuery(sql); // 执行sql server中的语句，得到ResultSet值
        ResultSetMetaData rsmd = rs.getMetaData();
        int coulum = rsmd.getColumnCount(); // 统计在rs中有多少列
        // 在mysql已有的数据库表中插入数据
        int count = 1;
        while (rs.next()) {
            StringBuilder del = new StringBuilder("delete from ").append(tableName).append(" where ");
            String mysql = "insert into " + tableName + "";
//            String mysql = "insert into " + tableName.toLowerCase() + "";
            String mysql1 = "(";// 用以处理字段名
            String mysql2 = "values(";// 用以处理字段的值
            List<String> columList = new ArrayList<String>();
            for (int i = 0; i < coulum; i++) {
                String columName = rsmd.getColumnName(i + 1); // 获取指定列的名称。 Constant.HOSPITAL_ID+","+DateFormatUtils.format(new Date(), "yyyy-MM-DD HH:mm:ss")
                columList.add(columName);
                if (i == (coulum - 1)) {
                    mysql1 = mysql1 + columName + "," + Constant.HOSPITAL_ID + "," + Constant.BACK_DATE + ")";
//                    del.append(columName+"="+rs.getString(i+1)+";");
                } else {
                    mysql1 = mysql1 + columName + ",";
//                    del.append(columName+"="+rs.getString(i+1)+" and ");
                }
                // 分情况Date，int，float，double，varchar的不同类型的字段
                int columType = rsmd.getColumnType(i + 1); // 获取指定列的 SQL 类型。
                mysql2 = typeCheck(rs, coulum, count, mysql2, i, columType);
                // System.out.println();
            }
            mysql = mysql + mysql1 + mysql2;
            mysql = mysql.replace(");", ",'" + hospitalid + "','" + DateFormatUtils.format(new Date(), "yyyy-MM-DD HH:mm:ss") + "');");
            //执行数据复制，根据返回值获得是否插入成功
            int flag = this.getmysqlStatement().executeUpdate(mysql);
//            获得表主键，查询为什么metadata无法获得主键
            ResultSet primarykeys = this.getStatement().executeQuery("select b.column_name " +
                    "from information_schema.table_constraints  a " +
                    "inner join information_schema.constraint_column_usage  b " +
                    "on a.constraint_name = b.constraint_name " +
                    "where a.constraint_type = 'PRIMARY KEY' and a.table_name ='" + tableName + "'");
            List<String> primaries = new ArrayList<String>();
            while (primarykeys.next()) {
                primaries.add(primarykeys.getString(1));
            }
            for (int i = 0; i < primaries.size(); i++) {
                String colomnName = primaries.get(i);
                del.append(colomnName + "=");
                int pos = columList.indexOf(colomnName);
                int columType = rsmd.getColumnType(pos + 1); // 获取指定列的 SQL 类型。
                if (columType == Types.VARCHAR) {
                    del.append("'" + rs.getString(colomnName) + "'");
                } else if (columType == Types.INTEGER) {
                    del.append(rs.getInt(colomnName));
                } else if (columType == Types.BIGINT) {
                    del.append(rs.getInt(colomnName));
                } else if (columType == Types.CHAR) {
                    del.append("'" + rs.getString(colomnName) + "'");
                } else if (columType == Types.DOUBLE) {
                    del.append(rs.getDouble(colomnName));
                } else {
                    del.append("'" + rs.getString(colomnName) + "'");
                }
                del.append(" and ");
            }
            String s = del.toString();
            int and = del.lastIndexOf("and");
            String delstr = s.substring(0, and);

            int insertFlag = -1;
//            ty_FillInfo1表的情况，  移动图片；向数据表中添加数据
            if (tableName.equals("ty_FillInfo1")) {
                String sVoucherNo = rs.getString("sVoucherNo");
//                int lIndex = rs.getInt("lIndex");
                String tiff = "select sFilePathName from ty_VoucherFile where sVoucherNo='" + sVoucherNo + "'";
                ResultSet resultSet = this.getStatement().executeQuery(tiff);
                String src = "";
                String des = "";
                while (resultSet.next()) {
                    src = resultSet.getString(1);
                    des = targetDir + "/" + DateFormatUtils.format(new Date(), "yyyy-MM-DD") + src.substring(2);
                }
                if (copyTiff(src, des)) {
                    logger.info("tiff文件移动成功");
                }
                if (src != null && src.length() != 0) {
                    String insertTiff = "UPDATE ty_FillInfo1 SET " + Constant.TIFFDEST + "='" + des + "' where sVoucherNo='" + rs.getString("sVoucherNo") + "'";
                    insertFlag = this.getmysqlStatement().executeUpdate(insertTiff);
                }
            }

            if (flag > 0) {//受影响行数》0 表明插入成功。则删除旧表数据
                logger.info(mysql + "执行复制成功");
                int i = this.getStatement().executeUpdate(delstr);
                if (i > 0) {
                    logger.info(delstr + "执行删除成功");
                } else {
                    logger.info(delstr + "执行删除成功");
                }
            } else {
                logger.error(mysql + "执行复制失败！");
            }
        }
        rs.close();
    }

    private boolean copyTiff(String src, String des) {
        //        复制文件
        try {
            File srcFile = new File(src);
            File destFile = new File(des);
            if (!srcFile.exists()) {
                logger.error("目标为：" + src + "的文件未能找到！");
                throw new FileNotFoundException("目标文件未能找到！");
            }
            if (destFile.exists()) {
                FileUtils.deleteQuietly(destFile);
            }
            FileUtils.moveFile(srcFile, destFile);
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
            return false;
        } catch (IOException e) {
            logger.error("目标为：" + src + "的文件迁移出错！");
            return false;
        } catch (Exception e) {
            // e.printStackTrace();
            logger.error("目标为：" + src + "的文件迁移出错！");
            return false;
        }
        return true;
    }

    private String typeCheck(ResultSet rs, int coulum, int count, String mysql2, int i, int columType) throws SQLException, UnsupportedEncodingException {
        if (columType == Types.INTEGER) {
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
        } else if (columType == Types.BIGINT) {
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
        } else if (columType == Types.DOUBLE) {
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
        } else if (columType == Types.FLOAT) {
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
        } else if (columType == Types.DATE) {
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
        } else if (columType == Types.BOOLEAN) {
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
        } else if (columType == Types.CHAR) {

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
        return mysql2;
    }

    //    获得表的主键  0
    private String getIdName(Connection conn, String tableName) {
        String idName = "";
        DatabaseMetaData metaData = null;
        try {
            metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(conn.getCatalog(), "%", tableName, "%ID");
            if (rs.next()) {
                idName = rs.getString("COLUMN_NAME");
                System.out.println(idName);
            }
        } catch (Exception e) {
            logger.error(e);
        }
        return idName;
    }

    //0
    private String getPKName(String tableName) {
        try {
            DatabaseMetaData dbMeta = this.getMysqlConn().getMetaData();
            ResultSet rs = dbMeta.getPrimaryKeys(null, null, tableName);
            while (rs.next()) {
                System.out.println(rs.getObject("PK_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
//        String idName = "";
//        ResultSetMetaData metaData = null;
//        try {
//            metaData = rs.getMetaData();
//            idName = metaData.getColumnName(1);
//            System.out.println(idName);
//        } catch (Exception e) {
//            logger.error("查询表的主键名出错！ ",e);
//        }
//        return idName;
    }

    // 单个的执行mysql 表插入数据的操作
    private void getTableData(String tableName) throws SQLException {
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
                columCopy(tableName, rs, rsmd, coulum, count);
            }
            rs.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.close();
    }

    private void columCopy(String tableName, ResultSet rs, ResultSetMetaData rsmd, int coulum, int count) throws SQLException {
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
            if (columType == Types.INTEGER) {
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

            } else if (columType == Types.BIGINT) {
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

            } else if (columType == Types.DOUBLE) {
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

            } else if (columType == Types.FLOAT) {
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

            } else if (columType == Types.DATE) {
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

            } else if (columType == Types.BOOLEAN) {
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

            } else if (columType == Types.LONGVARCHAR) {
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

            } else if (columType == Types.CHAR) {
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

    /**
     * 处理特殊字符：如果多行文本域中有单引号这些特殊符号怎么用代码进行处理
     */
    private void testTableData(String tableName) throws SQLException {
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

    @Override
    public boolean initTable() {
        return false;
    }

    @Override
    public boolean checkTable() {
        logger.info("检查数据表一致性……");
        List<String> listsqlserver = initsqlserverTable();
        List<String> listmysql = initMyTable();
        for (String s :
                validTables(listsqlserver, listmysql) ) {
            logger.info("正在创建差异数据表：" + s);
            createTableByName(s);
            logger.info("差异数据表：" + s + "创建成功");
        }
        return false;
    }

    /**
     * 已保证sqlserver中的数据库在mysql中都有。分析表结构的差异
     * 简版在此只考虑表字段增减，暂不考虑外键更改，字段类型更改的过程。
     * 实现：将旧表重命名+时间戳，重建新表。
     *
     * @return
     */
    @Override
    public boolean checkTableUpdate() {
        logger.info("表结构检测中……");
        List<String> listFromAll = getListFromControllTable();
//        List<String> listFromAll = getListFromAll();
        for (String s :
                listFromAll) {
            validateTableStructure(s);
        }
//        listFromAll.forEach(s -> validateTableStructure(s));
        logger.info("表结构检测结束");
        return false;
    }

    /**
     * 数据复制，旧数据删除
     *
     * @return
     */
    @Override
    public boolean dataTransfer() {
        logger.info("数据迁移中……");
        for (String s :
                mTables) {
            copyTableData(s);
        }
//        mTables.forEach(s -> copyTableData(s));
        logger.info("数据迁移结束");
        return false;
    }

    @Override
    public boolean tiffMove() {
        logger.info("移动tiff文件中……");
        copyTiffFiles(getTiffFile());
        logger.info("tiff文件移动结束");
        return false;
    }
}
