package np.supermagicalloveparty.server;

import java.io.Serializable;

public class ExtraMessage extends Extra implements Serializable
{
	private static final long serialVersionUID = -3450194294520470352L;
	String message;
	int sender;
	
	public ExtraMessage(String message, int sender)
	{
		this.message = message;
		this.sender = sender;
	}
	
	@Override
	public int getType()
	{
		return Packet.MESSAGE;
	}

	public String getMessage()
	{
		return message;
	}

	/**
	 * gets the sender of the message. 
	 * @return player number or -1 for server.
	 */
	public int getSender()
	{
		return sender;
	}
	
	@Override
	public String toString()
	{
		return message + " from: " + sender;
	}
}
