package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.EntityProjectileEgg;
import np.supermagicalloveparty.game.GameMP;

public class EntityEggSpawnData extends EntitySpawnData
{
	double x, y, damage;
	boolean superEgg;
	int playerNumber, synchNumber;
	public EntityEggSpawnData(int playerNumber, double x, double y, double damage, boolean superEgg, int synchNumber)
	{
		this.playerNumber = playerNumber;
		this.synchNumber = synchNumber;
		this.superEgg = superEgg;
		this.x = x;
		this.y = y;
		this.damage = damage;
	}

	@Override
	public int getType()
	{
		return ExtraEntitySpawn.BIRD_EGG;
	}

	@Override
	public void spawnEntityInGame(GameMP game)
	{
		EntityProjectileEgg e =  new EntityProjectileEgg(game, game.getPlayers()[playerNumber], x, y, damage, superEgg);
		e.setSynchIndex(synchNumber);
		game.synchronizedEntities.put(synchNumber, e);
	}

}
