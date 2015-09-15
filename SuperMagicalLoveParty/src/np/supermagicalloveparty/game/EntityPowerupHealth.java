package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;

import np.supermagicalloveparty.server.EntityPowerupSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ExtraPowerupAquire;
import np.supermagicalloveparty.server.Packet;
import np.supermagicalloveparty.server.ServerGame;


public class EntityPowerupHealth extends Entity
{

	public EntityPowerupHealth(Game g, double x, double y)
	{
		super(g, x, y, 2, 2, false);
		animations.play(AnimationManager.ANIMATE);
		checkCollisions();
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
				((ServerGame)game).server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.HEALTH)));
				p.powerupHealth();
			}
			else if(!game.multiplayer)
				p.powerupHealth();
		}
	}

	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		 super.draw(g, interpolation);
		//g.setColor(new Color(255, 0, 0, 150));
		//g.fillOval(drawX, drawY, drawWidth, drawHeight);
		g.drawImage(animations.getFrame(), drawX, drawY, drawWidth, drawHeight, null); 
	}
	
	@Override
	protected void addAnimations()
	{
		super.addAnimations();
		animations.addAnimation(new Animation(game, game.textures.getApple(), new int[]{2, 2, 2, 2, 2, 2, 2, 2}), AnimationManager.ANIMATE);
	}
	
	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.POWERUP_HEALTH, new EntityPowerupSpawnData(ExtraEntitySpawn.POWERUP_HEALTH, x, y));
	}
}
