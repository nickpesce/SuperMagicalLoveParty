package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;

import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ExtraEntityUpdate;
import np.supermagicalloveparty.server.ServerGame;




public abstract class Entity implements Drawable, Collidable, Updatable, Physicsable{

	protected double x, y, vX, vY, aX, aY;
	protected int drawX, drawY, drawWidth, drawHeight;
	protected double width, height = 0;
	protected Game game;
	protected boolean onGround, visible, bounceable, collidedRight, collidedLeft, collidable, physicsable, frictionable, noClip;
	public int synchIndex;
	protected Shape bounds;
	protected Shape defaultBounds;
	protected Point boundsOffset;
	protected Point defaultBoundsOffset;
	protected CopyOnWriteArrayList<Collidable> collisions;
	protected AnimationManager animations;
	protected byte direction;//might have to change to angles if ever do arrows or something like that...
	
	public final static byte 
		LEFT = -1,//must stay -1 and 1. multiply by width for flipping
		RIGHT = 1;
	
	public Entity(Game g, double x, double y, double width, double height, Shape bounds)
	{
		game = g;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.collidable = true;
		this.physicsable = true;
		this.frictionable = true;//These booleans can be set with their setters after creation
		this.bounds = bounds;
		defaultBounds = bounds;
		boundsOffset = new Point(bounds.getX() - x, bounds.getY() - y);
		defaultBoundsOffset = new Point(boundsOffset.getX(), boundsOffset.getY());
		direction = RIGHT;
		bounceable = false;
		onGround = true;
		setAy(game.settings.getGravity()/Math.pow(GameLoop.BASE_SPEED, 2));
		collisions = new CopyOnWriteArrayList<Collidable>();
		animations = new AnimationManager(this);
		addAnimations();
		synchIndex= -1;
		g.updatables.add(this);
		if(collidable)
		{
			g.collidables.add(this);
		}
		if(physicsable)
		{
			g.physicsables.add(this);
		}
		g.drawables.add(this);
		visible = true;
	}
	
	public Entity(Game g, double x, double y, double width, double height, boolean rectangle)
	{
		this(g, x, y, width, height, rectangle? new Rectangle(x, y, width, height): new Circle(x, y, width/2.0));
	}
	
	@Override
	public boolean collided(Collidable c)
	{
		if(bounds.intersects(c.getBounds()))
		{
			return true;
		}
		this.onUnCollided(c);
		c.onUnCollided(this);
		return false;
	}
	
	@Override
	public void onHit(Collidable c)
	{
		collisions.add(c);
	}
	
	@Override
	public void onUnCollided(Collidable c)
	{
		collisions.remove(c);
	}
	
	@Override
	public boolean alreadyCollided(Collidable c)
	{
		return collisions.contains(c);
	}

	@Override
	public Shape getBounds()
	{
		return bounds;
	}
	
	/**
	 * changes the collision bounds of the entity.
	 * @param widthRatio number to multiply default bounds width by
	 * @param heightRatio number to multiply the default bounds height by. Does nothing for circle bounds.
	 * @param xRatio proportion of bounds width to offset x.
	 * @param yRatio proportion of bounds height to offset y. ( .5 will half the size of the original)
	 */
	protected boolean adjustBounds(double widthRatio, double heightRatio, double xRatio, double yRatio)
	{
		double newBoX = xRatio*defaultBounds.getWidth() + defaultBoundsOffset.getX();
		double newBoY = yRatio*defaultBounds.getHeight() + defaultBoundsOffset.getY();
		double newWidth = defaultBounds.getWidth()*widthRatio;
		double newHeight = defaultBounds.getHeight()*heightRatio;
		//if(widthRatio > 1 || heightRatio > 1 || xRatio != 0 || yRatio != 0)
		//{
		for(int r = (int)(newBoX + x); r < newWidth + x; r++)
		{
			for(int c = (int)(newBoY + y); c < newHeight + y; c++)
			{
				if(canCollideInside(game.level.getTileAtLocation(r, c)))
					return false;
			}
		}
		//}
		boundsOffset.setX(newBoX);
		boundsOffset.setY(newBoY);
		if(bounds instanceof Rectangle)
		{
			bounds = new Rectangle(x + boundsOffset.getX(), y + boundsOffset.getY(), newWidth, newHeight);
		}
		else
			bounds = new Circle(x + boundsOffset.getX(), y + boundsOffset.getY(), newWidth / 2);
		return true;
	}
	
	protected void defaultBounds()
	{
		bounds.setWidth(defaultBounds.getWidth());
		bounds.setHeight(defaultBounds.getHeight());
		boundsOffset.setX(defaultBoundsOffset.getX());
		boundsOffset.setY(defaultBoundsOffset.getY());
		bounds.setX(x + boundsOffset.getX());
		bounds.setY(y + boundsOffset.getY());

	}
	
	protected void checkCollisions()
	{
		//TODO should collidables be with updatables and physicsables in the game class? What if it isnt an entity(somehow)
		for(Collidable c : game.collidables)
		{
			if(c != this && this.collided(c) && !this.alreadyCollided(c))
			{
				this.onHit(c);
				c.onHit(this);
			}
		}
	}
	
	@Override
	public void updatePhysics()
	{
		CollisionEvent ce;
		
		if(!noClip &&(ce = isCollidedWithTerrain(game.level)) != null)
		{
			//System.out.println(System.currentTimeMillis() + " "+ this + " s: " + ce.getSide() + " og:  " + onGround + " xC: " + ce.getTilePoint().getX() + " yC: " + ce.getTilePoint().getY() + " x: " + x + " y: " + y + "vX: " + vX + " vy: " + vY + " distance: " + ce.getDistance());
			
			switch (ce.getSide())
			{
				case BOTTOM :
					onCollideBottom(ce);
					return;
				case TOP :
					onCollideTop(ce);
					return;
				case LEFT :
					onCollideLeft(ce);
					
					return;
				case RIGHT :
					onCollideRight(ce);
					return;
				case CORNER_TOP_LEFT:
					System.out.println("TOP LEFT");
					setX(ce.getTilePoint().getX() + 1);
					setY(ce.getTilePoint().getY()+ 1.0001);
					if(getBounceable())
					{
						setVx(-getVx());
						setVy(-getVy());
					}else
					{
						setVy(0);
						collidedLeft = true;
					}
					return;
				case CORNER_TOP_RIGHT:
					System.out.println("TOP RIGHT");

					setY(ce.getTilePoint().getY()+ 1.0001);
					setX(ce.getTilePoint().getX() - bounds.getWidth());
					if(getBounceable())
					{
						setVx(-getVx());
						setVy(-getVy());
					}
					else
					{
						setVy(0);
						collidedRight = true;
					}
					return;
				case CORNER_BOTTOM_LEFT:
					System.out.println("BOTTOM LEFT");

					setX(ce.getTilePoint().getX() + 1);
					setY(ce.getCollisionPoint().getY() - bounds.getHeight());
					if(getBounceable())
					{
						setVx(-getVx());
						setVy(-getVy());
					}
					else
					{
						setVy(0);
						//collidedLeft = true;
					}
					return;
				case CORNER_BOTTOM_RIGHT:
					System.out.println("BOTTOM RIGHT");

					setY(ce.getCollisionPoint().getY() - bounds.getHeight());
					setX(ce.getTilePoint().getX() - bounds.getWidth());
					if(getBounceable())
					{
						setVx(-getVx());
						setVy(-getVy());
					}else
					{
						onGround = true;
						setVy(0);
						//collidedRight = true;
					}
					return;
				case INSIDE :
					onCollideInside();
					return;
		    }			
		}
		normalYPhysicsUpdate();
		normalXPhysicsUpdate();
	}

	public void normalYPhysicsUpdate()
	{
		if(vY<0 || aY<0 || isFloating())
			onGround = false;
		if(!onGround)
		{
		    addY(getVy()); 
		    addVy(getAy());
		}else
			setVy(0);
	}
	
	public void normalXPhysicsUpdate()
	{

		if(!((collidedLeft && getVx() < 0)||(collidedRight && getVx() > 0)))
		{
			addX(getVx());
			addVx(getAx());
		}
		if((vX<0 || canMoveRight()) && collidedRight)
			collidedRight = false;
		else if((vX>0 || canMoveLeft()) && collidedLeft)
			collidedLeft = false;
		if(!frictionable)
			return;
		
		//TODO better friction.
		double friction = 10.0;
		if(isOnIce())
			friction = .1;
		else if(!onGround)
			friction = 2.5;
		
		if(getScaledVx() < -friction)
			addScaledVx(friction);
		else if(getScaledVx() > friction)
			addScaledVx(-friction);
		else
			setVx(0);
	}
	
	private CollisionEvent isCollidedWithTerrain(Level level)
    {
		CollisionEvent ce;
		//CollisionEvent closestCollision = null;
		CollisionEvent closestCollisionH = null;
		CollisionEvent closestCollisionV = null;
		double vXC = 0;
		//HORIZONTAL MOVEMENT.
		if(getVx()!=0)
		{
			if(getVx()>0)
			//MOVING RIGHT: Only Check Right Side For Collisions
			{
				for (int i = 0; i <= Math.ceil( bounds.getHeight() )-1; i++) 
			    {
			    	if ((ce = level.isObstacleBetween(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() + i , bounds.getX() + bounds.getWidth() + vX + .0001, bounds.getY() + i + vY - .0001, this)) != null) 
			    	{
			    		//double distance = MathHelper.distance(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() + i - .0001, ce.getPoint().getX(), ce.getPoint().getY());
			    		if(ce.getSide().equals(CollisionEvent.Side.RIGHT) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
			    		{
				    		if(closestCollisionH == null)
				    			closestCollisionH = ce;
				    		else if (ce.getDistance() < closestCollisionH.getDistance()) 
				    		{
				    			closestCollisionH = ce;
				    		}
			    		}
			    	}
			    }
			    if ((ce = level.isObstacleBetween(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() +  bounds.getHeight()  - .0001, bounds.getX() + bounds.getWidth() + vX - .0001, bounds.getY() +  bounds.getHeight()  - .0001 + vY, this)) != null)//TODO ghetto bug fix
			    {
		    		if (ce.getSide().equals(CollisionEvent.Side.RIGHT) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
					{
						//double distance = MathHelper.distance(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() +  bounds.getHeight()  - .0001, ce.getPoint().getX(), ce.getPoint().getY());
						if (closestCollisionH == null)
							closestCollisionH = ce;
						else if (ce.getDistance() < closestCollisionH.getDistance())
						{
							closestCollisionH = ce;
						}
					}
			    }
			}
			else
			//MOVING LEFT: Only Check Left Side For Collisions
			{
				for (int i = 0; i <= Math.ceil( bounds.getHeight() )-1; i++) 
			    {
			    	if ((ce = level.isObstacleBetween(bounds.getX() + .0001, bounds.getY() + i  , bounds.getX() + vX + .0001, bounds.getY() + i + vY - .0001, this)) != null) 
			    	{
			    		if (ce.getSide().equals(CollisionEvent.Side.LEFT) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
						{
							//double distance = MathHelper.distance(bounds.getX() + .0001, bounds.getY() + i - .0001, ce.getPoint().getX(), ce.getPoint().getY());
							if (closestCollisionH == null)
								closestCollisionH = ce;
							else if (ce.getDistance() < closestCollisionH.getDistance())
							{
								closestCollisionH = ce;
							}
						}
			    	}
			    }
			    if ((ce = level.isObstacleBetween(bounds.getX() + .0001, bounds.getY() +  bounds.getHeight()  - .0001, bounds.getX() + vX + .0001, bounds.getY() +  bounds.getHeight()  - .0001 + vY, this)) != null)//TODO ghetto bug fix
			    {
		    		if (ce.getSide().equals(CollisionEvent.Side.LEFT) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
					{
						//double distance = MathHelper.distance(bounds.getX() - .0001, bounds.getY() +  bounds.getHeight()  - .0001, ce.getPoint().getX(), ce.getPoint().getY());
						if (closestCollisionH == null)
							closestCollisionH = ce;
						else if (ce.getDistance() < closestCollisionH.getDistance())
						{
							closestCollisionH = ce;
						}
					}
			    }
			}
		}
		if(getVy()!=0)
		{
			if(collidedLeft || collidedRight)
				vXC = 0;
			else
				vXC = vX;
			
			if(getVy()>0)
			//MOVING DOWN: Only Check Bottom For Collisions
			{
				for (int i = 0; i <= Math.ceil(bounds.getWidth())-1; i++) 
			    {
			    	if ((ce = level.isObstacleBetween(bounds.getX() + i, bounds.getY() +  bounds.getHeight()  -.0001 , bounds.getX() + i + vXC , bounds.getY() +  bounds.getHeight()  + vY - .0001, this)) != null) 
			    	{
			    		if (ce.getSide().equals(CollisionEvent.Side.BOTTOM) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
						{
							//System.out.println("Obstacle between " + (x+i) + " , " + (y+j-.0001) + " and " + (x+i+vXC) + " , " + (y+j+vY-.0001));
							//	double distance = MathHelper.distance(bounds.getX() + i, bounds.getY() +  bounds.getHeight()  - .0001, ce.getPoint().getX(), ce.getPoint().getY());
							if (closestCollisionV == null)
								closestCollisionV = ce;
							else if (ce.getDistance() < closestCollisionV.getDistance())
							{
								closestCollisionV = ce;
							}
						}
			    	}
			    }
			    if ((ce = level.isObstacleBetween(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() +  bounds.getHeight()  - .0001, bounds.getX() + bounds.getWidth() + vXC - .0001, bounds.getY() +  bounds.getHeight()  - .0001 + vY, this)) != null)//TODO ghetto bug fix
			    {
		    		//System.out.println("Obstacle between " + (bounds.getX() + bounds.getWidth() - .0001) + " , " + (y +  bounds.getHeight()  - .0001) + " and " + ( bounds.getX() + bounds.getWidth() + vXC - .0001) + " , " + (y +  bounds.getHeight()  - .0001 + vY) + " (END)");

		    		if (ce.getSide().equals(CollisionEvent.Side.BOTTOM) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
					{
						//double distance = MathHelper.distance(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() +  bounds.getHeight()  - .0001, ce.getPoint().getX(), ce.getPoint().getY());
						if (closestCollisionV == null)
							closestCollisionV = ce;
						else if (ce.getDistance() < closestCollisionV.getDistance())
						{
							closestCollisionV = ce;
						}
					}
			    }
			}
			else
			//MOVING UP: Only Check Top For Collisions
			{
				for (int i = 0; i <= Math.ceil(bounds.getWidth())-1; i++) 
			    {
			    	if ((ce = level.isObstacleBetween(bounds.getX() + i + .0001, bounds.getY() - .0001, bounds.getX() + i + vXC + .0001, bounds.getY() + vY - .0001, this)) != null) 
			    	{
			    		if (ce.getSide().equals(CollisionEvent.Side.TOP) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
						{
							//System.out.println("Obstacle between " + (x+i) + " , " + (y+j-.0001) + " and " + (x+i+vXC) + " , " + (y+j+vY-.0001));
							//double distance = MathHelper.distance(bounds.getX() + i, bounds.getY() - .0001, ce.getPoint().getX(), ce.getPoint().getY());
							if (closestCollisionV == null)
								closestCollisionV = ce;
							else if (ce.getDistance() < closestCollisionV.getDistance())
							{
								closestCollisionV = ce;
							}
						}
			    	}
			    }
			    if ((ce = level.isObstacleBetween(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() - .0001, bounds.getX() + bounds.getWidth() + vXC-.0001, bounds.getY() - .0001 + vY, this)) != null)//TODO ghetto bug fix
			    {
		    		//System.out.println("Obstacle between " + (bounds.getX() + bounds.getWidth() - .0001) + " , " + (y +  bounds.getHeight()  - .0001) + " and " + ( bounds.getX() + bounds.getWidth() + vXC - .0001) + " , " + (y +  bounds.getHeight()  - .0001 + vY) + " (END)");

		    		if (ce.getSide().equals(CollisionEvent.Side.TOP) || ce.getSide().equals(CollisionEvent.Side.INSIDE))
					{
						//double distance = MathHelper.distance(bounds.getX() + bounds.getWidth() - .0001, bounds.getY() - .0001, ce.getPoint().getX(), ce.getPoint().getY());
						if (closestCollisionV == null)
							closestCollisionV = ce;
						else if (ce.getDistance() < closestCollisionV.getDistance())
						{
							closestCollisionV = ce;
						}
					}
			    }
			}
		}
		
		if(closestCollisionH == null)
		{
			if(closestCollisionV == null)
				return null;
			else
				return closestCollisionV;
		}else
		{
			if(closestCollisionV == null)
				return closestCollisionH;
		}
		
		if(closestCollisionH.getSide() == CollisionEvent.Side.INSIDE)
			return closestCollisionH;
		else if(closestCollisionV.getSide() == CollisionEvent.Side.INSIDE)
			return closestCollisionV;
		
		//TODO test corner collisions
		if(closestCollisionH.getCollisionPoint().equals(closestCollisionV.getCollisionPoint()))
		{
			if(closestCollisionV.getSide() == CollisionEvent.Side.TOP)
			{
				if(closestCollisionH.getSide() == CollisionEvent.Side.RIGHT)
				{
					return new CollisionEvent(new Point(closestCollisionH.getTilePoint().getX(), closestCollisionV.getTilePoint().getY()), new Point( closestCollisionH.getTilePoint().getX(),  closestCollisionV.getTilePoint().getY()+1), CollisionEvent.Side.CORNER_TOP_RIGHT, MathHelper.distance(x, y, closestCollisionH.getTilePoint().getX(),  closestCollisionV.getTilePoint().getY()+1), closestCollisionH.getTile());

				}
				else//LEFT
				{
					return new CollisionEvent(new Point(closestCollisionH.getTilePoint().getX(), closestCollisionV.getTilePoint().getY()), new Point(closestCollisionH.getTilePoint().getX() + 1,  closestCollisionV.getTilePoint().getY() + 1), CollisionEvent.Side.CORNER_TOP_LEFT, MathHelper.distance(x, y, closestCollisionH.getTilePoint().getX() + 1,  closestCollisionV.getTilePoint().getY() + 1), closestCollisionH.getTile());
				}
			}
			else//BOTTOM
			{
				if(closestCollisionH.getSide() == CollisionEvent.Side.RIGHT)
				{
					return new CollisionEvent(new Point(closestCollisionH.getTilePoint().getX(), closestCollisionV.getTilePoint().getY()), new Point(closestCollisionH.getTilePoint().getX(), closestCollisionV.getTilePoint().getY()), CollisionEvent.Side.CORNER_BOTTOM_RIGHT, MathHelper.distance(x, y, closestCollisionH.getTilePoint().getX(),  closestCollisionV.getTilePoint().getY()), closestCollisionH.getTile());
				}
				else//LEFT
				{
					return new CollisionEvent(new Point(closestCollisionH.getTilePoint().getX(), closestCollisionV.getTilePoint().getY()), new Point(closestCollisionH.getTilePoint().getX() + 1,  closestCollisionV.getTilePoint().getY()), CollisionEvent.Side.CORNER_BOTTOM_LEFT, MathHelper.distance(x, y, closestCollisionH.getTilePoint().getX() + 1,  closestCollisionV.getTilePoint().getY()), closestCollisionH.getTile());
				}
			}
			
		}else
		if(closestCollisionH.getDistance() > closestCollisionV.getDistance())
			return closestCollisionV;
		return closestCollisionH;
	}
	
	public boolean canCollideBottom(int tile)
	{
		if((this instanceof EntityLiving && ((EntityLiving)this).isSquatting() && tile == Level.UP) || tile == Level.EMPTY || tile == Level.BORDER || noClip)
			return false;
		return true;
	}
	
	public boolean canCollideTop(int tile)
	{
		if(tile == Level.UP || tile == Level.EMPTY || tile == Level.BORDER || noClip)
			return false;
		return true;
	}
	
	public boolean canCollideLeft(int tile)
	{
		if(tile == Level.UP || collidedLeft || tile == Level.EMPTY || tile == Level.BORDER || noClip)
			return false;
		return true;
	}
	
	public boolean canCollideRight(int tile)
	{
		if(tile == Level.UP || collidedRight || tile == Level.EMPTY || tile == Level.BORDER || noClip)
			return false;
		return true;
	}
	
	public boolean canCollideInside(int tile)
	{
		if(tile == Level.UP || tile == Level.EMPTY || tile == Level.BORDER || noClip)
			return false;
		return true;
	}
	
	public boolean canMoveLeft()
	{
		for(int h = (int)(bounds.getY()); h <= (int)(bounds.getY()+bounds.getHeight()); h++)
		{
			if(game.level.getTileAtLocation((bounds.getX()-1), h)!=Level.EMPTY)
				return false;
		}
		return true;
	}
	
	public boolean canMoveRight()
	{
		for(int h = (int)(bounds.getY()); h <= (int)(bounds.getY()+bounds.getHeight()); h++)
		{
			if(game.level.getTileAtLocation((bounds.getX() + bounds.getWidth()), h)!=Level.EMPTY)
				return false;
		}
		return true;
	}
	
	protected void onCollideTop(CollisionEvent ce)
	{
		setY(ce.getTilePoint().getY() - (boundsOffset.getY()>0?boundsOffset.getY():boundsOffset.getY()) + 1.0001);
		if(ce.getTile() == Level.DAMAGE_AND_KNOCKBACK)
			setScaledVy(75);
		else if (getBounceable())
			setVy(-getVy());
		else
			setVy(0);
	}
	
	protected void onCollideBottom(CollisionEvent ce)
	{
		if (!onGround)
		{
			setY(ce.getCollisionPoint().getY() - (boundsOffset.getY()<0?boundsOffset.getY():boundsOffset.getY())- bounds.getHeight());
			if(this instanceof EntityLiving && ((EntityLiving)this).isJumping())
			{
				((EntityLiving)this).land();
				collidedLeft = collidedRight = false;
			}
			if(ce.getTile() == Level.DAMAGE_AND_KNOCKBACK)
			{
				setScaledVy(-50);
			}
			else if (getBounceable())
				setScaledVy(-(getVy()-aY));
			else
			{
				onGround = true;
				setVy(0);
			}
		}
	}
	
	protected void onCollideLeft(CollisionEvent ce)
	{
		setX(ce.getTilePoint().getX() - (boundsOffset.getX() < 0 ? boundsOffset.getX() : boundsOffset.getX()) + 1);
		if (getBounceable())
		{
			setVx(-getVx());
			addX(getVx());//TODO this is a bug. projectiles get stuck without it. Sometimes get this really weird error where red projectile balls split into two semi visible balls stuck on the wall
		}
		else
		{
			setVx(0);//May be temporary 0 if EntityLiving is running. Override method will set back to run speed. This stops only other vx sources.
			collidedLeft = true;
		}
	}
	
	protected void onCollideRight(CollisionEvent ce)
	{
		setX(ce.getTilePoint().getX() - (boundsOffset.getX() > 0 ? boundsOffset.getX() : boundsOffset.getX()) - bounds.getWidth());
		if (getBounceable())
		{
			setVx(-getVx());
			addX(getVx());//TODO this is a bug. projectiles get stuck without it.(but the get stuck anyway. Seems like this does help for some)
		}
		else
		{
			setVx(0);
			collidedRight = true;
		}
	}
	
	protected void onCollideInside()
	{
		setY(y-1);
		//onDestroy();
		//onGround = true;
	}
	
	@Override
	public void update()
	{
		animations.update();
		if((vX != 0 || !onGround) && this.collidable)
			checkCollisions();
		if(!game.level.inBounds(x, y, width, height) && !(this instanceof Player))
		{
			destroy();
		}
		if((this instanceof Player) && ((Player)this).isDead() == false && !game.level.inBoundsExcludingAbove(x, y, width, height))
		{
			((Player)this).respawn();
			game.consoleLog("[INFO] " +this + " went out of bounds, respawning.", Color.MAGENTA, false);
		}
	}
	
	@Override
	public void fineUpdate(double interpolation)
	{
		drawX = (int)((x+(getVx()*interpolation))*game.canvas.getScaleWithCamera());
		drawY = (int)((y+(getVy()*interpolation))*game.canvas.getScaleWithCamera());
		drawWidth = (int)(width*game.canvas.getScaleWithCamera());
		drawHeight = (int)(height*game.canvas.getScaleWithCamera());
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		if(game.debug)
		{
			g.setColor(Color.BLUE);
			bounds.draw(g, game.canvas.getScaleWithCamera());
		}
	}
	
	protected void addAnimations()
	{
	}
	
	public boolean isFloating()
	{
		for(int i = 0; i <= Math.ceil(bounds.getWidth()); i++)
		{ 
			char tile = game.level.getTileAtLocation(bounds.getX()+i, bounds.getY() + bounds.getHeight());
			if(tile != Level.EMPTY && tile != Level.BORDER)
				return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return true if every tile under entity is SOLID or empty/border(as long as there is at least one solid)
	 */
	public boolean isOnSolidTiles()
	{
		boolean solid = false;
		for(int i = 0; i <= Math.ceil(width); i++)
		{
			char t = game.level.getTileAtLocation(x+i, y + height);
			if(t == Level.SOLID)
				solid = true;
			else if(t != Level.EMPTY && t!= Level.BORDER)
				return false;
		}
		return solid;
	}
	
	/**
	 * 
	 * @return true if every tile under entity is ICE(or empty/border as long as there is at least 1 ice)
	 */
	public boolean isOnIce()
	{
		boolean ice = false;
		for(int i = 0; i <= Math.ceil(width); i++)
		{
			char t = game.level.getTileAtLocation(x+i, y + height);
			if(t == Level.ICE)
				ice = true;
			else if(t != Level.EMPTY && t!= Level.BORDER)
				return false;
		}
		return ice;
	}
	
	protected void destroy()
	{
		game.entityDestroyed(this);

		game.collidables.remove(this);
		game.updatables.remove(this);
		game.physicsables.remove(this);
		game.drawables.remove(this);
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean getBounceable() 
	{
		return bounceable;
	}
	
	@Override
	public void setBounceable(boolean bounceable)
	{
		this.bounceable = bounceable;
	}
	
	@Override
	public boolean getVisible()
	{
		return visible;
	}
	
	public int getDrawX()
	{
		return drawX;
	}
	
	public int getDrawY()
	{
		return drawY;
	}
	
	public int getDrawWidth()
	{
		return drawWidth;
	}
	
	public int getDrawHeight()
	{
		return drawHeight;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) 
	{
		bounds.setX(x + boundsOffset.getX());
		this.x = x;
	}

	public void addX(double x)
	{
		this.x += x;
		bounds.setX(this.x + boundsOffset.getX());
	}
	
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
		bounds.setY(y + boundsOffset.getY());
	}
	
	public void addY(double y) {
		this.y += y;
		bounds.setY(this.y + boundsOffset.getY());
	}

	/**
	 * converts vx to tiles per second, then returns
	 * @return Vx in tiles per second
	 */
	public double getScaledVx()
	{
		return vX*GameLoop.BASE_SPEED;
	}
	
	public double getVx() {
		return vX;
	}

	
	public void setVx(double vX) 
	{
		this.vX = vX;
		if(vX > 0)
			direction = RIGHT;
		else if(vX < 0)
			direction = LEFT;
	}
	
	public void setVxIfFaster(double vx)
	{
		if((vx/Math.abs(vx)) == (this.vX / Math.abs(this.vX)))
		{
			if(Math.abs(vx) > Math.abs(this.vX))
				setVx(vx);
		}
		else
			setVx(vx);
	}
	
	/**
	 * Automatically converts speed based on TPS
	 * @param vX in blocks per second
	 */
	
	public void setScaledVx(double vX) {
		setVx(vX/GameLoop.BASE_SPEED);
	}

	public void addVx(double vX) {
		setVx(this.vX + vX);
	}
	
	public void addScaledVx(double vX) {
		setVx(this.vX + vX/GameLoop.BASE_SPEED);
	}
	
	public double getVy() {
		return vY;
	}
	
	public double getScaledVy() {
		return vY*GameLoop.BASE_SPEED;
	}

	public void setVy(double vY) {
		this.vY = vY;
	}
	
	public void setScaledVy(double vY) {
		this.vY = vY/GameLoop.BASE_SPEED;
	}
	
	public void addVy(double vY) {
		setVy(this.vY + vY);
	}
	
	public void setVyIfFaster(double vy)
	{
		if((vy/Math.abs(vy)) == (this.vY / Math.abs(this.vY)))
				if(Math.abs(vy) > Math.abs(this.vY))
					setVy(vy);
		else
			setVy(vy);
	}
	
	public void addScaledVy(double vY){
		setVy(this.vY + vY/GameLoop.BASE_SPEED);
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double w) {
		this.width = w;
		bounds.setWidth(w);
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
		bounds.setHeight(height);
	}
	
	public byte getDirection()
	{
		return direction;
	}
	
	public void setDirection(byte direction)
	{
		this.direction = direction;
	}

	public double getAx() {
		return aX;
	}

	public void setAx(double aX) {
		this.aX = aX;
	}
	
	public void addAx(double aX) {
		this.aX += aX;
	}
	
	public double getScaledAx() {
		return aX*GameLoop.BASE_SPEED;
	}

	public void setScaledAx(double aX) {
		this.aX = aX/GameLoop.BASE_SPEED;
	}
	
	public void addScaledAx(double aX) {
		this.aX += aX/GameLoop.BASE_SPEED;
	}
	
	public double getAy() {
		return aY;
	}

	public void setAy(double aY) {
		this.aY = aY;
	}
	
	public void addAy(double aY)
	{
		this.aY += aY/GameLoop.BASE_SPEED;
	}
	
	public double getScaledAy() {
		return aY*GameLoop.BASE_SPEED;
	}

	public void setScaledAy(double aY) {
		this.aY = aY/GameLoop.BASE_SPEED;
	}
	
	public void addScaledAy(double aY)
	{
		this.aY += aY/GameLoop.BASE_SPEED;
	}
	
	public boolean isCollidedLeft()
	{
		return collidedLeft;
	}
	
	public boolean isCollidedRight()
	{
		return collidedRight;
	}

	public boolean isCollidable()
	{
		return collidable;
	}

	public void setCollidable(boolean collidable)
	{
		this.collidable = collidable;
	}

	public boolean isPhysicsable()
	{
		return physicsable;
	}

	public void setPhysicsable(boolean physicsable)
	{
		this.physicsable = physicsable;
	}

	public boolean isFrictionable()
	{
		return frictionable;
	}
	
	public boolean isNoClip()
	{
		return noClip;
	}
	
	public void setNoClip(boolean f)
	{
		noClip = f;
	}

	public void setFrictionable(boolean frictionable)
	{
		this.frictionable = frictionable;
	}

	public int getSynchIndex()
	{
		return synchIndex;
	}

	public void setSynchIndex(int synchIndex)
	{
		this.synchIndex = synchIndex;
	}

	public ExtraEntitySpawn getExtraSpawn()
	{
		return null;
	}
	
}
