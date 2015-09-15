package np.supermagicalloveparty.server;

import java.io.Serializable;

import np.supermagicalloveparty.game.Entity;
import np.supermagicalloveparty.game.GameMP;

public abstract class EntitySpawnData implements Serializable
{
	private static final long serialVersionUID = -4391027914426803364L;
	public abstract int getType();
	public abstract void spawnEntityInGame(GameMP game);
	
}
