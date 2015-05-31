package np.supermagicalloveparty.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class ServerConsole extends JPanel
{
	private static final long serialVersionUID = 1L;
	ServerGame game;
	public ServerConsole(ServerGame g)
	{
		game = g;
		setSize(698, 422);
	}
	
	@Override
	public void paint(Graphics g)
	{
		super.paint(g);
		System.out.println(g);
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.WHITE);
		game.guiConsole.render((Graphics2D)g, 20);
	}

}
