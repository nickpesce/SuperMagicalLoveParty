package np.supermagicalloveparty.game;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import np.supermagicalloveparty.server.EntityExplosionSpawnData;
import np.supermagicalloveparty.server.EntityHeartSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ServerGame;


public class EntityExplosion extends Entity
{

	private int timeLeft;
	private int duration;
	private int stun;
	private Player owner;
	private double radius;
	private double speed;
	private String name;
	private double damage;
	
	//TODO figure out explosions and server situation. Cancel all client produced explosions?
	public EntityExplosion(Game g, double x, double y, double radius, Player owner, double speed, int stun, int duration, double damage, boolean visible, String attackName)
	{
		super(g, x-radius, y-radius, radius*2, radius*2, false);
		timeLeft = duration;
		this.owner = owner;
		this.speed = speed;
		this.stun = stun;
		this.duration = duration;
		this.name = attackName;
		this.radius = radius;
		this.damage = damage;
		this.visible = visible;
		setAy(0);
		bounds = new Circle(x - radius, y - radius, radius);
		checkCollisions();
		animations.play(AnimationManager.ANIMATE);
		if(game instanceof ServerGame)
			((ServerGame)game).addSynchedEntity(this);
		//damage();
	}

	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		if(visible)
			g.drawImage(animations.getFrame(), drawX, drawY, drawWidth, drawHeight, null);
	}
	
	public void damage(EntityLiving e)
	{
		if(!e.equals(owner))
		{
			e.hit(new DamageEvent(damage, owner, name));
		}
	}
	
	public void onHit(Collidable c)
	{
		super.onHit(c);
		if(c instanceof EntityLiving)
		{
			EntityLiving e = (EntityLiving)c;
			if(stun > 0 && !e.equals(owner))
				e.stun(stun);
			//if(speed != 0)
				//knockback(e);
			damage(e);
		}

	}
	
	public void knockback(EntityLiving e)
	{
		if(e.equals(owner))
			return;
		double m = MathHelper.getSlope(((Circle)getBounds()).getCenter().getX(), ((Circle)getBounds()).getCenter().getY(), e.getX() + (e.getWidth()/2), e.getY() + (e.getHeight()/2));
		//m = b/a
		//a = bm
		//a^2 + b^2 = c^2
		//b = c/rad(m^2 +1)
		//a = cm/rad(m^2 + 1)
		
		//turned off vy. Only moves x now.
		e.setVyIfFaster(((-speed/GameLoop.BASE_SPEED)* Math.abs(m))/Math.pow(Math.pow(m, 2), .5));
		e.setVxIfFaster(((((Circle)getBounds()).getCenter().getX() > e.getX() + (e.getWidth()/2))? -1:1) * /*For signage*/((speed/GameLoop.BASE_SPEED))/Math.pow(Math.pow(m, 2), .5));
		//game.soundManager.playSound(SoundManager.POP);
	}
	
	@Override
	public void update()
	{
		super.update();
		//Caution: only decrase time one at a time. Must equal 0. -1 will not destroy.
		timeLeft--;
		if(timeLeft==0)
			destroy();
	}
	
	@Override
	public void addAnimations()
	{
		animations.addAnimation(new Animation(game, new BufferedImage[]{game.textures.getHeart()}, null), AnimationManager.ANIMATE);
	}
	
	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.EXPLOSION, new EntityExplosionSpawnData(x, y, radius, speed, stun, damage, owner.number, duration, visible, name, synchIndex));
	}
}
