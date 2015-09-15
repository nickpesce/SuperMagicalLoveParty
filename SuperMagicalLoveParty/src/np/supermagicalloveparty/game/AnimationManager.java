package np.supermagicalloveparty.game;
import java.awt.image.BufferedImage;

public class AnimationManager
{
	Animation[] animations;
	Animation currAnimation;
	private boolean changed;
	Entity entity;
	int index = -1;
	//Standard animation indexies.
	public static final int
	IDLE = 0,//When idle, should check for other animation on update.
	RUN = 1,
	JUMP = 2,
	SQUAT = 3,
	ATTACK = 5,
	ATTACK_CHARGED = 6,
	ATTACK_CHARGING = 7,
	EVADE = 8,
	ANIMATE = 9;//for everything else, like an object that changes color.
	
	public AnimationManager(Entity entity)
	{
		this.entity = entity;
		animations = new Animation[10];
	}
	
	public void addAnimation(Animation animation, int index)
	{
		animations[index] = animation;
	}
	
	public void play(int index)
	{
		if(currAnimation != null)
			currAnimation.reset();
		this.index = index;
		if(animations[index] != null)
			currAnimation = animations[index];
	}
	
	public int getAnimationIndex()
	{
		return index;
	}
	
	public void update()
	{
		if(currAnimation != null)
			currAnimation.update();
	}
	
	public BufferedImage getFrame()
	{
		if(currAnimation != null)
			return currAnimation.getFrame();
		return null;
	}

	public boolean isChanged()
	{
		return changed;
	}

	public void setChanged(boolean changed)
	{
		this.changed = changed;
	}
	
}
