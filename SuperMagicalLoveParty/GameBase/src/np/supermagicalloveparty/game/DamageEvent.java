package np.supermagicalloveparty.game;

public class DamageEvent
{
	Entity source;
	String sourceString;
	String attack;
	double damage;
	
	public DamageEvent(double damage, Entity source, String attack)
	{
		this.damage = damage;
		this.source = source;
		this.sourceString = source.toString();
		this.attack = attack;
	}

	public DamageEvent(double damage, String source, String attack)
	{
		this.damage = damage;
		this.sourceString = source;
		this.attack = attack;
	}
	
	public Entity getSource()
	{
		return source;
	}

	public String getSourceString()
	{
		return sourceString;
	}
	
	public void setSource(Entity source)
	{
		this.source = source;
	}

	public String getAttack()
	{
		return attack;
	}

	public void setAttack(String attack)
	{
		this.attack = attack;
	}

	public double getDamage()
	{
		return damage;
	}

	public void setDamage(double damage)
	{
		this.damage = damage;
	}
}
