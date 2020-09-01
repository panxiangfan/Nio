package Netty.singleThreadManyFile2;

import java.io.File;

public class Client {
	public static void main(String[] args) {
		Client client = new Client();
		client.recursionDirectory(new File("C:\\Users\\Administrator\\Desktop\\apache-tomcat-8.0.48"));
	}
	
	/**
	 * 递归文件
	 */
	public void recursionDirectory(File file) {
		try {
			File[] fileArray = file.listFiles();//获得file对象下的所有文件、文件夹数组
			for (File files:fileArray) {//循环文件下的所有文件
				if (files.isDirectory()) {//是否是文件夹
					ClientThread c = new ClientThread(files.getPath(),0);
					c.start();
					c.join();
					recursionDirectory(files);
				} else {//否则是文件
					ClientThread c = new ClientThread(files.getPath(),1);
					c.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
