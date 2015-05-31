package np.supermagicalloveparty.game;
import java.util.ArrayList;


public class Settings
{
	private Pair<String, Boolean> fullscreenMode, camera, showTiles;
	private Pair<String, Integer> gravity, musicVolume, sfxVolume, powerups, speed;
	public ArrayList<Pair> list;
	private String directory;
	
	public Settings(String directory)
	{
		this.directory = directory;
		fullscreenMode = new Pair<String, Boolean>("Fullscreen", false);
		camera = new Pair<String, Boolean>("Auto Camera", false);
		showTiles = new Pair<String, Boolean>("Show Tiles", true);
		powerups = new Pair<String, Integer>("Powerups", 50);
		gravity = new Pair<String, Integer>("Gravity", 50);
		speed = new Pair<String, Integer>("Speed", 20);
		musicVolume = new Pair<String, Integer>("Music Volume", 50);
		sfxVolume = new Pair<String, Integer>("Sounds Volume", 50);
		list = new ArrayList<Pair>();
		list.add(fullscreenMode);
		list.add(camera);
		list.add(showTiles);
		list.add(powerups);
		list.add(gravity);
		list.add(speed);
		list.add(musicVolume);
		list.add(sfxVolume);

	}
	
	public Settings(boolean fullscreen, boolean camera, boolean showTiles, int powerups, int gravity, int speed, int musicVolume, int sfxVolume)
	{
		this.fullscreenMode = new Pair<String, Boolean>("Fullscreen", fullscreen);
		this.camera = new Pair<String, Boolean>("Auto Camera", camera);
		this.showTiles = new Pair<String, Boolean>("Show Tiles", showTiles);
		this.powerups = new Pair<String, Integer>("Powerups", powerups);
		this.gravity = new Pair<String, Integer>("Gravity", gravity);
		this.speed = new Pair<String, Integer>("Speed", speed);
		this.sfxVolume = new Pair<String, Integer>("Sounds Volume", sfxVolume);
		this.musicVolume = new Pair<String, Integer>("Music Volume", musicVolume);

		list = new ArrayList<Pair>();
		list.add(this.fullscreenMode);
		list.add(this.camera);
		list.add(this.showTiles);
		list.add(this.powerups);
		list.add(this.gravity);
		list.add(this.speed);
		list.add(this.musicVolume);
		list.add(this.sfxVolume);

	}
	
	public Settings(Level level)
	{
		this(Game.BASE_DIRECTORY + "Levels/" + level.toFileString());
	}
	
	public void setToDefault()
	{
		fullscreenMode.setRight(false);
		camera.setRight(false);
		showTiles.setRight(true);
		powerups.setRight(50);
		gravity.setRight(50);
		speed.setRight(20);
		musicVolume.setRight(50);
		sfxVolume.setRight(50);
		Game.soundManager.setSfxVolume(50);
		Game.soundManager.setMusicVolume(50);
	}
	
	public boolean isFullscreenMode()
	{
		return fullscreenMode.getRight();
	}
	public void setFullscreenMode(boolean fullscreenMode)
	{
		if(directory==null)
			return;
		this.fullscreenMode.setRight(fullscreenMode);
		FileHelper.saveSettings(this);
	}
	public boolean isCamera()
	{
		return camera.getRight();
	}
	
	public void setCamera(boolean camera)
	{
		if(directory==null)
			return;
		this.camera.setRight(camera);
		FileHelper.saveSettings(this);
	}
	
	public boolean isShowTiles()
	{
		return showTiles.getRight();
	}

	public void setShowTiles(boolean tiles)
	{
		if(directory==null)
			return;
		this.showTiles.setRight(tiles);
		FileHelper.saveSettings(this);
	}
	public String getDirectory()
	{
		return directory;
	}

	public int getPowerups()
	{
		return powerups.getRight();
	}
	
	public void setPowerups(int amtPowerups)
	{
		if(directory==null)
			return;
		this.powerups.setRight(amtPowerups);
		FileHelper.saveSettings(this);
	}
	
	public int getGravity()
	{
		return gravity.getRight();
	}
	
	public void setGravity(int g)
	{
		if(directory==null)
			return;
		this.gravity.setRight(g);
		FileHelper.saveSettings(this);
	}
	
	public int getSpeed()
	{
		return speed.getRight();
	}
	
	public void setSpeed(int s)
	{
		if(directory==null)
			return;
		this.speed.setRight(s);
		FileHelper.saveSettings(this);
	}
	
	public int getMusicVolume()
	{
		return musicVolume.getRight();
	}
	
	public void setMusicVolume(int v)
	{
		if(directory==null)
			return;
		this.musicVolume.setRight(v);
		Game.soundManager.setMusicVolume(v);
		FileHelper.saveSettings(this);
	}	
	
	public int getSfxVolume()
	{
		return sfxVolume.getRight();
	}
	
	public void setSfxVolume(int v)
	{
		if(directory==null)
			return;
		this.sfxVolume.setRight(v);
		Game.soundManager.setSfxVolume(v);
		FileHelper.saveSettings(this);
	}
	
	@Override
	public String toString()
	{
		String s = "";
		for(Pair p : list)
		{
			s += p.getLeft() + " : " + p.getRight() + ", ";
		}
		return s;
	}
}
