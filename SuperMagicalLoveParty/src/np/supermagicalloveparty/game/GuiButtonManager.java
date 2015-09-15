package np.supermagicalloveparty.game;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiButtonManager
{

	Component component;
	CopyOnWriteArrayList<GuiButton> buttons;
	Point offset;
	double scaleW, scaleH;
	public GuiButtonManager(Component c)
	{
		component = c;
		scaleW = scaleH = 1;
		offset = new Point(0, 0);
		buttons = new CopyOnWriteArrayList<GuiButton>();
	}

	public void renderAll(Graphics2D g, double scaleW, double scaleH, Point offset)
	{
		if(scaleW != 1 && scaleH != 1)
		{
			this.scaleW = scaleW;
			this.scaleH = scaleH;
		}
		this.offset = offset;
		for(GuiButton b : buttons)
			b.render(g, scaleW, scaleH);
	}
	
	public void renderAll(Graphics2D g, double scale, Point offset)
	{
		if(scale != 1)
			this.scaleW = this.scaleH = scale;
		this.offset = offset;
		for(GuiButton b : buttons)
			b.render(g, scale);
	}
	
	public void setClickScaleW(double s)
	{
		scaleW = s;
	}
	
	public void setClickScaleH(double s)
	{
		scaleH = s;
	}
	
	public void renderAll(Graphics2D g, double scale)
	{
		renderAll(g, scale, new Point(0, 0));
	}

	public void add(GuiButton guiButton)
	{
		buttons.add(guiButton);		
	}
	
	public GuiButton get(int index)
	{
		return buttons.get(index);
	}
	
	public void mouseClicked(MouseEvent e)
	{
		for(GuiButton b : buttons)
		{
			if(b.contains((e.getX()-offset.getX())/scaleW , (e.getY()-offset.getY())/scaleH))
			{
				b.onClick();
			}
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
		for(GuiButton b : buttons)
		{
			if(b.contains((e.getX()-offset.getX())/scaleW, (e.getY()-offset.getY())/scaleH))
			{
				b.onMouseDown();
			}
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		for(GuiButton b : buttons)
		{
			if(b.contains((e.getX()-offset.getX())/scaleW, (e.getY()-offset.getY())/scaleH))
			{
				b.onMouseUp();
			}else if(b.state.equals(GuiButton.State.CLICKED))
			{
				b.onRemove();
			}
		}
	}
	
	public void mouseDragged(MouseEvent e)
	{		
	}


	public void mouseMoved(MouseEvent e)
	{
		for(GuiButton b : buttons)
		{
			if(b.contains((e.getX()-offset.getX())/scaleW,  (e.getY()-offset.getY())/scaleH))
			{
				b.onHover();
			}else if(b.state.equals(GuiButton.State.HIGHLIGHTED))
			{
				b.onRemove();
			}
		}		
	}
	

}
