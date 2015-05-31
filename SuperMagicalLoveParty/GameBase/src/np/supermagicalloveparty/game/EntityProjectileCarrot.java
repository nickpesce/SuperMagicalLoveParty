package np.supermagicalloveparty.game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import np.supermagicalloveparty.server.EntityCarrotSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ServerGame;

public class EntityProjectileCarrot extends Entity
{

	private Player owner;
	private double damage;
	private boolean special;
	
	public EntityProjectileCarrot(Game g, Player p, double x, double y, int direction, double damage, boolean special)
	{
		super(g, x, y, special? 8:4, special? 4:2, true);
		owner = p;
		this.damage = damage;
		this.special = special;
		setScaledVx(direction*75);
		setAy(0);
		setVy(0);
		setBounceable(false);
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
		if(c instanceof EntityLiving && c != owner)
		{
			if(((EntityLiving)c).hit(new DamageEvent(damage, owner, "Carrot toss")))
			destroy();
		}
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.drawImage(animations.getFrame(), drawX + (direction==LEFT ? drawWidth:0), drawY, drawWidth*direction, drawHeight, null);
	}
	
	@Override
	protected void addAnimations()
	{
		animations.addAnimation(new Animation(game, new BufferedImage[] {game.textures.getCarrot()}, null, new BufferedImage[] {game.textures.getCarrot()}, null), AnimationManager.ANIMATE);
	}
	
	@Override
	protected void onCollideBottom(CollisionEvent ce)
	{
		destroy();
	}
	
	@Override
	protected void onCollideTop(CollisionEvent ce)
	{
		destroy();
	}
	
	@Override
	protected void onCollideInside()
	{
		destroy();
	}
	
	@Override
	protected void onCollideLeft(CollisionEvent ce)
	{
		destroy();
	}
	
	@Override
	protected void onCollideRight(CollisionEvent ce)
	{
		destroy();
	}
	
	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.RABBIT_CARROT, new EntityCarrotSpawnData(owner.getNumber(), x, y, direction, damage, synchIndex, special));
	}
	
}
