package np.supermagicalloveparty.game;

import java.io.Serializable;

public class Point implements Serializable{
	double x, y;

	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
 	}

	public double getX() 
	{
		return x;
	}

	public void setX(double x) 
	{
		this.x = x;
	}

	public double getY() 
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}
	
	@Override
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Point && ((Point) o).getX() == x && ((Point)o).getY() == y)
			return true;
		return false;
	}
}
