package np.supermagicalloveparty.server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

import np.supermagicalloveparty.game.Settings;

public class ExtraLevelData extends Extra
{
	private static final long serialVersionUID = -8108131059640061643L;
	private char[] terrain;
	private char[] entities;
	private boolean fullscreen, camera, hasSettings, showTiles;
	private int powerups, gravity, speed, musicVolume, sfxVolume, width, height;
	transient BufferedImage background, backgroundSpecial;
	
	public ExtraLevelData(int width, int height, char[] terrain, char[] entities, Settings settings, BufferedImage background, BufferedImage backgroundSpecial)
	{
		this.width = width;
		this.height = height;
		this.terrain = terrain;
		this.entities = entities;
		this.background = background;
		this.backgroundSpecial = backgroundSpecial;
		if(settings == null)
			hasSettings = false;
		else
		{
			hasSettings = true;
			this.powerups = settings.getPowerups();
			this.gravity = settings.getGravity();
			this.camera = settings.isCamera();
			this.musicVolume = settings.getMusicVolume();
			this.fullscreen = settings.isFullscreenMode();
			this.speed = settings.getSpeed();
			this.sfxVolume = settings.getSfxVolume();
			this.showTiles = settings.isShowTiles();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		System.out.println("Writing Level Data");
		out.defaultWriteObject();
		if(background!=null)
			ImageIO.write(background, "png", out);
		if(backgroundSpecial!=null)
			ImageIO.write(backgroundSpecial, "png", out);
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		System.out.println("Reading level data");
		in.defaultReadObject();
		background = ImageIO.read(in);
		backgroundSpecial = ImageIO.read(in);
	}
	
	public char[] getTerrain()
	{
		return terrain;
	}
	
	public char[] getEntities()
	{
		return entities;
	}
	
	public BufferedImage getBackground()
	{
		return background;
	}
	
	public BufferedImage getBackgroundSpecial()
	{
		return backgroundSpecial;
	}

	@Override
	public int getType()
	{
		return Packet.LEVEL_DATA;
	}

	public boolean isFullscreen()
	{
		return fullscreen;
	}

	public boolean isCamera()
	{
		return camera;
	}

	public boolean isHasSettings()
	{
		return hasSettings;
	}

	public int getPowerups()
	{
		return powerups;
	}

	public int getGravity()
	{
		return gravity;
	}

	public int getMusicVolume()
	{
		return musicVolume;
	}

	public int getSfxVolume()
	{
		return sfxVolume;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}
	
	public boolean isShowTiles()
	{
		return showTiles;
	}

	public int getSpeed()
	{
		return speed;
	}
}
