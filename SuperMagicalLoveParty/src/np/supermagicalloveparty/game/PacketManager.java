package np.supermagicalloveparty.game;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JOptionPane;

import np.supermagicalloveparty.server.ExtraDisconnect;
import np.supermagicalloveparty.server.Packet;

public class PacketManager
{

	String hostAddress;
	int port;
	ObjectOutputStream output;
	ObjectInputStream input;
	public int packetsSent;
	boolean listening;
	Socket connection;
	Runnable listen;
	GameMP game;
	
	public PacketManager(GameMP g, String host, int port)
	{
		this.game = g;
		this.hostAddress = host;
		this.port = port;
		try
		{
			connection = new Socket(InetAddress.getByName(hostAddress), port);
			connection.setTcpNoDelay(true);
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();//is this needed?
			input = new ObjectInputStream(connection.getInputStream());
			listen = new Runnable()
			{
				@Override
				public void run()
				{
					Packet packet = null;
					listening = true;
					do
					{
						try
						{
							packet = (Packet)input.readObject();
							game.handlePacket(packet);
						}
						catch (ClassNotFoundException e)
						{
							System.out.println("Packet read error!");
							//send(new Packet(Packet.DISSCONECT, new ExtraDisconnect(game.myPlayerNumber, ExtraDisconnect.ERROR)));
							//game.stop();
							//close();
						}
						catch(IOException e)
						{
							e.printStackTrace();
							close();
							game.stop();
						}
					}while(listening);
				}
			};
			listen();
		}catch(IOException e)
		{
			game.stop();
			JOptionPane.showMessageDialog(game.frame, "Could not connect to server.");
			e.printStackTrace();
		}
	}
	
	public synchronized void send(Packet packet)
	{
		packetsSent++;
		packet.setNumber(packetsSent);
		try
		{
			output.writeObject(packet);
		}
		catch (IOException e)
		{
			packetsSent--;
			e.printStackTrace();
		}
	}
	
	public void listen()
	{
		Thread thread = new Thread(listen);
		thread.start();
	}
	
	public void close()
	{
		listening = false;
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
