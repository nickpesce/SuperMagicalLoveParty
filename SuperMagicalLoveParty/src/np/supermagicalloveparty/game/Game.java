package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import np.supermagicalloveparty.server.ServerGame;


/**
 * @author Nick Pesce
 *
 */
public class Game implements GameLoopable{


	public static final String NAME = "Super Magical Love Party!";
	public static final String BASE_DIRECTORY = System.getProperty("user.home")+"/.supermagicalloveparty/Settings/";
	public static final String VERSION = "0.090";
	public static final int DEFAULT_GRAVITY = 50;
	/**
	 * shows terrain tiles and spawn points
	 */
	protected boolean debug = false;
	boolean multiplayer;
	protected BufferStrategy doubleBuffer;
	protected Frame frame;
	InputHandler input;
	public TextureManager textures;
	protected GameCanvas canvas;
	public Camera camera;
	protected GameLoop gameLoop;
	protected PowerupGenerator powerupGenerator;
	protected static SoundManager soundManager;
	protected Level level;
	protected Strings strings;
	protected Settings settings;
	protected GuiIngame guiIngame;
	protected GuiControls guiControls;
	public GuiConsole guiConsole;
	protected GuiIngameCountdown guiCountdown;
	protected GuiIngameGameOver guiGameOver;
	protected FontMetrics fontMetrics;
	public int fps, tps;
	public boolean specialMode = false;
	protected CopyOnWriteArrayList<Drawable> drawables = new CopyOnWriteArrayList<Drawable>();
	protected CopyOnWriteArrayList<Collidable> collidables = new CopyOnWriteArrayList<Collidable>();
	protected CopyOnWriteArrayList<Physicsable> physicsables = new CopyOnWriteArrayList<Physicsable>();
	protected CopyOnWriteArrayList<Updatable> updatables = new CopyOnWriteArrayList<Updatable>();
	protected ArrayList<Entity> powerups = new ArrayList<Entity>();
	protected Player[] players;
	private Player winner;
	protected int numPlayers;
	protected String[] characters;
	protected String[] playerNames;
	protected boolean ending;
	
	public static enum Mode
	{
		SATISFACTION, HEALTH
	}
	public Mode mode;
	
	protected enum State
	{
		PLAYING, PAUSED, GAME_OVER, INITILIZED, LOADING, ANIMATING, CLOSING, COUNTDOWN, CONNECTING, DOWNLOADING
	}
	public State state;
	
	public enum Gui
	{
		NONE, MENU, CONTROLS, CONSOLE, COUNTDOWN, GAME_OVER
	}
	
	static{
		soundManager = new SoundManager(50, 50);
	}
	public Gui activeGui;	
	public Game(Frame frame, Level level, String[] characters, String[] playerNames, Settings settings, Mode mode)//, String levelName, int width, int height, String[] playerCharacterStrings, String[] playerNames)
	{
		this.level = level;
		this.frame = frame;
		this.characters = characters;
		this.playerNames = playerNames;
		this.settings = settings;
		this.mode = mode;
	}
	
	public void init()
	{
		state = State.LOADING;
		players = new Player[playerNames.length];
		activeGui = Gui.NONE;
		camera = new Camera(this);
		canvas = new GameCanvas(this);
		frame.setGameCanvas(canvas);
		textures = new TextureManager(this);
		textures.init();
		strings = new Strings(this);
		input = new InputHandler(this);
		canvas.createBufferStrategy(2);
		doubleBuffer = canvas.getBufferStrategy();
		canvas.requestFocusInWindow();
		canvas.addKeyListener(input);
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		//updateFrame();
		guiIngame = new GuiIngame(this);
		guiControls = new GuiControls(this);
		guiConsole = new GuiConsole(this);
		guiCountdown = new GuiIngameCountdown(this);
		guiGameOver = new GuiIngameGameOver(this);
		//****
		//level.setGame(this);
		if(characters!=null&&playerNames!=null)
			addPlayers(characters, playerNames);
		if(settings.isFullscreenMode())
			frame.setFullScreen(true);
		if(settings.getPowerups()>0)
			powerupGenerator = new PowerupGenerator(this);
		
		soundManager.setMusicVolume(settings.getMusicVolume());
		soundManager.setSfxVolume(settings.getSfxVolume());
		gameLoop = new GameLoop(this);
		gameLoop.startGame(settings.getSpeed());
		state = State.INITILIZED;
		if(!(this instanceof ServerGame))
		{
			soundManager.setMusic(SoundManager.MUSIC_LOOP);
			if(!multiplayer)
			{
				guiCountdown.start(true);
			}
		}
	}
	
	/**
	 * update game physics, updatables, and camera. Only to be called from main game loop.
	 */
	@Override
	public void update()
	{
		if(state.equals(Game.State.ANIMATING))
			progressAnimation();
		if(activeGui.equals(Gui.COUNTDOWN))
			guiCountdown.update();
		updateUpdatables();
		updatePhysicsables();
		guiConsole.updateChatTimes();
		//updateCollidables();
	}
	
	/**
	 * updates faster than update(). optimally, at the same speed as the fps. Only for small calculations.
	 */
	@Override
	public void fineUpdate(double interpolation)
	{
		if(isUsingCamera())
		{
			camera.update(interpolation);
			canvas.cameraX = camera.getOffsetX();
			canvas.cameraY = camera.getOffsetY();
			canvas.cameraZoom = camera.getZoom();
			canvas.scaleWithCamera = canvas.scale*canvas.cameraZoom;
		}
		updateUpdatablesFine(interpolation);
	}
	
	private void updateUpdatables()
	{
		for(Updatable u : updatables)
			u.update();
	}
	
	private void updateUpdatablesFine(double interpolation)
	{
		for(Updatable u : updatables)
			u.fineUpdate(interpolation);
	}
	
	private void updatePhysicsables()
	{
		for(Physicsable p : physicsables)
			p.updatePhysics();
	}
	
	/**
	 * update game graphics
	 * @param interpolation 
	 */
	@Override
	public void updateFrame(double interpolation)
	{
		try
		{
			Graphics g;
			g = doubleBuffer.getDrawGraphics();
			canvas.render(g, interpolation);
			fontMetrics = g.getFontMetrics();//TODO get font metrics every frame??
			g.dispose();
			doubleBuffer.show();
		}catch(Exception e)
		{
			System.out.println("Tried to draw, but cant! QQ");
			e.printStackTrace();
		}
	}
	
	public void progressAnimation()
	{
		if(ending)
			return;
		final Point center = new Point((level.getWidth()/2.0), (level.getHeight()/2.0)-(winner.getHeight()/2.0));
		double dX = (center.getX() - winner.getX());
		double dY = (center.getY() - winner.getY());
//		if(dX < 1 && dX > 0)dX = 1;
//		else if((dX < 0 && dX > -1)) dX = -1;
//		if(dY < 1 && dY > 0)dY = 1;
//		else if((dY < 0 && dY > -1)) dY = -1;

		winner.setScaledVx(dX);
		winner.setScaledVy(dY);
		
		if(Math.abs((center.y - winner.getY()))<.5 && Math.abs(center.x - winner.getX())<.5)
		{
			ending = true;
			if(!specialMode)
			{
				for(double x = center.getX()-20; x < center.getX()+20; x+=.1)
				{
					Particle.spawnMany(Game.this, x + (winner.getWidth()/2.0), -Math.sqrt(400-(Math.pow(x-center.getX(), 2))) + winner.getY() + winner.getHeight(), 10, Particle.Type.RAINBOW, false);
				}
			}
			winner.visible=false;
			for(double x = center.getX(); x < center.getX() + winner.getWidth(); x+=.3)
			{
				for(double y = center.getY(); y < center.getY() + winner.getWidth(); y+=.3)
					Particle.spawnMany(Game.this, x, y, 4, specialMode? Particle.Type.RED_GOO : Particle.Type.FAIRY_DUST);
			}
			soundManager.playSound(SoundManager.MENU_SPARKLE);
			if(!multiplayer)
				endGameAfterTime(2000);
		}
	}
	
	public void endGameAfterTime(final int millis)
	{
		ending = true;
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					Thread.sleep(millis);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				gameOver(winner, true);
				ending = false;
			}
		}).start();
		
	}
	/**
	 * Creates and adds game characters to the game on game start.
	 * @param characters Array of character names of players. ex. "Unicorn"
	 * @param playerNames Array of player names associated with the same indexed character. ex. "Nicky"
	 */
	private void addPlayers(String[] characters, String[] playerNames)
	{
		for(int i = 0; i < characters.length; i++)
		{
			if(characters[i]==null)
				continue;
			switch(characters[i])
			{
				case "Unicorn":
					addPlayer(new EntityUnicorn(this, level.getSpawns()[i].getX(), level.getSpawns()[i].getY(), KeyMap.getPlayerKeysFromFile(i), i));
					break;
				case "Panda":
					addPlayer(new EntityPanda(this, level.getSpawns()[i].getX(), level.getSpawns()[i].getY(), KeyMap.getPlayerKeysFromFile(i), i));
					break;
				case "Bird":
					addPlayer(new EntityBird(this, level.getSpawns()[i].getX(), level.getSpawns()[i].getY(), KeyMap.getPlayerKeysFromFile(i), i));
					break;
				case "Bunny":
					addPlayer(new EntityBunny(this, level.getSpawns()[i].getX(), level.getSpawns()[i].getY(), KeyMap.getPlayerKeysFromFile(i), i));
					break;
			}
		}
	}
	
	public void addPlayer(String character, String name, int number, KeyMap keys)
	{
		playerNames[number] = name;
		switch(character)
		{
			case "Unicorn":
				addPlayer(new EntityUnicorn(this, level.getSpawns()[number].getX(), level.getSpawns()[number].getY(), keys, number));
				break;
			case "Panda":
				addPlayer(new EntityPanda(this, level.getSpawns()[number].getX(), level.getSpawns()[number].getY(), keys, number));
				break;
			case "Bird":
				addPlayer(new EntityBird(this, level.getSpawns()[number].getX(), level.getSpawns()[number].getY(), keys, number));
				break;
			case "Bunny":
				addPlayer(new EntityBunny(this, level.getSpawns()[number].getX(), level.getSpawns()[number].getY(), keys, number));
				break;
		}
	}
	
	/**
	 * adds a player to the game.
	 * @param p player to be added.
	 */
	public void addPlayer(Player p)
	{
		players[p.getNumber()] = p;
		numPlayers++;
		input.addPlayer(p, p.getKeys());
		guiControls.addPlayer(p, multiplayer);
	}
	
	public void removePlayer(int n)
	{
		if(n == -1)
			return;
		updatables.remove(players[n]);
		physicsables.remove(players[n]);
		collidables.remove(players[n]);
		drawables.remove(players[n]);
		playerNames[n] = null;
		characters[n] = null;
		players[n] = null;
		numPlayers--;
		if(numPlayers == 1)
		{
			consoleLog("I guess you kinda win?", Color.BLUE, true);
		}
	}
	
	@Override
	public void setFps(int fps) 
	{
		this.fps = fps;
	}
	
	@Override
	public void setTps(int tps) 
	{
		this.tps = tps;
	}
	
	@Override
	public boolean isUpdating()
	{
		return state.equals(Game.State.PLAYING) || state.equals(Game.State.ANIMATING) || state.equals(Game.State.COUNTDOWN);
	}
	/**
	 * Add a string to the ingame console. If string is too long, will split to multiple lines at spaces. If one word is too long, will replace with "(String too long)".
	 * @param s String to print to console.
	 * @param color Color to display string in.
	 */
	public void consoleLog(String s, Color color, boolean playerMessage)
	{
		if(fontMetrics == null)
		{
			fontMetrics = doubleBuffer.getDrawGraphics().getFontMetrics();
		}
		if(fontMetrics.stringWidth(s) > guiConsole.getWidth())
		{
			String[] words = s.split(" ");
			int totalLength = 0;
			for(int i = 0; i < words.length; i++)
			{
				if((fontMetrics.stringWidth(words[i]) + fontMetrics.stringWidth(" "))>guiConsole.getWidth())
					words[i] = "(string too long)";//TODO proper handling of long console strings.
				if((totalLength += (fontMetrics.stringWidth(words[i]) + fontMetrics.stringWidth(" "))) > guiConsole.getWidth())
				{
					String s1 = "";
					String s2 = "";
					for(int j = 0; j < i; j++)
					{
						s1 += (words[j] + " ");
					}
					for(int j = i; j < words.length; j++)
					{
						s2 += (words[j] + " ");
					}
					consoleLog(s1, color, playerMessage);
					consoleLog(s2, color, playerMessage);
					return;
				}
			}
			//If new string isnt too long anymore(after modifications above)
			String s1 = "";
			for(int i = 0; i < words.length; i++)
			{
				s1 += words[i] + " ";
			}
			consoleLog(s1, color, playerMessage);
		}else
			guiConsole.log(s, color, playerMessage);
	}
	
	public void pause()
	{
		//guiIngame = new GuiIngame(this);
		state = State.PAUSED;
		soundManager.pauseMusic();
	}
	
	public void resume()
	{
		//frame.removeGui(guiIngame);
		soundManager.resumeMusic();
		state = State.PLAYING;
		ending = false;
		if(activeGui!=Gui.COUNTDOWN)
			activeGui = Gui.NONE;
	}
	
	public void reset()
	{
		ending = false;
		for(Entity e : powerups)
		{
			e.destroy();
		}
		powerups.clear();
		for(Player p : players)
		{
			if(p!=null)
			{
				p.lives = 1;
				if(mode == Game.Mode.HEALTH)
					p.lives = 3;
				p.respawn();
			}
		}
	}

	/**
	 * stops the current game and opens the main menu.
	 */
	public void stop()
	{
		if(state.equals(State.CLOSING))
			return;
		state = State.CLOSING;
		if(gameLoop!=null)
			gameLoop.stop();
		if(specialMode)
			frame.setTitle(Game.NAME);
		if(settings.isFullscreenMode())
			frame.setFullScreen(false);
		specialMode = false;
		GuiOutOfGame menu = new GuiOutOfGame(frame);
		frame.setGui(menu);
		if(soundManager != null)
			soundManager.pauseMusic();
	}
	
	/**
	 * Negates the specialMode variable.
	 * @return true if special mode is now on
	 */
	public boolean toggleMode()
	{
		specialMode = !specialMode;
		frame.setTitle(specialMode? "Satanic Blood Orgy": Game.NAME);
		soundManager.setMusic(specialMode? SoundManager.MUSIC2_LOOP:SoundManager.MUSIC_LOOP);
		soundManager.resumeMusic();
		return specialMode;
	}
	
	/**
	 * Ends the game. Either after an animation or immediately.
	 * @param instant True if want to skip the animation and go straight to game over screen.
	 */
	public void gameOver(Player p, boolean instant)
	{
		winner = p;
		if(p!=null)
		{
			if(specialMode)
				consoleLog("[WINNER] Horay! " + p + " was filled with so much Satanic evil that he exploded in a torrent of blood!", Color.CYAN, true);
			else
				consoleLog("[WINNER] Horay! " + p + " was filled with so much satisfaction that he burst into a rainbow and ascended to Nirvana!", Color.CYAN, true);
		}
		if(!instant)
		{
			winner.setNoClip(true);
			state = State.ANIMATING;
			canvas.updateScale(frame);
			winner.collidedLeft = winner.collidedRight = winner.onGround = false;
			winner.stopRunning();
			winner.setAy(0);
			winner.setVy(0);
			winner.setVx(0);
		}
		else if(!multiplayer && !(this instanceof ServerGame))
		{
			state = State.GAME_OVER;
			activeGui = Gui.GAME_OVER;
		}
	}
	
	public boolean isUsingCamera()
	{
		return settings.isCamera() && !state.equals(State.ANIMATING);
	}
	public int checkSpecialAllowed()
	{
		 BufferedReader bufferedreader;
		try
		{
			bufferedreader = new BufferedReader(new InputStreamReader((new URL("https://www.dropbox.com/s/as9fniyvqsp5rf1/special.txt?raw=1")).openStream()));
	        String s = bufferedreader.readLine();
	        if(s.equals("1"))
	        	return 1;
	        else if(s.equals("0"))
	        	return 0;
	        else if(s.equals("3"))
	        	return 3;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getSecretKey()
	{
		return Calendar.getInstance().get(7) + System.getProperty("user.name").substring(0, 1) + (Calendar.getInstance().get(5) + 3) + "7q";
	}
	
	/**
	 * Will check string to see if fits a command. If not, will print the input to console with a "[MESSAGE]" prefix.
	 * @param input Command to execute.
	 */
	public void doCommand(String input)
	{
		if(input.equalsIgnoreCase("/quit") || input.equalsIgnoreCase("/stop"))
		{
			System.exit(0);
		}
		else if(input.toLowerCase().contains("satan"))
		{
			if(checkSpecialAllowed() != 1)
			{
				consoleLog("This \"feature\" is not enabled right now.", Color.RED, false);
				return;
			}
			if(toggleMode())
			{
				consoleLog("Hail Satan!", Color.RED, false);
			}
			else
			{
				consoleLog("Love is the answer to life <3", Color.BLUE, false);
			}
		}
		else if(input.startsWith("#") && input.equalsIgnoreCase("#" + getSecretKey()) && checkSpecialAllowed() != 0)
		{
			if(toggleMode())
			{
				consoleLog("Hail Satan!", Color.RED, false);
			}
			else
			{
				consoleLog("Love is the answer to life <3", Color.BLUE, false);
			}
		}
		else if(input.equalsIgnoreCase("/debug"))
		{
			debug = !debug;
			if(debug)
				consoleLog("Debug mode activated.", Color.GREEN, false);
			else
				consoleLog("Debug mode deactivated.", Color.GREEN, false);
		}
		else if(input.equalsIgnoreCase("/help"))
		{
			consoleLog("-----help-----", Color.GREEN, false);
			consoleLog("quit...........Exit the game", Color.GREEN, false);
			if(checkSpecialAllowed() == 1)
				consoleLog("satan......Toggle game mode. Not for Donlon use.", Color.GREEN, false);
			consoleLog("--------------", Color.GREEN, false);
		}else
		{
			sendMessage(input);
		}
	}
	
	@Override
	public boolean isDisplaying()
	{
		return true;
	}
	
	public void sendMessage(String input)
	{
		consoleLog("[MESSAGE] " + input, Color.WHITE, true);
	}
	
	public ArrayList<Entity> getPowerups()
	{
		return powerups;
	}
	
	public Camera getCamera()
	{
		return camera;
	}

	public Player[] getPlayers()
	{
		return players;
	}

	public Level getLevel()
	{
		return level;
	}
	
	public void entityDestroyed(Entity entity)
	{
	}

	public String[] getPlayerNames()
	{
		return playerNames;
	}

}
