package np.supermagicalloveparty.game;

public interface GameLoopable
{

	public void update();
	public void fineUpdate(double interpolation);
	public void updateFrame(double interpolation);
	public boolean isUpdating();
	public void setTps(int tps);
	public void setFps(int fps);
	public boolean isDisplaying();
}
