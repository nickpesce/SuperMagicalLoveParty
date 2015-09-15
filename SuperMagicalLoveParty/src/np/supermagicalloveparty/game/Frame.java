package np.supermagicalloveparty.game;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;


public class Frame extends JFrame implements ComponentListener{

	private static final long serialVersionUID = 7552526367108577562L;
	private GraphicsDevice vc;
	GameCanvas canvas;
	
	public Frame() 
	{
		setVisible(true);
		setSize(1600, 910);
		setTitle(Game.NAME);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try
		{
			setIconImage(ImageIO.read(getClass().getResourceAsStream("/rainbowcupcake.png")));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	    setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
	    this.addComponentListener(this);
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		vc = env.getDefaultScreenDevice();
	}

	public Frame(GuiOutOfGame gui)
	{
		this();
		setGui(gui);
	}
	
	public void setFullScreen(boolean f)
	{
		if(isDisplayable())dispose();
		setVisible(false);
		if(f)
		{
			setUndecorated(true);//Title bars/scroll bars etc
			setResizable(false);
			vc.setFullScreenWindow(this);
		}else
		{
			setUndecorated(false);
			setVisible(true);
			setResizable(true);
			vc.setFullScreenWindow(null);
		}
		//TODO throws error and dont think i need it
		/*
		DisplayMode dm = new DisplayMode(Toolkit.getDefaultToolkit().getScreenSize().width, Toolkit.getDefaultToolkit().getScreenSize().height, 24, 60);

		if(dm != null && vc.isDisplayChangeSupported())
		{
			try
			{
				vc.setDisplayMode(dm);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		*/
	}

	public void setGui(GuiOutOfGame gui)
	{
		if(canvas != null)
		{
			remove(canvas);
			canvas = null;
		}
		gui.setFrame(this);
		add(gui);
		revalidate();
	}
	
	public void setGameCanvas(GameCanvas c)
	{
		this.canvas = c;
		this.add(canvas);
		if(canvas instanceof GameCanvas)
			canvas.updateScale(this);
		revalidate();
	}

	@Override
	public void componentResized(ComponentEvent e) 
	{
		if(canvas != null)
		{
			canvas.updateScale(this);
			revalidate();
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {}
	@Override
	public void componentHidden(ComponentEvent e) {}
	@Override
	public void componentMoved(ComponentEvent e) {}

	
}
