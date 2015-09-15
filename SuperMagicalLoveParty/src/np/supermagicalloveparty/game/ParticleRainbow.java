package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;


public class ParticleRainbow extends Particle
{
	Color color;
	public ParticleRainbow(Game g, double x, double y, boolean physics)
	{
		super(g, x, y, .5, .5, 20, physics);
		color = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), 200);
		setNoClip(true);
	}
	
	public ParticleRainbow(Game g, double x, double y)
	{
		this(g, x, y, true);
	}

	@Override
	public void update()
	{
		super.update();
		setY(y+(Math.random())-0.5);
		setX(x+(Math.random())-0.5);
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.setColor(color);
		g.fillRect(drawX, drawY, drawWidth, drawHeight);
	}
}
