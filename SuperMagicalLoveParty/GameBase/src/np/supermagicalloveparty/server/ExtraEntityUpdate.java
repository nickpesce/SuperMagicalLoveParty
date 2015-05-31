package np.supermagicalloveparty.server;


public class ExtraEntityUpdate extends Extra
{
	private static final long serialVersionUID = -7519215063457735708L;

	int index;
	double x, y;
	
	public ExtraEntityUpdate(int index, double x, double y)
	{
		this.index = index;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public int getType()
	{
		return Packet.ENTITY_UPDATE;
	}

	public int getIndex()
	{
		return index;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}
	
	@Override
	public String toString()
	{
		return "Entity Location Update: index " + index + " is now at: " + x + ", " + y;
	}

}
