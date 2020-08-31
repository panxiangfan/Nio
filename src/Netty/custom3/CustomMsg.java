package Netty.custom3;

/**
 * 实体类
 * @作者  罗玲红
 *
 * @时间 2017年6月14日 上午11:24:17
 */
public class CustomMsg {  
      
    private int nameLength;
    
    private String name;
    
    private int bodyLength;
    
    private String body;
      
    public CustomMsg() {  
          
    }  
      
    public CustomMsg(int nameLength,String name,int boydLength,String body) {  
        this.nameLength = nameLength;  
        this.name = name;
        this.bodyLength = boydLength;
        this.body = body;
    }

	public int getNameLength() {
		return nameLength;
	}

	public void setNameLength(int nameLength) {
		this.nameLength = nameLength;
	}

	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	@Override
	public String toString() {
		return "CustomMsg [nameLength=" + nameLength + ", name=" + name + ", bodyLength=" + bodyLength + ", body="
				+ body + "]";
	}

	

	
  
}  