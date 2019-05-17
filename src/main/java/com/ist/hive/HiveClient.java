package com.ist.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

//DDL操作测试类
public class HiveClient {

    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static String url = "jdbc:hive2://192.168.1.222:10000";
    private static String user = "root";
    private static String password = "123456";

    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;

    //初始化连接
    static {
       try{
           Class.forName(driverName);
           conn = DriverManager.getConnection(url,user,password);
           stmt = conn.createStatement();
       }
       catch (Exception e){
           e.printStackTrace();
       }
    }

    //查看数据库
    public void showDatabases() throws Exception{
        String sql = "show databases";
        System.out.println("查询语句:" + sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }

    //创建数据库
    public void createDatabase(String dataBaseName) throws Exception{
        String sql = "create database "+dataBaseName+"";
        System.out.println("查询语句:" + sql);
        stmt.execute(sql);
    }


    //删除数据库
    public void deleteDatabase(String dataBaseName) throws Exception{
        String sql = "drop database if exists "+dataBaseName+"";
        System.out.println("查询语句:" + sql);
        stmt.execute(sql);
    }


    //创建表
    public void createTable(String tableName) throws Exception{
        String sql = "create table "+tableName+"(\n" +
                "id int,\n" +
                "name string,\n" +
                "phone string,\n" +
                "age int\n" +
                ")\n" +
                "row format delimited "+
                "fields terminated by '\\t'";//行记录用tab分割属性
        System.out.println("查询语句:");
        System.out.println(sql);
        //stmt.execute(sql);
    }

    //查看表结构
    public void showTable(String tableName) throws Exception{
        String sql = "desc "+tableName+"";
        System.out.println("查询语句:" + sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t" + rs.getString(2));
        }
    }


    //删除表
    public void deleteTable(String tableName) throws Exception{
        String sql = "drop table if exists "+tableName+"";
        System.out.println("查询语句:" + sql);
        stmt.execute(sql);
    }

    //查看表
    public void showTables() throws Exception{
        String sql = "show tables";
        System.out.println("查询语句:" + sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }


    //导入数据(从本地文件导入)
    //文件userData.txt放到/root下,文件内容如下
    //1	tyf	13388236294	5
    //2	zd	12345434565	6
    //3	sd	12345434532	5
    //4	lsm	12232323232	70
    public void loadData(String filePath,String tableName) throws Exception{
        String sql = "load data local inpath '" + filePath + "' overwrite into table "+tableName+"";
        System.out.println("查询语句:" + sql);
        stmt.execute(sql);
    }

    //查询数据
    public void query(String tableName) throws Exception{
        String sql = "select * from "+tableName+"";
        System.out.println("查询语句:" + sql);
        rs = stmt.executeQuery(sql);
        System.out.println("id" + "\t" + "name" + "\t" + "phone"+"\t" + "age");
        while (rs.next()) {
            System.out.println(rs.getInt("id") + "\t\t" + rs.getString("name") + "\t\t" + rs.getString("phone")+"\t\t" + rs.getInt("age"));
        }
    }


    //清空数据表
    public void truncateTable(String tableName)throws Exception{
        String sql = "truncate table "+tableName+"";
        System.out.println("查询语句:" + sql);
        stmt.execute(sql);
    }


    //count(*)
    //count(1)
    //count(column)
    public void countTable(String tableName,String word) throws Exception{
        String sql = "select count('"+word+"') from "+tableName+"";
        System.out.println("查询语句:" + sql);
        rs = stmt.executeQuery(sql);
        while (rs.next()) {
            System.out.println(rs.getInt(1) );
        }

    }

    //多列单次计数
    public void wordCount(String tableName,String column)throws Exception{
        String sql="select word,count(*) as count from (select explode(split("+column+",'\\t')) as word from tb_user) word group by word order by count";
        System.out.println("查询语句:" + sql);
        rs = stmt.executeQuery(sql);
        System.out.println("word" + "\t" + "count" );
        while (rs.next()) {
            System.out.println(rs.getInt("word") + "\t\t" + rs.getString("word") + "\t\t" + rs.getInt("count"));
        }
    }


    public static void main(String [] args) throws Exception{

        HiveClient client = new HiveClient();
        //查看数据库
        //client.showDatabases();
        //创建数据库
        //client.createDatabase("db_test3");
        //删除数据库
        //client.deleteDatabase("db_test3");
        //创建表
        //client.createTable("tb_user");
        //查看所有表
        //client.showTables();
        //查看表结构
        //client.showTable("tb_user");
        //删除表
        //client.deleteTable("tb_user");
        //导入数据
        //client.loadData("/root/userData.txt","tb_user");
        //查询数据
        //client.query("tb_user");
        //清空数据表
        //client.truncateTable("tb_user");
        //记录计数
        //client.countTable("tb_user","*");
        //单词计数
        client.wordCount("tb_user","name");//统计一列

    }

}
