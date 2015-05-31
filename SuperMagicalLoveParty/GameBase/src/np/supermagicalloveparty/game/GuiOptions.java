package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class GuiOptions extends JPanel implements MouseListener, ItemListener, ChangeListener
{
	private static final long serialVersionUID = 1L;
	JButton bBack, bDefault;
	JLabel lFullscreen, lCamera, lPowerups, lGravity, lShowTiles, lMusic, lSfx, lEmpty, lSpeed;
	JToggleButton bFullscreen, bCamera, bShowTiles;
	JSlider sGravity, sPowerups, sMusic, sSfx, sSpeed;
	GuiOutOfGame cards;
	Settings settings;
	public GuiOptions(GuiOutOfGame parent, Settings settings)
	{
		cards = parent;
		this.settings = settings;
		int cols = 8 + ((settings.getDirectory() != Game.BASE_DIRECTORY)?1:0);
		setLayout(new GridLayout(cols, 4, 20, 30));
		addComponents();
		setBackground(Color.CYAN);
	}
	
	private void addComponents()
	{		
		bBack = new JButton("Back");
		bBack.addMouseListener(this);
		
		lFullscreen = new JLabel("Fullscreen");
		bFullscreen = new JToggleButton(settings.isFullscreenMode()?"ON":"OFF", !settings.isFullscreenMode());
		bFullscreen.addItemListener(this);
		
		lCamera = new JLabel("Auto Camera");
		bCamera = new JToggleButton(settings.isCamera()?"ON":"OFF", !settings.isCamera());
		bCamera.addItemListener(this);
		
		lShowTiles = new JLabel("Show Tiles");
		bShowTiles = new JToggleButton(settings.isShowTiles()?"ON":"OFF", !settings.isShowTiles());
		bShowTiles.addItemListener(this);
		
		bDefault = new JButton("Reset to Default");
		bDefault.addMouseListener(this);
		
		lPowerups = new JLabel("Spawn Powerups");
		sPowerups = new JSlider();
		sPowerups.setMaximum(100);
		sPowerups.setMinimum(0);
		sPowerups.setOpaque(false);
		sPowerups.setMajorTickSpacing(50);
		sPowerups.setMinorTickSpacing(25);
		sPowerups.setPaintTicks(true);
		sPowerups.setValue(settings.getPowerups());
		sPowerups.addChangeListener(this);
		
		lGravity = new JLabel("Gravity");
		sGravity = new JSlider();
		sGravity.setMaximum(100);
		sGravity.setMinimum(1);
		sGravity.setOpaque(false);
		sGravity.setMajorTickSpacing(50);
		sGravity.setMinorTickSpacing(25);
		sGravity.setPaintTicks(true);
		sGravity.setValue(settings.getGravity());
		sGravity.addChangeListener(this);
		
		lSpeed = new JLabel("Speed");
		sSpeed = new JSlider();
		sSpeed.setMaximum(100);
		sSpeed.setMinimum(1);
		sSpeed.setOpaque(false);
		sSpeed.setMajorTickSpacing(50);
		sSpeed.setMinorTickSpacing(20);
		sSpeed.setPaintTicks(true);
		sSpeed.setValue(settings.getSpeed());
		sSpeed.addChangeListener(this);
		
		lMusic = new JLabel("Music Volume");
		sMusic = new JSlider();
		sMusic.setMaximum(100);
		sMusic.setMinimum(0);
		sMusic.setOpaque(false);
		sMusic.setMajorTickSpacing(50);
		sMusic.setMinorTickSpacing(25);
		sMusic.setPaintTicks(true);
		sMusic.setValue(settings.getMusicVolume());
		sMusic.addChangeListener(this);
		
		lSfx = new JLabel("Sfx Volume");
		sSfx = new JSlider();
		sSfx.setMaximum(100);
		sSfx.setMinimum(0);
		sSfx.setOpaque(false);
		sSfx.setMajorTickSpacing(50);
		sSfx.setMinorTickSpacing(25);
		sSfx.setPaintTicks(true);
		sSfx.setValue(settings.getSfxVolume());
		sSfx.addChangeListener(this);
		
		add(bBack);add(bDefault);
		add(lFullscreen);add(bFullscreen);
		add(lCamera);add(bCamera);
		if(settings.getDirectory() != Game.BASE_DIRECTORY)
		{
			add(lShowTiles);add(bShowTiles);
		}
		add(lPowerups);add(sPowerups);
		add(lGravity);add(sGravity);
		add(lSpeed);add(sSpeed);
		add(lMusic);add(sMusic);
		add(lSfx);add(sSfx);
	}

	public void setSettings(Settings settings)
	{
		this.settings = settings;
		this.removeAll();
		int cols = 8 + ((settings.getDirectory() != Game.BASE_DIRECTORY)?1:0);
		setLayout(new GridLayout(cols, 4, 20, 30));
		addComponents();
		repaint();
	}
	
	public void resetToDefault()
	{
		settings.setToDefault();
		removeAll();
		addComponents();
		revalidate();
		if(!settings.getDirectory().equals(Game.BASE_DIRECTORY))
		{
			FileHelper.deleteFile(settings.getDirectory()+"\\Settings.txt");
		}
		cards.setUpdateLevelSelect();
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		if(arg0.getComponent().equals(bBack))
		{
			cards.flipBack();
		}
		else if(arg0.getComponent().equals(bDefault))
		{
			resetToDefault();
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
		if(arg0.getSource().equals(bFullscreen))
		{
			if(arg0.getStateChange()==ItemEvent.SELECTED)
			{
				settings.setFullscreenMode(false);
				bFullscreen.setText("OFF");
			}
			else
			{
				settings.setFullscreenMode(true);
				bFullscreen.setText("ON");
			}
		}
		else if(arg0.getSource().equals(bCamera))
		{
			if(arg0.getStateChange()==ItemEvent.SELECTED)
			{
				settings.setCamera(false);
				bCamera.setText("OFF");
			}
			else
			{
				settings.setCamera(true);
				bCamera.setText("ON");
			}
		}
		else if(arg0.getSource().equals(bShowTiles))
		{
			if(arg0.getStateChange()==ItemEvent.SELECTED)
			{
				settings.setShowTiles(false);
				bShowTiles.setText("OFF");
			}
			else
			{
				settings.setShowTiles(true);
				bShowTiles.setText("ON");
			}
		}
		cards.setUpdateLevelSelect();
	}

	@Override
	public void stateChanged(ChangeEvent arg0)
	{
		if(arg0.getSource().equals(sGravity))
		{
			settings.setGravity(sGravity.getValue());
		}
		else if(arg0.getSource().equals(sSpeed))
		{
			settings.setSpeed(sSpeed.getValue());
		}
		else if(arg0.getSource().equals(sPowerups))
		{
			settings.setPowerups(sPowerups.getValue());
		}
		else if(arg0.getSource().equals(sMusic))
		{
			settings.setMusicVolume(sMusic.getValue());
		}
		else if(arg0.getSource().equals(sSfx))
		{
			settings.setSfxVolume(sSfx.getValue());
		}
		cards.setUpdateLevelSelect();
	}
}
