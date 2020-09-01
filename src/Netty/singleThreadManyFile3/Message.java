package Netty.singleThreadManyFile3;

public class Message {
	private int nameLength;
	private String name;
	private long contentLength;
	private int directoryLength;
	private String directory;
	//内容过大不能放在这里面
	public int getNameLength() {
		return nameLength;
	}
	public void setNameLength(int nameLength) {
		this.nameLength = nameLength;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getContentLength() {
		return contentLength;
	}
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public int getDirectoryLength() {
		return directoryLength;
	}

	public void setDirectoryLength(int directoryLength) {
		this.directoryLength = directoryLength;
	}
}
