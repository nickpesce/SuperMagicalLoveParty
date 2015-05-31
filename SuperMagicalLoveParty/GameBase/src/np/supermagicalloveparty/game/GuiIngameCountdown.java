package np.supermagicalloveparty.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class GuiIngameCountdown
{
	String string;
	Game game;
	boolean startAfter;
	int stage;
	long nextTime, timeRemaining;
	
	public GuiIngameCountdown(Game game)
	{
		this.game = game;
		string = "3";
		stage = 0;
	}

	public void start(boolean startAfter)
	{
		this.startAfter = startAfter;
		stage = 0;
		string = "3";
		nextTime = System.currentTimeMillis()+1000;
		game.soundManager.playSound(SoundManager.POP);
		game.state = Game.State.COUNTDOWN;
		game.activeGui = Game.Gui.COUNTDOWN;
		update();
	}
	
	public void update()
	{
		timeRemaining = nextTime - System.currentTimeMillis();
		if(timeRemaining <= 0)
		{
			if(stage == 3)
				onFinish();
			else
			{
				nextTime = System.currentTimeMillis()+1000;
				stage ++;
				//g.setFont(new Font("ARIAL", Font.PLAIN, (int)(game.canvas.getGuiScale()*10)));
				switch(stage)
				{
					case 1:
						string = "2";
						game.soundManager.playSound(SoundManager.POP);
						break;
					case 2:
						string = "1";
						game.soundManager.playSound(SoundManager.POP);
						break;
					case 3:
						string = "LOVE!";
						game.soundManager.playSound(SoundManager.KISS);
						game.resume();
						for(int i = (game.level.getWidth()/2)-12; i <= (game.level.getWidth()/2)+12; i++)
						{
							Particle.spawnMany(game, i/(game.canvas.getScaleWithCamera()/game.canvas.getScale()), (game.level.getHeight()/2)/(game.canvas.getScaleWithCamera()/game.canvas.getScale()), 10, Particle.Type.HEART);
						}
						
						break;
				}
			}
		}
	}
	
	public void render(Graphics2D g, double scale)
	{
		g.setFont(new Font("ARIAL", Font.PLAIN, (int)(game.canvas.getGuiScale()*10*(timeRemaining/1000.0))));
		g.setColor(Color.BLUE);
		g.drawString(string, (game.canvas.width/2) - (g.getFontMetrics().stringWidth(string)/2), game.canvas.height/2);
	}

	public void onFinish()
	{
		game.activeGui=Game.Gui.NONE;
		game.state = Game.State.PLAYING;
	}
	
}
