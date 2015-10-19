package np.supermagicalloveparty.server;

import java.util.ArrayList;

public class ExtraPhysics extends Extra
{
	private static final long serialVersionUID = 7421199098844889831L;
	int playerNumber;
	byte direction;
	double x, y, health, vX, vY, aX, aY;
	//ArrayList<Integer> state;
	
	public ExtraPhysics(int playerNumber, double x, double y, double vX, double vY, double aX, double aY, double health, byte direction)
	{
		this.playerNumber = playerNumber;
		this.x = x;
		this.y = y;
		this.vX = vX;
		this.vY = vY;
		this.aX = aX;
		this.aY = aY;
		this.health = health;
		this.direction = direction;
	}

	
//	public ArrayList<Integer> getState()
//	{
//		return state;
//	}
	
	@Override
	public int getType()
	{
		return Packet.PHYSICS;
	}

	public int getPlayerNumber()
	{
		return playerNumber;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getHealth()
	{
		return health;
	}
	
	public double getvX()
	{
		return vX;
	}

	public double getvY()
	{
		return vY;
	}

	public double getaX()
	{
		return aX;
	}

	public double getaY()
	{
		return aY;
	}
	
	public byte getDirection()
	{
		return direction;
	}
	@Override
	public String toString()
	{
		return "Player " + playerNumber + " physics update: X: " + x + " y: " + y + " state: ";// + state;//+ " vX: " + vX + " vY: " + vY + " aX: " + aX + " aY: " + aY;
	}

}
