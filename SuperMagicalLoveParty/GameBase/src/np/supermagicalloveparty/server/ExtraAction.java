package np.supermagicalloveparty.server;


public class ExtraAction extends Extra
{

	private static final long serialVersionUID = -7129205969295262315L;
	public static final int
	ATTACK = 0,
	ATTACK_START = 1,
	ATTACK_CHARGED = 2,
	RUN_LEFT = 3,
	RUN_RIGHT = 4,
	JUMP = 5,
	EVADE = 6,
	STOP_EVADING = 7,
	STOP_LEFT = 8,
	STOP_RIGHT = 9,
	SQUAT = 10,
	STAND = 11,
	LAND= 12;
	
	int actionType;
	int chargeTime;
	int playerNumber;
	public ExtraAction(int playerNumber, int actionType, int chargeTime)
	{
		this.playerNumber = playerNumber;
		this.actionType = actionType;
		this.chargeTime = chargeTime;
	}
	
	public ExtraAction(int playerNumber, int type)
	{
		this(playerNumber, type, 0);
	}

	@Override
	public int getType()
	{
		return Packet.ACTION;
	}

	public int getChargeTime()
	{
		return chargeTime;
	}

	public int getActionType()
	{
		return actionType;
	}

	public int getPlayerNumber()
	{
		return playerNumber;
	}

	@Override
	public String toString()
	{
		return "Player " + playerNumber + " did action " + actionType;
	}
}
