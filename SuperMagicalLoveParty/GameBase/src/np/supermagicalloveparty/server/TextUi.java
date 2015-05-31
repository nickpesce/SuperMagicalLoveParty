package np.supermagicalloveparty.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class TextUi extends JPanel implements ActionListener
{

	JTextArea textArea;
	JScrollPane scrollPane;
	JTextField textField;
	ServerGame game;
	public TextUi(ServerGame game)
	{
		this.game = game;
		setLayout(new BorderLayout());
		textArea = new JTextArea();
		textArea.setText("Server Started...");
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textField = new JTextField();
		textField.addActionListener(this);
		scrollPane = new JScrollPane(textArea);
		add(scrollPane, BorderLayout.CENTER);
		add(textField, BorderLayout.SOUTH);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().equals(textField))
		{
			game.doCommand(textField.getText());
			textField.setText("");
		}
	}

	public void log(String s)
	{
		textArea.append("\n" + s);
	}
	
	
}
