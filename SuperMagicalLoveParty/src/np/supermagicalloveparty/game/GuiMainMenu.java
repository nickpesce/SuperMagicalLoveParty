package np.supermagicalloveparty.game;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTextArea;


public class GuiMainMenu extends Canvas implements MouseListener, MouseMotionListener, ComponentListener, GameLoopable
{
	private static final long serialVersionUID = 1L;
	JTextArea taChangelog;
	//JButton bOptions, bLevelEditor, bStart, bMultiplayer, bServer;
	GuiButtonManager buttons;
	ImageIcon bIconRegular, bIconHover, bIconClicked;
	GuiOutOfGame cards;
	
	BufferedImage heart, boot, cupcake, potion, apple, sun, s, u, p, e, r, m, a, g, i, c, a2, l, l2, o, v, e2, p2, a3, r2, t, y;//, ex;
	GradientPaint backgroundPaint;
	Color backgroundBottom, backgroundTop;
	double backgroundBottomR, backgroundBottomG, backgroundBottomB;
	boolean backgroundRising;
	double scaleW, scaleH;
	CopyOnWriteArrayList<Graphic> graphics;
	BufferStrategy buffer;
	int motdX, motdWidth;
	String[] motdArray;
	String motd;
	Font motdFont;
	FontMetrics fontMetrics;
	boolean updateAvailable, updating;
	GameUpdater updater;
	GameLoop gameLoop;
	//int titleX, titleY, titleWidth, titleHeight, titlePhase;
	int titleMaxY;
	
	public GuiMainMenu(GuiOutOfGame parent)
	{
		cards = parent;
		//titlePhase = 0;
		setVisible(true);
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		buttons = new GuiButtonManager(this);
		addComponents();
		
		motdArray = new String[1];
		motdArray[0] = "";
		motd = "";
		updateAvailable = false;
		new Thread(new Runnable(){
			@Override
			public void run()
			{
				motdArray = getMotdArray();
				updateAvailable = isUpdateAvailable();
				resetMotd();
			}
		}).start();
		motdFont = new Font("ARIAL", Font.BOLD, 18);
		motdX = -20;
		motdWidth = 0;
		setBackground(Color.BLUE);
		graphics = new CopyOnWriteArrayList<Graphic>();
		try{
		heart = ImageIO.read(getClass().getResourceAsStream("/Heart.png"));
		boot = ImageIO.read(getClass().getResourceAsStream("/Speed.png"));
		cupcake = ImageIO.read(getClass().getResourceAsStream("/Coopcake.png"));
		potion = ImageIO.read(getClass().getResourceAsStream("/Strength.png"));
		apple = ImageIO.read(getClass().getResourceAsStream("/Apple.png"));
		sun = ImageIO.read(getClass().getResourceAsStream("/Sun.png"));
		s = ImageIO.read(getClass().getResourceAsStream("/S.png"));
		u = ImageIO.read(getClass().getResourceAsStream("/U.png"));
		p = ImageIO.read(getClass().getResourceAsStream("/P.png"));
		e= ImageIO.read(getClass().getResourceAsStream("/E.png"));
		r = ImageIO.read(getClass().getResourceAsStream("/R.png"));
		m = ImageIO.read(getClass().getResourceAsStream("/M.png"));
		a = ImageIO.read(getClass().getResourceAsStream("/A.png"));
		g = ImageIO.read(getClass().getResourceAsStream("/G.png"));
		i = ImageIO.read(getClass().getResourceAsStream("/I.png"));
		c = ImageIO.read(getClass().getResourceAsStream("/C.png"));
		l = ImageIO.read(getClass().getResourceAsStream("/L.png"));
		o = ImageIO.read(getClass().getResourceAsStream("/O.png"));
		v = ImageIO.read(getClass().getResourceAsStream("/V.png"));
		t = ImageIO.read(getClass().getResourceAsStream("/T.png"));
		y = ImageIO.read(getClass().getResourceAsStream("/Y.png"));

		}catch(IOException e){
			e.printStackTrace();
		}
		backgroundRising = true;
		backgroundTop = Color.RED;
		backgroundBottomR = 100;
		backgroundBottomG = 75;
		backgroundBottomB = 255;
		backgroundBottom = new Color(100, 75, 255);
		backgroundPaint = new GradientPaint(0, 0, backgroundTop, 1920, 1080, backgroundBottom);
		gameLoop = new GameLoop(this);
		gameLoop.startGame(60);
		addTitle();
	}
	
	public void addTitle()
	{
		graphics.add(new Graphic(this, false, Graphic.S));
		graphics.add(new Graphic(this, false, Graphic.U));
		graphics.add(new Graphic(this, false, Graphic.P));
		graphics.add(new Graphic(this, false, Graphic.E));
		graphics.add(new Graphic(this, false, Graphic.R));
		graphics.add(new Graphic(this, false, Graphic.M));
		graphics.add(new Graphic(this, false, Graphic.A));
		graphics.add(new Graphic(this, false, Graphic.G));
		graphics.add(new Graphic(this, false, Graphic.I));
		graphics.add(new Graphic(this, false, Graphic.C));
		graphics.add(new Graphic(this, false, Graphic.A2));
		graphics.add(new Graphic(this, false, Graphic.L));
		graphics.add(new Graphic(this, false, Graphic.L2));
		graphics.add(new Graphic(this, false, Graphic.O));
		graphics.add(new Graphic(this, false, Graphic.V));
		graphics.add(new Graphic(this, false, Graphic.E2));
		graphics.add(new Graphic(this, false, Graphic.P2));
		graphics.add(new Graphic(this, false, Graphic.A3));
		graphics.add(new Graphic(this, false, Graphic.R2));
		graphics.add(new Graphic(this, false, Graphic.T));
		graphics.add(new Graphic(this, false, Graphic.Y));
		//graphics.add(new Graphic(this, false, Graphic.EX));
	}

	public void addComponents()
	{	
		buttons.add(new GuiButton(0, 0, 0, 0, "Play(Local)", new Runnable(){

			@Override
			public void run()
			{
				cards.enterGameSelect();
				Game.soundManager.playSound(SoundManager.MENU_SPARKLE);
			}
		}));
		buttons.add(new GuiButton(0, 0, 0, 0, "Multiplayer", new Runnable(){

			@Override
			public void run()
			{
				cards.enterMultiplayer();
				Game.soundManager.playSound(SoundManager.MENU_SPARKLE);
			}
		}));		
		buttons.add(new GuiButton(0, 0, 0, 0, "Start Server", new Runnable(){

			@Override
			public void run()
			{
				cards.enterServerSetup();
				Game.soundManager.playSound(SoundManager.MENU_SPARKLE);
			}
		}));		
		buttons.add(new GuiButton(0, 0, 0, 0, "Level Editor", new Runnable(){

			@Override
			public void run()
			{
				cards.enterLevelEditor();
				Game.soundManager.playSound(SoundManager.MENU_SPARKLE);
			}
		}));		
		buttons.add(new GuiButton(0, 0, 0, 0, "Options", new Runnable(){

			@Override
			public void run()
			{
				cards.enterOptions();
				Game.soundManager.playSound(SoundManager.MENU_SPARKLE);
			}
		}));
		buttons.add(new GuiButton(0, 0, 0, 0, "Credits", new Runnable(){

			@Override
			public void run()
			{
				cards.enterCredits();
				Game.soundManager.playSound(SoundManager.MENU_SPARKLE);
			}
		}));
		/*
		bStart = new JButton("Play (Local)");
		bStart.addMouseListener(this);
		bStart.setIcon(bIconRegular);
		bStart.setRolloverIcon(bIconHover);
		bStart.setPressedIcon(bIconClicked);

		bMultiplayer = new JButton("Play (Multiplayer)");
		bMultiplayer.addMouseListener(this);
		bServer = new JButton("Start Server");
		bServer.addMouseListener(this);
		bLevelEditor = new JButton("Level Editor");
		bLevelEditor.addMouseListener(this);
		bOptions = new JButton("Options");	
		bOptions.addMouseListener(this);
		*/
		updateComponentLocations();
		
		

		/*
		add(bStart);
		add(bMultiplayer);
		add(bServer);
		add(bLevelEditor);
		add(bOptions);
		*/

	}
	
	public void updateComponentLocations()
	{
		final double Y_SPACING = 5.0;
		final double WIDTH_MODIFIER = 6.0;
		final double HEIGHT_MODIFIER = 3.0;

		buttons.get(0).setSize((int)(100*WIDTH_MODIFIER), (int)(40*HEIGHT_MODIFIER));
		buttons.get(0).setLocation((int)((1920/2.0)-(50*WIDTH_MODIFIER)), (int)(1080/3.0));
		buttons.get(1).setSize((int)(45*WIDTH_MODIFIER), (int)(40*HEIGHT_MODIFIER));
		buttons.get(1).setLocation((int)((1920/2.0)-(50*WIDTH_MODIFIER)), (int)((1080/3.0)+30*Y_SPACING));
		buttons.get(2).setSize((int)(45*WIDTH_MODIFIER), (int)(40*HEIGHT_MODIFIER));
		buttons.get(2).setLocation((int)((1920/2.0)+(5*WIDTH_MODIFIER)), (int)((1080/3.0)+30*Y_SPACING));
		buttons.get(3).setSize((int)(100*WIDTH_MODIFIER), (int)(40*HEIGHT_MODIFIER));
		buttons.get(3).setLocation((int)((1920/2.0)-(50*WIDTH_MODIFIER)), (int)((1080/3.0)+60*Y_SPACING));
		buttons.get(4).setSize((int)(100*WIDTH_MODIFIER), (int)(40*HEIGHT_MODIFIER));
		buttons.get(4).setLocation((int)((1920/2.0)-(50*WIDTH_MODIFIER)), (int)((1080/3.0)+90*Y_SPACING));
		buttons.get(5).setSize((int)(100*WIDTH_MODIFIER), (int)(40*HEIGHT_MODIFIER));
		buttons.get(5).setLocation((int)((1920/2.0)-(50*WIDTH_MODIFIER)), (int)((1080/3.0)+120*Y_SPACING));		
		//titleX = (1920/2)-(titleWidth/2);
		//titleY = (1080/4)-(titleHeight/2);
		titleMaxY = (1080/5) + 64;
	}
	

	@Override
	public void componentHidden(ComponentEvent e)
	{
		stop();
	}


	@Override
	public void componentMoved(ComponentEvent e)
	{
	}


	@Override
	public void componentResized(ComponentEvent e)
	{
		//setSize(1920, 1080);
		scaleW = getWidth()/1920.0;
		buttons.setClickScaleW(scaleW);
		scaleH = getHeight()/1080.0;
		buttons.setClickScaleH(scaleH);
		updateComponentLocations();
		for(Graphic g : graphics)
			g.componentResized(e);
	}


	@Override
	public void componentShown(ComponentEvent e)
	{
		createBufferStrategy(2);
		buffer = getBufferStrategy();
		gameLoop.startGame(60);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		if(e.getY() > 20*scaleH && e.getY()<40*scaleH)
		{
			if(updateAvailable)
				updateGame();
			else if(updating)
			{
				try
				{
					Desktop.getDesktop().browse(new URL("http://smlp.ml/changelog.php").toURI());
				}
				catch (IOException | URISyntaxException e1)
				{
					e1.printStackTrace();
				}
			}
		}
		else if(e.getY() < 20*scaleH)
		{
			resetMotd();
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		buttons.mouseEntered(e);
	}


	@Override
	public void mouseExited(MouseEvent e)
	{
		buttons.mouseExited(e);
	}


	@Override
	public void mousePressed(MouseEvent e)
	{
		buttons.mousePressed(e);
	}


	@Override
	public void mouseReleased(MouseEvent e)
	{
		buttons.mouseReleased(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e)
	{
		buttons.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		buttons.mouseMoved(e);
	}
	
	public String[] getMotdArray()
	{
		BufferedReader bufferedreader;
		try
		{
			bufferedreader = new BufferedReader(new InputStreamReader((new URL("https://www.dropbox.com/s/l5e17w3rqufhpdd/motd.txt?raw=1")).openStream()));
			ArrayList<String> a = new ArrayList<String>();
			String[] sa = new String[0];
			String s;
	        while((s = bufferedreader.readLine())!=null)
	        	a.add(s);
	        sa = a.toArray(sa);
	        return sa;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return new String[]{"Welcome to Super Magical Love Party!"};
	}
	
	public boolean isUpdateAvailable()
	{
		BufferedReader bufferedreader;
		try
		{
			bufferedreader = new BufferedReader(new InputStreamReader((new URL("https://www.dropbox.com/s/e13g6gn3kq8imiz/version.txt?raw=1")).openStream()));
	        String s = bufferedreader.readLine();
	        return !s.equals(Game.VERSION);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public void stop()
	{
		gameLoop.stop();
	}

	public void trySpawn()
	{
		if((int)(Math.random()*10)==0 && 1920 > 0 && 1080 > 0)
			graphics.add(new Graphic(this, true, -1));
	}

	public void updateGame()
	{
		cards.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		updater = new GameUpdater();
		updater.start();
		updating = true;
		updateAvailable = false;
	}
	
	@Override
	public void update()
	{
		backgroundBottomR += (backgroundRising? .3 : -.3);
		backgroundBottomB += (backgroundRising? -.45 : .45);
		backgroundBottom = new Color((int)(backgroundBottomR), (int)backgroundBottomG, (int)(backgroundBottomB));
		backgroundTop = new Color((int) backgroundBottomB, (int)backgroundBottomG, (int)backgroundBottomR);
		if(backgroundBottomR >= 255 && backgroundRising)
			backgroundRising = false;
		else if(backgroundBottomR <= 100 && !backgroundRising)
			backgroundRising = true;
		backgroundPaint = new GradientPaint(0, 0, backgroundTop, 1920, (float) (1080*(backgroundBottomR/255.0f)), backgroundBottom);
		for(int i = 0; i < graphics.size(); i++)
			graphics.get(i).update();
		trySpawn();
		if(motdX < -motdWidth)
		{
			resetMotd();
		}
		motdX-=2;
		
		/*
		if(titlePhase < 100 && this.isDisplayable())
		{
			titlePhase++;
			titleHeight += 2;
			titleWidth += 10;
			titleX = (1920/2)-(titleWidth/2);
			titleY = (1080/4)-(titleHeight/2);
		}
		*/
	}
	
	private void resetMotd()
	{
		motd = motdArray[(int) (Math.random()*motdArray.length)];
		if(fontMetrics != null)
		{
			motdWidth = fontMetrics.stringWidth(motd);
			motdX = 1920;
		}
		else
		{
			motdX = 1920;
			motdWidth = 0;
		}
	}
	@Override
	public void fineUpdate(double interpolation)
	{
	}

	@Override
	public void updateFrame(double interpolation)
	{
		if(!isDisplayable() || !isShowing())
			return;
		if(buffer == null || buffer.contentsLost())
		{
			createBufferStrategy(2);
			buffer = getBufferStrategy();
		}
		Graphics2D g2d = (Graphics2D) buffer.getDrawGraphics();
		g2d.scale(scaleW, scaleH);
		g2d.setPaint(backgroundPaint);
		//g2d.setColor(getBackground());
		g2d.fillRect(0, 0, 1920, 1080);
		g2d.setFont(motdFont);
		for(Graphic gr : graphics)
		{
			switch(gr.type)
			{
				case Graphic.HEART:
					g2d.drawImage(heart, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.CUPCAKE:
					g2d.drawImage(cupcake, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.POTION:
					g2d.drawImage(potion, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.APPLE:
					g2d.drawImage(apple, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.SUN:
					g2d.drawImage(sun, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.S:
					g2d.drawImage(s, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.U:
					g2d.drawImage(u, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.P:
					g2d.drawImage(p, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.E:
					g2d.drawImage(e, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.R:
					g2d.drawImage(r, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.M:
					g2d.drawImage(m, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.A2:
					g2d.drawImage(a, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.G:
					g2d.drawImage(g, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.I:
					g2d.drawImage(i, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.C:
					g2d.drawImage(c, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.A:
					g2d.drawImage(a, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.L:
					g2d.drawImage(l, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.L2:
					g2d.drawImage(l, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.O:
					g2d.drawImage(o, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.V:
					g2d.drawImage(v, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.E2:
					g2d.drawImage(e, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.P2:
					g2d.drawImage(p, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.A3:
					g2d.drawImage(a, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.R2:
					g2d.drawImage(r, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.T:
					g2d.drawImage(t, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
				case Graphic.Y:
					g2d.drawImage(y, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
					break;
//				case Graphic.EX:
//					g2d.drawImage(ex, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
//					break;
				default:
					g2d.drawImage(sun, (int)(gr.x+(gr.vX*interpolation)), (int)(gr.y + gr.vY*interpolation), Graphic.width, Graphic.height, null);
			}
		}

		g2d.setColor(new Color(50, 50, 50, 100));
		g2d.fillRect(0, 0, 1920, 20);
		g2d.setColor(Color.WHITE);
		fontMetrics = g2d.getFontMetrics();
		g2d.drawString(motd, motdX, 16);
		if(updateAvailable)
		{
			g2d.setColor(new Color(0, 250, 50, 150));
			g2d.fillRect(0, 20, 1920, 20);
			g2d.setColor(new Color(0, 50, 250, 150));
			g2d.drawString("UPDATE AVAILABLE! Cick here to update.", 0, 36);
		}else if(updating)
		{
			g2d.setColor(new Color(200, 0, 200, 150));
			g2d.fillRect(0, 20, 1920, 20);
			g2d.setColor(Color.GREEN);
			g2d.fillRect(0, 20, (int)(1920*updater.getRatioDone()), 20);
			g2d.setColor(Color.WHITE);
			g2d.drawString(updater.getStatus(), 0, 36);
			g2d.drawString("Click here to see the change log.", 1920 - fontMetrics.stringWidth("Click here to see the change log."), 36);
		}
		buttons.renderAll(g2d, 1);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Version " + Game.VERSION, 0, 1080);
		buffer.show();
	}

	@Override
	public boolean isUpdating()
	{
		return true;
	}

	@Override
	public void setTps(int tps)
	{
		
	}

	@Override
	public void setFps(int fps)
	{
		
	}

	@Override
	public boolean isDisplaying()
	{
		return true;
	}
}

class Graphic
{
	public static final int 
	CUPCAKE = 0,
	BOOT = 1,
	HEART = 2,
	POTION = 3,
	APPLE = 4,
	SUN = 5,
	S = 10,
	U = 11,
	P = 12,
	E = 13,
	R = 14,
	M = 16,
	A = 17,
	G = 18,
	I = 19,
	C = 20,
	A2 = 21,
	L = 22,
	L2 = 24,
	O = 25,
	V = 26,
	E2 = 27,
	P2 = 29,
	A3 = 30,
	R2 = 31,
	T = 32,
	Y = 33;
	//EX = 34;
	
	public static final int width = 64, height = 64;
	int type;
	double vX, vY, aX, aY, x, y;
	GuiMainMenu container;
	Random r;
	
	public Graphic(GuiMainMenu container, boolean launch, int type)
	{
		this.container = container;
		aY = 50.0/container.gameLoop.getActualSpeed();
		r = new Random();
		if(type == -1)
			type = r.nextInt(6);
		else if(type >= Graphic.S)
			setLocationForLetter(type);
		this.type = type;
		if(launch)
			launch();
	}
	
	public void componentResized(ComponentEvent e3)
	{
		if(type>=Graphic.S)
			setLocationForLetter(type);
	}

	public void setLocationForLetter(int type)
	{
		double multiplier = (type-10)/(11.9);
		x = (1920/2) * multiplier;
		y = -(23*20) +(type*20);
		vY = 0;
	}
	
	public void launch()
	{
		
		try{
			switch(r.nextInt(4))
			{
				case 0://FROM TOP
					y = -height;
					x = r.nextInt(1920);
					//vY = r.nextDouble()*30;
					vX = r.nextDouble()*40-30;
					break;
				case 1://FROM BOTTOM
					y = 1080;
					x = r.nextInt(1920);
					vY = r.nextDouble()*-40;
					vX = r.nextDouble()*40-30;
					break;
				case 2://FROM LEFT
					y = r.nextInt(1080);
					x = -width;
					vY = r.nextDouble()*-40;
					vX = r.nextDouble()*20;
					break;
				case 3://FROM RIGHT
					y = r.nextInt(1080);
					x = 1920;
					vY = r.nextDouble()*-40;
					vX = r.nextDouble()*-20;
					break;
			}
		}catch(Exception e)//if the window was too small, Jpanel size = 0, Random bounds error
		{
			e.printStackTrace();
		}
	}
	
	public void update()
	{
		if(type >= Graphic.S)
		{
			if(y + vY > container.titleMaxY)
			{
				y = container.titleMaxY;
				vY = -20;
			}
		}
		y += vY;
		vY += aY;
		x += vX;
		vX += aX;
		if((x > 1920 || x + width < 0 /*|| y + width < 0 */|| y > 1080) && type < Graphic.S)
		{
			destroy();
		}
	}
	
	public void destroy()
	{
		container.graphics.remove(this);
		try
		{
			this.finalize();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
	}

	public double getvX()
	{
		return vX;
	}

	public void setvX(double vX)
	{
		this.vX = vX;
	}

	public double getvY()
	{
		return vY;
	}

	public void setvY(double vY)
	{
		this.vY = vY;
	}

	public double getaX()
	{
		return aX;
	}

	public void setaX(double aX)
	{
		this.aX = aX;
	}

	public double getaY()
	{
		return aY;
	}

	public void setaY(double aY)
	{
		this.aY = aY;
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}
	
}
