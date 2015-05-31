package np.supermagicalloveparty.game;

public class MathHelper {
	public static double distance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2-y1, 2));
	}
	
	public static double getXOnLine(double m, double x1, double y1, double y2)
	{
		return (((y2-y1)/m)+x1);
	}
	
	public static double getYOnLine(double m, double x1, double y1, double x2)
	{
		return y1-(m*(x1-x2));
		//return m*(x2-x1) + y1;
	}
	
	public static double getSlope(double x1, double y1, double x2, double y2)
	{
		return (y2-y1)/(x2-x1);
	}
	
	public static double getSlope(Point p1, Point p2)
	{
		return getSlope(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}
}
