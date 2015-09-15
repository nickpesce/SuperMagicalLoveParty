package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Image;
import java.awt.TextField;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class GuiGameSelect extends JPanel implements MouseListener, KeyListener, ComponentListener, ItemListener, GuiWithLevel
{
	private static final long serialVersionUID = 1L;
	JButton bStart, bBack;
	JLabel ownSettings, lLevel, lPlayers;
	JComboBox<Integer> playerNumChooser;
	GuiLevelSelectBox levelSelect;
	int numPlayers;
	PlayerSelection[] playerSelections;

	GuiLevelPreview preview;
	GuiOutOfGame cards;
	Level level;
	
	public GuiGameSelect(GuiOutOfGame parent)
	{
		cards = parent;
		numPlayers = 0;
		setVisible(true);
		setBackground(Color.BLUE);
		setLayout(null);
		addComponents();
		this.addComponentListener(this);
		this.addKeyListener(this);
		revalidate();
	}
	
	private void addComponents()
	{
		bStart = new JButton("Start Game");
		bStart.addMouseListener(this);
		bBack = new JButton("Back");
		bBack.addMouseListener(this);
		lPlayers = new JLabel("# Players");
		lPlayers.setForeground(Color.WHITE);
		playerNumChooser = new JComboBox<Integer>();
		try
		{
			ownSettings = new JLabel(new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/Coopcake.png")).getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
			ownSettings.setToolTipText("This level uses its own settings");
		}
		catch (IOException e)
		{
			ownSettings = new JLabel("Level Settings");
			e.printStackTrace();
		}
		lLevel = new JLabel("Level");
		lLevel.setForeground(Color.WHITE);
		levelSelect = new GuiLevelSelectBox(this);
		levelSelect.addKeyListener(this);
		playerNumChooser.addItemListener(this);
		playerNumChooser.addKeyListener(this);
		playerSelections = new PlayerSelection[4];
		playerSelections[0] = new PlayerSelection("Player 1");
		playerSelections[0].addKeyListener(this);
		playerSelections[1] = new PlayerSelection("Player 2");
		playerSelections[1].addKeyListener(this);
		playerSelections[2] = new PlayerSelection("Player 3");
		playerSelections[2].addKeyListener(this);
		playerSelections[3] = new PlayerSelection("Player 4");
		playerSelections[3].addKeyListener(this);
		ownSettings.setVisible(level.hasOwnSettings());
		preview = new GuiLevelPreview(level, numPlayers);
		playerNumChooser.setSelectedItem(2);
		
		add(bStart);
		add(bBack);
		add(ownSettings);
		add(lLevel);
		add(levelSelect);
		add(lPlayers);
		add(preview);
		add(playerNumChooser);

		updateNumPlayers();
	}
	
	public void updateComponentLocations()
	{
		bStart.setSize(220, 30);
		bStart.setLocation((getWidth()/2) - 110, 10);
		bBack.setSize(100, 30);
		bBack.setLocation(10, 10);
		lPlayers.setLocation((getWidth()/2)-110, 37);
		lPlayers.setSize(60, 30);
		playerNumChooser.setSize(100, 30);
		playerNumChooser.setLocation((getWidth()/2)-110, 60);
		lLevel.setLocation((getWidth()/2)+10, 37);
		lLevel.setSize(40, 30);
		levelSelect.setSize(100, 30);
		levelSelect.setLocation((getWidth()/2)+10, 60);
		ownSettings.setSize(30, 30);
		ownSettings.setLocation((getWidth()/2)+110, 60);
		for(int i = 0; i < numPlayers; i++)
		{
			playerSelections[i].setSize(170, 35);
			playerSelections[i].setLocation((int)((getWidth()/2) - (playerSelections[i].getWidth()/2) - ((numPlayers-i-.5)-(numPlayers/2.0))*(playerSelections[i].getWidth()+30/*x-spacing*/)), 100);
		}
		preview.setLocation((getWidth()/2)-(preview.getWidth()/2), 150);
	}
	
	private String[] getCharacterArray()
	{
		String[] characters = new String[numPlayers];
		for(int i = 0; i < numPlayers; i++)
		{
			characters[i] = playerSelections[i].getCharacter();
		}
		return characters;
	}
	
	private String[] getNameArray()
	{
		String[] characters = new String[numPlayers];
		for(int i = 0; i < numPlayers; i++)
		{
			characters[i] = playerSelections[i].getPlayerName();
		}
		return characters;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		if(arg0.getComponent().equals(bStart))
		{
			if(level.getSpawns().length<=0)
				JOptionPane.showMessageDialog(this, "Error: this map does not have any spawn points!");
			else
			{
				Game.Mode mode = Game.Mode.SATISFACTION;
				if(SwingUtilities.isRightMouseButton(arg0))
				{
					mode = Game.Mode.HEALTH;
				}
				cards.startGame(level, getCharacterArray(), getNameArray(), mode);
			}
		}else if(arg0.getComponent().equals(bBack))
		{
			cards.flipBack();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		if(arg0.getSource().equals(playerNumChooser))
		{
			updateNumPlayers();
		}
	}

	public void updateNumPlayers()
	{
		if(playerNumChooser.getSelectedItem()!=null)
		{
			numPlayers = (int)(playerNumChooser.getSelectedItem());
		}else
			numPlayers = 0;
		updatePlayerSelections();
		preview.setPlayers(numPlayers);	
	}
	
	public void setLevel(Level level)
	{
		this.level = level;
		playerNumChooser.removeAllItems();
		ownSettings.setVisible(level.hasOwnSettings());
		for(int i = 0; i < level.getSpawns().length && i < 4; i++)
		{
			playerNumChooser.addItem(i+1);
		}
		//playerNumChooser.setSelectedItem(Math.min(level.getSpawns().length-1, 4));
		playerNumChooser.setSelectedIndex(playerNumChooser.getItemCount()-1);
		if(preview!=null)
		{
			preview.setLevel(level);
			preview.setPlayers(numPlayers);
		}
	}
	
	private void updatePlayerSelections()
	{
		for(PlayerSelection ps : playerSelections)
			remove(ps);
		for(int i = 0; i < numPlayers; i++)
		{
			add(playerSelections[i]);
		}
		updateComponentLocations();
		revalidate();
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			cards.enterMainMenu();
		}else if(arg0.getKeyCode() == KeyEvent.VK_F5)
		{
			levelSelect.refresh(0);
		}
	}
	
	@Override
	public void componentShown(ComponentEvent arg0)
	{
		requestFocusInWindow();//TODO guis fix foxus for esc and F5
	}
	
	@Override
	public void componentResized(ComponentEvent arg0)
	{
		updateComponentLocations();
	}

	@Override
	public void keyReleased(KeyEvent arg0){}

	@Override
	public void keyTyped(KeyEvent arg0){}

	@Override
	public void componentHidden(ComponentEvent arg0){}

	@Override
	public void componentMoved(ComponentEvent arg0)	{}

}
