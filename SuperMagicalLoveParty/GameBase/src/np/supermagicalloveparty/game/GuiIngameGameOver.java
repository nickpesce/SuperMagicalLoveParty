package np.supermagicalloveparty.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import np.supermagicalloveparty.server.ExtraDisconnect;
import np.supermagicalloveparty.server.Packet;

public class GuiIngameGameOver
{

	Game game;
	GuiButtonManager buttons;

	public GuiIngameGameOver(final Game game)
	{
		buttons = new GuiButtonManager(game.canvas);
		this.game = game;
		buttons.add(new GuiButton((80)-5, (45)-9, 10, 5, "Replay", new Runnable()
		{
			@Override
			public void run()
			{
				game.reset();
				game.activeGui = Game.Gui.COUNTDOWN;
				game.guiCountdown.start(true);
			}
		
		}));
		buttons.add(new GuiButton((80)-5, (45)-3, 10, 5, "Menu", new Runnable()
		{
			@Override
			public void run()
			{
				if(game.multiplayer)
					((GameMP)game).packetManager.send(new Packet(Packet.DISSCONECT, new ExtraDisconnect(((GameMP)game).myPlayerNumber, ExtraDisconnect.LEAVE)));
				game.stop();
			}
		}
		));
		buttons.add(new GuiButton((80)-5, (45)+3, 10, 5, "Desktop", new Runnable()
		{
			@Override
			public void run()
			{
				System.exit(0);
			}
		
		}));
	}
	
	public void render(Graphics2D g, double scale)
	{
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(-game.canvas.offx, -game.canvas.offy, game.canvas.availableWidth, game.canvas.availableHeight);
		g.setColor(new Color(0, 200, 150));
		String gameOverString = "GAME OVER!";
		g.setFont(new Font(Font.SERIF, Font.ITALIC, 100));
		g.drawString(gameOverString, (game.canvas.width/2) - (g.getFontMetrics().stringWidth(gameOverString)/2), game.canvas.height/3);
		buttons.renderAll(g, scale, new Point(game.canvas.offx, game.canvas.offy));
	}
	
	public void keyPress(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			System.exit(0);
		}
	}
}
