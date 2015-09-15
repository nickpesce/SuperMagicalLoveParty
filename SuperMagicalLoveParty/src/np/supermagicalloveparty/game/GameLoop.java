package np.supermagicalloveparty.game;




public class GameLoop
{
	
	GameLoopable game;
	private boolean running = true;
	public static final int BASE_SPEED = 20;
	public static final int MAX_FPS = 60;
	private int actualSpeed = BASE_SPEED;
	private double d = 1.0/actualSpeed;
	/**
	 * Time between ticks in seconds
	 */
	public static final double SKIP_TIME = 1.0 / BASE_SPEED;
	
	public GameLoop(GameLoopable game)
	{
		this.game = game;
	}
	
	public void startGame()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				startLoop();
			}
		}).start();
	}
	
	public void startGame(int tps)
	{
		actualSpeed = tps;
		startGame();
	}
	
	public void stop()
	{
		running = false;
	}
	
	
	private void startLoop()
	{
		running = true;
		int skippedFrames = 0;//ie. updates in a row
		d = 1.0/actualSpeed;
		double next = (System.nanoTime() / 1000000000.0);
		double lastFrameTime = 0, lastTickTime = 0;
		double interpolation;
		while(running)
		{
			double curr = (System.nanoTime() / 1000000000.0);
			if(next<=curr && (skippedFrames < 10 || !game.isDisplaying()) && !(!game.isUpdating() && !(game instanceof GameMP)))
			{
				updateTps((int)(1.0/(curr-lastTickTime)));
				lastTickTime = curr;
				skippedFrames++;
				next+=d;
				game.fineUpdate(0);
				game.update();
			}else if(game.isDisplaying() && (curr-lastFrameTime)>=1.0/MAX_FPS)
			{
				skippedFrames = 0;
				updateFps((int)(1.0/(curr-lastFrameTime))+1);
				if(!game.isUpdating() && !(game instanceof GameMP))
				{
					interpolation = 0;
					next = curr;
				}
				else
					interpolation = 1-((next-curr)*actualSpeed);
				if((curr-lastFrameTime)<(1.0/actualSpeed))
					game.fineUpdate(interpolation);
				game.updateFrame(interpolation);
				lastFrameTime = curr;
			}
		}
	}
	
	/*
	private void startLoop()
	{
		//http://entropyinteractive.com/2011/02/game-engine-design-the-game-loop/
		double nextTime = (double)System.nanoTime() / 1000000000.0;
        double maxTimeDiff = 0.5;
        double lastFrameTime = 0;
        double lastTickTime = 0;
        int skippedFrames = 1;
        int maxSkippedFrames = 5;
        while(running)
        {
            // convert the time to seconds
            double currTime = (System.nanoTime() / 1000000000.0);
            if((currTime - nextTime) > maxTimeDiff) 
            	nextTime = currTime;
            if(currTime >= nextTime)
            {
                // assign the time for the next update
                nextTime += 1.0/BASE_SPEED;
                if(game.state.equals(Game.State.PLAYING))
                {
                	game.update();
                	updateTps(-(int)Math.round(1.0 / (lastTickTime - (lastTickTime = (double)System.nanoTime() / 1000000000.0))));
                }
                if((currTime < nextTime) || (skippedFrames >= maxSkippedFrames))
                {
                    updateFps(-(int)Math.round(1.0 / (lastFrameTime - (lastFrameTime = (double)System.nanoTime() / 1000000000.0))));
                    game.updateFrame((nextTime-currTime)*BASE_SPEED);
                    skippedFrames = 1;
                }
                else
                {
                    skippedFrames++;
                }
            }
            else
            {
                // calculate the time to sleep
                int sleepTime = (int)(1000.0 * (nextTime - currTime));
                // sanity check
                if(sleepTime > 0)
                {
                    // sleep until the next update
                    try
                    {
                        Thread.sleep(sleepTime);
                    }
                    catch(InterruptedException e)
                    {
                        // do nothing
                    }
                }
            }
        }
	}
*/
	
	public void updateFps(int frames) 
	{
		game.setFps(frames);
	}
	
	public void updateTps(int ticks)
	{
		game.setTps(ticks);
	}

	public int getActualSpeed()
	{
		return actualSpeed;
	}
	
	public void setActualSpeed(int speed)
	{
		actualSpeed = speed;
		d = 1.0/actualSpeed;
	}
}
