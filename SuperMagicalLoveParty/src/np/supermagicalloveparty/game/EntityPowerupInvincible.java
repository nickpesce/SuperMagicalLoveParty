package np.supermagicalloveparty.game;
import java.awt.Graphics2D;

import np.supermagicalloveparty.server.EntityPowerupSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ExtraPowerupAquire;
import np.supermagicalloveparty.server.Packet;
import np.supermagicalloveparty.server.ServerGame;


public class EntityPowerupInvincible extends Entity
{
	public EntityPowerupInvincible(Game g, double x, double y)
	{
		super(g, x, y, 2, 2, false);
		animations.play(AnimationManager.ANIMATE);
	}

	@Override
	public void onHit(Collidable c)
	{
		if(c instanceof Player)
		{
			game.powerups.remove(this);
			destroy();
			Player p = (Player)c;
			if(game instanceof ServerGame)
			{
				((ServerGame)game).server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.INVINCIBLE)));
				p.powerupInvincible();
			}
			else if(!game.multiplayer)
				p.powerupInvincible();

		}
	}
	
	@Override
	public void update() 
	{
		super.update();
	}
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.drawImage(animations.getFrame(), drawX, drawY, drawWidth, drawHeight, null);
		//g.setColor(new Color(255, 255, 0, 150));
		///g.fillOval();
	}
	
	@Override
	protected void addAnimations()
	{
		super.addAnimations();
		animations.addAnimation(new Animation(game, game.textures.getSun(), new int[]{2, 2, 2}), AnimationManager.ANIMATE);
	}
	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.POWERUP_INVINCIBLE, new EntityPowerupSpawnData(ExtraEntitySpawn.POWERUP_INVINCIBLE, x, y));
	}
}
