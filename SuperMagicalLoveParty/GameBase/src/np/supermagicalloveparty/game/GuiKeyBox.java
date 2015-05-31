package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class GuiKeyBox extends Rectangle
{
	String text;
	int id, action;
	KeyMap keyMap;
	Color textColor;
	
	static enum State
	{
		DEFAULT, HIGHLIGHTED, CLICKED
	}

	public GuiKeyBox(double x, double y, double width, double height, int id, int action, KeyMap keyMap)
	{
		super(x, y, width, height);
		this.id = id;
		this.text = KeyEvent.getKeyText(id);
		this.action = action;
		this.keyMap = keyMap;
		textColor = Color.WHITE;
	}

	public void render(Graphics2D g, double scale)
	{
		//super.draw(g, scale);
		g.setColor(textColor);
		g.drawString(text, (int) (x * scale), (int) (y * scale + g.getFontMetrics().getAscent()));
	}

	public void onClick()
	{
		setText("?");
	}

	public void setKey(int newId)
	{
		keyMap.changeKey(action, id, newId);
		id = newId;
		setText(KeyEvent.getKeyText(id));
	}
	
	public void deactivate()
	{
		setTextColor(Color.WHITE);
		setText(KeyEvent.getKeyText(id));
	}
	
	public void setText(String newText)
	{
		text = newText;
	}

	public String getText()
	{
		return text;
	}
	
	public void setTextColor(Color color)
	{
		textColor = color;
	}

	public void reset()
	{
		setTextColor(Color.WHITE);
		setText(KeyEvent.getKeyText(id));
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getAction()
	{
		return action;
	}

	public void setAction(int action)
	{
		this.action = action;
	}

	public Color getTextColor()
	{
		return textColor;
	}
}
