package np.supermagicalloveparty.game;
import java.util.Random;

import np.supermagicalloveparty.server.EntityPowerupSpawnData;
import np.supermagicalloveparty.server.ExtraEntitySpawn;
import np.supermagicalloveparty.server.Packet;
import np.supermagicalloveparty.server.ServerGame;


public class PowerupGenerator implements Updatable
{
	Game game;
	Level level;
	public PowerupGenerator(Game g)
	{
		game = g;
		level = g.level;
		g.updatables.add(this);
	}
	
	public void addPowerup()
	{
		Point location = getValidLocation();
		Random r = new Random();
		Entity e = null;
		switch(r.nextInt(5))
		{
			case 0://Special Attack
				e = new EntityPowerupSpecialAttack(game, location.getX(), location.getY());
				break;
			case 1://Health
				e = new EntityPowerupHealth(game, location.getX(), location.getY());
				break;
			case 2://DAMAGE
				e = new EntityPowerupStrength(game, location.getX(), location.getY());
				break;
			case 3://SPEED
				e = new EntityPowerupSpeed(game, location.getX(), location.getY());
				break;
			case 4://Invincible
				e = new EntityPowerupInvincible(game, location.getX(), location.getY());
				break;
		}
		if(game instanceof ServerGame)
		{
			((ServerGame)game).server.sendAll(new Packet(Packet.ENTITY_SPAWN, e.getExtraSpawn()));
		}
		game.powerups.add(e);
	}
	
	public Point getValidLocation()
	{
		//TODO find location by guess and check. Any better(easy) way?
		Random r = new Random();
		search: while (true)
		{
			int id = r.nextInt(level.getWidth()*level.getHeight());
			int x = level.getTileX(id);
			int y = level.getTileY(id);
			char tile = level.getTileById(id);
			if((tile  == Level.SOLID) || (tile == Level.UP))
			{
				for(int i = x; i < x+2; i++)
				{
					for(int j = y-2; j < y; j++)
					{
						if(level.getTileAtLocation(i, j) != Level.EMPTY)
							continue search;
					}
				}
				return new Point(x, y-2);
			}
		}

	}
	
	@Override
	public void update()
	{
		Random r = new Random();
		if(r.nextInt((2000 - (game.settings.getPowerups()*20))+1) == 0)
		{
			addPowerup();
		}
	}
	
	@Override
	public void fineUpdate(double interpolation)
	{
	}
}
