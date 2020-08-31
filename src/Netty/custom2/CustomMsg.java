package Netty.custom2;

/**
 * 实体类
 * @作者  罗玲红
 *
 * @时间 2017年6月14日 上午11:24:17
 */
public class CustomMsg {  
      
    private int nameLength;
    
    private int bodyLength;
      
    public CustomMsg() {  
          
    }  
      
    public CustomMsg(int nameLength,int boydLength) {  
        this.nameLength = nameLength;  
        this.bodyLength = boydLength;
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

	@Override
	public String toString() {
		return "CustomMsg [nameLength=" + nameLength + ", bodyLength=" + bodyLength + "]";
	}

  
}  