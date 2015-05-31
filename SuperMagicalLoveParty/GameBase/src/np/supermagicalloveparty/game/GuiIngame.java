package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import np.supermagicalloveparty.server.ExtraDisconnect;
import np.supermagicalloveparty.server.Packet;

public class GuiIngame
{

	GuiButtonManager buttons;
	Game game;
	public GuiIngame(final Game game)
	{
		buttons = new GuiButtonManager(game.canvas);
		this.game = game;
		buttons.add(new GuiButton((80)-5, (45)-9, 10, 5, "Resume", new Runnable()
		{
			@Override
			public void run()
			{
				game.activeGui = Game.Gui.NONE;
				game.resume();
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
		buttons.add(new GuiButton((80)-5, (45)+3, 10, 5, "Controls", new Runnable()
		{
			@Override
			public void run()
			{
				game.activeGui = Game.Gui.CONTROLS;
			}
		
		}));
		buttons.add(new GuiButton((80)-5, (45)+9, 10, 5, "Desktop", new Runnable()
		{
			@Override
			public void run()
			{
				System.exit(0);	
			}
		}
		));
	}
	
	public void render(Graphics2D g, double scale)
	{
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(-game.canvas.offx, -game.canvas.offy, game.canvas.availableWidth, game.canvas.availableHeight);
		buttons.renderAll(g, scale, new Point(game.canvas.offx, game.canvas.offy));
	}
	
//	public void mouseClicked(int x, int y)
//	{
//		for(GuiButton b : buttons)
//		{
//			if(b.contains(x/game.canvas.getGuiScale() , y/game.canvas.getGuiScale()))
//			{
//				b.onClick();
//			}
//		}
//	}
//	
//	public void mouseReleased(int x, int y)
//	{
//		for(GuiButton b : buttons)
//		{
//			if(b.contains(x/game.canvas.getGuiScale() , y/game.canvas.getGuiScale()))
//			{
//				b.onMouseUp();
//			}else if(b.state.equals(GuiButton.State.CLICKED))
//			{
//				b.onRemove();
//			}
//		}
//	}
//	
//	public void mousePressed(int x, int y)
//	{
//		for(GuiButton b : buttons)
//		{
//			if(b.contains(x/game.canvas.getGuiScale() , y/game.canvas.getGuiScale()))
//			{
//				b.onMouseDown();
//			}
//		}
//	}
//	
//	public void mouseMoved(int x, int y)
//	{
//		for(GuiButton b : buttons)
//		{
//			if(b.contains(x/game.canvas.getGuiScale() , y/game.canvas.getGuiScale()))
//			{
//				b.onHover();
//			}else if(b.state.equals(GuiButton.State.HIGHLIGHTED))
//			{
//				b.onRemove();
//			}
//
//		}
//	}
	
	public void keyPress(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			game.activeGui = Game.Gui.NONE;
			game.resume();
		}
	}
}
