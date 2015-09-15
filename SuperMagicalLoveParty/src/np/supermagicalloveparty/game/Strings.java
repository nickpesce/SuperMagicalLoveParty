package np.supermagicalloveparty.game;

public class Strings
{
	
	Game game;
	public Strings(Game game)
	{
		this.game = game;
	}
	
	public String attack()
	{
		if(game.specialMode)
			return "Attack";
		return "Love";
	}
}
