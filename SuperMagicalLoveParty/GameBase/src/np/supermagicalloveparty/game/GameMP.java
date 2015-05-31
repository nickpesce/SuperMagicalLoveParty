package np.supermagicalloveparty.game;

import java.awt.Color;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.swing.JOptionPane;

import np.supermagicalloveparty.server.ExtraAction;
import np.supermagicalloveparty.server.ExtraCommand;
import np.supermagicalloveparty.server.ExtraDisconnect;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ExtraEntityUpdate;
import np.supermagicalloveparty.server.ExtraJoinRequest;
import np.supermagicalloveparty.server.ExtraJoinResponse;
import np.supermagicalloveparty.server.ExtraLevelData;
import np.supermagicalloveparty.server.ExtraMessage;
import np.supermagicalloveparty.server.ExtraPhysics;
import np.supermagicalloveparty.server.ExtraPowerupAquire;
import np.supermagicalloveparty.server.ExtraSpawn;
import np.supermagicalloveparty.server.Packet;


public class GameMP extends Game
{
	PacketManager packetManager;
	long pingStartTime;
	int myPlayerNumber;
	public static final int MAX_PLAYERS = 9;
	public HashMap<Integer, Entity> synchronizedEntities;
	
	public GameMP(Frame frame, final String character, final String playerName, Mode mode,  final String ip, final int port) throws URISyntaxException
	{
		super(frame, 
				new Level("MP", 160, 90, FileHelper.loadTerrain(GameMP.class.getResourceAsStream("/Terrain.txt"), 160, 90), 
						FileHelper.loadEntities(GameMP.class.getResourceAsStream("/Entities.txt"), 160, 90),
						FileHelper.loadImage("/background.png"),
						FileHelper.loadImage("/backgroundSpecial.png")),
				new String[MAX_PLAYERS], new String[MAX_PLAYERS], getSemiStandardizedSettings(), mode);
		synchronizedEntities = new HashMap<Integer, Entity>();
		multiplayer = true;
		init();
		new Thread(new Runnable(){

			@Override
			public void run()
			{
				packetManager = new PacketManager(GameMP.this, ip, port);
				System.out.println("Sending join request");
				packetManager.send(new Packet(Packet.JOIN_REQUEST, new ExtraJoinRequest(character, playerName, Game.VERSION)));//TODO multiple players on one comp
			}
		}).start();
	}

	private static Settings getSemiStandardizedSettings()
	{
		Settings userSettings = new Settings(Game.BASE_DIRECTORY);
		FileHelper.loadSettings(userSettings);
		return new Settings(userSettings.isFullscreenMode(), userSettings.isCamera(), true, 0, 50, 20, userSettings.getMusicVolume(), userSettings.getSfxVolume());
	}

	@Override
	public void init()
	{
		super.init();
		state = State.CONNECTING;
	}
	public void addPlayerMP(String character, String name, int n)
	{
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
		addPlayerMP(player);
	}
	
	public void addPlayerMP(Player p)
	{
		players[p.getNumber()] = p;
		numPlayers++;
	}
	
	@Override
	public void update()
	{
		super.update();
		//if(numPlayers <= myPlayerNumber)
		//	return;
		//Player p = players[myPlayerNumber];
		//packetManager.send(new Packet(Packet.PHYSICS, new ExtraPhysics(myPlayerNumber, p.getX(), p.getY(), p.getHealth())));//, p.getState())));//p.getVx(), p.getVy(), p.getAx(), p.getAy())));
	}
	
	public void handlePacket(Packet p)
	{
		//System.out.println("Client recieved: " + p);
		Player player;//MULTI USE
		switch(p.getType())
		{
			case Packet.JOIN_RESPONSE:
				ExtraJoinResponse ejr = (ExtraJoinResponse)(p.getInfo());
				if(!ejr.isAllowed())
				{
					switch(ejr.getPlayerNumber())
					{
						case ExtraJoinResponse.FULL:
							JOptionPane.showMessageDialog(null, "The server is full!");
							break;
						case ExtraJoinResponse.VERSION:
							JOptionPane.showMessageDialog(null, "Client/Server version mismatch!");
							break;
						case ExtraJoinResponse.BANNED:
							JOptionPane.showMessageDialog(null, "You are banned from this server!");
							break;
					}
					stop();
					return;
				}
				mode = ejr.getMode();
				myPlayerNumber =  ejr.getPlayerNumber();
				addPlayer(ejr.getCharacter(), ejr.getPlayerName(), myPlayerNumber, KeyMap.getPlayerKeysFromFile(0));
				resume();
				break;
			case Packet.LEVEL_DATA:
				ExtraLevelData eld = (ExtraLevelData)p.getInfo();
				state = State.DOWNLOADING;
				if(eld.isHasSettings())
				{
					boolean cameraSetting = eld.isCamera();
					int musicVolume = eld.getMusicVolume();
					int sfxVolume = eld.getSfxVolume();
					Settings oldSettings = new Settings(Game.BASE_DIRECTORY);
					FileHelper.loadSettings(oldSettings);
					if(cameraSetting != oldSettings.isCamera())
					{
						String turn = cameraSetting? "on":"off";
						if(JOptionPane.showConfirmDialog(frame, "This server recommends that the auto-camera is turned " + turn +". Do you want to turn it " + turn + "?", "Auto Camera", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
						{
							cameraSetting = oldSettings.isCamera();
						}
					}
					if(musicVolume != oldSettings.getMusicVolume() || sfxVolume != oldSettings.getSfxVolume())
					{
						if(JOptionPane.showConfirmDialog(frame, "The recommended volume settings for this server are different from your currently set levels. Would you like to adjust them to match the server?", "Volume", JOptionPane.YES_NO_OPTION)==JOptionPane.NO_OPTION)
						{
							musicVolume = oldSettings.getMusicVolume();
							sfxVolume = oldSettings.getSfxVolume();
						}
					}
					if(settings.isFullscreenMode() != oldSettings.isFullscreenMode()){
						frame.setFullScreen(settings.isFullscreenMode());
						canvas.createBufferStrategy(2);
						doubleBuffer = canvas.getBufferStrategy();
					}
					settings = new Settings(oldSettings.isFullscreenMode(), cameraSetting, eld.isShowTiles(), eld.getPowerups(), eld.getGravity(), eld.getSpeed(), musicVolume, sfxVolume);
					Game.soundManager.setSfxVolume(sfxVolume);
					Game.soundManager.setMusicVolume(musicVolume);
				}
				level = new Level("Downloaded MP Map", eld.getWidth(), eld.getHeight(), eld.getTerrain(), eld.getEntities(), eld.getBackground(), eld.getBackgroundSpecial()==null?eld.getBackground():eld.getBackgroundSpecial(), settings);
				gameLoop.setActualSpeed(settings.getSpeed());
				canvas.updateScale(frame);
				break;
			case Packet.MESSAGE:
				ExtraMessage em = (ExtraMessage)p.getInfo();
				consoleLog("[" + (em.getSender()==-1? "SERVER":playerNames[em.getSender()]) + "]" + em.getMessage() , Color.WHITE, true);
				if(((ExtraMessage)p.getInfo()).getMessage().equals("PONG!"))
				{
					consoleLog("Your ping is " + (System.currentTimeMillis() - pingStartTime) + " ms", Color.BLUE, false);
				}
				break;
			case Packet.DISSCONECT:
				ExtraDisconnect ed = (ExtraDisconnect)p.getInfo();
				if(ed.getPlayerNumber() == myPlayerNumber)
				{
					stop();
					switch(ed.getReason())
					{
						case ExtraDisconnect.KICK:
							JOptionPane.showMessageDialog(frame, "You have been kicked :D");
							break;
						case ExtraDisconnect.SERVER_CLOSE:
							JOptionPane.showMessageDialog(frame, "The server has been closed.");
							break;
					}
				}
				else
				{
					switch(ed.getReason())
					{
						case ExtraDisconnect.KICK:
							consoleLog("[INFO] " + playerNames[ed.getPlayerNumber()] + " was kicked from the game." , Color.CYAN, true);
							break;
						case ExtraDisconnect.ERROR:
							//consoleLog("[INFO] " + playerNames[ed.getPlayerNumber()] + " left the game due to an error." , Color.CYAN, true);
							break;
						case ExtraDisconnect.LEAVE:
							consoleLog("[INFO] " + playerNames[ed.getPlayerNumber()] + " left the game." , Color.CYAN, true);
							break;
						case ExtraDisconnect.TIMEOUT:
							consoleLog("[INFO] " + playerNames[ed.getPlayerNumber()] + " lagged out of the game." , Color.CYAN, true);
							break;
						default:
							consoleLog("[INFO]" + playerNames[ed.getPlayerNumber()] + " left the game." , Color.CYAN, true);

					}
					removePlayer(ed.getPlayerNumber());
				}
				break;
			case Packet.SPAWN:
				ExtraSpawn e = (ExtraSpawn)(p.getInfo());
				addPlayerMP(e.getCharacter(), e.getName(), e.getN());
				break;
			case Packet.PHYSICS:
				ExtraPhysics ep = (ExtraPhysics)p.getInfo();
				player = players[ep.getPlayerNumber()];
				if(player == null)
					return;
				player.setX(ep.getX());
				player.setY(ep.getY());
				player.setPoints(ep.getHealth());
				if(ep.getPlayerNumber()!=myPlayerNumber)
				{
					player.setVx(ep.getvX());
					player.setVy(ep.getvY());
					player.setAx(ep.getaX());
					player.setAy(ep.getaY());
				}
				break;
			case Packet.ACTION:
				ExtraAction ea = (ExtraAction)p.getInfo();
				Player pa = players[ea.getPlayerNumber()];
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
					case ExtraAction.SQUAT:
						pa.squat();
						break;
					case ExtraAction.LAND:
						pa.land();
						break;
					case ExtraAction.ATTACK:
						pa.attackChargeTime = ea.getChargeTime();
						pa.doAttack();
						break;
					case ExtraAction.ATTACK_START:
						pa.startAttack();
						break;
//					case ExtraAction.ATTACK_CHARGED:
//						pa.attackCharging = false;
//						pa.attackChargeTime = ea.getChargeTime();
//						pa.attackCharged();
//						pa.attackChargeTime = 0;
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
			case Packet.ENTITY_UPDATE:
				ExtraEntityUpdate euu = (ExtraEntityUpdate)p.getInfo();
				Entity e2u = synchronizedEntities.get(euu.getIndex());
				if(e2u == null)
					return;
				if(euu.getX() == -1)
				{
					synchronizedEntities.remove(e2u.getSynchIndex());
					e2u.destroy();
					return;
				}
				e2u.setX(euu.getX());
				e2u.setY(euu.getY());
				break;
			case Packet.ENTITY_SPAWN:
				ExtraEntitySpawn ees = ((ExtraEntitySpawn)p.getInfo());
				ees.getEntityData().spawnEntityInGame(this);
				break;
			case Packet.POWERUP_AQUIRE:
				ExtraPowerupAquire epa = ((ExtraPowerupAquire)p.getInfo());
				switch(epa.getPowerupType())
				{
					case ExtraPowerupAquire.HEALTH:
						players[epa.getPlayerNumber()].powerupHealth();
						break;
					case ExtraPowerupAquire.SPEED:
						players[epa.getPlayerNumber()].powerupSpeed();
						break;
					case ExtraPowerupAquire.STRENGTH:
						players[epa.getPlayerNumber()].powerupDamage();
						break;
					case ExtraPowerupAquire.SPECIAL:
						players[epa.getPlayerNumber()].powerupSpecialAttack();;
						break;
					case ExtraPowerupAquire.INVINCIBLE:
						players[epa.getPlayerNumber()].powerupInvincible();;
						break;
					case ExtraPowerupAquire.CARROT:
						player = players[epa.getPlayerNumber()];
						if(player instanceof EntityBunny)
							((EntityBunny)player).setVegetable(EntityBunny.CARROT);
						break;
					case ExtraPowerupAquire.CABBAGE:
						player = players[epa.getPlayerNumber()];
						if(player instanceof EntityBunny)
							((EntityBunny)player).setVegetable(EntityBunny.CABBAGE);
						break;
					case ExtraPowerupAquire.TURNIP:
						player = players[epa.getPlayerNumber()];
						if(player instanceof EntityBunny)
							((EntityBunny)player).setVegetable(EntityBunny.TURNIP);
						break;
				}
				break;
			case Packet.COMMAND:
				ExtraCommand ec = ((ExtraCommand)p.getInfo());
				switch(ec.command)
				{
					case ExtraCommand.GAME_OVER:
						gameOver(players[ec.n], ec.flag);
						break;
					case ExtraCommand.RESTART:
						gameOver(null, true);
						break;
					case ExtraCommand.STREAMLINE:
						soundManager.setMusic(SoundManager.MUSIC_STREAMLINE);
						SoundManager.setVolume(soundManager.music, 6.0206f);
						break;
				}
				break;
		}
	}
	
	@Override
	public void sendMessage(String message)
	{
		//super.sendMessage(message);
		packetManager.send(new Packet(Packet.MESSAGE, new ExtraMessage(message, players[myPlayerNumber].getNumber())));
	}
	
	@Override
	public void doCommand(String input)
	{
		if(input.equalsIgnoreCase("/ping"))
		{
			consoleLog("Pinging server...", Color.BLUE, false);
			packetManager.send(new Packet(Packet.MESSAGE, new ExtraMessage("/ping", myPlayerNumber)));
			pingStartTime = System.currentTimeMillis();
		}else
			super.doCommand(input);
	}
	@Override
	public void stop()
	{
		if(packetManager != null)
			packetManager.close();
		super.stop();
	}

	@Override
	public void gameOver(Player p, boolean instant)
	{
		super.gameOver(p, instant);
		if(instant)
		{
			pause();
			reset();
			guiCountdown.start(true);
		}
	}
	public PacketManager getPacketManager()
	{
		return packetManager;
	}
}
