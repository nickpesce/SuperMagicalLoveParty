package np.supermagicalloveparty.game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class GuiServerSetup extends JPanel implements GuiWithLevel, MouseListener, KeyListener, ItemListener
{
	GuiOutOfGame cards;
	JButton bStart, bBack;
	GuiLevelSelectBox levelSelect;
	GuiLevelPreview preview;
	JComboBox<Integer> playerNumChooser;
	JCheckBox cbDefaultLevel;
	JTextField tfPort;
	JLabel lLevel, lPlayers, ownSettings, lDefaultLevel, lPort;
	Level level;
	int maxPlayers;
	int port;
	boolean defaultLevel;
	
	public GuiServerSetup(GuiOutOfGame parent)
	{
		cards = parent;
		maxPlayers = 9;
		port = 6969;
		setVisible(true);
		setBackground(Color.GREEN);
		addComponents();
		revalidate();
	}
	
	public void addComponents()
	{
		bStart = new JButton("Start");
		bStart.addMouseListener(this);
		bBack = new JButton("Back");
		bBack.addMouseListener(this);
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
		playerNumChooser = new JComboBox<Integer>();
		lLevel = new JLabel("Level: ");
		lLevel.setForeground(Color.WHITE);
		levelSelect = new GuiLevelSelectBox(this);
		levelSelect.addKeyListener(this);
		lPlayers = new JLabel("Max Players: ");
		lPlayers.setForeground(Color.WHITE);
		preview = new GuiLevelPreview(level, maxPlayers);
		lDefaultLevel = new JLabel("Use Default level: ");
		lDefaultLevel.setForeground(Color.WHITE);
		cbDefaultLevel = new JCheckBox();
		cbDefaultLevel.addItemListener(this);
		cbDefaultLevel.setSelected(true);
		cbDefaultLevel.setBackground(Color.GREEN);
		playerNumChooser.addItemListener(this);
		playerNumChooser.setSelectedItem(level.getSpawns().length);
		lPort = new JLabel("Port: ");
		lPort.setForeground(Color.WHITE);
		tfPort = new JTextField("6969");
		tfPort.addKeyListener(this);
		tfPort.setPreferredSize(new Dimension(60,20));
		add(bBack);
		add(bStart);
		add(lDefaultLevel);
		add(cbDefaultLevel);
		add(ownSettings);
		add(lLevel);
		add(levelSelect);
		add(lPlayers);
		add(playerNumChooser);
		add(lPort);
		add(tfPort);
		add(preview);
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
					mode = Game.Mode.HEALTH;
				cards.startServer(defaultLevel ? null:level, maxPlayers, port, mode);
			}
		}else if(arg0.getComponent().equals(bBack))
		{
			cards.flipBack();
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		
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
	public void keyReleased(KeyEvent arg0)
	{
		if(arg0.getSource().equals(tfPort))
		{
			try{
				port = Integer.parseInt(tfPort.getText());
			}catch(NumberFormatException e)
			{
				port = 0;
				tfPort.setText("");
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent arg0)
	{
		if(arg0.getSource().equals(tfPort))
		{
            char character = arg0.getKeyChar();
            if (((character < '0') || (character > '9'))
                    && (character != '\b')) {
                arg0.consume();
                return;
            }
		}
	}

	@Override
	public void setLevel(Level level)
	{
		this.level = level;
		playerNumChooser.removeAllItems();
		ownSettings.setVisible(level.hasOwnSettings());
		for(int i = 0; i < level.getSpawns().length; i++)
		{
			playerNumChooser.addItem(i+1);
		}
		playerNumChooser.setSelectedItem(level.getSpawns().length);
		if(preview!=null)
		{
			preview.setLevel(level);
			preview.setPlayers(maxPlayers);
		}
	}
	
	public void updateNumPlayers()
	{
		if(playerNumChooser.getSelectedItem()!=null)
		{
			maxPlayers = (int)(playerNumChooser.getSelectedItem());
		}else
			maxPlayers = 0;
		preview.setPlayers(maxPlayers);	
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		if(arg0.getSource().equals(playerNumChooser))
		{
			updateNumPlayers();
		}else if(arg0.getSource().equals(cbDefaultLevel))
		{
			defaultLevel = cbDefaultLevel.isSelected();
			levelSelect.setVisible(!defaultLevel);
			lLevel.setVisible(!defaultLevel);
			ownSettings.setVisible(!defaultLevel && level.hasOwnSettings());
			preview.setVisible(!defaultLevel);
			playerNumChooser.removeAllItems();
			if(defaultLevel)
			{
				for(int i = 0; i < 9; i++)
				{
					playerNumChooser.addItem(i+1);
				}
				playerNumChooser.setSelectedItem(9);
			}else
			{
				for(int i = 0; i < level.getSpawns().length; i++)
				{
					playerNumChooser.addItem(i+1);
				}
				playerNumChooser.setSelectedItem(level.getSpawns().length);
			}
			revalidate();
			preview.repaint();
			repaint();
		}
	}

}
