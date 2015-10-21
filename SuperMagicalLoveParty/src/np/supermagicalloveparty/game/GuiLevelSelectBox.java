package np.supermagicalloveparty.game;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JComboBox;


public class GuiLevelSelectBox extends JComboBox<String> implements ItemListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	File levelDirectory;
	String[] levelStrings;
	Settings[] levelSettings;
	int selectedIndex;
	int[] levelWidths;
	int[] levelHeights;
	String[] folders;
	GuiWithLevel parent;
	
	public GuiLevelSelectBox(GuiWithLevel parent)
	{
		this.parent = parent;
		levelDirectory = new File(Game.BASE_DIRECTORY +"Levels/");
		levelDirectory.mkdirs();
		folders = levelDirectory.list(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name)
			{
				if(name.contains("."))
					return false;
				return true;
			}
			
		});	
		if(folders.length==0)
		{
			FileHelper.createDefaultLevel();
			folders = levelDirectory.list(new FilenameFilter(){
				@Override
				public boolean accept(File dir, String name)
				{
					if(name.contains("."))
						return false;
					return true;
				}
				
			});
		}
		addItemListener(this);

		refresh(0);
	}
	
	public void reset(int index)
	{
		selectedIndex = index;
		setSelectedIndex(index);
	}
	
	public void refresh(int index)
	{
		removeAllItems();
		folders = levelDirectory.list();
		Arrays.sort(folders);
		convertNames(folders);
		for(String n : levelStrings)
			addItem(n);
		reset(index);
		parent.setLevel(FileHelper.loadLevelFromDirectory(levelStrings[selectedIndex], levelWidths[selectedIndex], levelHeights[selectedIndex]));
		revalidate();
		repaint();
	}
	
	public void refresh(String name)
	{
		refresh(0);
		setSelectedByName(name);
	}
	
	public void setSelectedByName(String name)
	{
		for(int i = 0; i < getItemCount(); i++)
		{
			if(getItemAt(i).equals(name))
			{
				setSelectedIndex(i);
				selectedIndex = i;
				return;
			}
		}
		setSelectedIndex(0);
		selectedIndex = 0;
	}
	
	private void convertNames(String[] rawNames)
	{
		if(rawNames != null)
		{
			levelStrings = new String[rawNames.length];
			levelHeights = new int[rawNames.length];
			levelWidths = new int[rawNames.length];
			levelSettings = new Settings[rawNames.length];
			for(int i = 0; i < rawNames.length; i++)
			{
				String[] chunks = rawNames[i].split("~");
				levelStrings[i]=chunks[0];
				if(chunks.length>1)
				{
					String[] dimensions = chunks[1].split("x");
					if(dimensions.length>1)
					{
						levelWidths[i] = Integer.parseInt(dimensions[0]);
						levelHeights[i] = Integer.parseInt(dimensions[1]);
					}else
						levelStrings[i] = "ERROR~ Missing a dimension!";
				}else
					levelStrings[i] = "ERROR~ No dimensions!";
			}
		}else
		{
			levelStrings = new String[1];
			levelHeights = new int[1];
			levelWidths = new int[1];
			levelStrings[0] = "Default";
			levelHeights[0] = 90;
			levelWidths[0] = 160;
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0)
	{
		if(getSelectedItem()==null)
			return;
			if(((String) getSelectedItem()).startsWith("ERROR~ "))
				setSelectedIndex(selectedIndex);
			else
			{
				selectedIndex = getSelectedIndex();
				parent.setLevel(FileHelper.loadLevelFromDirectory(levelStrings[selectedIndex], levelWidths[selectedIndex], levelHeights[selectedIndex]));
			}
	}
}
