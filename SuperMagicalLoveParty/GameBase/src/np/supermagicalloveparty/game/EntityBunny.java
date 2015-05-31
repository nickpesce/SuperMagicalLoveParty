package np.supermagicalloveparty.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import np.supermagicalloveparty.server.ExtraPowerupAquire;
import np.supermagicalloveparty.server.Packet;
import np.supermagicalloveparty.server.ServerGame;

public class EntityBunny extends Player
{
	BunnyGhostPath jumpPath;
	int jumpChargeTime;
	boolean jumpingAttack, jumpCanceled;
	int vegetable;
	Random random;
	
	public static final int
	NOTHING = -1,
	CARROT = 0,
	TURNIP = 1,
	CABBAGE = 2;
	
	public EntityBunny(Game g, double x, double y, KeyMap keys, int number)
	{
		super(g, x, y, 4, 4, new Rectangle(x, y, 4, 4), keys, number);
		setJumpHeight(30);
		random = new Random();
		vegetable = NOTHING;
		type = "Bunny";
		jumpPath = new BunnyGhostPath(this);
		maxAttackCd = 60;
	}

	@Override
	public boolean attack()
	{
		if(game.multiplayer)
		{
			vegetable = NOTHING;//WILL BE SET TO ACTUAL FROM SERVER PACKET
			attackCd = maxAttackCd/10;
			super.attack();//Sends packet to server, then server will tell vegetable
			return false;
		}
		super.attack();
		if(!canAttack())
			return false;
		attackCd = maxAttackCd/10;
		if(vegetable != NOTHING)
		{
			double x =  direction == LEFT? bounds.getX() - 1 :  bounds.getX() + bounds.getWidth() + 1;
			switch(vegetable)
			{
				case CARROT:
					new EntityProjectileCarrot(game, this, x, bounds.getY() + 1, direction, 2*damageMultiplier, hasPowerupSpecialAttack());
					break;
				case TURNIP:
					new EntityProjectileTurnip(game, this, x, bounds.getY() + 1, direction, 2.5*damageMultiplier, hasPowerupSpecialAttack());
					break;
				case CABBAGE:
					new EntityExplosion(game, x, y, hasPowerupSpecialAttack()? 6:3, this, 0, 7, 0, 3*damageMultiplier, false, "Cabbage cuddle");
					break;
			}
			vegetable = NOTHING;
		}
		else if(isOnSolidTiles())
		{
			vegetable = random.nextInt(3);
			if(game instanceof ServerGame)
			{
				((ServerGame)game).server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(number, vegetable+10)));
			}
		}
		return true;
	}
	
	@Override
	public boolean attackCharged()
	{
		super.attackCharged();
		if(!jumpPath.isValid() || !onGround)
			cancelJumpAttack();
		if(jumpCanceled)
		{
			jumpCanceled = false;
			return false;
		}
		attackCd = maxAttackCd/(hasPowerupSpecialAttack()?3:1);
		jumpPath.visible = false;
		setVx(direction*jumpChargeTime/30.0);
		setVy(-jumpChargeTime/20.0);
		jumpingAttack = true;
		stopRunning();
		setFrictionable(false);
		return true;
	}
	
	@Override
	public void jump()
	{
		if(!jumpingAttack)
			super.jump();
	}
		
	@Override
	public void runLeft()
	{
		if(!jumpingAttack)
			super.runLeft();
	}
	
	@Override
	public void runRight()
	{
		if(!jumpingAttack)
			super.runRight();
	}
	
	@Override
	public void update()
	{
		super.update();
		if(attackCharging && attackChargeTime>10)
		{
			jumpPath.visible = true;
			jumpChargeTime = attackChargeTime*(hasPowerupSpecialAttack()? 2:1);
			if(jumpChargeTime>300)
				jumpChargeTime = 300;
			jumpPath.repopulatePositions(direction*jumpChargeTime/30.0, -jumpChargeTime/20.0);
		}
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		int offsetY = (int)(drawHeight/2.0);
		int offsetX = (int)(drawWidth/3.0);
		if(direction == RIGHT)
		{
			offsetX = drawWidth - (int)(drawWidth/3.0);
		}
		switch(vegetable)
		{
			case CARROT:
				g.drawImage(game.textures.getCarrot(), drawX + offsetX, drawY + offsetY, (int)(drawWidth/3.0) * direction, (int)(drawWidth/3.0), null);
				break;
			case TURNIP:
				g.drawImage(game.textures.getTurnip(), drawX + offsetX, drawY + offsetY, (int)(drawWidth/3.0) * direction, (int)(drawWidth/3.0), null);
				break;
			case CABBAGE:
				g.drawImage(game.textures.getCabbage(), drawX + offsetX, drawY + offsetY, (int)(drawWidth/3.0) * direction, (int)(drawWidth/3.0), null);
				break;
		}
	}
	
	@Override
	public void addAnimations()
	{
		super.addAnimations();
			int[] runTimes = {3, 3, 3, 3, 3};
			animations.addAnimation(new Animation(game, game.textures.getBunnyRun(), runTimes), AnimationManager.RUN);
			
			BufferedImage[] idle = {game.textures.getBunnyBase()};
			BufferedImage[] idleS = {game.textures.getBunnyBaseSpecial()};

			animations.addAnimation(new Animation(game, idle, null, idleS, null), AnimationManager.IDLE);
			
			BufferedImage[] jump = {game.textures.getBunnyBase()};
			animations.addAnimation(new Animation(game, jump, null), AnimationManager.JUMP);
			
			BufferedImage[] squat = {game.textures.getBunnySquat()};
			animations.addAnimation(new Animation(game, squat, null), AnimationManager.SQUAT);
	}
	@Override
	public void squat()
	{
		if(attackCharging)
			cancelJumpAttack();
		else if(!jumpingAttack)
			super.squat();
		
	}
	@Override
	public void respawn()
	{
		super.respawn();
		jumpChargeTime = 0;
		jumpingAttack = false;
		vegetable = NOTHING;
	}
	@Override
	protected void onCollideBottom(CollisionEvent ce)
	{
		super.onCollideBottom(ce);
		if(jumpingAttack)
			landJumpAttack();
	}
	
	@Override
	protected void onCollideLeft(CollisionEvent ce)
	{
		super.onCollideLeft(ce);
		if(jumpingAttack)
			landJumpAttack();
	}
	
	@Override
	protected void onCollideRight(CollisionEvent ce)
	{
		super.onCollideRight(ce);
		if(jumpingAttack)
			landJumpAttack();
	}
	
	@Override
	protected void onCollideTop(CollisionEvent ce)
	{
		super.onCollideTop(ce);
		if(jumpingAttack)
			landJumpAttack();
	}
	
	public void landJumpAttack()
	{
		new EntityExplosion(game, x, y, 4, this, 0, 15, 1, (jumpChargeTime/100.0 + 3.5), true, "love jump");
		setVx(0);
		jumpingAttack = false;
		setFrictionable(true);
	}

	public void cancelJumpAttack()
	{
		jumpCanceled = true;
		jumpingAttack = false;
		setFrictionable(true);
		jumpPath.visible = false;
		attackCd = 0;
	}
	
	@Override
	protected void destroy()
	{
		jumpPath.destroy();
		super.destroy();
	}

	public void setVegetable(int v)
	{
		vegetable = v;
	}
}

class BunnyGhostPath implements Drawable
{
	ArrayList<Shape> positions;
	EntityBunny bunny;
	boolean visible = false;
	boolean valid;
	
	public BunnyGhostPath(EntityBunny bunny)
	{
		this.bunny = bunny;
		this.positions = new ArrayList<Shape>(100);
		bunny.game.drawables.add(this);
		valid = true;
	}
	
	public void repopulatePositions(double jumpVx, double jumpVy)
	{
		valid = true;
		positions.clear();
		positions.add(bunny.bounds);
		double x = bunny.getX();
		double y = bunny.getY();
		double rectX, rectY;
		int i = 1;
		while(!bunny.game.level.isInsideTerrain(positions.get(i-1), ((jumpVy + (bunny.getAy() * i))<0)))
		{
			rectX = (jumpVx * i + x);
			rectY = (jumpVy*i + 0.5*bunny.getAy()*i*i) + y;
			if(rectX<0 || rectX + bunny.bounds.getWidth() > bunny.game.level.getWidth())
			{
				valid = false;
				return;
				//bunny.cancelJumpAttack();
				//return;
			}else
			{
				positions.add(new Rectangle(rectX, rectY, bunny.bounds.getWidth(), bunny.bounds.getHeight(), true));
				i++;
			}
		}
		i--;
		positions.remove(i);
		//positions.set(i, new Circle(jumpVx * i + x, jumpVy*i + 0.5*bunny.getAy()*i*i + y, 4));
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		if(valid)
			g.setColor(new Color(0, 100, 250, 150));
		else
			g.setColor(new Color(250, 0, 0, 150));
		Point last = new Point(bunny.getBounds().getX()+(bunny.getBounds().getWidth()/2), bunny.getBounds().getY()+(bunny.getBounds().getHeight()/2));
		for(Shape s : positions)
		{
			Point p = new Point(s.getX() + (s.getWidth()/2), s.getY() + (s.getHeight()/2));
			g.drawLine((int)(last.getX()*bunny.game.canvas.getScaleWithCamera()), (int)(last.getY()*bunny.game.canvas.getScaleWithCamera()), (int)(p.getX()*bunny.game.canvas.getScaleWithCamera()), (int)(p.getY()*bunny.game.canvas.getScaleWithCamera()));
			last = p;
			//s.draw(g, bunny.game.canvas.getScaleWithCamera());
		}
		g.setColor(new Color(255, 0, 0, 100));
		g.fillOval((int)(last.getX()*bunny.game.canvas.getScaleWithCamera()) - (int)(4*bunny.game.canvas.getScaleWithCamera()), (int)(last.getY()*bunny.game.canvas.getScaleWithCamera())- (int)(4*bunny.game.canvas.getScaleWithCamera()), (int)(8*bunny.game.canvas.getScaleWithCamera()), (int)(8*bunny.game.canvas.getScaleWithCamera()));
	}

	@Override
	public boolean getVisible()
	{
		return visible;
	}
	
	public boolean isValid()
	{
		return valid;
	}
	
	public void destroy()
	{
		bunny.game.drawables.remove(this);
	}
}