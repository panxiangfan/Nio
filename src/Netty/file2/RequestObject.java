package Netty.file2;
import java.io.Serializable;  
import java.util.Arrays;  
  
/** 
 * 此类用于我们接收client端发来信息 
 * 使用字节数组可以兼容字符串，文件，图片 
 * 
 */  
public class RequestObject implements Serializable {  
      

    /** 文件名长度 */  
    private int nameLength;  
    /** 文件名 */  
    private byte[] fileName;  
    /** 文件长度 */  
    private int contentLength;  
    /** 文件内容 */  
    private byte[] contents;  
      
    public RequestObject() {}  
      
    public RequestObject(int nameLength, byte[] fileName, int contentLength, byte[] contents) {  
        this.nameLength = nameLength;  
        this.fileName = fileName;  
        this.contentLength = contentLength;  
        this.contents = contents;  
    }  
  
    public int getNameLength() {  
        return nameLength;  
    }  
  
    public void setNameLength(int nameLength) {  
        this.nameLength = nameLength;  
    }  
  
    public byte[] getFileName() {  
        return fileName;  
    }  
  
    public void setFileName(byte[] fileName) {  
        this.fileName = fileName;  
    }  
  
    public int getContentLength() {  
        return contentLength;  
    }  
  
    public void setContentLength(int contentLength) {  
        this.contentLength = contentLength;  
    }  
  
    public byte[] getContents() {  
        return contents;  
    }  
  
    public void setContents(byte[] contents) {  
        this.contents = contents;  
    }  
  
    @Override  
    public String toString() {  
        return "[ 文件名长度 : " + nameLength  + " ,文件名 : " + Arrays.toString(fileName) 
        + " ,文件长度 : " + contentLength  + " ,文件内容 : " +contentLength+"]";  
    }  
}  