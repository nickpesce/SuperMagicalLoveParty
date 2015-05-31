package np.supermagicalloveparty.game;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import np.supermagicalloveparty.server.EntityHeartSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ServerGame;


public class EntityProjectileHeart extends Entity{

	Player owner;
	double originalX;
	int reverseWave = 1;
	double waveCoefficient;
	double radius;
	double damage;
	
	public EntityProjectileHeart(Game g, Player p, double x, double y, double radius, int direction, double waveCoefficient, double damage)
	{
		super(g, x, y, radius*2, radius*2, false);
		this.radius = radius;
		originalX = x;
		owner = p;
		this.waveCoefficient = waveCoefficient;
		this.damage = damage;
		setNoClip(true);
		setScaledVx(direction*50);
		setAy(0);
		setVy(0);
		//setBounceable(true);
		setFrictionable(false);
		Particle.spawnMany(g, x, y, 20, game.specialMode? Particle.Type.RED_GOO : Particle.Type.FAIRY_DUST);
		animations.play(AnimationManager.ANIMATE);
		if(game instanceof ServerGame)
			((ServerGame)game).addSynchedEntity(this);
	}
	
	@Override
	public void onHit(Collidable c) 
	{
		super.onHit(c);
		if(c instanceof EntityLiving && c != owner && owner != null)
		{
			if(((EntityLiving)c).hit(new DamageEvent(damage, owner, game.strings.attack())))
			{
				((EntityLiving)c).stun(10);
				((EntityLiving)c).setScaledVy(-4);
				this.destroy();
			}
		}
	}

	@Override
	public void update()
	{
		super.update();
		damage += .05;
		//TODO heart hitting corner
		Particle.spawnMany(game, x+(width/2), y+(height/2), 3, game.specialMode? Particle.Type.RED_GOO:Particle.Type.HEART);
	}
	
	@Override
	public void fineUpdate(double interpolation)
	{
		setScaledVy((waveCoefficient * 100 * (Math.sin(Math.abs(originalX - x)/100.0*16*Math.PI))/4));
		super.fineUpdate(interpolation);
	}
	
	@Override
	public void updatePhysics()
	{
		super.updatePhysics();
	}
	
	@Override
	public void onCollideBottom(CollisionEvent ce)
	{
		super.onCollideBottom(ce);
		waveCoefficient*=-1;
	}
	
	@Override
	public void onCollideTop(CollisionEvent ce)
	{
		super.onCollideTop(ce);
		waveCoefficient *= -1;
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.drawImage(animations.getFrame(), drawX, drawY, drawWidth, (int)(getDrawHeight()*(game.specialMode? 1.5:1)), null);
		/*
		g.setColor(Color.RED);
		g.fillOval((int)(getX() * scale), (int)(getY() * scale), (int)(getWidth() * scale), (int)(getHeight() * scale));
		*/
	}
	
	@Override
	public void addAnimations()
	{
		animations.addAnimation(new Animation(game, new BufferedImage[]{game.textures.getHeart()}, null, new BufferedImage[]{game.textures.getHeartSpecial()}, null), AnimationManager.ANIMATE);
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.UNICORN_HEART, new EntityHeartSpawnData(owner.getNumber(), x, y, radius, direction, waveCoefficient, damage, synchIndex));
	}
	
	@Override
	public String toString()
	{
		return "heart " + synchIndex;
	}

}
