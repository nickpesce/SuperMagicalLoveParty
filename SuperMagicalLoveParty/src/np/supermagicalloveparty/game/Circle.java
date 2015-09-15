package np.supermagicalloveparty.game;
import java.awt.Graphics;



public class Circle extends Shape
{
	private double radius;
	Point center;
	public Circle(double x, double y, double radius)
	{
		this.x = x;
		this.y = y;
		this.radius = radius;
		center = new Point(x + radius, y + radius);
	}
	
	public boolean intersects(Circle c)
	{
		return MathHelper.distance(center.x, center.y, c.center.x, c.center.y) <= radius + c.radius;
	}
	
	public boolean intersects(Rectangle r)
	{
		if(r.contains(center.x, center.y))
			return true;
		double closestX = center.x;
		double closestY = center.y;
		
		if(center.x < r.upperLeft.x)
			closestX = r.upperLeft.x;
		else if(center.x > r.lowerRight.x)
			closestX = r.lowerRight.x;
		if(center.y < r.upperLeft.y)
			closestY = r.upperLeft.y;
		else if(center.y > r.lowerLeft.y)
			closestY = r.lowerLeft.y;
		return MathHelper.distance(center.x, center.y, closestX, closestY) < radius;
	}
	
	public boolean contains(double x, double y)
	{
		return MathHelper.distance(x, y, center.x, center.y) <= radius;
	}
	
	public double getX() 
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
		center = new Point(x + radius, y + radius);
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
		center = new Point(x + radius, y + radius);
	}
	
	public double getRadius() 
	{
		return radius;
	}

	public void setWidth(double w)
	{
		setRadius(w/2);
	}
	
	public void setHeight(double h)
	{
		setRadius(h/2);
	}
	
	/**
	 * returns radius*2
	 */
	public double getWidth()
	{
		return radius*2;
	}
	
	/**
	 * returns radius*2
	 */
	public double getHeight()
	{
		return radius*2;
	}
	
	public void setRadius(double radius)
	{
		this.radius = radius;
		center = new Point(x + radius, y + radius);
	}
	
	public Point getCenter()
	{
		return center;
	}
	
	@Override
	public void draw(Graphics g, double scale)
	{
		g.drawOval((int)(x*scale), (int)(scale*y), (int)(radius*2*scale), (int)(radius*2*scale));
	}
}
