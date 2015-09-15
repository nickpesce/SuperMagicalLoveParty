package np.supermagicalloveparty.server;



public class ExtraSpawn extends Extra
{
	private static final long serialVersionUID = -2084423174793668052L;

	String character, name;
	int n;
	public ExtraSpawn(String character, String name, int n)
	{
		this.n = n;
		this.character = character;
		this.name = name;
	}
	
	@Override
	public int getType()
	{
		return Packet.SPAWN;
	}

	public String getCharacter()
	{
		return character;
	}

	public String getName()
	{
		return name;
	}

	public int getN()
	{
		return n;
	}

	@Override
	public String toString()
	{
		return character + " " + name + " has joined the game with an id of " + n;
	}
}
