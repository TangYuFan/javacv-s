package com.ist.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;

import java.io.*;
import java.util.Arrays;
import java.util.Date;

public class HdfsClient {

	//配置
	private static Configuration conf = new Configuration();
	//地址
	private static final String HADOOP_URL="hdfs://192.168.1.222:9000";
	//用户
	private static final String HADOOP_USER_NAME="HADOOP_USER_NAME";
	private static final String USER="root";
	//dfs实例
	private static FileSystem fs;
	private static DistributedFileSystem hdfs;
	
	static {
		try {
			//用root替换掉默认用户
			System.setProperty(HADOOP_USER_NAME, USER);
			FileSystem.setDefaultUri(conf, HADOOP_URL);
			fs = FileSystem.get(conf);
			hdfs = (DistributedFileSystem)fs;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//列出所有节点信息
	public void listDataNodeInfo() {		
		try {
			DatanodeInfo[] dataNodeStats = hdfs.getDataNodeStats();
			String[] names = new String[dataNodeStats.length];
			String[] addr = new String[dataNodeStats.length];
			for (int i=0;i<names.length;i++) {
				names[i] = dataNodeStats[i].getHostName();//名字
				addr[i] = dataNodeStats[i].getIpAddr();//IP
			}
			System.out.println("节点name:"+Arrays.toString(names));
			System.out.println("节点appr:"+Arrays.toString(addr));
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
	}

	//上传文件
	public void copyFileToHDFS() {
		try {
			Path f = new Path("/test_1/hadoop_test");
			File file = new File("D:\\hadoop_test.txt");
			FileInputStream is = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(is, "utf-8");//UTF-8
			BufferedReader br = new BufferedReader(isr);
			FSDataOutputStream os = fs.create(f, true);
			Writer out = new OutputStreamWriter(os, "utf-8");//UTF-8
			String str = "";
			while((str=br.readLine()) != null) {
				out.write(str+"\n");
			}
			br.close();
			isr.close();
			is.close();
			out.close();
			os.close();
			System.out.println("上传成功!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//下载文件
	public void copyFileToLOCAL(){
		Path f = new Path("/test_1_output/part-r-00000");
		Path d = new Path("d:/test_1_output/output.txt");
		try{
			fs.copyToLocalFile(f,d);
			System.out.println("下载成功!");
		}
		 catch (Exception e) {
			e.printStackTrace();
		}

	}

	//读取文件内容
	public void readFileFromHdfs() {
		try {
			Path f = new Path("/test_1/hadoop_test");

			FSDataInputStream dis = fs.open(f);
			InputStreamReader isr = new InputStreamReader(dis, "utf-8");//UTF-8
			BufferedReader br = new BufferedReader(isr);
			String str = "";
			while ((str = br.readLine()) !=null) {
				System.out.println(str);
			}
			br.close();
			isr.close();
			dis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//列出所有文件夹/文件
	public void listFileStatus(String path) throws FileNotFoundException, IllegalArgumentException, IOException {
		FileStatus fileStatus[]=fs.listStatus(new Path(path));
		int listlength=fileStatus.length;
		for (int i=0 ;i<listlength ;i++){
			//文件
			if (fileStatus[i].isDirectory() == false) {
				System.out.println("文件:"+ fileStatus[i].getPath().getName() + "\tsize:"+ fileStatus[i].getLen());
			}
			//文件夹
			else {
				String newpath = fileStatus[i].getPath().toString();
				System.out.println("文件夹:"+ newpath);
				listFileStatus(newpath);
			}
		}
	}


	//查看文件是否存在
	public void checkFileExist() {
		try {
			Path a= hdfs.getHomeDirectory();
			Path f = new Path("/test_1/hadoop_test");
			boolean exist = fs.exists(f);
			System.out.println("是否存在文件:"+""+exist);
			//删除文件
//			if (exist) {
//				boolean isDeleted = hdfs.delete(f, false);
//				if(isDeleted) {
//					System.out.println("Delete success");
//				}				
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//创建文件
	public void createFile() {
		try {
			Path f = new Path("/test_1/hadoop_test2");
			FSDataOutputStream os = fs.create(f, true);
			Writer out = new OutputStreamWriter(os, "utf-8");//以UTF-8格式写入文件，不乱码
			out.write("hello world !");
			out.close();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//删除文件
	public void deFile(){
		try{
			// 删除文件夹 ，如果是非空文件夹，参数2必须给值true
			fs.delete(new Path("/test_1/hadoop_test2"), true);
			System.out.println("删除成功!");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	//获取文件所在的位置
	public void getLocation() {
		try {
			Path f = new Path("/test_1/hadoop_test");
			FileStatus fileStatus = fs.getFileStatus(f);
			
			BlockLocation[] blkLocations = fs.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
			for (BlockLocation currentLocation : blkLocations) {
				String[] hosts = currentLocation.getHosts();
				for (String host : hosts) {
					System.out.println(host);
				}
			}
			//取得最后修改时间
			long modifyTime = fileStatus.getModificationTime();
			Date d = new Date(modifyTime);
			System.out.println(d);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//创建目录
	public void mkDir(){
		try{
			// 创建目录
			fs.mkdirs(new Path("/hbase"));
			System.out.println("创建成功!");
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	//重命名目录
	public void reNameDir(){
		try{
			// 重命名文件或文件夹
			fs.rename(new Path("/test_2"), new Path("/test_3"));
			System.out.println("重命名成功!");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	//删除目录
	public void deDir(){
		try{
			// 删除文件夹 ，如果是非空文件夹，参数2必须给值true
			fs.delete(new Path("/test_1_output"), true);
			System.out.println("删除成功!");
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws Exception{
		HdfsClient a = new HdfsClient();
		//a.listDataNodeInfo();//获取所有节点信息
		//a.copyFileToHDFS();//上传文件
		//a.copyFileToLOCAL();//下载文件
		//a.readFileFromHdfs();//读取文件内容
		a.listFileStatus(HADOOP_URL+"/");//列出所有文件夹、文件
		//a.checkFileExist();//检查文件是否存在
		//a.createFile();//创建文件//
		// a.deFile();//删除文件
		//a.getLocation();//查看文件所在节点
		//a.mkDir();//创建目录
		//a.reNameDir();//重命名目录
		//a.deDir();//删除目录
	}
}
