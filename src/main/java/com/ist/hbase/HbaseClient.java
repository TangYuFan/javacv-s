package com.ist.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.util.Arrays;

public class HbaseClient {

    //配置类
    public static Configuration configuration = null;
    public static Connection connection = null;
    public static Admin admin =null;

    static {
        try{
            configuration = HBaseConfiguration.create();
            //这里写法和hbase配置文件一致就行不需要单独配置zk端口
            configuration.set("hbase.zookeeper.quorum", "192.168.1.200:2181,192.168.1.200:2180,192.168.1.200:2179");
            configuration.set("hbase.master", "192.168.1.218:16000");
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
            System.out.println("conf:"+configuration);
            System.out.println("conn:"+connection);
            System.out.println("admin:"+admin);
        }
        catch (Exception e){
        e.printStackTrace();
        }

    }

    //关闭连接
    public static void close(){
        try{
            if(admin!=null){
                admin.close();
            }
            if(connection!=null){
                connection.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //列出所有表
    public void showTable() throws Exception{
        HTableDescriptor hTableDescriptors[] = admin.listTables();
        for (HTableDescriptor hTableDescriptor : hTableDescriptors) {
            System.out.println("表:"+hTableDescriptor.getNameAsString());
        }
    }

    //建表
    public void createTable(String tableName,String [] columns) throws  Exception{

        TableName nameObj = TableName.valueOf(tableName);

        if (admin.tableExists(nameObj)) {
            System.out.println("表存在!");
        }
        else {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(nameObj);
            for (String col : columns) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(col);//每一列都是一个列族
                hTableDescriptor.addFamily(hColumnDescriptor);
            }
            admin.createTable(hTableDescriptor);
            System.out.println("创建成功!");
        }
    }

    //删除表
    public void deleteTable(String tableName) throws Exception{

        TableName nameObj = TableName.valueOf(tableName);
        if (!admin.tableExists(nameObj)) {
            System.out.println("表不存在!");
        }
        else{
            admin.disableTable(nameObj);//先禁用
            admin.deleteTable(nameObj);//再删除
            System.out.println("删除成功!");
        }
    }

    //插入数据
    public void insertData(String tableName,String rowKey,String columnFamily,String column,String value) throws Exception{

        TableName nameObj = TableName.valueOf(tableName);
        Table table = connection.getTable(nameObj);//表
        byte[] rowkey = Bytes.toBytes(rowKey);
        byte[] columnfamily = Bytes.toBytes(columnFamily);
        byte[] col = Bytes.toBytes(column);
        byte[] val = Bytes.toBytes(value);
        Put put = new Put(rowkey);
        put.addColumn(columnfamily,col,val);
        table.put(put);
        System.out.println("插入成功!");
    }

    //根据rowKey查找数据
    public void getData(String tableName, String rowKey, String columnFamily, String column) throws Exception{

        TableName nameObj = TableName.valueOf(tableName);
        Table table = connection.getTable(nameObj);//表
        byte[] rowkey = Bytes.toBytes(rowKey);
        byte[] columnfamily,col = null;
        Get get = new Get(rowkey);//根据行键查找

        if(columnFamily!=null && column==null){
            columnfamily = Bytes.toBytes(columnFamily);
            get.addFamily(columnfamily);//根据列族查找
        }
        if(columnFamily!=null && column!=null){
            columnfamily = Bytes.toBytes(columnFamily);
            col = Bytes.toBytes(column);
            get.addColumn(columnfamily,col);//根据列族和子列查找
        }

        Result result = table.get(get);
        //输出
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            System.out.print("rowKey:" + new String(CellUtil.cloneRow(cell)) + "|");
            System.out.print("timeTamp:" + cell.getTimestamp() + "|");
            System.out.print("columnFamily:" + new String(CellUtil.cloneFamily(cell)) + "|");
            System.out.print("column:" + new String(CellUtil.cloneQualifier(cell)) + "|");
            System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + "|");
        }
        System.out.println("查找成功!");

    }


    //删除数据
    public void deleteData(String tableName, String rowKey, String columnFamily, String column) throws Exception{
        TableName nameObj = TableName.valueOf(tableName);
        Table table = connection.getTable(nameObj);//表
        byte[] rowkey = Bytes.toBytes(rowKey);
        byte[] columnfamily,col = null;
        Delete delete = new Delete(rowkey);//根据行键删除

        if(columnFamily!=null && column==null){
            columnfamily = Bytes.toBytes(columnFamily);
            delete.addFamily(columnfamily);//根据列族删除
        }
        if(columnFamily!=null && column!=null){
            columnfamily = Bytes.toBytes(columnFamily);
            col = Bytes.toBytes(column);
            delete.addColumn(columnfamily,col);//根据列族和子列删除
        }
        table.delete(delete);
        System.out.println("删除成功!");
    }


    //范围查找
    public void scanData(String tableName, String startValue, String endValue) throws Exception{
        TableName nameObj = TableName.valueOf(tableName);
        Table table = connection.getTable(nameObj);//表
        byte[] svalue = Bytes.toBytes(startValue);
        byte[] evalue = Bytes.toBytes(endValue);
        System.out.println("查找范围:"+"("+Arrays.toString(svalue) +"---->"+Arrays.toString(evalue)+")");
        Scan scan = new Scan();
        scan.setStartRow(svalue);
        scan.setStopRow(evalue);
        ResultScanner resultScanner = table.getScanner(scan);
        //输出
        for (Result result : resultScanner) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                System.out.print("rowKey:" + new String(CellUtil.cloneRow(cell)) + "|");
                System.out.print("timeTamp:" + cell.getTimestamp() + "|");
                System.out.print("columnFamily:" + new String(CellUtil.cloneFamily(cell)) + "|");
                System.out.print("column:" + new String(CellUtil.cloneQualifier(cell)) + "|");
                System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + "|");
            }
            System.out.println("查找成功!");
        }
    }


    public static void main(String args[]) throws Exception{

        HbaseClient client = new HbaseClient();

        //**********列出所有表***********
        //client.showTable();

        //**********删除表***************
        //client.deleteTable("User");

        //**********建表*****************
        //String [] columns = {"name","age","phone","addr","id"};
        //client.createTable("UserInfo",columns);

        //**********插入数据*************
        //client.insertData("UserInfo","rowKey1","name","name_1","x");//name列族
        //client.insertData("UserInfo","rowKey1","name","name_2","y");
        //client.insertData("UserInfo","rowKey1","name","name_3","z");
        //client.insertData("UserInfo","rowKey1","age","age_1","xx");//age列族
        //client.insertData("UserInfo","rowKey1","age","age_2","yy");

        //**********查找数据*************
        //client.getData("UserInfo","rowKey1",null,null);//根据rowKey
        //client.getData("UserInfo","rowKey1","name",null);//根据rowKey、columnFamily
        //client.getData("UserInfo","rowKey1","name","name_1");//根据rowKey、columnFamily、column

        //**********删除数据*************
        //client.deleteData("UserInfo","rowKey1",null,null);//根据rowKey
        //client.deleteData("UserInfo","rowKey1","name",null);//根据rowKey、columnFamily
        //client.deleteData("UserInfo","rowKey1","name","name_1");//根据rowKey、columnFamily、column

        //**********范围扫描*************
        //client.scanData("UserInfo","rowKey1","rowKey2");

        close();
    }
}
