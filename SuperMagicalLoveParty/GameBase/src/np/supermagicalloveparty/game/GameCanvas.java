package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

public class GameCanvas extends java.awt.Canvas
{

	private static final long serialVersionUID = -8752629176670025434L;
	Game game;
	/**
	 * width(in pixels) of the playable screen area.
	 */
	protected int width;
	/**
	 * height(in pixels) of the playable screen area.
	 */
	protected int height;
	/**
	 * the height(in pixels) of the full screen area(excluding frame decorations).
	 */
	protected int availableHeight;
	/**
	 * the width(in pixels) of the full screen area(excluding frame decorations).
	 */
	protected int availableWidth;
	/**
	 * Amount of pixels(not tiles) to offset the playable screen from the left edge of the screen.
	 */
	protected int offx;
	/**
	 * Amount of pixels(not tiles) to offset the playable screen from the top of the screen.
	 */
	protected int offy;
	/**
	 * Value to multiply all values of game graphics by. Scales off size of the screen. Equals the size of one tile of the game. Does not apply to Guis.
	 */
	protected double scale;
	
	/**
	 * Scale value * camera Zoom value. What drawables are actually multiplied by when camera is active. 
	 */
	protected double scaleWithCamera;
	/**
	 * like scale, but always on a "160x90" scale. Does not scale to map size. a location of x = 160 will always be the rightmost point and y = 90 will always be the bottom most point.
	 */
	protected double guiScale;
	protected Frame frame;
	private Graphics2D graphics;
	/**
	 * value to offset the playable screen from the right side when the camera is active.
	 */
	protected double cameraX;
	/**
	 * value to offset the playable screen from the top when the camera is active.
	 */
	protected double cameraY;
	/**
	 * Amount to scale the game when camera is active.
	 */
	protected double cameraZoom;
	
	public GameCanvas(Game g)
	{
		super();
		game = g;
		setBackground(new Color(0,0,0,0));
		setIgnoreRepaint(true);
	}
	
	/**
	 * revalidate the scale and guiScale for new screen size.
	 * @param frame to get insets and size.
	 */
	public void updateScale(Frame frame)
	{
		Insets decoration = frame.getInsets();
		availableHeight = (frame.getHeight() - decoration.top - decoration.bottom);
		availableWidth = (frame.getWidth() - decoration.left - decoration.right);
		setMinimumSize(new Dimension(availableWidth, availableHeight));
		setScale(Math.min(availableHeight/(double)game.level.getHeight(), availableWidth/(double)game.level.getWidth()));
		guiScale = Math.min(availableHeight/90.0, availableWidth/160.0);//TODO ratio for verical maps
		height = (int) (game.level.getHeight()*getScale());
		width = (int) (game.level.getWidth()*getScale());
		offy = (availableHeight - height) / 2;
		offx = (availableWidth - width) / 2;
		scaleWithCamera = scale;
	}

	/**
	 * @return value to multiply location and dimension values by for in game elements(non gui).
	 */
	public double getScale()
	{
		return scale;
	}

	public void setScale(double scale)
	{
		this.scale = scale;
	}
	
	/**
	 * @return value to multiply location and dimension values by for gui elements. Does not change with map tile size, but does change with screen size. Always 160x90.
	 */
	public double getGuiScale()
	{
		return guiScale;
	}

	public void setGuiScale(double guiScale)
	{
		this.guiScale = guiScale;
	}
	
	/**
	 * main render method of the game. Offsets, scales, camera stuff, draws drawables, and draws guis if active.
	 * @param g
	 * @param interpolation 
	 */
	public void render(Graphics g, double interpolation)
	{
		if(graphics != null)
			graphics.dispose();//is this necessary?
		graphics = (Graphics2D)g;
		if(game.state.equals(Game.State.CONNECTING))
		{
			graphics.setFont(new Font("ARIAL", Font.BOLD, (int) (scale*10)));
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0,  0,  availableWidth, availableHeight);
			graphics.setColor(Color.WHITE);
			String connecting = "CONNECTING...";
			graphics.drawString(connecting, (availableWidth/2)-(graphics.getFontMetrics().stringWidth(connecting)/2), availableHeight/2);
			return;
		}else if(game.state.equals(Game.State.DOWNLOADING))
		{
			graphics.setFont(new Font("ARIAL", Font.BOLD, (int) (scale*10)));
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0,  0,  availableWidth, availableHeight);
			graphics.setColor(Color.WHITE);
			String connecting = "DOWNLOADING LEVEL...";
			graphics.drawString(connecting, (availableWidth/2)-(graphics.getFontMetrics().stringWidth(connecting)/2), availableHeight/2);
			return;
		}else if(game.state.equals(Game.State.LOADING))
		{
			graphics.setFont(new Font("ARIAL", Font.BOLD, (int) (scale*20)));
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0,  0,  availableWidth, availableHeight);
			graphics.setColor(Color.WHITE);
			String loading = "LOADING GAME...";
			graphics.drawString(loading, (availableWidth/2)-(graphics.getFontMetrics().stringWidth(loading)/2), availableHeight/2);
			return;
		}
		graphics.setColor(Color.BLUE);
		graphics.fillRect(0,  0,  availableWidth, availableHeight);//BEFORE CAMERA
		if(game.isUsingCamera())
		{
			graphics.translate(cameraX, cameraY);
			//game.level.draw(graphics, cameraZoom, false, true, game.settings.isShowTiles());
			//graphics.drawImage(game.specialMode? game.level.getSpecialBackground():game.level.getBackground(), 0, 0, (int)(width*cameraZoom), (int)(height*cameraZoom), null);
			//graphics.scale(cameraZoom, cameraZoom);//Screws with resolution. Looses precision. Scales after render. can possibly use later for some type of moving camera. Causes lag
		}
		else
		{
			graphics.translate(offx, offy);
			//graphics.drawImage(game.specialMode? game.level.getSpecialBackground():game.level.getBackground(), 0, 0, (int)(width), (int)(height), null);
		}
		game.level.draw(graphics, scaleWithCamera, false, true, game.specialMode, game.settings.isShowTiles());
		//game.fineUpdate(interpolation);//Refresh draw values with new camera value.
		if(game.debug && game.level != null)
			game.level.draw(graphics, scaleWithCamera, true, false, false, false);

		for(Drawable d : game.drawables)
		{
			if(d.getVisible())
			{
				d.draw(graphics, interpolation);
			}
		}

		if(game.isUsingCamera())
		{
			//graphics.scale(1/cameraZoom, 1/cameraZoom);
			//scale/=cameraZoom;
			graphics.translate(-cameraX, -cameraY);
			graphics.translate(offx, offy);//TODO not best implementation of camera+gui
		}

		graphics.setFont(new Font("ARIAL", Font.PLAIN, (int)(getGuiScale()*2)));
		if(game.debug)
		{
			graphics.setColor(Color.WHITE);
			graphics.drawString("Fps: " + game.fps + " Tps: " + game.tps, 0, (int)(2*scale));
		}
		switch(game.activeGui)
		{
			case MENU: 
				game.guiIngame.render(graphics, getGuiScale());
				break;
			case CONTROLS: 
				game.guiControls.render(graphics, getGuiScale());
				break;
			case CONSOLE:
				game.guiConsole.render(graphics, scale);
				break;
			case COUNTDOWN:
				game.guiCountdown.render(graphics, getGuiScale());
				break;
			case GAME_OVER:
				game.guiGameOver.render(graphics, getGuiScale());
				break;
			case NONE:
				break;
		}
		if(!game.activeGui.equals(Game.Gui.CONSOLE))
		{
			graphics.setFont(new Font("ARIAL", Font.PLAIN, (int)(getGuiScale()*2)));
			game.guiConsole.renderChat(graphics, scale);
		}
		if(!game.settings.isCamera())
			graphics.translate(-offx, -offy);
		//Far left of total screen drawing
	}
	
	/** 
	 * @return the total height(in pixels) from the top of the frame to the bottom(excluding frame decorations)
	 */
	public int getAvailableHeight()
	{
		return availableHeight;
	}

	/**
	 * @return the total width(in pixels) from the left to right of the screen(excluding frame decorations)
	 */
	public int getAvailableWidth()
	{
		return availableWidth;
	}
	
	/**
	 * @return the width of the playable screen. Does not factor in any camera effects. Equals width in tiles * scale.
	 */
	public int getCanvasWidth()
	{
		return width;
	}
	
	/**
	 * @return the height of the playable screen. Does not factor in any camera effects. Equals height in tiles * scale.
	 */
	public int getCanvasHeight()
	{
		return height;
	}

	public double getCameraZoom()
	{
		return cameraZoom;
	}

	public double getScaleWithCamera()
	{
		return scaleWithCamera;
	}
}