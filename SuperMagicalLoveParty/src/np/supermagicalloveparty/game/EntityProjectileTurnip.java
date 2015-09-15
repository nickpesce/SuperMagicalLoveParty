package np.supermagicalloveparty.game;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import np.supermagicalloveparty.server.EntityTurnipSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ServerGame;

public class EntityProjectileTurnip extends Entity
{

	private Player owner;
	private double damage;
	private double rotation;
	private boolean special;
	AffineTransform identity = new AffineTransform();
	
	public EntityProjectileTurnip(Game g, Player p, double x, double y, int direction, double damage, boolean special)
	{
		super(g, x, y, special? 6:3, special? 6:3, true);
		owner = p;
		this.damage = damage;
		this.special = special;
		animations.play(AnimationManager.ANIMATE);
		setScaledVx(direction*50);
		setScaledVy(-40);
		setBounceable(false);
		setFrictionable(false);
		Particle.spawnMany(g, x, y, 20, game.specialMode? Particle.Type.RED_GOO : Particle.Type.FAIRY_DUST);
		if(game instanceof ServerGame)
			((ServerGame)game).addSynchedEntity(this);
	}
	
	@Override
	public void onHit(Collidable c)
	{
		super.onHit(c);
		if(c instanceof EntityLiving && c != owner)
		{
			if(((EntityLiving)c).hit(new DamageEvent(damage, owner, "Turnip toss")))
			{
				((EntityLiving)c).stun(50);
				destroy();
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);		
		BufferedImage originalTexture = animations.getFrame();
		BufferedImage img = new BufferedImage(originalTexture.getWidth(), originalTexture.getHeight(), originalTexture.getType());
		Graphics2D gImg = img.createGraphics();
		gImg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		gImg.rotate(rotation, originalTexture.getWidth()/2, originalTexture.getHeight()/2);
		gImg.drawImage(originalTexture, 0, 0, null);
		g.drawImage(img, drawX + (direction==RIGHT? 0 : drawWidth), drawY, drawWidth * direction, drawHeight, null);
	}
	
	@Override
	protected void addAnimations()
	{
		animations.addAnimation(new Animation(game, new BufferedImage[] {game.textures.getTurnip()}, null, new BufferedImage[] {game.textures.getTurnip()}, null), AnimationManager.ANIMATE);
	}
	
	@Override
	public void update()
	{
		super.update();
		rotation = direction*Math.tan(getVy()/getVx()) - Math.PI/2;
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
		return new ExtraEntitySpawn(ExtraEntitySpawn.RABBIT_TURNIP, new EntityTurnipSpawnData(owner.getNumber(), x, y, direction, damage, synchIndex, special));
	}
	
}
