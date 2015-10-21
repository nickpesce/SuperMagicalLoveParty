package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import np.supermagicalloveparty.server.ServerGame;


public class GuiConsole
{
	public static final int CHAT_TIME = 5000;
	Game game;
	private Clipboard clipboard;
	private CopyOnWriteArrayList<Pair<String, Color>> log;
	private CopyOnWriteArrayList<Pair<Pair<String, Color>, Long>> chat;
	protected String input;
	protected int width;

	public GuiConsole(Game game)
	{
		this.game = game;
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		chat = new CopyOnWriteArrayList<Pair<Pair<String, Color>, Long>>();
		input = "";
		if(game instanceof ServerGame && game.frame != null)
			width = game.frame.getWidth();
		width = game.canvas.getCanvasWidth()/2;
		log = new CopyOnWriteArrayList<Pair<String, Color>>();
	}
	
	public void log(String message, Color color, boolean show)
	{
		log.add(new Pair<String, Color>(message, color));
		if(show)
		{
			chat.add(new Pair<Pair<String, Color>, Long>(new Pair<String, Color>(message, color), System.currentTimeMillis()+CHAT_TIME));
		}
	}
	
	public void updateChatTimes()
	{
		for(Pair<Pair<String, Color>, Long> p: chat)
		{
			if(p.getRight() <= System.currentTimeMillis())
			{
				chat.remove(p);
			}
		}
	}
	
	public void renderChat(Graphics2D g, double scale)
	{
		for(int i = chat.size()-1; i > chat.size() - 11; i--)
		{
			if(i < 0)
				break;
			int y = (int)(game.canvas.getCanvasHeight() - (chat.size() - i ) * 2 * scale);
			//g.setColor(chat.get(i).getLeft().getRight());
			g.setColor(Color.BLACK);
			g.drawString(chat.get(i).getLeft().getLeft(), (int)scale, y);
		}
	}
	
	public void render(Graphics2D g, double scale)
	{
		if(game instanceof ServerGame)
			width = game.frame.getWidth();
		width = game.canvas.getCanvasWidth()/2;
		g.setColor(new Color(0, 0, 0, 150));
		g.fillRect(0, (int)(game.canvas.getCanvasHeight() - (scale * 22)), (int)(width+scale), (int)(scale*22));
		g.fillRect(0, (int)(game.canvas.getCanvasHeight() - (scale*2)), (int)(width+scale), (int)(scale*2));
		g.setColor(Color.WHITE);
		for(int i = log.size()-1; i > log.size() - 11; i--)
		{
			if(i < 0)
				break;
			int y = (int)((game.canvas.getCanvasHeight()) - (log.size() - i ) * 2 * scale);
			g.setColor(log.get(i).getRight());
			g.drawString(log.get(i).getLeft(), (int)scale, y);
		}
		g.setColor(Color.MAGENTA);
		g.drawString(input, (int)scale, (int)(game.canvas.getCanvasHeight() - scale/2));
	}
	
	public void keyPress(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_BACK_QUOTE || e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			game.activeGui = Game.Gui.NONE;
			game.resume();
		}
		else if(e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			game.doCommand(input);
			input = "";
		}
		else if((e.getModifiers()&KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK)
		{
			if(e.getKeyCode() == KeyEvent.VK_V)
			{
				Transferable contents = clipboard.getContents(null);
				if(contents!=null && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
				{
					try
					{
						input += (String)contents.getTransferData(DataFlavor.stringFlavor);
					}
					catch (UnsupportedFlavorException | IOException e1)
					{
						e1.printStackTrace();
					}
				}
			}
			else if(e.getKeyCode() == KeyEvent.VK_C)
			{
				if(input.length()>0)
					clipboard.setContents(new StringSelection(input), null);
			}
			else if(e.getKeyCode() == KeyEvent.VK_X)
			{
				if(input.length()>0)
				{
				    clipboard.setContents(new StringSelection(input), null);
				    input="";
				}
			}
		}
		else
		{
			if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
			{
				if(input.length() > 0)
					input = input.substring(0, input.length()-1);
			}
			else if(e.getKeyCode() == KeyEvent.VK_DELETE)
			{
				//TODO delete and cursor in console
			}
			else if(!(e.getKeyChar() == KeyEvent.CHAR_UNDEFINED))
				input += e.getKeyChar();
		}
	}
	
	public int getWidth()
	{
		return width;
	}
}
