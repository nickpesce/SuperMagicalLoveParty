package np.supermagicalloveparty.game;

public class CollisionEvent
{
	static enum Side
	{
		TOP, BOTTOM, LEFT, RIGHT, INSIDE, CORNER_TOP_LEFT, CORNER_BOTTOM_LEFT, CORNER_TOP_RIGHT, CORNER_BOTTOM_RIGHT
	}
	
	private Side side;
	private Point tilePoint, collisionPoint;
	private double distance = 69;
	private int tile;
	
	public CollisionEvent(Point tP, Point cP, Side s, double distance, int tile)
	{
		this.side = s;
		this.tilePoint = tP;
		this.collisionPoint = cP;
		this.distance = distance;
		this.tile = tile;
	}

	public Side getSide()
	{
	    return side;
	}

	public void setSide(Side side)
	{
	    this.side = side;
	}

	public Point getTilePoint() 
	{
	    return tilePoint;
	}

	public void setTilePoint(Point point)
	{
	    this.tilePoint = point;
	}

	public double getDistance()
	{
		return distance;
	}

	public void setDistance(double distance)
	{
		this.distance = distance;
	}

	public Point getCollisionPoint()
	{
		return collisionPoint;
	}

	public void setCollisionPoint(Point collisionPoint)
	{
		this.collisionPoint = collisionPoint;
	}

	public int getTile()
	{
		return tile;
	}

	public void setTile(int tile)
	{
		this.tile = tile;
	}
	
	@Override
	public String toString()
	{
		return side + " TIle: " + tile + " Tile Point: " + tilePoint + " COllision Point: " + collisionPoint + " distance: " + distance;
	}
}
