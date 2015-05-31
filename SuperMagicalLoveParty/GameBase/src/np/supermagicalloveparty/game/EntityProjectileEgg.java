package np.supermagicalloveparty.game;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import np.supermagicalloveparty.server.EntityEggSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ServerGame;


public class EntityProjectileEgg extends Entity{
	Player owner;
	double damage;
	boolean superEgg;
	public EntityProjectileEgg(Game g, Player p, double x, double y, double damage, boolean superEgg)
	{
		super(g, x, y, superEgg? 5:2, superEgg? 5:2, false);
		setVy(p.getVy());
		owner = p;
		this.damage = damage;
		this.superEgg = superEgg;
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
			((EntityLiving)c).hit(new DamageEvent(getDamage(), owner, "Egg drop"));//Direct hit damage
			explode();
			destroy();
		}
	}
	
	public void explode()
	{
		if(!game.multiplayer)
			new EntityExplosion(game, x + (width/2), y + (height/2),  superEgg? 20.0:10.0, this.owner, this.getScaledVy()/4, 2, 10, (superEgg? 6:3)*owner.getDamageMultiplier(), true, "Eggsplosion of Love");
	}
	
	@Override
	public void onCollideBottom(CollisionEvent ce)
	{
		explode();
		super.onCollideBottom(ce);
		destroy();
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.drawImage(animations.getFrame(), drawX, drawY, drawWidth, drawHeight, null);
		/*
		g.setColor(Color.RED);
		g.fillOval((int)(getX() * scale), (int)(getY() * scale), (int)(getWidth() * scale), (int)(getHeight() * scale));
		*/
	}
	
	@Override
	public void addAnimations()
	{
		BufferedImage[] heart = {game.textures.getEgg()};
		animations.addAnimation(new Animation(game, heart, null), AnimationManager.ANIMATE);
	}
	
	public double getDamage()
	{
		return damage;
	}
	
	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.BIRD_EGG, new EntityEggSpawnData(owner.number, x, y, damage, superEgg, synchIndex));
	}
}
