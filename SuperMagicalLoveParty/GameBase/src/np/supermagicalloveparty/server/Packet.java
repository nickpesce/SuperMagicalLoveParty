package np.supermagicalloveparty.server;

import java.io.Serializable;

public class Packet implements Serializable
{
	private static final long serialVersionUID = 8282045820844932612L;
	public transient static final int
	PHYSICS = 0,
	SPAWN = 1,
	DAMAGE = 2,
	JOIN_REQUEST= 3,
	JOIN_RESPONSE = 4,
	DISSCONECT = 5,
	MESSAGE = 6,
	ACTION = 7,
	POWERUP_AQUIRE = 9,
	ENTITY_UPDATE = 10,
	ENTITY_SPAWN = 11,
	COMMAND = 12,
	LEVEL_DATA = 13;
	
	int type;
	Extra info;
	int number;
	
	public Packet(int type, Extra info)
	{
		this.type = type;
		this.info = info;
	}
	
	public int getType()
	{
		return type;
	}
	
	public Extra getInfo()
	{
		return info;
	}
	
	public void setNumber(int n)
	{
		this.number = n;
	}
	
	@Override
	public String toString()
	{
		return "Type: " + type + " number " + number + " info: " + info;
	}
	
}
