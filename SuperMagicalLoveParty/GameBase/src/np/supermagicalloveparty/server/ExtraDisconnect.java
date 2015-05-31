package np.supermagicalloveparty.server;


public class ExtraDisconnect extends Extra
{
	private static final long serialVersionUID = -3861177274733481822L;

	public static final int
	LEAVE = 0,
	TIMEOUT = 1,
	SERVER_CLOSE = 2,
	ERROR = 3,
	KICK = 4;

	
	int reason, playerNumber;
	
	public ExtraDisconnect(int playerNumber, int reason)
	{
		this.reason = reason;
		this.playerNumber = playerNumber;
	}
	
	public int getType()
	{
		return Packet.DISSCONECT;
	}

	public int getReason()
	{
		return reason;
	}

	@Override
	public String toString()
	{
		return "player " + playerNumber + " has left the game due to " + reason;
	}

	public int getPlayerNumber()
	{
		return playerNumber;
	}
}
