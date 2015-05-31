package np.supermagicalloveparty.server;


public class ExtraEntitySpawn extends Extra
{
	public static final int
	UNICORN_HEART = 0,
	BIRD_EGG = 1,
	EXPLOSION = 2,
	RABBIT_CARROT = 3,
	RABBIT_TURNIP = 4,
	RABBIT_CABBAGE = 5,
	POWERUP_SPEED = 10,
	POWERUP_HEALTH = 11,
	POWERUP_SPECIAL = 12,
	POWERUP_INVINCIBLE = 13,
	POWERUP_STRENGTH = 14;



	
	int type;
	EntitySpawnData entityData;
	public ExtraEntitySpawn(int type, EntitySpawnData entityData)
	{
		this.type = type;
		this.entityData = entityData;
	}

	@Override
	public int getType()
	{
		return Packet.ENTITY_SPAWN;
	}

	public EntitySpawnData getEntityData()
	{
		return entityData;
	}
}
