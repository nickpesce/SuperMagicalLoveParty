package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Level implements Updatable
{
	private Point[] spawns;
	private ArrayList<Pair<Character, Point>> graphics;
	private byte graphicsColor;
	private char[] entities;
	private BufferedImage background, specialBackground;
	public static BufferedImage iSolid, iIce, iUp, iBBHH, iGumdrop, iLollipopRed, iLollipopGreen, iLollipopBlue, iLollipopTurquoise, iLollipopYellow, iLollipopOrange, iLollipopPink, iLollipopPurple;
	private boolean ownBackground, ownBackgroundSpecial;
	/**
	 * only instantiated if has level specific settings. else null
	 */
	private Settings settings;
	private String name;
	private char[] tiles;
//	private ArrayList<TileState> tilesToReplace;
	private int width, height;
	
	public static final char
	EMPTY = 'E',
	SOLID = 'S',
	UP = 'U',
	DAMAGE_AND_KNOCKBACK = 'K',
	BORDER = 'X',
	ICE = 'I',
	VANISH = 'V',
	LOLLIPOP = 'L',
	GUMDROP = 'G';
	
	public Level(String name, int width, int height, char[] terrain, char[] entities, BufferedImage background, BufferedImage specialBackground, Settings settings)
	{
		this.width = width;
		this.height = height;

		this.name = name;
		this.tiles = new char[width * height];
		this.entities = entities;
		this.graphics = new ArrayList<Pair<Character, Point>>();
		graphicsColor = (byte)(Math.random()*127);
//		tilesToReplace = new ArrayList<TileState>();
		if(iSolid == null)
		{
			try
			{
				iSolid = ImageIO.read(getClass().getResourceAsStream("/Kenney/choco.png"));
				iUp = ImageIO.read(getClass().getResourceAsStream("/PepermintPlatform.png"));
				iIce = ImageIO.read(getClass().getResourceAsStream("/Kenney/hillCaneGreen.png"));
				iBBHH = ImageIO.read(getClass().getResourceAsStream("/Kenney/cakeHalfAlt.png"));
				iLollipopRed = ImageIO.read(getClass().getResourceAsStream("/LollipopRed.png"));
				iLollipopGreen = ImageIO.read(getClass().getResourceAsStream("/LollipopGreen.png"));
				iLollipopBlue = ImageIO.read(getClass().getResourceAsStream("/LollipopBlue.png"));
				iLollipopOrange = ImageIO.read(getClass().getResourceAsStream("/LollipopOrange.png"));
				iLollipopPink = ImageIO.read(getClass().getResourceAsStream("/LollipopPink.png"));
				iLollipopPurple = ImageIO.read(getClass().getResourceAsStream("/LollipopPurple.png"));
				iLollipopYellow = ImageIO.read(getClass().getResourceAsStream("/LollipopYellow.png"));
				iLollipopTurquoise = ImageIO.read(getClass().getResourceAsStream("/LollipopTurquoise.png"));
				iGumdrop = ImageIO.read(getClass().getResourceAsStream("/Kenney/candyGreen.png"));
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		if(settings == null)
		{
			File settingsFile = new File(Game.BASE_DIRECTORY+"Levels/"+makeFileString(name, ""+width, ""+height)+"/Settings.txt");
			if(settingsFile.exists())
			{
				settings = new Settings(this);
				FileHelper.loadSettings(settings);
			}
		}
		this.settings = settings;
		if(background == null)
		{
			ownBackground = false;
			try
			{
				this.background = ImageIO.read(getClass().getResourceAsStream("/background.png"));
			}
			catch (IOException e){e.printStackTrace();}
		}else
		{
			ownBackground = true;
			this.background = background;
		}
		if(specialBackground == null)
		{
			ownBackgroundSpecial = false;
			try
			{
				this.specialBackground = ImageIO.read(getClass().getResourceAsStream("/backgroundSpecial.png"));
			}
			catch (IOException e){e.printStackTrace();}
		}else
		{
			ownBackgroundSpecial = true;
			this.specialBackground = specialBackground;
		}
		importEntitiesFromEntityArray(entities);
		importTerrainFromCharArray(terrain);
	}
	
	public Level(String name, int width, int height, char[] terrain, char[] entities, BufferedImage background, BufferedImage specialBackground)
	{
		this(name, width, height, terrain, entities, background, specialBackground, null);
	}

	public Level(String name, int width, int height, char[] terrain, char[] entities, BufferedImage background)
	{
		this(name, width, height, terrain, entities, background, null);
	}
	
	/**
	 * @param g graphics object
	 * @param scale 
	 * @param skeleton Draw borders of tiles?
	 * @param background draw Background?
	 * @param tiles draw Tile images
	 */
	public void draw(Graphics2D g, double scale, boolean skeleton, boolean background, boolean special, boolean drawTiles)
	{
		if(background)
			g.drawImage(special? getSpecialBackground() : getBackground(), 0, 0, (int)(getWidth()*scale), (int)(getHeight()*scale), null);
		if(skeleton)
		{
			for(int i = 0; i < spawns.length; i++)
			{
				if(spawns[i]==null)
					continue;
				g.setColor(new Color(100, 200, 100, 100));
				g.fillRect((int)(spawns[i].getX()*scale), (int)(spawns[i].getY()*scale), (int)(scale*Player.MAX_WIDTH), (int)(Player.MAX_HEIGHT*scale));
				//g.fillOval((int)(level.spawns[i].getX()*scale), (int)(level.spawns[i].getY()*scale), (int)(scale*2), (int)(2*scale));
				g.setColor(Color.BLACK);
				g.setFont(new Font("ARIAL", Font.PLAIN, (int) scale*2));
				g.drawString(i+1+"", (int)((spawns[i].getX()*scale)+scale*.5), (int)((spawns[i].getY()*scale)+scale*2));
			}	
		}
		for(Pair<Character, Point> p : graphics)
		{
			int x = (int) p.getRight().getX();
			int y = (int) p.getRight().getY();
			if(p.getLeft() == LOLLIPOP)
			{
				switch((graphicsColor*(x*y)+y+x)%8)
				{
					case 0: g.drawImage(iLollipopRed, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
					case 1: g.drawImage(iLollipopOrange, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
					case 2: g.drawImage(iLollipopPurple, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
					case 3: g.drawImage(iLollipopTurquoise, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
					case 4: g.drawImage(iLollipopYellow, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
					case 5: g.drawImage(iLollipopBlue, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
					case 6: g.drawImage(iLollipopGreen, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
					case 7: g.drawImage(iLollipopPink, (int)(x*scale), (int)(y*scale), (int)(scale*4), (int)(scale*10), null);break;
				}
			}
			else if(p.getLeft() == GUMDROP)
				g.drawImage(iGumdrop, (int)(p.getRight().getX()*scale), (int)(p.getRight().getY()*scale), (int)scale, (int)(scale), null);
		}
		for(int i = 0; i < tiles.length; i++)
		{
			char b = tiles[i];
			if(b == SOLID)
			{
				g.setColor(Color.WHITE);
				if(drawTiles)
					g.drawImage(iSolid, (int)(getTileX(i)*scale), (int)(getTileY(i) * scale), (int)scale+1, (int)scale+1, null);
			}
			else if(b == UP)
			{
				g.setColor(Color.YELLOW);
				if(drawTiles)
					g.drawImage(iUp, (int)(getTileX(i)*scale), (int)(getTileY(i) * scale), (int)scale+1, (int)scale+1, null);
			}
			else if(b == DAMAGE_AND_KNOCKBACK)
			{
				g.setColor(Color.DARK_GRAY);
				if(drawTiles)
					g.drawImage(iBBHH, (int)(getTileX(i)*scale), (int)(getTileY(i) * scale), (int)scale+1, (int)scale+1, null);
			}else if(b == ICE)
			{
				g.setColor(Color.CYAN);
				if(drawTiles)
					g.drawImage(iIce, (int)(getTileX(i)*scale), (int)(getTileY(i) * scale), (int)scale+1, (int)scale+1, null);
			}
			else
				continue;
			if(skeleton)
				g.drawRect((int)(getTileX(i)*scale), (int)(getTileY(i) * scale), (int)scale, (int)scale);
		}
		
	}

	@Override
	public void update()
	{
		
	}

	@Override
	public void fineUpdate(double interpolation)
	{		
	}
	
//	public void temporarilyRemoveTile(Tile tile, int time)
//	{
//		tilesToReplace.add(new TileState(tile, time));
//		tiles[getTileIdByLocation(tile.getX(), tile.getY())] = 'E';
//	}
	
	public int getTileX(int locationID)
	{
		return locationID%width;
	}
	
	public int getTileY(int locationID)
	{
		return locationID/width;
	}
	
	public Point getTileLocation(int LocationID)
	{
		return new Point(getTileX(LocationID), getTileY(LocationID));
	}
	
	public char getTileById(int id)
	{
		return tiles[id];
	}
	public char getTileAtLocation(int x, int y)
	{
		//x = x +1 ;
		//y = y + 1;
		if(x < width && x > -1 && y < height && y >-1)
			return(tiles[y*width + x]);
		else
			return BORDER;
	}
	
	public int getTileIdByLocation(int x, int y)
	{
		return y*width + x;
	}
	
	public char getTileAtLocation(double x, double y)
	{
		return getTileAtLocation((int)x, (int)y);
	}
	
	public void setTile(int x, int y, char tile)
	{
		if(x < width && x > -1 && y < height && y >-1)
			tiles[x+(y*width)] = tile;
 	}
	
	public void setEntity(int x, int y, char entity)
	{
		if(x < width && x > -1 && y < height && y >-1)
			entities[x+(y*width)] = entity;
		importEntitiesFromEntityArray(entities);
	}
	
	public char getEntityAtLocation(int x, int y)
	{
		if(x < width && x > -1 && y < height && y >-1)
			return(entities[y*width + x]);
		else
			return '0';
	}
	
	public boolean inBounds(double x, double y, double width, double height)
	{
		return ((x >= -width)&&(x <= this.width) && (y >= -height) && (y <= this.height));
	}
	
	/**
	 * Altered version of inBounds for Players who are allowed to jump above the map, but cant go out to the side or below.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public boolean inBoundsExcludingAbove(double x, double y, double width, double height)
	{
		return ((x >= -width)&&(x <= this.width) && (y <= this.height));
	}
	
	/**
	 * if moving from (x1, y1) to (x2, y2), will there be an impassable object in between
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return Collision Event with point of obstacle(TOP LEFT) and side of moving object that was hit, null if clear
	 */
	public CollisionEvent isObstacleBetween(final double x1, final double y1, final double x2, final double y2, Entity e)
	{
		int tile;
		tile = getTileAtLocation(x1, y1);
		if(e.canCollideInside(tile))
		{
			return new CollisionEvent(new Point((int)x1, (int)y1), new Point((int)x1, (int)y1), CollisionEvent.Side.INSIDE, 0, tile);
		}
		
		
		double m;
		if(x2==x1)
		{
			//slope undefined: check vertical between y values
			if(y1 > y2)
			{
				for(int y = (int)y1;y > y2; y--)//y refers to y value of head hit. (y-1) is tile coord value.
				{
					tile = getTileAtLocation(x1, y-1);
					if(e.canCollideTop(tile))
					{
						//Y bottom
						return new CollisionEvent(new Point((int)x1, (int)y-1), new Point(x1, y), CollisionEvent.Side.TOP, MathHelper.distance(x1, y1, x1, y), tile);

					}
				}
				return null;
			}
			else if(y2 > y1)
			{
				for(int y = (int)Math.ceil(y1); y < y2; y++)
				{
					tile = getTileAtLocation(x1, y);
					if(e.canCollideBottom(tile))
					{
						//y top
						return new CollisionEvent(new Point((int)x1, (int)y), new Point(x1, y), CollisionEvent.Side.BOTTOM, MathHelper.distance(x1, y1, x1, y), tile);
					}
				}
				
				return null;
			}
			else
			{
				return null;
			}
		}
		else 
			m = (y2-y1)/(x2-x1);
		

		
		double x;
		double y;
		CollisionEvent eventH = null;
		CollisionEvent eventV = null;
		
		if(x1 > x2)//FROM RIGHT
		{
			for(x = Math.ceil(x2)-1; x + 1 <= x1; x++)
			{

				double yL = MathHelper.getYOnLine(m, x1, y1, x);
				double xC = x+1;
				double yC = MathHelper.getYOnLine(m, x1, y1, xC);
				tile = getTileAtLocation(x, (int)yC);
				if(e.canCollideLeft(tile))
				{
					eventH = new CollisionEvent(new Point((int)x, (int)yL), new Point(xC, yC), CollisionEvent.Side.LEFT, MathHelper.distance(x1, y1, xC, yC), tile);
					break;
				}
			}

		}
		else //FROM LEFT
		{
			for(x = (int)x2; x>=x1; x--)
			{
				double yL = MathHelper.getYOnLine(m, x1, y1, x);
				tile = getTileAtLocation(x, yL);
				if(e.canCollideRight(tile))
				{
					eventH = new CollisionEvent(new Point((int)x, (int)yL), new Point(x, yL), CollisionEvent.Side.RIGHT, MathHelper.distance(x1, y1, x, yL), tile);
					break;
				}
			}

		}
		
		
		if(y1!=y2)
		{
			if(y1 > y2)//FROM BOTTOM
			{
				for(y = (int)(y2); y<=(int)y1; y++)
				{
					double yC = y+1;
					double xC = MathHelper.getXOnLine(m, x1, y1, yC);
					tile = getTileAtLocation((int)xC, y);
					if(e.canCollideTop(tile))
					{
						eventV = new CollisionEvent(new Point((int)xC, y), new Point(xC, yC), CollisionEvent.Side.TOP, MathHelper.distance(x1, y1, xC, yC), tile);
						break;
					}
				}
	
			}else//FROM TOP
			{
				//TODO ball gets stuck from top sometimes. also when going upwards can get stuck, butr i think it is still a bottom collsions
				for(y = (int)y2; y>=y1;y--)
				{
					double xL = MathHelper.getXOnLine(m, x1, y1, y);
					tile = getTileAtLocation(xL, y);
	
					if(e.canCollideBottom(tile))
					{
						//Y going down
						eventV = new CollisionEvent(new Point((int)xL, (int)y), new Point(xL, y), CollisionEvent.Side.BOTTOM, MathHelper.distance(x1, y1, xL, y), tile);
						break;
					}
				}
			}
		}
		if(eventV == null)
		{
			if(eventH == null)
				return null;
			else //does not hit vertical, but does horizontal.
			{
			    return eventH;
			}
		}else if(eventH == null && eventV != null)
		{
		    return eventV;
		}
		else if(eventH.getCollisionPoint().equals(eventV.getCollisionPoint()))
		{
			if(eventV.getSide() == CollisionEvent.Side.TOP)
			{
				if(eventH.getSide() == CollisionEvent.Side.RIGHT)
				{
					return new CollisionEvent(new Point(eventH.getTilePoint().getX(), eventV.getTilePoint().getY()), new Point( eventH.getTilePoint().getX(),  eventV.getTilePoint().getY()+1), CollisionEvent.Side.CORNER_TOP_RIGHT, MathHelper.distance(x1, y1, eventH.getTilePoint().getX(),  eventV.getTilePoint().getY()+1), eventH.getTile());
				}
				else//LEFT
				{
					return new CollisionEvent(new Point(eventH.getTilePoint().getX(), eventV.getTilePoint().getY()), new Point(eventH.getTilePoint().getX() + 1,  eventV.getTilePoint().getY() + 1), CollisionEvent.Side.CORNER_TOP_LEFT, MathHelper.distance(x1, y1, eventH.getTilePoint().getX() + 1,  eventV.getTilePoint().getY() + 1), eventH.getTile());
				}
			}
			else//BOTTOM
			{
				if(eventH.getSide() == CollisionEvent.Side.RIGHT)
				{
					return new CollisionEvent(new Point(eventH.getTilePoint().getX(), eventV.getTilePoint().getY()), new Point(eventH.getTilePoint().getX(), eventV.getTilePoint().getY()), CollisionEvent.Side.CORNER_BOTTOM_RIGHT, MathHelper.distance(x1, y1, eventH.getTilePoint().getX(),  eventV.getTilePoint().getY()), eventH.getTile());
				}
				else//LEFT
				{
					return new CollisionEvent(new Point(eventH.getTilePoint().getX(), eventV.getTilePoint().getY()), new Point(eventH.getTilePoint().getX() + 1,  eventV.getTilePoint().getY()), CollisionEvent.Side.CORNER_BOTTOM_LEFT, MathHelper.distance(x1, y1, eventH.getTilePoint().getX() + 1,  eventV.getTilePoint().getY()), eventH.getTile());
				}
			}
			
		}
		else if(Math.min(eventH.getDistance(), eventV.getDistance()) == eventV.getDistance())
		{
			//System.out.println("Distance dispute: V: "  +  eventV  + " h: " + eventH);
			return eventV;
		}
		//System.out.println("Distance dispute: H: "  +  eventH + " v: " + eventV);

		return eventH;
	}
	
	/**
	 * Checks if a shape is inside a tile of the level
	 * @param shape bounds to check
	 * @param up direction of y. true for negative. For "up" tile collisions. If moving up in an up tile, not inside.
	 * @return true of false if inside terrain.
	 */
	public boolean isInsideTerrain(Shape shape, boolean up)
	{
		for(double x = shape.getX(); x < shape.getWidth()+shape.getX(); x++)
		{
			for(double y = shape.getY(); y < shape.getHeight()+shape.getY(); y++)
			{
				char tile = getTileAtLocation(x, y);
				if(!((tile==EMPTY) || (tile==UP && up) || tile == BORDER))
					return true;
			}
		}
		return false;
	}
	
	public void importTerrainFromCharArray(char[] terrain)
	{
		tiles = terrain;
	}
	
	/*
	public void importEntitiesFromCharArray(char[] entityArray)
	{
		for(int i = 0; i < entityArray.length; i++)
		{
			int x = (int)getTileLocation(i).getX();
			int y = (int)getTileLocation(i).getY();
			if(entityArray[i] == '1' && characters.length>0)
				addPlayer(x, y, KeyMap.getPlayer1KeysFromFile(), 1);
			else if(entityArray[i] == '2' && characters.length > 1)
				addPlayer(x, y, KeyMap.getPlayer2KeysFromFile(), 2);
			else if(entityArray[i] == '3' && characters.length > 2)
				addPlayer(x, y, KeyMap.getPlayer3KeysFromFile(), 3);
			else if(entityArray[i] == '4' && characters.length > 3)
				addPlayer(x, y, KeyMap.getPlayer4KeysFromFile(), 4);
		}
	}
	
	public void addPlayer(int x, int y, KeyMap keys, int number)
	{
		switch(characters[number-1])
		{
			case "Unicorn":
				entities.add(new EntityUnicorn(game, x, y, keys, number));
				break;
			case "Panda":
				entities.add(new EntityPanda(game, x, y, keys, number));
				break;
			case "Bird":
				entities.add(new EntityBird(game, x, y, keys, number));
				break;
			case "Bunny":
				entities.add(new EntityBunny(game, x, y, keys, number));
				break;
		}
	}
	*/
	
	private void importEntitiesFromEntityArray(char[] entityArray)
	{
		Point[] temp = new Point[9];
		graphics.clear();
		for(int i = 0; i < entityArray.length; i++)
		{
			int x = (int)getTileLocation(i).getX();
			int y = (int)getTileLocation(i).getY();
			if(entityArray[i] == '1')
				temp[0]=new Point(x, y);
			else if(entityArray[i] == '2')
				temp[1]=new Point(x, y);
			else if(entityArray[i] == '3')
				temp[2]=new Point(x, y);
			else if(entityArray[i] == '4')
				temp[3]=new Point(x, y);
			else if(entityArray[i] == '5')
				temp[4]=new Point(x, y);
			else if(entityArray[i] == '6')
				temp[5]=new Point(x, y);
			else if(entityArray[i] == '7')
				temp[6]=new Point(x, y);
			else if(entityArray[i] == '8')
				temp[7]=new Point(x, y);
			else if(entityArray[i] == '9')
				temp[8]=new Point(x, y);
			else if(entityArray[i] == LOLLIPOP)
			{
				graphics.add(new Pair<Character, Point>(LOLLIPOP, new Point(x, y)));
			}
			else if(entityArray[i] == GUMDROP)
				graphics.add(new Pair<Character, Point>(GUMDROP, new Point(x, y)));;
		}
		
		int num = 0;
		//Shrink Array
		for(int i = 0; i < temp.length; i++)
		{
			if(temp[i]!=null)
				num++;
		}
		
		spawns = new Point[num];
		for(int i = 0; i < num; i++)
		{
			spawns[i] = temp[i];
		}
	}

	/*
	public void setGame(Game game)
	{
		this.game = game;
		if(Game.DEBUG)
			game.drawables.add(this);
		importEntitiesFromCharArray(rawEntities);
	}
	*/
	public int getWidth()
	{
		return width;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public String getName()
	{
		return name;
	}

	public BufferedImage getBackground()
	{
		return background;
	}

	public boolean hasOwnBackground()
	{
		return ownBackground;
	}
	
	public void setBackground(BufferedImage background)
	{
		this.background = background;
		ownBackground = true;
	}

	public BufferedImage getSpecialBackground()
	{
		return specialBackground;
	}
	
	public boolean hasOwnBackgroundSpecial()
	{
		return ownBackgroundSpecial;
	}

	public void setSpecialBackground(BufferedImage specialBackground)
	{
		ownBackground = true;
		this.specialBackground = specialBackground;
	}
	
	public String toFileString()
	{
		return makeFileString(name, ""+width, ""+height);
	}
	
	public static String makeFileString(String name, String width, String height)
	{
		return name+"~" + width+"x"+height;
	}
	
	public boolean hasOwnSettings()
	{
		return settings!=null;
	}
	
	public Settings getSettings()
	{
		return settings;
	}
	
	public void setSettings(Settings s)
	{
		settings = s;
	}

	public char[] getTiles()
	{
		return tiles;
	}
	
	public char[] getEntities()
	{
		return entities;
	}

	public Point[] getSpawns()
	{
		return spawns;
	}
}

//class Tile
//{
//	private int x, y;
//	private char type;
//	public Tile(int x, int y, char type)
//	{
//		this.x = x;
//		this.y = y;
//		this.type = type;
//	}
//	
//	public int getY()
//	{
//		return y;
//	}
//	
//	public int getX()
//	{
//		return x;
//	}
//	
//	public char getType()
//	{
//		return type;
//	}
//	
//	public void steppedOn()
//	{
//}
//
//class TileState
//{
//	private int time;
//	Tile tile;
//	public TileState(Tile tile, int time)
//	{
//
//		this.time = time;
//	}
//	
//	public boolean readyToReplace()
//	{
//		return time <= 0;
//	}
//	
//	public void update()
//	{
//		time--;
//	}
//	
//	public Tile getTile()
//	{
//		return tile;
//	}
//}