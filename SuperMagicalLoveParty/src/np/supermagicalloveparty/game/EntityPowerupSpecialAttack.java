package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;

import np.supermagicalloveparty.server.EntityPowerupSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.ExtraPowerupAquire;
import np.supermagicalloveparty.server.Packet;
import np.supermagicalloveparty.server.ServerGame;


public class EntityPowerupSpecialAttack extends Entity
{

	public EntityPowerupSpecialAttack(Game g, double x, double y)
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
				((ServerGame)game).server.sendAll(new Packet(Packet.POWERUP_AQUIRE, new ExtraPowerupAquire(p.getNumber(), ExtraPowerupAquire.SPECIAL)));
				p.powerupSpecialAttack();
			}
			else if(!game.multiplayer)
				p.powerupSpecialAttack();
		}
	}
	
	@Override
	public void update() 
	{
		super.update();
		Particle.spawnMany(game, x+(width/2), y+(height/2), 2, Particle.Type.RAINBOW);
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.drawImage(game.textures.getCupcake(), drawX, drawY, drawWidth, drawHeight, null);
	}

	@Override
	public ExtraEntitySpawn getExtraSpawn()
	{
		return new ExtraEntitySpawn(ExtraEntitySpawn.POWERUP_SPECIAL, new EntityPowerupSpawnData(ExtraEntitySpawn.POWERUP_SPECIAL, x, y));
	}
	
}
