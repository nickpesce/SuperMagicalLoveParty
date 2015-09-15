package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.EntityProjectileCarrot;
import np.supermagicalloveparty.game.GameMP;

public class EntityCarrotSpawnData extends EntitySpawnData
{
	double x, y, damage;
	int playerNumber, synchNumber, direction;
	boolean special;
	
	public EntityCarrotSpawnData(int playerNumber, double x, double y, int direction, double damage, int synchNumber, boolean special)
	{
		this.playerNumber = playerNumber;
		this.synchNumber = synchNumber;
		this.x = x;
		this.y = y;
		this.damage = damage;
		this.direction = direction;
		this.special = special;
	}

	@Override
	public int getType()
	{
		return ExtraEntitySpawn.RABBIT_CARROT;
	}

	@Override
	public void spawnEntityInGame(GameMP game)
	{
		EntityProjectileCarrot e = new EntityProjectileCarrot(game, game.getPlayers()[playerNumber], x, y, direction, damage, special);
		e.setSynchIndex(synchNumber);
		game.synchronizedEntities.put(synchNumber, e);
	}
}
