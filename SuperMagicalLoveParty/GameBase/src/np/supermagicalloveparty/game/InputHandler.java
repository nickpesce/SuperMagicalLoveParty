package np.supermagicalloveparty.game;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;


public class InputHandler implements KeyListener, MouseListener, MouseMotionListener
{
	ArrayList<Integer> pressedKeys;
	ArrayList<Player> players;
	HashMap<Player, KeyMap> keyMaps;
	Game game;
	
	public static final int 
	PLAYER_1 = 0,
	PLAYER_2 = 1,
	
	LEFT = 5,
	RIGHT = 6,
	JUMP = 7,
	SQUAT = 8,
	EVADE = 9,
	ATTACK = 10,
	ATTACK_CHARGED = 11;

	
	public InputHandler(Game game)
	{
		this.game = game;
		players = new ArrayList<Player>(4);
		keyMaps = new HashMap<Player, KeyMap>(4);
		pressedKeys = new ArrayList<Integer>();
	}
	
	public void addPlayer(Player p, KeyMap keys)
	{
		players.add(players.size(), p);
		keyMaps.put(p, keys);
	}
	
	public void changeKeyMap(Player p, KeyMap keys)
	{
		keyMaps.put(p, keys);
	}
	
	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		if(game.activeGui.equals(Game.Gui.CONTROLS))
		{
			game.guiControls.keyPress(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.keyPress(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.CONSOLE))
		{
			game.guiConsole.keyPress(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.keyPress(arg0);
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_BACK_QUOTE)
		{
			if(game.state.equals(Game.State.ANIMATING) || game.state.equals(Game.State.COUNTDOWN))
				return;
			game.activeGui = Game.Gui.CONSOLE;
			game.pause();
		}
		else if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			if(game.state.equals(Game.State.ANIMATING) || game.state.equals(Game.State.COUNTDOWN))
				return;
			if(game.state.equals(Game.State.PAUSED))
			{
				game.resume();
			}
			else
			{
				game.activeGui = Game.Gui.MENU;
				game.pause();
			}
		}
		else
		{
			if(!game.state.equals(Game.State.PLAYING))
				return;
			for(int i = 0; i < players.size(); i++)
			{
				Player p = players.get(i);
				if(keyMaps.get(p).getIsAssigned(arg0.getKeyCode()))
				{
					if(!pressedKeys.contains(arg0.getKeyCode()))
					{
						pressedKeys.add(arg0.getKeyCode());
						doAction(keyMaps.get(p).getAction(arg0.getKeyCode()), p);
					}
				}
			}
		}
	}

	public void doAction(int action, Player p)
	{
		switch(action)
		{
		case LEFT: p.runLeft();
			break;
		case RIGHT: p.runRight();
			break;
		case JUMP: if(p.canJump())p.jump();
			break;
		case SQUAT: p.squat();
			break;
		case EVADE: p.evade();
			break;
		case ATTACK: p.startAttack();
			break;
		}
	}
	
	public void stopAction(int action, Player p)
	{
		
		switch(action)
		{
		case LEFT:
			p.stopLeft();
			break;
		case RIGHT:
			p.stopRight();
			break;
		case JUMP:
			break;
		case SQUAT:
			p.stand();
			for(int k : pressedKeys)
				if(keyMaps.get(p).getAction(k) == LEFT)
					p.runLeft();
				else if(keyMaps.get(p).getAction(k) == RIGHT)
					p.runRight();
		case EVADE:
			break;
		case ATTACK: p.doAttack();
			break;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent arg0)
	{
		for(int i = 0; i < players.size(); i++)
		{
			Player p = players.get(i);
			if(keyMaps.get(p).getIsAssigned(arg0.getKeyCode()))
			{
				stopAction(keyMaps.get(p).getAction(arg0.getKeyCode()), p);
			}
		}
		pressedKeys.remove((Integer)arg0.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent arg0) 
	{
	}

	public boolean isKeyUnique(int keyCode)//TODO put somewhere better. give all KeyMaps to game class?
	{
		if(keyCode == KeyEvent.VK_BACK_QUOTE || keyCode == KeyEvent.VK_ESCAPE)
			return false;
		for(KeyMap keyMap : keyMaps.values())
		{
			for(int k : keyMap.getKeyMap().keySet())
			{
				if(k == keyCode)
					return false;
			}
		}
		return true;
	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{

		if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.buttons.mouseClicked(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.buttons.mouseClicked(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.CONTROLS))
		{
			game.guiControls.mouseClicked(arg0);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.buttons.mouseEntered(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.buttons.mouseEntered(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.CONTROLS))
		{
			game.guiControls.mouseEntered(arg0);
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.buttons.mouseExited(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.buttons.mouseExited(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.CONTROLS))
		{
			game.guiControls.mouseExited(arg0);
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.buttons.mousePressed(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.buttons.mousePressed(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.CONTROLS))
		{
			game.guiControls.mousePressed(arg0);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.buttons.mouseReleased(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.buttons.mouseReleased(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.CONTROLS))
		{
			game.guiControls.mouseReleased(arg0);
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.buttons.mouseDragged(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.buttons.mouseDragged(arg0);
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		if(game.activeGui.equals(Game.Gui.MENU))
		{
			game.guiIngame.buttons.mouseMoved(arg0);
		}
		else if(game.activeGui.equals(Game.Gui.GAME_OVER))
		{
			game.guiGameOver.buttons.mouseMoved(arg0);
		}
	}
}
