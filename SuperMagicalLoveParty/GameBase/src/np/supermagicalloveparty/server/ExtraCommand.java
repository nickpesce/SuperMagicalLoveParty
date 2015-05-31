package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.Player;

public class ExtraCommand extends Extra
{

	public static final int
	GAME_OVER = 0,
	RESTART = 1,
	STREAMLINE = 2;

	
	public int command;
	/**
	 * for gameOver, this is the player number.
	 */
	public int n = -1;
	
	/**
	 * for gameOver this is the instant flag.
	 */
	public boolean flag;
	
	/**
	 * use this constructor for restart command
	 */
	public ExtraCommand(int type)
	{
		this.command = type;
	}
	
	/**
	 * use this constructor for game over
	 * @param type Use gameover
	 * @param n Player Index
	 * @param flag instant?
	 */
	public ExtraCommand(int type, int n, boolean flag)
	{
		this(type);
		this.n = n;
		this.flag = flag;
	}

	@Override
	public int getType()
	{
		return Packet.COMMAND;
	}
	
}
