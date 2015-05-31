package np.supermagicalloveparty.server;

import java.awt.Color;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import np.supermagicalloveparty.game.Entity;
import np.supermagicalloveparty.game.EntityBird;
import np.supermagicalloveparty.game.EntityBunny;
import np.supermagicalloveparty.game.EntityPanda;
import np.supermagicalloveparty.game.EntityUnicorn;
import np.supermagicalloveparty.game.FileHelper;
import np.supermagicalloveparty.game.Frame;
import np.supermagicalloveparty.game.Game;
import np.supermagicalloveparty.game.Level;
import np.supermagicalloveparty.game.Player;
import np.supermagicalloveparty.game.Settings;
import np.supermagicalloveparty.game.StringHelper;

public class ServerGame extends Game
{
	public Server server;
	public ConcurrentHashMap<Integer, Entity> synchronizedEntities;
	private TextUi textUi;
	private boolean customLevel;

	public ServerGame(Server server, Frame frame, Mode mode) throws URISyntaxException
	{
		this(server, frame, mode, new Level("MP", 160, 90, FileHelper.loadTerrain(ServerGame.class.getResourceAsStream("/Terrain.txt"), 160, 90), 
				FileHelper.loadEntities(ServerGame.class.getResourceAsStream("/Entities.txt"), 160, 90),
				FileHelper.loadImage("/background.png"),
				FileHelper.loadImage("/backgroundSpecial.png")));
		customLevel = false;
	}
	
	public ServerGame(Server server, Frame frame, Mode mode, Level level)
	{
		super(frame, level, new String[server.getMaxPlayers()], new String[server.getMaxPlayers()], level.hasOwnSettings() ? level.getSettings():new Settings(false, false, false, 50, 50, 20, 50, 50), mode);
		customLevel = true;
		synchronizedEntities = new ConcurrentHashMap<Integer, Entity>();
		this.server = server;
	}
	public void init()
	{
		super.init();
		textUi = new TextUi(this);
		frame.remove(canvas);
		frame.add(textUi);
		resume();
//		state = State.LOADING;
//		strings = new Strings(this);
//		if(settings.isSpawnPowerups())
//			powerupGenerator = new PowerupGenerator(this);
//		gameLoop = new GameLoop(this);
//		state = State.INITILIZED;
//		start();
	}
	
	@Override
	public void update()
	{
		super.update();
		server.updateClients();
	}
	
	@Override
	public void updateFrame(double interpolation)
	{
		super.updateFrame(interpolation);
	}
	
	@Override
	public boolean isDisplaying()
	{
		return super.isDisplaying()&&debug;
	}
	
	public void addPlayer(String character, String name, int n, ClientConnection c)
	{
		if(name.equals("Player"))
			name = "Player " + (n+1);
		for(String s : playerNames)
		{
			if(s != null && s.equals(name))
				name += "(2)";
		}
		playerNames[n] = name;
		Player player = null;
		switch(character)
		{
			case "Unicorn":
				player = (new EntityUnicorn(this, level.getSpawns()[n].getX(), level.getSpawns()[n].getY(), null, n));
				break;
			case "Panda":
				player = (new EntityPanda(this, level.getSpawns()[n].getX(), level.getSpawns()[n].getY(), null, n));
				break;
			case "Bird":
				player = (new EntityBird(this, level.getSpawns()[n].getX(), level.getSpawns()[n].getY(), null, n));
				break;
			case "Bunny":
				player = (new EntityBunny(this, level.getSpawns()[n].getX(), level.getSpawns()[n].getY(), null, n));
				break;
		}
		players[n] = player;
		numPlayers++;
		server.playerSpawn(player, c);
	}
	
	public void removePlayer(int n)
	{
		super.removePlayer(n);
	}
	
	@Override
	public void entityDestroyed(Entity entity)
	{
		super.entityDestroyed(entity);
		if(entity.synchIndex != -1)
		{
			synchronizedEntities.remove(entity.synchIndex);
			server.sendAll(new Packet(Packet.ENTITY_UPDATE, new ExtraEntityUpdate(entity.synchIndex, -1, -1)));
		}
	}
	
	public void addSynchedEntity(Entity entity)
	{
		int synchIndex = getNextSynchKey();
		entity.setSynchIndex(synchIndex);
		synchronizedEntities.put(synchIndex, entity);
		server.sendAll(new Packet(Packet.ENTITY_SPAWN, entity.getExtraSpawn()));				
	}
	
	public int getNextSynchKey()
	{
		int i = 0;
		while(true)
		{
			if(!synchronizedEntities.containsKey(i))
				return i;
			i++;
		}
	}
	
	@Override
	public void sendMessage(String input)
	{
		super.sendMessage(input);
		server.sendAll(new Packet(Packet.MESSAGE, new ExtraMessage(input, -1)));
	}
	
	@Override
	public void doCommand(String input)
	{
		if(input.equalsIgnoreCase("/debug"))
		{
			if(!debug)
			{
				frame.remove(textUi);
				frame.setGameCanvas(canvas);
				canvas.createBufferStrategy(2);
				doubleBuffer = canvas.getBufferStrategy();
			}else
			{
				frame.remove(canvas);
				frame.add(textUi);
			}
			super.doCommand(input);
		}
		else if(input.equalsIgnoreCase("/quit") || input.equalsIgnoreCase("/stop"))
		{
			for(int i = 0; i < players.length; i++)
			{
				if(players[i] != null)
					server.getConnectionByPlayerNumber(i).send(new Packet(Packet.DISSCONECT, new ExtraDisconnect(i, ExtraDisconnect.SERVER_CLOSE)));
			}
			super.doCommand(input);
		}
		else if(input.toLowerCase().equals("/list") || input.toLowerCase().equals("/who") || input.toLowerCase().equals("players"))
		{
			String playerString = "";
			for(Player p : players)
			{
				if(p != null)
				{
					playerString += "[" + p + "] ";
				}
			}
			consoleLog("Online Players: " + playerString, Color.WHITE, false);
		}
		else if(input.toLowerCase().startsWith("/ban-ip "))
		{
			String[] args = input.split(" ");
			if(args.length >= 2)
			{
				String name = StringHelper.combineStrings(args, 1);
				int n = StringHelper.getIndexInArray(name, playerNames);
				if(n == -1)
				{
					consoleLog("ERROR: " + name + " could not be found.", Color.RED, false);
					return;
				}
				server.banIP(server.getConnectionByPlayerNumber(n).connection.getInetAddress().getHostAddress());
				server.removePlayer(n, ExtraDisconnect.KICK);
				consoleLog(name + " has been BANNED.", Color.BLACK, false);
			}
		}
		else if(input.toLowerCase().startsWith("/respawn "))
		{
			String[] args = input.split(" ");
			if(args.length >= 2)
			{
				String name = StringHelper.combineStrings(args, 1);
				int n = StringHelper.getIndexInArray(name, playerNames);
				if(n == -1)
				{
					consoleLog("ERROR: " + name + " could not be found.", Color.RED, false);
					return;
				}
				players[n].respawn();
				consoleLog(name + " has been respawned.", Color.GREEN, false);
			}
		}
		else if(input.equalsIgnoreCase("/restart"))
		{
			gameOver(null, true);
			server.sendAll(new Packet(Packet.COMMAND, new ExtraCommand(ExtraCommand.RESTART)));
		}
		else if(input.toLowerCase().startsWith("/kick "))
		{
			String[] args = input.split(" ");
			if(args.length >= 2)
			{
				String name = StringHelper.combineStrings(args, 1);
				int n = StringHelper.getIndexInArray(name, playerNames);
				if(n == -1)
				{
					consoleLog("ERROR: " + name + " could not be found.", Color.RED, false);
					return;
				}
				server.removePlayer(n, ExtraDisconnect.KICK);
				consoleLog(name + " has been kicked.", Color.GREEN, false);
			}
		}
		else if(input.toLowerCase().startsWith("/set points "))
		{
			String[] args = input.split(" ");
			if(args.length >= 4)
			{
				String name = StringHelper.combineStrings(args, 3);
				double points;
				try{
					points = Double.parseDouble(args[2]);
				}catch(NumberFormatException e)
				{
					consoleLog("ERROR: " + args[2] + " is not a number! Syntax: /set points [POINTS] [PLAYER]", Color.RED, false);
					return;
				}
				int n = StringHelper.getIndexInArray(name, playerNames);
				if(n == -1)
				{
					consoleLog("ERROR: " + name + " could not be found.", Color.RED, false);
					return;
				}
				players[n].setPoints(points);
				if(players[n].getPoints() >= players[n].getMaxPoints())
					players[n].onPointsFull();
				else if(players[n].getPoints() <= 0)
					players[n].onPointsEmpty();
				consoleLog(name + " now has " + points + " points!", Color.GREEN, false);
			}
		}
		else if(input.equalsIgnoreCase("/pardon-all"))
		{
			FileHelper.deleteFile(Game.BASE_DIRECTORY + "Server/banned-ips.txt");
			server.pardonAll();
		}
		else if(input.toLowerCase().startsWith(("/pardon ")))
		{
			String[] args = input.split(" ");
			double n = 1;
			if(args.length >= 2)
			{
				try{
					n = Double.parseDouble(args[1]);
				}catch(NumberFormatException e)
				{
					consoleLog("ERROR: " + args[1] + " is not a number! Syntax: /pardon (# previous players to unban)", Color.RED, false);
					return;
				}
				consoleLog(n + " players have been pardoned!", Color.GREEN, false);
			}
			else
				consoleLog("Player has been pardoned!", Color.GREEN, false);
			for(int i = 0; i < n; i++)
				server.pardonLast();
		}
		else if(input.toLowerCase().startsWith("/give "))
		{
			String[] args = input.split(" ");
			if(args.length >= 3)
			{
				String name = StringHelper.combineStrings(args, 2);
				String powerup = args[1].toLowerCase();
				int n = StringHelper.getIndexInArray(name, playerNames);
				if(n == -1)
				{
					consoleLog("ERROR: " + name + " could not be found.", Color.RED, false);
					return;
				}
				Player p = players[n];
				switch(powerup)
				{
					case "speed":
						server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.SPEED)));
						p.powerupSpeed();
						break;
					case "special":
						server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.SPECIAL)));
						p.powerupSpecialAttack();
						break;
					case "invincible":
						server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.INVINCIBLE)));
						p.powerupInvincible();
						break;
					case "strength":
						server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.STRENGTH)));
						p.powerupDamage();
						break;
					case "health":
						server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.HEALTH)));
						p.powerupHealth();
						break;
				}
				consoleLog(name + " has been given " + powerup, Color.GREEN, false);
			}
		}else if(input.toLowerCase().startsWith("/streamline "))
		{
			String[] args = input.split(" ");
			if(args.length >= 2)
			{
				String name = StringHelper.combineStrings(args, 1);
				int n = StringHelper.getIndexInArray(name, playerNames);
				if(n == -1)
				{
					consoleLog("ERROR: " + name + " could not be found.", Color.RED, false);
					return;
				}
				server.getConnectionByPlayerNumber(players[n].getNumber()).send(new Packet(Packet.COMMAND, new ExtraCommand(ExtraCommand.STREAMLINE)));
				consoleLog(name + " is now listening to Streamine at full volume!", Color.GREEN, false);
			}
		}
		else
			super.doCommand(input);
	}
	
	@Override
	public void consoleLog(String s, Color color, boolean playerMessage)
	{
		if(debug)
			super.consoleLog(s, color, playerMessage);
		else
			textUi.log(s);
	}
	
	@Override
	public void gameOver(Player p, boolean instant)
	{
		if(p != null)
		{
			server.sendAll(new Packet(Packet.COMMAND, new ExtraCommand(ExtraCommand.GAME_OVER, p.getNumber(), instant)));
		}
		super.gameOver(p, instant);
		if(instant)
		{
			pause();
			reset();
			guiCountdown.start(true);
		}
	}
	
	@Override
	public void stop()
	{
		server.close();
		super.stop();
	}
	
	@Override
	public boolean isUpdating()
	{
		return true;
	}
	
	public boolean hasCustomLevel()
	{
		return customLevel;
	}

}
