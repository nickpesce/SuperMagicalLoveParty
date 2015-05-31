package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


public class GuiControls
{
	Game game;
	KeyMap p1Controls, p2Controls, p3Controls, p4Controls;
	ArrayList<GuiKeyBox> boxes;
	GuiKeyBox activeBox;
	
	public GuiControls(Game game)
	{
		this.game = game;
		boxes = new ArrayList<GuiKeyBox>();
	}
	
	public void render(Graphics2D g, double scale)
	{
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(-game.canvas.offx, -game.canvas.offy, game.canvas.availableWidth, game.canvas.availableHeight);
		g.setColor(Color.WHITE);
		g.drawRect((int)(50*scale), (int)(10*scale), (int)(60*scale), (int)(24*scale));
		g.setColor(new Color(0, 0, 0, 225));
		g.fillRect((int)(50*scale), (int)(10*scale), (int)(60*scale)+1, (int)(24*scale)+1);
		g.setColor(Color.WHITE);
		g.drawString("Action", (int)(51*scale), (int)(10*scale));
		g.drawString("Player 1", (int)(70*scale), (int)(10*scale));
		g.drawString("Player 2", (int)(80*scale), (int)(10*scale));
		g.drawString("Player 3", (int)(90*scale), (int)(10*scale));
		g.drawString("Player 4", (int)(100*scale), (int)(10*scale));
		g.drawRect((int)(50*scale), (int)(10*scale), (int)(19*scale), (int)(24*scale));
		
		g.drawString("Move Left", (int)(51*scale), (int)(12*scale));
		g.drawString("Move Right", (int)(51*scale), (int)(16*scale));
		g.drawString("Jump", (int)(51*scale), (int)(20*scale));
		g.drawString("Squat", (int)(51*scale), (int)(24*scale));
		g.drawString("Evade", (int)(51*scale), (int)(28*scale));
		g.drawString(game.strings.attack(), (int)(51*scale), (int)(32*scale));
		
		for(GuiKeyBox b: boxes)
		{
			b.render(g, scale);
		}
	}
	
	public void mouseClicked(MouseEvent e)
	{
		for(GuiKeyBox b : boxes)
		{
			if(b.contains((e.getX() - game.canvas.offx)/game.canvas.getGuiScale() , (e.getY()-game.canvas.offy)/game.canvas.getGuiScale()))
			{
				if(activeBox != null)
					activeBox.deactivate();
				b.onClick();
				activeBox = b;
			}
		}
	}
	
	public void keyPress(KeyEvent arg0)
	{
		if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			game.activeGui = Game.Gui.MENU;
			if(activeBox != null)
			{
				activeBox.reset();
				activeBox = null;
			}
		}
		else if(activeBox != null)
		{
			if(arg0.getKeyCode() == activeBox.getId() || game.input.isKeyUnique(arg0.getKeyCode()))
			{
				activeBox.setTextColor(Color.WHITE);
				activeBox.setKey(arg0.getKeyCode());
				activeBox = null;
			}
			else
			{
				activeBox.setTextColor(Color.RED);
			}
		}
	}
	
	public void addP1Buttons()
	{
		boxes.add(new GuiKeyBox(70, 10, 7, 4, p1Controls.getKeyCode(InputHandler.LEFT), InputHandler.LEFT, p1Controls));
		boxes.add(new GuiKeyBox(70, 14, 7, 4, p1Controls.getKeyCode(InputHandler.RIGHT), InputHandler.RIGHT, p1Controls));
		boxes.add(new GuiKeyBox(70, 18, 7, 4, p1Controls.getKeyCode(InputHandler.JUMP), InputHandler.JUMP, p1Controls));
		boxes.add(new GuiKeyBox(70, 22, 7, 4, p1Controls.getKeyCode(InputHandler.SQUAT), InputHandler.SQUAT, p1Controls));
		boxes.add(new GuiKeyBox(70, 26, 7, 4, p1Controls.getKeyCode(InputHandler.EVADE), InputHandler.EVADE, p1Controls));
		boxes.add(new GuiKeyBox(70, 30, 7, 4, p1Controls.getKeyCode(InputHandler.ATTACK), InputHandler.ATTACK, p1Controls));
	}
	
	public void addP2Buttons()
	{
		boxes.add(new GuiKeyBox(80, 10, 7, 4, p2Controls.getKeyCode(InputHandler.LEFT), InputHandler.LEFT, p2Controls));
		boxes.add(new GuiKeyBox(80, 14, 7, 4, p2Controls.getKeyCode(InputHandler.RIGHT), InputHandler.RIGHT, p2Controls));
		boxes.add(new GuiKeyBox(80, 18, 7, 4, p2Controls.getKeyCode(InputHandler.JUMP), InputHandler.JUMP, p2Controls));
		boxes.add(new GuiKeyBox(80, 22, 7, 4, p2Controls.getKeyCode(InputHandler.SQUAT), InputHandler.SQUAT, p2Controls));
		boxes.add(new GuiKeyBox(80, 26, 7, 4, p2Controls.getKeyCode(InputHandler.EVADE), InputHandler.EVADE, p2Controls));
		boxes.add(new GuiKeyBox(80, 30, 7, 4, p2Controls.getKeyCode(InputHandler.ATTACK), InputHandler.ATTACK, p2Controls));
	}
	
	public void addP3Buttons()
	{
		boxes.add(new GuiKeyBox(90, 10, 7, 4, p3Controls.getKeyCode(InputHandler.LEFT), InputHandler.LEFT, p3Controls));
		boxes.add(new GuiKeyBox(90, 14, 7, 4, p3Controls.getKeyCode(InputHandler.RIGHT), InputHandler.RIGHT, p3Controls));
		boxes.add(new GuiKeyBox(90, 18, 7, 4, p3Controls.getKeyCode(InputHandler.JUMP), InputHandler.JUMP, p3Controls));
		boxes.add(new GuiKeyBox(90, 22, 7, 4, p3Controls.getKeyCode(InputHandler.SQUAT), InputHandler.SQUAT, p3Controls));
		boxes.add(new GuiKeyBox(90, 26, 7, 4, p3Controls.getKeyCode(InputHandler.EVADE), InputHandler.EVADE, p3Controls));
		boxes.add(new GuiKeyBox(90, 30, 7, 4, p3Controls.getKeyCode(InputHandler.ATTACK), InputHandler.ATTACK, p3Controls));
	}
	
	public void addP4Buttons()
	{
		boxes.add(new GuiKeyBox(100, 10, 7, 4, p4Controls.getKeyCode(InputHandler.LEFT), InputHandler.LEFT, p4Controls));
		boxes.add(new GuiKeyBox(100, 14, 7, 4, p4Controls.getKeyCode(InputHandler.RIGHT), InputHandler.RIGHT, p4Controls));
		boxes.add(new GuiKeyBox(100, 18, 7, 4, p4Controls.getKeyCode(InputHandler.JUMP), InputHandler.JUMP, p4Controls));
		boxes.add(new GuiKeyBox(100, 22, 7, 4, p4Controls.getKeyCode(InputHandler.SQUAT), InputHandler.SQUAT, p4Controls));
		boxes.add(new GuiKeyBox(100, 26, 7, 4, p4Controls.getKeyCode(InputHandler.EVADE), InputHandler.EVADE, p4Controls));
		boxes.add(new GuiKeyBox(100, 30, 7, 4, p4Controls.getKeyCode(InputHandler.ATTACK), InputHandler.ATTACK, p4Controls));
	}
	
	public void addPlayer(Player p, boolean multiplayer)
	{
		if(multiplayer)
		{
			p1Controls = p.getKeys();
			addP1Buttons();
			return;
		}
		switch(p.getNumber())
		{
			case 0:
				p1Controls = p.getKeys();
				addP1Buttons();
				break;
			case 1:
				p2Controls = p.getKeys();
				addP2Buttons();
				break;
			case 2:
				p3Controls = p.getKeys();
				addP3Buttons();
				break;
			case 3:
				p4Controls = p.getKeys();
				addP4Buttons();
				break;
		}
	}

	public void mouseEntered(MouseEvent e)
	{
		
	}

	public void mouseExited(MouseEvent e)
	{
		
	}

	public void mousePressed(MouseEvent e)
	{
		
	}

	public void mouseReleased(MouseEvent e)
	{
		
	}
}
