package np.supermagicalloveparty.game;

import java.awt.Color;
import java.awt.Graphics2D;

public class ParticleHeart extends Particle
{

	int waveDirection;
	public ParticleHeart(Game g, double x, double y, boolean physics)
	{
		super(g, x, y, 1, 1, 20, true);
		setNoClip(true);
		setScaledVy(-Math.random()*5);
		setScaledVx((Math.random() - .5)*50);
		setScaledAy(-game.settings.getGravity()/10);
	}
	
	public ParticleHeart(Game g, double x, double y)
	{
		this(g, x, y, true);
	}

	@Override
	public void fineUpdate(double interpolation)
	{
		super.fineUpdate(interpolation);
		drawX += 10*Math.sin(((timeAlive/10.0) + ((timeAlive/10.0)*interpolation))*Math.PI);
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		g.drawImage(game.textures.getHeart(), drawX, drawY, drawWidth, drawHeight, null);
	}
	
}
