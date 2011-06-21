package at.ac.uniklu.mobile.message;

import java.io.Serializable;

public class UncoverMessage implements Serializable {
	private VectorTimestamp vt;
	private int x;
	private int y;
	
	public UncoverMessage(VectorTimestamp myTimestamp, int x, int y) {
		this.vt = myTimestamp;
		this.x = x;
		this.y = y;
	}
	
	public VectorTimestamp getVectorTimestamp() {
		return vt;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	
}
