package np.supermagicalloveparty.game;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GuiMultiplayerSelect extends JPanel implements MouseListener, KeyListener
{
	private static final long serialVersionUID = -5353940122387799266L;
	PlayerSelection playerSelect;
	GuiOutOfGame cards;
	JButton bStart, bBack, bFriends;
	JTextField tfIP, tfPort;
	JLabel lIP, lPort;
	int port;
	
	public GuiMultiplayerSelect(GuiOutOfGame parent)
	{
		cards = parent;
		port = 6969;
		setVisible(true);
		setBackground(Color.BLUE);
		addComponents();
		revalidate();
	}
	
	public void addComponents()
	{
		playerSelect = new PlayerSelection("Player");
		lIP = new JLabel("IP: ");
		lIP.setForeground(Color.WHITE);
		try
		{
			tfIP = new JTextField(Inet4Address.getLocalHost().getHostAddress());
		}
		catch (UnknownHostException e)
		{
			tfIP = new JTextField("127.0.0.1");
			e.printStackTrace();
		}
		tfIP.setPreferredSize(new Dimension(120,20));
		lPort = new JLabel(":");
		lPort.setForeground(Color.WHITE);
		tfPort = new JTextField("6969");
		tfPort.addKeyListener(this);
		tfPort.setPreferredSize(new Dimension(60,20));
		bFriends = new JButton("I don't have any friends. HELP!");
		bFriends.addMouseListener(this);
		bStart = new JButton("Start");
		bStart.addMouseListener(this);
		bBack = new JButton("Back");
		bBack.addMouseListener(this);

		add(bBack);
		add(bFriends);

		add(lIP);
		add(tfIP);
		add(lPort);
		add(tfPort);
		add(playerSelect);
		add(bStart);
	}
	

	@Override
	public void mouseClicked(MouseEvent arg0)
	{
		if(arg0.getComponent().equals(bStart))
		{
			cards.startMultiplayerGame(playerSelect.getCharacter(), playerSelect.getPlayerName(), tfIP.getText(), port);
		}else if(arg0.getComponent().equals(bBack))
		{
			cards.flipBack();
		}
		else if(arg0.getComponent().equals(bFriends))
		{
			try {
				Desktop.getDesktop().browse(new URI("http://smlp.pesce.host/servers.html"));
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
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
			System.out.println(port);
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
}
