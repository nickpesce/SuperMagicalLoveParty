package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.EntityProjectileEgg;
import np.supermagicalloveparty.game.EntityProjectileHeart;
import np.supermagicalloveparty.game.GameMP;

public class EntityHeartSpawnData extends EntitySpawnData
{
	double x, y, damage, radius, waveCoefficient;
	int playerNumber, synchNumber, direction;
	public EntityHeartSpawnData(int playerNumber, double x, double y, double radius, int direction, double waveCoefficient, double damage, int synchNumber)
	{
		this.playerNumber = playerNumber;
		this.synchNumber = synchNumber;
		this.x = x;
		this.y = y;
		this.damage = damage;
		this.direction = direction;
		this.waveCoefficient = waveCoefficient;
		this.radius = radius;
	}

	@Override
	public int getType()
	{
		return ExtraEntitySpawn.BIRD_EGG;
	}

	@Override
	public void spawnEntityInGame(GameMP game)
	{
		EntityProjectileHeart e = new EntityProjectileHeart(game, game.getPlayers()[playerNumber], x, y, radius, direction, waveCoefficient, damage);
		e.setSynchIndex(synchNumber);
		game.synchronizedEntities.put(synchNumber, e);
	}
}
