package np.supermagicalloveparty.game;
import java.util.Random;


public abstract class Particle extends Entity
{

	int timeAlive;
	int maxTime;
	boolean physics;
	int totalTime;
	Random r;
	public enum Type
	{
		FAIRY_DUST, RAINBOW, RED_GOO, HEART
	}
	public Particle(Game g, double x, double y, double width, double height, int time, boolean physics)
	{
		super(g, x, y, width, height, false);
		onGround = false;
		setCollidable(false);
		timeAlive = 0;
		this.physics = physics;
		r = new Random();
		maxTime = time;
		totalTime = r.nextInt(maxTime);
	}
	
	
	@Override
	public void updatePhysics()
	{
		if(physics)
		{
			super.updatePhysics();
		}
	}
	
	@Override
	public void update()
	{
		super.update();
		timeAlive++;
		if(totalTime - timeAlive <= 0)
			destroy();
	}
	
	public static void spawnMany(final Game g, final double x, final double y, final int n, final Particle.Type type, final boolean physics)
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for(int i = 0; i < n; i++)
				{
					switch(type)
					{
						case FAIRY_DUST:
							new ParticleFairyDust(g, x, y, physics);
							break;
						case RAINBOW:
							new ParticleRainbow(g, x, y, physics);
							break;
						case RED_GOO:
							new ParticleRedGoo(g, x, y, physics);
							break;
						case HEART:
							new ParticleHeart(g, x, y, physics);
							break;
					}
				}
			}
		});
		thread.start();
	}
	
	public static void spawnMany(final Game g, final double x, final double y, final int n, final Particle.Type type)
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for(int i = 0; i < n; i++)
				{
					switch(type)
					{
						case FAIRY_DUST:
							new ParticleFairyDust(g, x, y);
							break;
						case RAINBOW:
							new ParticleRainbow(g, x, y);
							break;
						case RED_GOO:
							new ParticleRedGoo(g, x, y);
							break;
						case HEART:
							new ParticleHeart(g, x, y);
							break;
					}
				}
			}
		});
		thread.start();
	}
}
