package np.supermagicalloveparty.game;
import java.awt.CardLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JPanel;

import np.supermagicalloveparty.game.Game.Mode;
import np.supermagicalloveparty.server.Server;


public class GuiOutOfGame extends JPanel
{
	//TODO Redo all out of game guis. dont use swing components. Better scaling.
	private static final long serialVersionUID = 1L;
	JButton bStart;
	Frame frame;
	JButton bOptions;
	CardLayout layout;
	GuiMainMenu mainMenu;
	GuiGameSelect gameSelect;
	GuiLevelEditor levelEditor;
	GuiMultiplayerSelect multiplayer;
	GuiServerSetup serverSetup;
	GuiOptions options;
	GuiCredits credits;
	Settings settings;
	ArrayList<String> parentTree;
	boolean updateLevelSelect;
	
	public GuiOutOfGame(Frame frame)
	{
		this.frame = frame;
		frame.setGui(this);
		updateLevelSelect = false;
		layout = new CardLayout();
		settings = new Settings(Game.BASE_DIRECTORY);
		FileHelper.loadSettings(settings);
		Game.soundManager.setMusicVolume(settings.getMusicVolume());
		Game.soundManager.setSfxVolume(settings.getSfxVolume());
		setVisible(true);
		setLayout(layout);
		setSize(getMaximumSize());
		mainMenu = new GuiMainMenu(this);
		add(mainMenu, "Main Menu");
		gameSelect = new GuiGameSelect(this);
		options = new GuiOptions(this, settings);
		levelEditor = new GuiLevelEditor(this);
		multiplayer = new GuiMultiplayerSelect(this);
		serverSetup = new GuiServerSetup(this);
		credits = new GuiCredits(this);
		add(options, "Options");
		add(levelEditor, "Level Editor");
		add(gameSelect, "Game Select");
		add(multiplayer, "Multiplayer");
		add(serverSetup, "Server Setup");
		add(credits, "Credits");
		parentTree = new ArrayList<String>(2);
		parentTree.add("Main Menu");
		Game.soundManager.setMusic(SoundManager.MUSIC_STREAMLINE);
		Game.soundManager.resumeMusic();	
	}

	public void enterGameSelect()
	{
		if(updateLevelSelect)
		{
			gameSelect.levelSelect.refresh(0);
		}
		layout.show(this, "Game Select");
		gameSelect.levelSelect.reset(0);
		parentTree.add("Game Select");
	}
	
	public void enterOptions()
	{
		layout.show(this, "Options");
		options.setSettings(settings);
		parentTree.add("Options");
	}
	
	public void enterOptions(Level level)
	{
		Settings s = new Settings(level);
		FileHelper.loadSettings(s);
		options.setSettings(s);
		layout.show(this, "Options");
		parentTree.add("Options");
	}
	
	public void enterMainMenu()
	{
		layout.show(this, "Main Menu");
		parentTree.add("Main Menu");
	}
	
	public void enterLevelEditor()
	{
		layout.show(this, "Level Editor");
		levelEditor.levelSelect.reset(0);
		parentTree.add("Level Editor");
	}
	
	public void enterMultiplayer()
	{
		layout.show(this, "Multiplayer");
		parentTree.add("Multiplayer");
	}
	
	public void enterServerSetup()
	{
		layout.show(this, "Server Setup");
		parentTree.add("Server Setup");		
	}
	
	public void enterCredits()
	{
		layout.show(this, "Credits");
		parentTree.add("Credits");		
	}
	
	public void flipBack()
	{
		parentTree.remove(parentTree.size()-1);
		layout.show(this, parentTree.get(parentTree.size()-1));//Does not call "enter" method and therfore all states remain the same. 
	}
	
	public void startGame(Level level, String[] characters, String[] names, Game.Mode mode)
	{
		if(Game.soundManager.music != null && Game.soundManager.music.isOpen())
			Game.soundManager.music.close();
		if(level.getName().startsWith("ERROR~ "))
			return;
		frame.remove(this);
		Game game = new Game(frame, level, characters, names, level.hasOwnSettings()? level.getSettings():settings, mode);
		game.init();
	}
	
	public void startMultiplayerGame(String character, String name, String ip, int port)
	{
		if(Game.soundManager.music != null)
			Game.soundManager.music.close();
		frame.remove(this);
		GameMP game;
		try
		{
			game = new GameMP(frame, character, name, Game.Mode.SATISFACTION, ip, port);
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}
	
	public void startServer(Level level, int maxPlayers, int port, Mode mode)
	{
		if(Game.soundManager.music != null)
			Game.soundManager.music.close();
		mainMenu.stop();
		frame.remove(this);
		Server server = new Server(frame, port, level, maxPlayers, mode);
		server.start();
	}
	
	public void setFrame(Frame frame)
	{
		this.frame = frame;
	}

	public void setUpdateLevelSelect()
	{
		updateLevelSelect = true;
	}
}
