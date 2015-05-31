package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.Entity;
import np.supermagicalloveparty.game.EntityPowerupHealth;
import np.supermagicalloveparty.game.EntityPowerupInvincible;
import np.supermagicalloveparty.game.EntityPowerupSpecialAttack;
import np.supermagicalloveparty.game.EntityPowerupSpeed;
import np.supermagicalloveparty.game.EntityPowerupStrength;
import np.supermagicalloveparty.game.GameMP;

public class EntityPowerupSpawnData extends EntitySpawnData
{

	double x, y;
	int type;
	public EntityPowerupSpawnData(int type, double x, double y)
	{
		this.x = x;
		this.y = y;
		this.type = type;
	}

	@Override
	public int getType()
	{
		return type;
	}

	@Override
	public void spawnEntityInGame(GameMP game)
	{
		Entity e = null;
		switch(type)
		{
			case ExtraEntitySpawn.POWERUP_HEALTH:
				e = new EntityPowerupHealth(game, x, y);
				break;
			case ExtraEntitySpawn.POWERUP_INVINCIBLE:
				e = new EntityPowerupInvincible(game, x, y);
				break;
			case ExtraEntitySpawn.POWERUP_SPECIAL:
				e = new EntityPowerupSpecialAttack(game, x, y);
				break;
			case ExtraEntitySpawn.POWERUP_SPEED:
				e = new EntityPowerupSpeed(game, x, y);
				break;
			case ExtraEntitySpawn.POWERUP_STRENGTH:
				e = new EntityPowerupStrength(game, x, y);
				break;
		}
		game.getPowerups().add(e);
	}

}
