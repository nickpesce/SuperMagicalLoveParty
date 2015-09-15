package np.supermagicalloveparty.game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class GuiCredits extends JPanel implements MouseListener
{
	private GuiOutOfGame cards;
	private JButton bBack;
	private String[] credits = {"Credits",
			"",
			"Programmer: Nick Pesce",
			"Game Designer: Nick Pesce",
			"Art Designer: Lauren DiGiovanni",
			"",
			"Music:",
			"Streamline by Newton",
			"",
			"Art that Lauren wouldn't make/finish:",
			"Letter Blocks by GameArtForge(OpenGameArt.org)",
			"Platformer Art: Candy by Kenney.nl"};
	
	public GuiCredits(GuiOutOfGame parent)
	{
		cards = parent;
		setLayout(null);
		bBack = new JButton("Back");
		bBack.setSize(100, 30);
		bBack.setLocation(10, 10);
		bBack.addMouseListener(this);
		add(bBack);
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,(int)Math.min(getHeight()/30.0, getWidth()/30.0)));
		FontMetrics metrics = g.getFontMetrics();
		for(int i = 0; i < credits.length; i++)
			g.drawString(credits[i], (getWidth()/2) - metrics.stringWidth(credits[i])/2, ((metrics.getHeight() + 5) *i) + getHeight()/10);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) 
	{
		if(arg0.getSource().equals(bBack))
			cards.flipBack();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
