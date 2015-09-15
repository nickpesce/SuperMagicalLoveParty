package np.supermagicalloveparty.server;

import np.supermagicalloveparty.game.EntityExplosion;
import np.supermagicalloveparty.game.GameMP;

public class EntityExplosionSpawnData extends EntitySpawnData
{
	int synchNumber;
	double x, y, radius, speed, damage;
	int playerNumber, duration, stun;
	boolean visible;
	String attackName;
	public EntityExplosionSpawnData(double x, double y, double radius, double speed, int stun, double damage, int playerNumber, int duration, boolean visible, String attackName, int synch)
	{
		this.synchNumber = synch;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.stun = stun;
		this.speed = speed;
		this.damage = damage;
		this.playerNumber = playerNumber;
		this.duration = duration;
		this.attackName = attackName;
		this.visible = visible;
	}

	@Override
	public int getType()
	{
		return ExtraEntitySpawn.EXPLOSION;
	}

	@Override
	public void spawnEntityInGame(GameMP game)
	{
		EntityExplosion e = new EntityExplosion(game, x, y, radius, game.getPlayers()[playerNumber], speed, stun, duration, damage, visible, attackName);
		e.setSynchIndex(synchNumber);
		game.synchronizedEntities.put(synchNumber, e);
	}

}
