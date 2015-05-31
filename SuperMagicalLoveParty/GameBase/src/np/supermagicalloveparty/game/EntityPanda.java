package np.supermagicalloveparty.game;
import java.awt.image.BufferedImage;
import java.util.Calendar;


public class EntityPanda extends Player
{
	private boolean canDive;
	public EntityPanda(Game g, double x, double y, KeyMap keys, int number)
	{
		super(g, x, y, 6, 6, new Rectangle(x, y, 6, 6), keys, number);
		canDive = false;
		if(game.mode.equals(Game.Mode.HEALTH))
		{
			maxPoints = points = 18;
		}
		else if(game.mode.equals(Game.Mode.SATISFACTION))
		{
			maxPoints = 7;
		}
		type = "Panda";
		maxAttackCd = 50;
		setJumpHeight(20);
	}

	@Override
	public boolean attack()
	{
		if(!canAttack())
			return false;
		super.attack();
		if(hasPowerupSpecialAttack())
			new EntityExplosion(game, x + width/2, y +height/2, 10.0, this, -10.0, 25, 1, 2, true, "Mega Hug");//TODO make panda pull in better. 
		attackCd = maxAttackCd;
		new EntityExplosion(game, x + width/2, y +height/2, 5.0, this, -5, 15, 1, 3*damageMultiplier, false, "Bear Hug");
		return true;
	}

	@Override
	public boolean attackCharged()
	{
		if(!canAttack())
			return false;
		super.attackCharged();
		attackCd = maxAttackCd;
		if(attackChargeTime > 720)
			attackChargeTime = 720;
		new EntityExplosion(game, x + width/2, y +height/2, 5.0, this, -7, (int)((attackChargeTime*.01)+ 15), 1, 3*damageMultiplier+(attackChargeTime/240.0), false, "Long Bear Hug");
		return true;
	}
	
	@Override
	public void update()
	{
		super.update();
		if(attackCharging)
			Particle.spawnMany(game, bounds.getX() + (bounds.getWidth()/2) , bounds.getY() + (bounds.getHeight()/2), 2, Particle.Type.HEART);
	}
	
	@Override
	public void respawn()
	{
		super.respawn();
		canDive = false;
	}
	
	@Override
	public void onCollideBottom(CollisionEvent ce)
	{
		if(canDive)
		{
			new EntityExplosion(game, x+(width/2), y+(height/2), width/2, this, 5.0, 20, 10, getScaledVy()/50.0, true, "Dive Hug");
			/*
			for(Collidable c : collisions)
			{
				if(c instanceof EntityLiving && !((EntityLiving)c).isEvading())
				{
					((EntityLiving)c).hurt(new DamageEvent(1, this, "Dive Hug"));
				}
			}
			*/
		}
		super.onCollideBottom(ce);
	}
	
	@Override
	public void squat()
	{
		if(isJumping())
			canDive = true;
		super.squat();
		//adjustBounds(1, .5, 0, .5);
	}
	
	@Override
	public void land()
	{
		super.land();
		canDive = false;
	}
	
	@Override
	public void stand()
	{
		super.stand();
		canDive = false;
	}
	
	@Override
	public void addAnimations()
	{
		super.addAnimations();
		BufferedImage[] run = {
				game.textures.getPandaBase(),
				game.textures.getPandaBase(),
				game.textures.getPandaBase(),
				game.textures.getPandaBase()};
			int[] runTimes = {5, 10, 5, 10};
			animations.addAnimation(new Animation(game, run, runTimes), AnimationManager.RUN);
			
			BufferedImage[] idle = {game.textures.getPandaBase()};
			BufferedImage[] idleS = {game.textures.getPandaBaseSpecial()};

			animations.addAnimation(new Animation(game, idle, null, idleS, null), AnimationManager.IDLE);
			
			BufferedImage[] jump = {game.textures.getPandaBase()};
			animations.addAnimation(new Animation(game, jump, null), AnimationManager.JUMP);
			
			BufferedImage[] squat = {game.textures.getPandaSquat()};
			animations.addAnimation(new Animation(game, squat, null), AnimationManager.SQUAT);
	}
}
