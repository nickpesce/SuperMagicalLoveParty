package np.supermagicalloveparty.game;

public class Camera
{
	private double zoom;
	private double offsetX, offsetY, maxZoom, minZoom;
	private Game game;
	private double interpolation;
	public Camera(Game g)
	{
		zoom = 1;
		offsetX = offsetY = 0;
		game = g;
		maxZoom = Math.pow(game.level.getWidth()*game.level.getHeight(), .1);
		minZoom = 1;
	}
	public void update(double interpolation)
	{
		this.interpolation = interpolation;
		if(game.numPlayers == 0)
		{
			offsetY = game.canvas.offy;
			offsetX = game.canvas.offx;
			zoom = 1;
			return;
		}
		Point averagePlayerLocation = getAveragePlayerLocation();

		zoom = Math.min(game.level.getWidth()*.7/getFarthestDistanceX(), game.level.getHeight()*.7/(getFarthestDistanceY()*1.3/*Zooms y zoom out a little for platforms underneath can be seen*/));
		if(zoom > maxZoom)
			zoom = maxZoom;
		else if(zoom < minZoom)
			zoom = minZoom;
		double nOffsetX = -((averagePlayerLocation.getX() * game.canvas.getScale()) - (.5*game.canvas.getCanvasWidth()/zoom))*zoom;
		double nOffsetY = -((averagePlayerLocation.getY() * game.canvas.getScale()) - (.5*game.canvas.getCanvasHeight()/zoom))*zoom;

		//Can't fit whole screen (zoomed in), but is shifted to the right (gap on left)
		if(zoom > game.canvas.getAvailableWidth() / ((double)game.canvas.getCanvasWidth()) && nOffsetX > 0)
		{
			offsetX = 0;
		}
		//can't fit whole screen (zoomed in), but is shifted left (gap on right)
		else if(zoom > game.canvas.getAvailableWidth() / ((double)game.canvas.getCanvasWidth()) && nOffsetX < -(game.canvas.getCanvasWidth()   - (game.canvas.getAvailableWidth()/zoom))*zoom)
		{
			offsetX = -(game.canvas.getCanvasWidth() - (game.canvas.getAvailableWidth()/zoom))*zoom;
		}
		//whole screen can fit 
		else if(zoom <= game.canvas.getAvailableWidth() / ((double)game.canvas.getCanvasWidth()) )//&& (nOffsetX > game.canvas.offx || nOffsetX < game.canvas.offx))
		{
			offsetX = (-(game.canvas.getAvailableWidth() - game.canvas.getAvailableWidth()/zoom)/2.0) + game.canvas.offx;
		}
		else
		{
			offsetX = nOffsetX;
		}
		
		//Can't fit whole screen (zoomed in), but is shifted Down (gap on top)
		if(zoom > ((game.canvas.getAvailableHeight()) / (double)game.canvas.getCanvasHeight()) && nOffsetY > 0)
		{
			offsetY = 0;
		}
		//Can't fit whole screen (zoomed in), but is shifted to the Up (gap on bottom)
		else if(zoom > ((game.canvas.getAvailableHeight()) / (double)game.canvas.getCanvasHeight()) && nOffsetY < -(game.canvas.getCanvasHeight()  - (game.canvas.getAvailableHeight()/zoom))*zoom)
		{
			offsetY = -(game.canvas.getCanvasHeight() - (game.canvas.getAvailableHeight()/zoom))*zoom;
		}
		//whole screen can fit 
		else if(zoom <= ((game.canvas.getAvailableHeight()) / (double) game.canvas.getCanvasHeight()) )//&& (nOffsetY > game.canvas.offy || nOffsetY < game.canvas.offy))
		{
			offsetY = (-(game.canvas.getAvailableHeight() - game.canvas.getAvailableHeight()/zoom)/2.0) + game.canvas.offy;
		}
		else
		{
			offsetY = nOffsetY;
		}
	}
	
	/**
	 * Get the middle of the two FURTHEST players. Not all of them.
	 * @return average point
	 */
	private Point getAveragePlayerLocation()
	{
		/*
		double totalX = 0;
		double totalY = 0;
		double highestXWidth = 0;
		double highestYHeight = 0;
		for(Player p : game.players)
		{
			if(p.getX() > highestXWidth)
				highestXWidth = p.getWidth();
			if(p.getY() > highestYHeight)
				highestYHeight = p.getHeight();
			totalX += p.getX();
			totalY += p.getY();
		}
		totalX += highestXWidth;
		totalY += highestYHeight;
		return new Point(totalX / game.players.size(), totalY / game.players.size());
		*/
		return new Point(((getMaxPlayerX() + getMinPlayerX())/2), ((getMaxPlayerY() + getMinPlayerY())/2));
	}
	
	/**
	 * @return farthest distance between two players X coordinates in tile format
	 */
	private double getFarthestDistanceX()
	{
		/*
		double farthest = 0;
		for(Player p : game.players)
		{
			for(Player p2 : game.players)
			{
				if(p.getX() > p2.getX())
				{
					if(p.getX() - (p2.getX()+p2.getWidth()) > farthest)
						farthest = p.getX() - (p2.getX()+p2.getWidth());
				}else
				{
					if(p2.getX() - (p.getX()+p.getWidth()) > farthest)
						farthest = p2.getX() - (p.getX()+p.getWidth());
				}
				
			}
		}
		return farthest;
		*/
		return getMaxPlayerX() - getMinPlayerX();
	}
	
	/**
	 * @return farthest distance between two players Y coordinates in tile format
	 */
	private double getFarthestDistanceY()
	{
		/*
		double farthest = 0;
		for(Player p : game.players)
		{
			for(Player p2 : game.players)
			{
				if(p.getY() > p2.getY())
				{
					if(p.getY() - (p2.getY()+p2.getHeight()) > farthest)
						farthest = p.getY() - (p2.getY()+p2.getHeight());
				}else
				{
					if(p2.getY() - (p.getY()+p.getHeight()) > farthest)
						farthest = p2.getY() - (p.getY()+p.getHeight());
				}
			}
		}
		return farthest;
		*/
		return getMaxPlayerY() - getMinPlayerY();

	}
	
	public double getMinPlayerX()
	{
		double min = game.players[0].getX() + game.players[0].getVx()*interpolation;
		for(Player p : game.players)
		{
			if(p == null)continue;
			min = Math.min(min, p.getX() + p.getVx()*interpolation);
		}
		return min;
	}
	public double getMinPlayerY()
	{
		double min = game.players[0].getY() + game.players[0].getVy() * interpolation;
		for(Player p : game.players)
		{
			if(p == null)continue;
			min = Math.min(min, p.getY() + p.getVy()*interpolation);
		}
		return min;
	}
	public double getMaxPlayerX()
	{
		double max = game.players[0].getX() + game.players[0].getVx()*interpolation;
		for(Player p : game.players)
		{
			if(p == null)continue;
			max = Math.max(max, p.getX() + p.getVx()*interpolation);
		}
		return max;
	}
	public double getMaxPlayerY()
	{
		double max = game.players[0].getY() + game.players[0].getVy()*interpolation;
		for(Player p : game.players)
		{
			if(p == null)continue;
			max = Math.max(max, p.getY() + p.getVy()*interpolation);
		}
		return max;
	}
	
	public double getZoom()
	{
		return zoom;
	}

	public double getOffsetX()
	{
		return offsetX;
	}

	public double getOffsetY()
	{
		return offsetY;
	}
}
