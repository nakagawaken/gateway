package serial5;

public class RSSIXY {
	private String x = null;
	private String y = null;

	RSSIXY(String x, String y){
		this.x = x;
		this.y = y;
	}

	
	public void setX(String x){
		this.x = x;
	}

	public void setY(String y){
		this.y = y;
	}


	public String getX() {
		return x;
	}


	public String getY() {
		return y;
	}

}
