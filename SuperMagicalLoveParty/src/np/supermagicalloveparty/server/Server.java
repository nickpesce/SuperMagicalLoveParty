package np.supermagicalloveparty.server;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import np.supermagicalloveparty.game.Entity;
import np.supermagicalloveparty.game.FileHelper;
import np.supermagicalloveparty.game.Frame;
import np.supermagicalloveparty.game.Game;
import np.supermagicalloveparty.game.Game.Mode;
import np.supermagicalloveparty.game.Level;
import np.supermagicalloveparty.game.Player;

public class Server
{
	private ServerSocket server;
	private ServerGame game;
	private boolean running;
	private Runnable connectionListen;
	private CopyOnWriteArrayList<ClientConnection> clients;
	private ArrayList<String> bannedIps;
	private int maxPlayers;
	private int port;
	private Frame frame;
	
	public Server(Frame frame, int port, Level level, int maxPlayers, Mode mode)
	{
		bannedIps = new ArrayList<String>();
		this.frame = frame;
		this.maxPlayers = maxPlayers;
		FileHelper.readArrayListToFile(bannedIps, "Server/", "banned-ips.txt");
		running = true;
		this.setPort(port);
		try
		{
			if(level == null)
			{
				game = new ServerGame(this, frame, mode);
			}
			else
				game = new ServerGame(this, frame, mode, level);
		}
		catch (URISyntaxException e1)
		{
			e1.printStackTrace();
		}
		if(frame!=null)
		{
			frame.setSize(698, 422);
			frame.setLocationRelativeTo(null);
		}
		else
		{
			new Thread(new Runnable(){
				@Override
				public void run() {
					
					BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); 					
					try{
						while(running)
						{
							game.doCommand(in.readLine());
						}
						in.close();
					}catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}).start();
		}
		clients = new CopyOnWriteArrayList<ClientConnection>();
		try
		{
			server = new ServerSocket(port);
		}
		catch (IOException e)
		{
			System.exit(0);
			e.printStackTrace();
		}
		connectionListen = new Runnable(){
			@Override
			public void run()
			{
				try
				{
					while(running)
					{
						Socket connection = server.accept();
						new ClientConnection(Server.this, connection);
						game.consoleLog("[INFO] " + connection.getInetAddress().getHostAddress() + " is attempting to join.", Color.WHITE, false);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
		};
	}
	
	public void close()
	{
		for(ClientConnection c : clients)
		{
			c.send(new Packet(Packet.DISSCONECT, new ExtraDisconnect(c.playerNumber, ExtraDisconnect.SERVER_CLOSE)));
			c.close();
		}
		running = false;
		try
		{
			server.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void listenForConnection()
	{
		Thread thread = new Thread(connectionListen);
		thread.start();
	}
	
	public void start()
	{
		game.init();
		listenForConnection();
		game.consoleLog("[INFO] Server started!", Color.WHITE, false);
		if(frame != null)
			copyIpToClipboard();
	}
	
	/**
	 * PRECONDITION: The frame is visible/frame != null
	 * Finds the most relevant IP address for the server and copies it to the clipboard
	 */
	public void copyIpToClipboard()
	{
		new Thread(new Runnable(){

			@Override
			public void run()
			{
				String ip;
				try
				{
					BufferedReader bufferedreader = new BufferedReader(new InputStreamReader((new URL("http://wtfismyip.com/text")).openStream()));
			        ip = bufferedreader.readLine();
					Server.this.game.consoleLog("External server IP copied to clipboard!", Color.GREEN, false);
				}
				catch (IOException e)
				{
					try
					{
						ip = Inet4Address.getLocalHost().getHostAddress();
						Server.this.game.consoleLog("Internal server IP(LAN only) copied to clipboard!", Color.GREEN, false);
					}
					catch (UnknownHostException e1)
					{
						ip = "http://wtfismyip.com";
						Server.this.game.consoleLog("Could not get server IP!", Color.RED, false);
					}
				}
				Server.this.game.consoleLog(ip+":"+getPort(), Color.BLACK, false);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(ip), null);
			}
		}).start();
	}

	
	public ClientConnection getConnectionByPlayerNumber(int n)
	{
		for(ClientConnection c : clients)
		{
			if(c.playerNumber == n)
				return c;
		}
		return null;
	}
	
	public void updateClients()
	{
		for(ClientConnection cc1 : clients)
		{
			for(ClientConnection cc2 : clients)
			{
				if(/*cc1 != cc2 &&  *//*Client reports physics*/cc2.playerNumber != -1 && cc1.playerNumber != -1)// && cc1!=cc2)
				{
					Player p = game.getPlayers()[cc2.playerNumber];
					if(p!=null)
						cc1.send(new Packet(Packet.PHYSICS, new ExtraPhysics(cc2.playerNumber, p.getX(), p.getY() , p.getVx(), p.getVy(), p.getAx(), p.getAy(), p.getPoints(), p.getDirection())));
				}
			}
			for(Entry<Integer, Entity> e : game.synchronizedEntities.entrySet())
			{
				if(cc1.playerNumber != -1)
					cc1.send(new Packet(Packet.ENTITY_UPDATE, new ExtraEntityUpdate(e.getKey(), e.getValue().getX(), e.getValue().getY())));
			}
		}
		
	}
	
	public void sendAll(Packet p)
	{
		for(ClientConnection c : clients)
		{
			c.send(p);
		}
	}
	public void playerSpawn(Player p, ClientConnection cl)
	{
		for(ClientConnection c : clients)
		{
			if(cl!=c)
				c.send(new Packet(Packet.SPAWN, new ExtraSpawn(p.getType(), p.getName(), p.getNumber())));
		}
		game.consoleLog("[INFO] " + p + " has joined the game! (n = " + p.getNumber() + ")", Color.CYAN, true);
	}
	
	public void loadGameForPlayer(int n, ClientConnection c)
	{
		if(game.hasCustomLevel())
		{
			Level level = game.getLevel();
			c.send(new Packet(Packet.LEVEL_DATA, new ExtraLevelData(level.getWidth(), level.getHeight(), level.getTiles(), level.getEntities(), level.getSettings(), 
					level.hasOwnBackground()? level.getBackground():null, level.hasOwnBackgroundSpecial()? level.getSpecialBackground(): null)));
		}
		for(Player p : game.getPlayers())
		{
			if(p!=null)
				c.send(new Packet(Packet.SPAWN, new ExtraSpawn(p.getType(), p.getName(), p.getNumber())));
		}
		for(Entry<Integer, Entity> e : game.synchronizedEntities.entrySet())
		{
			c.send(new Packet(Packet.ENTITY_SPAWN, e.getValue().getExtraSpawn()));
		}
		for(Entity p : game.getPowerups())
		{
			c.send(new Packet(Packet.ENTITY_SPAWN, p.getExtraSpawn()));
		}
	}
	
	public void removeClient(ClientConnection c, int reason)
	{
		clients.remove(c);
		c.close();
	}
	
	public void removePlayer(int n, int reason)
	{
		game.consoleLog("[INFO] " + game.getPlayerNames()[n] + " has left the game!", Color.CYAN, true);
		//sendAll(new Packet(Packet.DISSCONECT, new ExtraDisconnect(n, reason)));
		for(ClientConnection c : clients)
		{
			if(c.playerNumber != n)
				c.send(new Packet(Packet.DISSCONECT, new ExtraDisconnect(n, reason)));
		}
		removeClient(getConnectionByPlayerNumber(n), reason);
		game.removePlayer(n);
	}
	
	public int getNextValidN()
	{
		int n = 0;
		ArrayList<Integer> nums = new ArrayList<Integer>();
		for(ClientConnection c : clients)
			nums.add(c.playerNumber);
		while(true)
		{
			if(!nums.contains(n))
				return n;
			n++;
		}
	}
	
	public void handlePacket(Packet p, ClientConnection c)
	{
	//	System.out.println("SERVER RECEIVED PACKET: " + p);
		switch(p.getType())
		{
			case Packet.JOIN_REQUEST:
				ExtraJoinRequest ejr = (ExtraJoinRequest)(p.getInfo());
				if(clients.size() >= maxPlayers)
				{
					c.send(new Packet(Packet.JOIN_RESPONSE, new ExtraJoinResponse(false, ejr.character, ejr.name, ExtraJoinResponse.FULL, game.mode)));
					game.consoleLog("[INFO] " + ejr.getName() + " was not allowed to join because the server is full.", Color.RED, false);
				}
				else if(!ejr.getVersion().equals(Game.VERSION))
				{
					c.send(new Packet(Packet.JOIN_RESPONSE, new ExtraJoinResponse(false, ejr.character, ejr.name, ExtraJoinResponse.VERSION, game.mode)));
					game.consoleLog("[INFO] " + ejr.getName() + " was not allowed to join because they are on version " + ejr.getVersion() + " and the server is running " + Game.VERSION + ".", Color.RED, false);
				}
				else if(bannedIps.contains(c.connection.getInetAddress().getHostAddress()))
				{
					c.send(new Packet(Packet.JOIN_RESPONSE, new ExtraJoinResponse(false, ejr.character, ejr.name, ExtraJoinResponse.BANNED, game.mode)));
					game.consoleLog("[INFO] " + ejr.getName() + " was not allowed to join because their IP(" + c.connection.getInetAddress().getHostAddress() + ") is banned.", Color.RED, false);
				}
				else
				{
					int n = getNextValidN();
					c.playerNumber = n;
					clients.add(c);
					loadGameForPlayer(n, c);
					game.addPlayer(ejr.getCharacter(), ejr.getName(), n, c);//TODO wait for full load before adding
					c.send(new Packet(Packet.JOIN_RESPONSE, new ExtraJoinResponse(true, ejr.character, game.getPlayerNames()[n], n,game.mode)));
				}
				break;
			case Packet.DISSCONECT:
				ExtraDisconnect ed = ((ExtraDisconnect)p.getInfo());
				removePlayer(c.playerNumber, ed.getReason());
				break;
			case Packet.MESSAGE:
				ExtraMessage em = (ExtraMessage)(p.getInfo());
				if(em.message.equals("/ping"))
					c.send(new Packet(Packet.MESSAGE, new ExtraMessage("PONG!", -1)));
				else
				{
					game.consoleLog("["+game.getPlayers()[em.getSender()]+"]"+em.message, Color.BLUE, true);
					sendAll(p);
				}
				break;
			case Packet.PHYSICS:
				/*Client reports physics*/
				/*
				ExtraPhysics ep = (ExtraPhysics)p.getInfo();
				Player player = game.getPlayers()[ep.getPlayerNumber()];
				if(player == null)
					return;
				player.setX(ep.getX());
				player.setY(ep.getY());
				player.setPoints(ep.getHealth());
				player.setVx(ep.getvX());
				player.setVy(ep.getvY());
				player.setAx(ep.getaX());
				player.setAy(ep.getaY());
				*/
				break;
			case Packet.ACTION:
				ExtraAction ea = (ExtraAction)p.getInfo();
				Player pa = game.getPlayers()[ea.getPlayerNumber()];
				for(ClientConnection cl : clients)
				{
					if(cl.playerNumber != ea.getPlayerNumber())
						cl.send(p);
				}
				switch(ea.getActionType())
				{
					case ExtraAction.RUN_LEFT:
						pa.runLeft();
						break;
					case ExtraAction.RUN_RIGHT:
						pa.runRight();
						break;
					case ExtraAction.JUMP:
						pa.jump();
						break;
					case ExtraAction.LAND:
						pa.land();
						break;
					case ExtraAction.SQUAT:
						pa.squat();
						break;
					case ExtraAction.ATTACK:
						//pa.setAttackCharging(true);
						pa.setAttackChargeTime(ea.chargeTime);
						pa.doAttack();
						break;
					case ExtraAction.ATTACK_START:
						pa.startAttack();
						break;
//					case ExtraAction.ATTACK_CHARGED:
//						pa.setAttackCharging(false);
//						pa.setAttackChargeTime(ea.chargeTime);
//						pa.attackCharged();
//						pa.setAttackChargeTime(0);
//						break;
					case ExtraAction.EVADE:
						pa.evade();
						break;
					case ExtraAction.STAND:
						pa.stand();
						break;
					case ExtraAction.STOP_LEFT:
						pa.stopLeft();
						break;
					case ExtraAction.STOP_RIGHT:
						pa.stopRight();
						break;
				}
				break;
		}
	}

	public void banIP(String hostAddress)
	{
		bannedIps.add(hostAddress);
		FileHelper.saveArrayListToFile(bannedIps, "Server/" , "banned-ips.txt");
	}

	public void pardonAll()
	{
		bannedIps.clear();
	}
	
	public void pardonLast()
	{
		bannedIps.remove(bannedIps.size()-1);
		FileHelper.saveArrayListToFile(bannedIps, "Server/" , "banned-ips.txt");
	}
	
	public CopyOnWriteArrayList<ClientConnection> getClients()
	{
		return clients;
	}

	public ServerGame getGame()
	{
		return game;
	}
	
	public int getMaxPlayers()
	{
		return maxPlayers;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}

class ClientConnection
{
	Socket connection;
	ObjectInputStream input;
	ObjectOutputStream output;
	Server server;
	Runnable listen;
	boolean listening;
	int playerNumber;
	
	public ClientConnection(final Server server, Socket connection)
	{
		this.playerNumber = -1;
		this.connection = connection;
		this.server = server;
		try
		{
			connection.setTcpNoDelay(true);
		}
		catch (SocketException e1)
		{
			e1.printStackTrace();
		}
		try
		{
			output = new ObjectOutputStream(connection.getOutputStream());
			input = new ObjectInputStream(connection.getInputStream());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		listen = new Runnable(){
			@Override
			public void run()
			{
				listening = true;
				Packet packet = null;
				do
				{
					try
					{
						packet = (Packet)input.readObject();
						server.handlePacket(packet, ClientConnection.this);//TODO client timeout
					}
					catch (ClassNotFoundException | IOException e)
					{
						if(playerNumber != -1 && server.getGame().getPlayers()[playerNumber] != null)
						{
							server.removePlayer(playerNumber, ExtraDisconnect.ERROR);
							System.out.println("Removed player " + playerNumber + " due to packet receive error. Probably closed client.");
						}
						else
							server.removeClient(ClientConnection.this, ExtraDisconnect.ERROR);
						//e.printStackTrace();
					}
				}while(listening && !ClientConnection.this.connection.isClosed());
			}
		};
		listen();
	}
	
	public synchronized void send(Packet p)
	{
		try
		{
			output.writeObject(p);
		}
		catch (IOException e)
		{
			if(playerNumber != -1 && server.getGame().getPlayers()[playerNumber] != null)
			{
				server.removePlayer(playerNumber, ExtraDisconnect.ERROR);
				System.out.println("Removed player " + playerNumber + " due to packet send error. Probably closed client.");
			}else
				server.removeClient(ClientConnection.this, ExtraDisconnect.ERROR);
			//e.printStackTrace();
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
		try
		{
			connection.close();
			output.close();
			input.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
