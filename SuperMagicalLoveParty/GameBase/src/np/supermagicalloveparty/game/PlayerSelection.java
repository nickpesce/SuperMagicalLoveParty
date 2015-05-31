package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.TextField;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public class PlayerSelection extends JPanel implements TextListener, ItemListener
{
	private static final long serialVersionUID = 1L;
	TextField tfName;
	String playerName;
	String character;
	JComboBox<String> characterChooser;
	
	public PlayerSelection(String defaultPlayerName)
	{
		setBackground(Color.GREEN);
		character = "Unicorn";
		playerName = defaultPlayerName;
		addComponents();
	}
	
	public void addComponents()
	{
		tfName = new TextField(playerName);
		tfName.addTextListener(this);
		characterChooser = new JComboBox<String>();
		characterChooser.addItem("Unicorn");
		characterChooser.addItem("Panda");
		characterChooser.addItem("Bird");
		characterChooser.addItem("Bunny");
		characterChooser.addItemListener(this);
		add(tfName);
		add(characterChooser);
	}

	public String getPlayerName()
	{
		return playerName;
	}
	
	public String getCharacter()
	{
		return character;
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		if(arg0.getSource().equals(characterChooser))
		{
			character = (String)(characterChooser.getSelectedItem());
		}
	}

	@Override
	public void textValueChanged(TextEvent arg0)
	{
		if(arg0.getSource().equals(tfName))
		{
			playerName = tfName.getText();
		}		
	}
}