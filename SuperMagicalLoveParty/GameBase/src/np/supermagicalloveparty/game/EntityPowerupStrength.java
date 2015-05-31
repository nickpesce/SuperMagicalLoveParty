package np.supermagicalloveparty.game;
import java.awt.Graphics2D;

import np.supermagicalloveparty.server.EntityPowerupSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ExtraPowerupAquire;
import np.supermagicalloveparty.server.Packet;
import np.supermagicalloveparty.server.ServerGame;


public class EntityPowerupStrength extends Entity
{
	public EntityPowerupStrength(Game g, double x, double y)
	{
		super(g, x, y, 2, 2, false);
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
				((ServerGame)game).server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.STRENGTH)));
				p.powerupDamage();
			}
			else if(!game.multiplayer)
				p.powerupDamage();
		}
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		//g.setColor(new Color(0, 0, 0, 150));
		//g.fillOval((int)(x*scale), (int)(y*scale), (int)(2*scale), (int)(2*scale));
		g.drawImage(game.textures.getPotion(), drawX, drawY, drawWidth, drawHeight, null);
	}
	
	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.POWERUP_STRENGTH, new EntityPowerupSpawnData(ExtraEntitySpawn.POWERUP_STRENGTH, x, y));
	}
}
