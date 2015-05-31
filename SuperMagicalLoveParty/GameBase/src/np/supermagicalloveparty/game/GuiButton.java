package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


public class GuiButton extends Rectangle
{

	String text;
	Runnable action;
	static enum State{
		DEFAULT, HIGHLIGHTED, CLICKED
	}
	State state;
	private static BufferedImage iconRegular, iconHover, iconClick;
	static
	{
		 try
		{
			iconRegular = ImageIO.read(GuiButton.class.getResourceAsStream("/Cloud.png"));
			iconHover = ImageIO.read(GuiButton.class.getResourceAsStream("/DarkCloud.png"));
			iconClick = ImageIO.read(GuiButton.class.getResourceAsStream("/BurstCloud.png"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public GuiButton(double x, double y, double width, double height, String text, Runnable runnable)
	{
		super(x, y, width, height);
		this.text = text;
		this.action = runnable;
		state = State.DEFAULT;
	}
	
	public GuiButton(double x, double y, double width, double height, String text)
	{
		super(x, y, width, height);
		action = new Runnable(){
			@Override
			public void run()
			{	
				System.out.println("button does nothing");
			}
		};
		this.text = text;
			
	}

	/**
	 * 
	 * @param g
	 * @param scale -1 for constant size/absolute values
	 */
	public void render(Graphics2D g, double scale)
	{
		render(g, scale, scale);
		/*
		switch(state)
		{
			case DEFAULT:
				g.setColor(Color.WHITE);
				break;
			case HIGHLIGHTED:
				g.setColor(Color.GRAY);
				break;
			case CLICKED:
				g.setColor(Color.RED);
				break;
		}
		g.fillRect((int)(x*scale), (int)(y*scale),(int)( width*scale), (int)(height*scale));
		g.setColor(Color.BLACK);
		*/

	}
	

	public void render(Graphics2D g, double scaleW, double scaleH)
	{
		if(scaleW == 1)
		{
			g.setFont(new Font("SERIF", Font.ITALIC, 30));
		}
		else
		{
			g.setFont(new Font("SERIF", Font.ITALIC, (int) (scaleW*3)));
		}
		switch(state)
		{
			case DEFAULT:
				g.drawImage(iconRegular, (int)(x*scaleW), (int)(y*scaleH), (int)(width*scaleW), (int)(height*scaleH), null);
				break;
			case HIGHLIGHTED:
				g.drawImage(iconHover, (int)(x*scaleW), (int)(y*scaleH), (int)(width*scaleW), (int)(height*scaleH), null);
				break;
			case CLICKED:
				g.drawImage(iconClick, (int)(x*scaleW), (int)(y*scaleH), (int)(width*scaleW), (int)(height*scaleH), null);
				break;
		}
		g.setColor(Color.BLACK);
		g.drawString(text, (int)((x*scaleW) - (g.getFontMetrics().stringWidth(text)/2.0) + ((getWidth()*scaleW)/2.0)), (int)((y*scaleH)+ ((getHeight()*scaleH)/2.0)));
	}
	
	public void onMouseDown()
	{
		state = State.CLICKED;
	}
	
	public void onMouseUp()
	{
		state = State.DEFAULT;
		action.run();
	}
	
	public void onClick()
	{
		//action.run();
	}
	
	public void onHover()
	{
		state = State.HIGHLIGHTED;
	}
	
	public void onRemove()
	{
		state = State.DEFAULT;
	}
	
	public void setText(String newText)
	{
		text = newText;
	}
	
	public void setLocation(int x, int y)
	{
		setX(x);
		setY(y);
	}
	
	public void setSize(int w, int h)
	{
		this.width = w;
		this.height = h;
	}
	
	public String getText()
	{
		return text;
	}
}
