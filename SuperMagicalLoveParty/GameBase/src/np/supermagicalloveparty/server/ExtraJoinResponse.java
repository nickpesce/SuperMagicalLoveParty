package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.Game;
import np.supermagicalloveparty.game.Game.Mode;


public class ExtraJoinResponse extends Extra
{
	
	private static final long serialVersionUID = -9122074155271485051L;
	public static final int FULL = -1,
			VERSION = -2,
			BANNED = -3;
	boolean allowed;
	private int playerNumber;
	private String character, name;
	private Mode mode;
	
	/**
	 * 
	 * @param allowed granted admittance into server
	 * @param character Character in question
	 * @param name Player name in question
	 * @param n Player number if accepted, or reason code if not
	 */
	public ExtraJoinResponse(boolean allowed, String character, String name, int n, Mode mode)
	{
		this.allowed = allowed;
		this.playerNumber = n;
		this.character = character;
		this.name = name;
		this.mode = mode;
	}
	
	@Override
	public int getType()
	{
		return Packet.JOIN_RESPONSE;
	}

	public int getPlayerNumber()
	{
		return playerNumber;
	}

	public void setPlayerNumber(int playerNumber)
	{
		this.playerNumber = playerNumber;
	}

	public boolean isAllowed()
	{
		return allowed;
	}

	public String getPlayerName()
	{
		return name;
	}
	
	public String getCharacter()
	{
		return character;
	}

	@Override
	public String toString()
	{
		if(allowed)
			return character + " " + name + " is allowed to join the server with a id number of " + playerNumber;
		else
			return character + " " + name + " is declined access to join the server";
	}

	public Game.Mode getMode()
	{
		return mode;
	}
}
