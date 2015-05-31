package np.supermagicalloveparty.server;

import java.io.Serializable;

public class ExtraJoinRequest extends Extra implements Serializable
{
	private static final long serialVersionUID = 5531375892225308119L;
	String character;
	String name;
	String version;
	
	public ExtraJoinRequest(String character, String playerName, String version)
	{
		this.character = character;
		this.name = playerName;
		this.version = version;
	}
	
	@Override
	public int getType()
	{
		return Packet.JOIN_REQUEST;
	}

	public String getCharacter()
	{
		return character;
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return character + "  " + name + " is trying to join.";
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

}
