package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.Point;

public class ExtraPowerupAquire extends Extra
{
	
	int playerNumber;
	int powerupType;
	public static final int
	SPEED = 0,
	STRENGTH = 1,
	SPECIAL = 2,
	INVINCIBLE = 3,
	HEALTH = 4,
	//KEEP VEGETABLES SAME. CONSTANTS IN ENTITYBUNNY + 10
	CARROT = 10,
	TURNIP = 11,
	CABBAGE = 12;
	
	public ExtraPowerupAquire(int playerNumber, int type)
	{
		this.playerNumber = playerNumber;
		this.powerupType = type;
	}

	@Override
	public int getType()
	{
		return Packet.POWERUP_AQUIRE;
	}

	public int getPlayerNumber()
	{
		return playerNumber;
	}


	public int getPowerupType()
	{
		return powerupType;
	}

}
