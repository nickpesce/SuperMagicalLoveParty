package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;


public class ParticleFairyDust extends Particle
{

	private static final Color color = new Color(255, 255, 0, 200);
	public ParticleFairyDust(Game g, double x, double y, boolean physics)
	{
		super(g, x, y, .1, .1, 20, physics);
		setNoClip(true);
	}
	
	public ParticleFairyDust(Game g, double x, double y)
	{
		this(g, x, y, false);
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
