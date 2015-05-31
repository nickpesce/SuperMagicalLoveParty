package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.TextField;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GuiLevelEditor extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, GuiWithLevel, TextListener, ItemListener, ComponentListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	JButton bBack, bNew, bDelete, bSave, bRevert, bSettings;
	ButtonGroup bgTileButtons;
	JRadioButton rbSolid, rbUp, rbBounceDamage, rbIce, rbSpawn, rbLollipop;
	char currTile;
	/**
	 * add tile to terrain or entities
	 */
	boolean editTerrain;
	boolean changed;
	JPanel pTileButtonContainer;
	GuiOutOfGame cards;
	GuiLevelSelectBox levelSelect;
	TextField tfWidth, tfHeight;
	JLabel lX;
	Level level;
	GuiLevelPreview preview;
	
	public GuiLevelEditor(GuiOutOfGame parent)
	{
		cards = parent;
		bgTileButtons = new ButtonGroup();
		addComponents();
		addComponentListener(this);
		addKeyListener(this);
		setBackground(Color.RED);
		currTile = 'S';
	}
	
	private void addComponents()
	{
		bBack = new JButton("Back");
		bBack.addMouseListener(this);
		bNew = new JButton("New Level");
		bNew.addMouseListener(this);
		bDelete = new JButton("Delete");
		bDelete.addMouseListener(this);
		bSave = new JButton("Save");
		bSave.addMouseListener(this);
		bRevert = new JButton("Revert Changes");
		bRevert.addMouseListener(this);
		bSettings = new JButton("Level Settings");
		bSettings.addMouseListener(this);
		pTileButtonContainer = new JPanel();
		rbSolid = new JRadioButton("Solid", true);
		rbSolid.addItemListener(this);
		bgTileButtons.add(rbSolid);
		pTileButtonContainer.add(rbSolid);
		rbUp = new JRadioButton("One way");
		rbUp.addItemListener(this);
		bgTileButtons.add(rbUp);
		pTileButtonContainer.add(rbUp);
		rbBounceDamage = new JRadioButton("Bouncy Bouncy hurt hurt");
		rbBounceDamage.addItemListener(this);
		bgTileButtons.add(rbBounceDamage);
		pTileButtonContainer.add(rbBounceDamage);
		rbIce = new JRadioButton("Ice");
		rbIce.addItemListener(this);
		bgTileButtons.add(rbIce);
		pTileButtonContainer.add(rbIce);
		rbSpawn = new JRadioButton("Spawn");
		rbSpawn.addItemListener(this);
		bgTileButtons.add(rbSpawn);
		pTileButtonContainer.add(rbSpawn);
		rbLollipop = new JRadioButton("Lollipop");
		rbLollipop.addItemListener(this);
		bgTileButtons.add(rbLollipop);
		pTileButtonContainer.add(rbLollipop);
		levelSelect = new GuiLevelSelectBox(this);
		levelSelect.addMouseListener(this);
		preview = new GuiLevelPreview(level);
		preview.addMouseListener(this);
		preview.addMouseMotionListener(this);
		preview.addMouseWheelListener(this);
		tfWidth = new TextField("160", 5);
		tfWidth.addTextListener(this);
		tfWidth.setEditable(false);//TODO change width and height in level editor
		lX = new JLabel("x");
		tfHeight = new TextField("90", 5);
		tfHeight.setEditable(false);
		tfHeight.addTextListener(this);
		add(bBack);
		add(bNew);
		add(bDelete);
		add(bSave);
		add(bRevert);
		add(levelSelect);
		add(bSettings);
		add(tfWidth);
		add(lX);
		add(tfHeight);
		add(pTileButtonContainer);
		add(preview);
	}

	public void setLevel(Level level)
	{
		if(changed)
			if(JOptionPane.showConfirmDialog(null, "Save", "Do you want to save " + level.getName()+"?", JOptionPane.YES_NO_OPTION)==JOptionPane.OK_OPTION)
				saveLevel(level);
		this.level = level;
		if(preview!=null)
		{
			changed = false;
			preview.setLevel(level);
			tfWidth.setText(level.getWidth()+"");
			tfHeight.setText(level.getHeight()+"");
			preview.revalidate();
		}
	}
	
	public void saveLevel(Level level)
	{
		FileHelper.saveLevel(level);
		changed = false;
		levelSelect.refresh(levelSelect.selectedIndex);
		cards.setUpdateLevelSelect();
	}
	
	private void tryRemove(int x, int y)
	{
		level.setTile(x, y, Level.EMPTY);
		preview.repaint((int)(x*preview.getScale()), (int)(y*preview.getScale()), (int)(preview.getScale())+1, (int)(preview.getScale())+1);
		for(int i = x-Player.MAX_WIDTH+1; i < x+1; i++)
		{
			for(int j = y- Player.MAX_HEIGHT+1; j < y+1; j++)
			{
				if(level.getEntityAtLocation(i, j) == (char)('0'+level.getSpawns().length))
				{
					level.setEntity(i, j, '0');
					preview.repaint((int)(x*preview.getScale()), (int)(y*preview.getScale()), (int)(preview.getScale()*Player.MAX_WIDTH)+1, (int)(preview.getScale()*Player.MAX_HEIGHT)+1);
				}
			}
		}
		for(int i = x-4+1; i < x+1; i++)
		{
			for(int j = y- 10+1; j < y+1; j++)
			{
				if(level.getEntityAtLocation(i, j) == Level.LOLLIPOP)
				{
					level.setEntity(i, j, '0');
					preview.repaint((int)(x*preview.getScale()), (int)(y*preview.getScale()), (int)(preview.getScale()*4)+1, (int)(preview.getScale()*10)+1);
				}
			}
		}
	}
	
	private void tryAdd(int x, int y, char c)
	{
		if(editTerrain)
		{
			for(int i = x-Player.MAX_WIDTH+1; i < x+1; i++)
			{
				for(int j = y- Player.MAX_HEIGHT+1; j < y+1; j++)
				{
					if(level.getEntityAtLocation(i, j) != '0')
						return;
				}
			}
			level.setTile(x, y, currTile);
			preview.repaint((int)(x*preview.getScale()), (int)(y*preview.getScale()), (int)(preview.getScale())+1, (int)(preview.getScale())+1);
		}
		else
		{
			if(currTile == Level.LOLLIPOP)
			{
				if(level.getTileAtLocation(x, y) == Level.EMPTY)
				{
					level.setEntity(x, y, Level.LOLLIPOP);
					preview.repaint((int)(x*preview.getScale()), (int)(y*preview.getScale()), (int)(preview.getScale()*4)+1, (int)(preview.getScale()*10)+1);
				}
			}else
			{
				for(int i = x; i < x+Player.MAX_WIDTH; i++)
				{
					for(int j = y; j < y+Player.MAX_HEIGHT; j++)
					{
						if(level.getTileAtLocation(i, j) != Level.EMPTY)
							return;
					}
				}
				level.setEntity(x, y, (char)('0'+(level.getSpawns().length+1)));
				preview.repaint((int)(x*preview.getScale()), (int)(y*preview.getScale()), (int)(preview.getScale()*Player.MAX_WIDTH)+1, (int)(preview.getScale()*Player.MAX_HEIGHT)+1);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		if(arg0.getComponent().equals(preview))
		{
			int x = (int) (arg0.getX() / preview.getScale());
			int y = (int) (arg0.getY() / preview.getScale());
			changed = true;
			if(SwingUtilities.isLeftMouseButton(arg0))
			{
				tryAdd(x, y, currTile);
			}
			else if(SwingUtilities.isRightMouseButton(arg0))
			{
				tryRemove(x, y);
			}
		}
		else if(arg0.getComponent().equals(bBack))
		{
			if(changed)
			{
				int answer = JOptionPane.showConfirmDialog(null, "Save", "Do you want to save " + level.getName()+"?", JOptionPane.YES_NO_CANCEL_OPTION);
				if(answer==JOptionPane.OK_OPTION)
				{
					FileHelper.saveLevel(level);
					cards.setUpdateLevelSelect();
				}
				else if(answer==JOptionPane.CANCEL_OPTION)
					return;
			}
			changed = false;
			cards.flipBack();
		}
		else if(arg0.getComponent().equals(bNew))
		{
			try{
				String newName, sNewWidth, sNewHeight;
				int newWidth, newHeight;
				if((newName = JOptionPane.showInputDialog("Enter Name"))==null)
					return;
				if((sNewWidth = JOptionPane.showInputDialog("Enter Width"))==null)
					return;
				newWidth = Integer.parseInt(sNewWidth);
				if((sNewHeight = JOptionPane.showInputDialog("Enter Height"))==null)
					return;
				newHeight = Integer.parseInt(sNewHeight);
				if(JOptionPane.showConfirmDialog(this, "Do you want to choose a background image?", "Background", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				{
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileFilter(new FileNameExtensionFilter("Picture file: png", "png"));
					if(fileChooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION)
					{
						FileHelper.copyImage(new File(Game.BASE_DIRECTORY + "Levels/" + newName + "~" + newWidth+"x"+newHeight+ "/Background.png"), fileChooser.getSelectedFile());
					}else 
						return;
				}
				FileHelper.writeEmpty(new File(Game.BASE_DIRECTORY + "Levels/" + newName + "~" + newWidth+"x"+newHeight+ "/Entities.txt"), newWidth, newHeight, '0');
				FileHelper.writeEmpty(new File(Game.BASE_DIRECTORY + "Levels/" + newName + "~" + newWidth+"x"+newHeight+ "/Terrain.txt"), newWidth, newHeight, 'E');
				changed = false;
				levelSelect.refresh(newName);
			}catch(NumberFormatException e)
			{
				JOptionPane.showMessageDialog(null, "Invalid Number!");
			}
		}
		else if(arg0.getComponent().equals(bDelete))
		{
			if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + level.getName() + "?", "Delete Level", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
			{
				 FileHelper.deleteDirectory(new File(Game.BASE_DIRECTORY + "Levels/" + level.getName() +"~" + level.getWidth()+"x"+level.getHeight()));
				 changed = false;
				 levelSelect.refresh(0);
			}
		}
		else if(arg0.getComponent().equals(bSave))
		{
			saveLevel(level);
		}
		else if(arg0.getComponent().equals(bRevert))
		{
			changed = false;
			levelSelect.refresh(levelSelect.selectedIndex);
		}
		else if(arg0.getComponent().equals(bSettings))
		{
			cards.enterOptions(level);
		}
		else if(arg0.getComponent().equals(levelSelect))
		{
			preview.render();
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
	public void textValueChanged(TextEvent arg0)
	{
		if(arg0.getSource().equals(tfWidth))
		{
			try
			{
				if(!tfWidth.getText().equals("ERROR"))
					level.setWidth(Integer.parseInt(tfWidth.getText()));
			}catch(Exception e)
			{
				tfWidth.setText("ERROR");
			}
		}else if(arg0.getSource().equals(tfHeight))
		{
			try
			{
				if(!tfHeight.getText().equals("ERROR"))
					level.setHeight(Integer.parseInt(tfHeight.getText()));
			}catch(Exception e)
			{
				tfHeight.setText("ERROR");
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		if(arg0.getComponent().equals(preview))
		{	
			int x = (int) (arg0.getX() / preview.getScale());
			int y = (int) (arg0.getY() / preview.getScale());
			changed =true;
			
			if(SwingUtilities.isLeftMouseButton(arg0) && editTerrain)
			{
				tryAdd(x, y, currTile);
			}
			else if(SwingUtilities.isRightMouseButton(arg0))
			{
				tryRemove(x, y);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		int mouseX = (int)(arg0.getX()/preview.getScale());
		int mouseY = (int)(arg0.getY()/preview.getScale());
		if(currTile == Level.LOLLIPOP)
			preview.drawExtra(Level.iLollipopTurquoise, mouseX, mouseY, 4, 10);
		else
			preview.drawExtra(null, 0, 0, 0, 0);
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		if(arg0.getSource().equals(rbSolid))
		{
			currTile = Level.SOLID;
			editTerrain = true;
		}
		else if(arg0.getSource().equals(rbUp))
		{
			currTile = Level.UP;
			editTerrain = true;
		}
		else if(arg0.getSource().equals(rbBounceDamage))
		{
			currTile = Level.DAMAGE_AND_KNOCKBACK;
			editTerrain = true;
		}
		else if(arg0.getSource().equals(rbIce))
		{
			currTile = Level.ICE;
			editTerrain = true;
		}
		else if(arg0.getSource().equals(rbLollipop))
		{
			currTile = Level.LOLLIPOP;
			editTerrain = false;
		}
		else if(arg0.getSource().equals(rbSpawn))
		{
			currTile = Level.ICE;
			editTerrain = false;
		}
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0)
	{
		if(arg0.getComponent().equals(preview))
		{
			if(preview.getScale()-(preview.getOrigScale()/arg0.getUnitsToScroll()) <= preview.getOrigScale())
			{
				preview.setScale(preview.getOrigScale());
			}else
				preview.setScale(preview.getScale()-(preview.getOrigScale()/arg0.getUnitsToScroll()));
			preview.centerAround(arg0.getPoint());
		}
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
		requestFocusInWindow();
	}

	@Override
	public void keyReleased(KeyEvent arg0){}

	@Override
	public void keyTyped(KeyEvent arg0){}

	@Override
	public void componentHidden(ComponentEvent arg0){}

	@Override
	public void componentMoved(ComponentEvent arg0)	{}

	@Override
	public void componentResized(ComponentEvent arg0){}
}
