package np.supermagicalloveparty.game;
import java.awt.image.BufferedImage;

public class Animation
{
	BufferedImage[] frames;
	BufferedImage[] specialFrames;
	/**
	 * The times for the frames must be CUMULATIVE
	 */
	int[] times;
	int[] specialTimes;
	int time;
	int totalTime, currFrame;
	Game game;
	
	public Animation(Game game, BufferedImage[] frames, int[] times, BufferedImage[] specialFrames, int[] specialTimes)
	{
		this.frames = frames;
		this.times = times;
		this.specialFrames = specialFrames;
		this.specialTimes = specialTimes;
		this.game = game;
		time = 0;
		currFrame = 0;
		if(times == null)
			totalTime = -1;
		else
		{
			for(int t : times)
			{
				totalTime += t;
			}
		}
	}
	
	public Animation(Game game,BufferedImage[] frames, int[] times)
	{
		this(game, frames, times, frames, times);
	}
	
	/**
	 * To be called every Tick. Does not automatically change frame, but adds to timer.
	 */
	public void update()
	{
		if(game.specialMode? specialTimes == null : times == null)
			return;
		if(time < (game.specialMode? specialTimes[currFrame] : times[currFrame]))
			time++;
		else 
		{
			if(currFrame == ((game.specialMode? specialFrames.length:frames.length) - 1))
				currFrame = 0;
			else
				currFrame++;
			time = 0;
		}
	}
	
	
	public BufferedImage getFrame()
	{
		assert frames[currFrame] != null;
		if(game.specialMode)
			return specialFrames[currFrame];
		return frames[currFrame];
	}
	
	public void reset()
	{
		time = 0;
		currFrame = 0;
	}
}
