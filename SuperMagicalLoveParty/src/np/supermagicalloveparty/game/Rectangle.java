package np.supermagicalloveparty.game;
import java.awt.Graphics;



public class Rectangle extends Shape
{

	double width, height;
	Point lowerLeft, lowerRight, upperLeft, upperRight;
	boolean fillDraw;

	public Rectangle(double x2, double y2, double width2, double height2)
	{
		this.x = x2;
		this.y = y2;
		this.height = height2;
		this.width = width2;
		this.fillDraw = false;
		upperLeft = new Point(x2, y2);
		lowerLeft = new Point(x2, y2+height2);
		upperRight = new Point(x2+width2, y2);
		lowerRight = new Point(x2+width2, y2+height2);
	}
	
	public Rectangle(double x2, double y2, double width2, double height2, boolean fillDraw)
	{
		this(x2, y2, width2, height2);
		this.fillDraw = fillDraw;
	}
	
	public boolean intersects(Rectangle r)
	{
		return
				lowerLeft.x <= r.lowerRight.x &&
				lowerRight.x >= r.lowerLeft.x &&
				lowerLeft.y >= r.upperLeft.y &&
				upperLeft.y <= r.lowerLeft.y;
	}
	
	public boolean intersects(Circle c)
	{
		if(contains(c.center.x, c.center.y))
			return true;
		
		double closestX = c.center.x;
		double closestY = c.center.y;
		
		if(c.center.x < lowerLeft.x)
			closestX = lowerLeft.x;
		else if(c.center.x > lowerRight.x)
			closestX = lowerRight.x;
		if(c.center.y < upperLeft.y)
			closestY = upperLeft.y;
		else if(c.center.y > lowerLeft.y)
			closestY = lowerLeft.y;
		
		return MathHelper.distance(c.center.x, c.center.y, closestX, closestY) < c.getRadius();
		
	}
	
	public boolean contains(double x, double y)
	{
		return 
				lowerLeft.x <= x && lowerRight.x >= x &&
				lowerLeft.y >= y && upperLeft.y <= y;
	}
	
	public double getX()
	{
		return x;
	}
	public void setX(double x)
	{
		this.x = x;
		upperLeft = new Point(x, y);
		lowerLeft = new Point(x, y+height);
		upperRight = new Point(x+width, y);
		lowerRight = new Point(x+width, y+height);
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y)
	{
		this.y = y;
		upperLeft = new Point(x, y);
		lowerLeft = new Point(x, y+height);
		upperRight = new Point(x+width, y);
		lowerRight = new Point(x+width, y+height);
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public void setWidth(double width)
	{
		this.width = width;
	}
	
	public double getHeight() 
	{
		return height;
	}
	
	public void setHeight(double height)
	{
		this.height = height;
	}
	
	public void setFillDraw(boolean f)
	{
		fillDraw = f;
	}
	
	@Override
	public void draw(Graphics g, double scale)
	{
		if(fillDraw)
			g.fillRect((int)(x*scale), (int)(scale*y), (int)(width*scale), (int)(height*scale));
		else
			g.drawRect((int)(x*scale), (int)(scale*y), (int)(width*scale), (int)(height*scale));
	}
}
