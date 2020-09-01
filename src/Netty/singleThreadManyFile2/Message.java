package Netty.singleThreadManyFile2;

import java.io.Serializable;

/**
 * @Author xiongdingkun
 * @Description //TODO Administrator
 * @Date 21:14 2018/11/14
 **/
public class Message implements Serializable {
	
	private int filePathLength;
	private String filePath;
	private int mark;
	private long fileLength;
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	public long getFileLength() {
		return fileLength;
	}
	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}
	public int getFilePathLength() {
		return filePathLength;
	}
	public void setFilePathLength(int filePathLength) {
		this.filePathLength = filePathLength;
	}
	
	
}
