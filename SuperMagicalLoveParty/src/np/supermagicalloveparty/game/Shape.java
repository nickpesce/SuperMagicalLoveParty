package np.supermagicalloveparty.game;
import java.awt.Graphics;


public abstract class Shape 
{
	protected double x, y;
	public boolean intersects(Shape s)
	{
		if(this instanceof Circle && s instanceof Rectangle)
			return ((Rectangle)s).intersects((Circle)this);
		else if(this instanceof Rectangle && s instanceof Circle)
			return ((Rectangle)this).intersects((Circle)s);
		else if(this instanceof Rectangle)
			return ((Rectangle)s).intersects((Rectangle)this);
		else if(this instanceof Circle)
			return ((Circle)s).intersects((Circle)this);
		else
		{
			System.out.println("ASDF ERROR!!! Shape " + s + " is not a circle or rectangle... WTF?!?!?");
		}
			return false;
	}
	
	public abstract void setX(double x);
	
	public abstract void setY(double y);
	
	public abstract void setWidth(double w);
	
	public abstract void setHeight(double h);
	
	public abstract double getWidth();
	
	public abstract double getHeight();

	public abstract double getX();

	public abstract double getY();
	
	public abstract void draw(Graphics g, double scale);

}
