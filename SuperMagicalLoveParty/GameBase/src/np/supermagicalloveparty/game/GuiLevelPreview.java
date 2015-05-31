package np.supermagicalloveparty.game;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;


public class GuiLevelPreview extends Canvas
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Level level;
	int numPlayers;
	double scale, origScale;
	int offsetX, offsetY;
	private BufferedImage extraImage;
	private int extraImageX, extraImageY, extraImageWidth, extraImageHeight;
	
	/**
	 * width to height ratio of the level being displayed
	 */
	double ratio;
	public GuiLevelPreview(Level level, int numPlayers)
	{
		setVisible(true);
		setSize(800, 450);
		setBackground(Color.BLUE);
		origScale = scale = getWidth()/level.getWidth();
		offsetX = offsetY = 0;
		this.level = level;
		this.numPlayers = numPlayers;
	}
	
	public GuiLevelPreview(Level level)
	{
		this(level, level.getSpawns().length);
	}
	
	@Override
	public void paint(Graphics g)
	{
		render();
	}
	
	public void render()
	{
		if(getBufferStrategy() == null)
			createBufferStrategy(2);
		Graphics g = getBufferStrategy().getDrawGraphics();//ghetto, but w/e
		g.translate(offsetX, offsetY);
		level.draw(((Graphics2D)g), scale, true, true, false, level.hasOwnSettings() ? level.getSettings().isShowTiles():true);
		for(int i = 0; i < level.getSpawns().length; i++)
		{
			if(level.getSpawns()[i]==null)
				continue;
			if(i < numPlayers)
				g.setColor(new Color(0, 255, 255, 200));
			else
				g.setColor(Color.ORANGE);
			g.fillRect((int)(level.getSpawns()[i].getX()*scale), (int)(level.getSpawns()[i].getY()*scale), (int)(scale*Player.MAX_WIDTH), (int)(Player.MAX_HEIGHT*scale));
			//g.fillOval((int)(level.getSpawns()[i].getX()*scale), (int)(level.getSpawns()[i].getY()*scale), (int)(scale*2), (int)(2*scale));
			g.setColor(Color.BLACK);
			g.setFont(new Font("ARIAL", Font.PLAIN, (int) scale*2));
			g.drawString(i+1+"", (int)((level.getSpawns()[i].getX()*scale)+scale*.5), (int)((level.getSpawns()[i].getY()*scale)+scale*2));
		}
		if(extraImage != null)
		{
			g.drawImage(extraImage, (int)(extraImageX*scale), (int)(extraImageY*scale), (int)(extraImageWidth*scale), (int)(extraImageHeight*scale), null);
		}
		g.dispose();
		getBufferStrategy().show();
	}
	
	public void setLevel(Level level)
	{
		this.level = level;
		ratio = level.getWidth()/level.getHeight();
		scale = origScale = Math.min(800.0/level.getWidth(), 450.0/level.getHeight());
		setSize((int)(level.getWidth()*scale), (int)(level.getHeight()*scale));
		render();
		revalidate();
	}

	public double getOrigScale()
	{
		return origScale;
	}
	
	public double getScale()
	{
		return scale;
	}
	
	public void setScale(double scale)
	{
		this.scale = scale;
		this.repaint();
	}
	
	public void setPlayers(int numPlayers)
	{
		this.numPlayers = numPlayers;
		repaint();
	}

	public int getOffsetX()
	{
		return offsetX;
	}

	public void setOffsetX(int offsetX)
	{
		this.offsetX = offsetX;
	}

	public int getOffsetY()
	{
		return offsetY;
	}

	public void setOffsetY(int offsetY)
	{
		this.offsetY = offsetY;
	}

	public void centerAround(Point point)
	{
		//TODO center camera
	}

	public void drawExtra(BufferedImage image, int x, int y, int width, int height)
	{
		extraImage = image;
		extraImageX = x;
		extraImageY = y;
		extraImageWidth = width;
		extraImageHeight = height;
		render();
	}
	
}
