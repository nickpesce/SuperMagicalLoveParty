package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;


public class ParticleRedGoo extends Particle
{
	public ParticleRedGoo(Game g, double x, double y, boolean physics)
	{
		super(g, x, y, Math.random()*1, Math.random()*2, 20, physics);
		setScaledVy(Math.random()*game.settings.getGravity());
		setScaledVx((Math.random() - .5)*100);
	}
	
	public ParticleRedGoo(Game g, double x, double y)
	{
		this(g, x, y, true);
	}

	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.setColor(Color.RED);
		g.fillOval(drawX, drawY, drawWidth, drawHeight);
	}
	
	@Override
	public void updatePhysics()
	{
		super.updatePhysics();
	}
	
	@Override
	protected void onCollideBottom(CollisionEvent ce)
	{
		if(ce.getTile() == Level.DAMAGE_AND_KNOCKBACK)
			destroy();
		else
			super.onCollideBottom(ce);
	}
}
