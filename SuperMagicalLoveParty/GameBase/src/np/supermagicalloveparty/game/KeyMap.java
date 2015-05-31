package np.supermagicalloveparty.game;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class KeyMap
{
	File file;

	static HashMap[] DEFAULT= {new HashMap(), new HashMap(), new HashMap(), new HashMap()};
	static
	{
		DEFAULT[0].put(KeyEvent.VK_A, InputHandler.LEFT);
		DEFAULT[0].put(KeyEvent.VK_D, InputHandler.RIGHT);
		DEFAULT[0].put(KeyEvent.VK_W, InputHandler.JUMP);
		DEFAULT[0].put(KeyEvent.VK_S, InputHandler.SQUAT);
		DEFAULT[0].put(KeyEvent.VK_E, InputHandler.EVADE);
		DEFAULT[0].put(KeyEvent.VK_SPACE, InputHandler.ATTACK);
		
		DEFAULT[2].put(KeyEvent.VK_J, InputHandler.LEFT);
		DEFAULT[2].put(KeyEvent.VK_L, InputHandler.RIGHT);
		DEFAULT[2].put(KeyEvent.VK_I, InputHandler.JUMP);
		DEFAULT[2].put(KeyEvent.VK_K, InputHandler.SQUAT);
		DEFAULT[2].put(KeyEvent.VK_O, InputHandler.EVADE);
		DEFAULT[2].put(KeyEvent.VK_SLASH, InputHandler.ATTACK);
		
		DEFAULT[3].put(KeyEvent.VK_LEFT, InputHandler.LEFT);
		DEFAULT[3].put(KeyEvent.VK_RIGHT, InputHandler.RIGHT);
		DEFAULT[3].put(KeyEvent.VK_UP, InputHandler.JUMP);
		DEFAULT[3].put(KeyEvent.VK_DOWN, InputHandler.SQUAT);
		DEFAULT[3].put(KeyEvent.VK_DELETE, InputHandler.EVADE);
		DEFAULT[3].put(KeyEvent.VK_PAGE_DOWN, InputHandler.ATTACK);
		
		DEFAULT[1].put(KeyEvent.VK_NUMPAD1, InputHandler.LEFT);
		DEFAULT[1].put(KeyEvent.VK_NUMPAD3, InputHandler.RIGHT);
		DEFAULT[1].put(KeyEvent.VK_NUMPAD5, InputHandler.JUMP);
		DEFAULT[1].put(KeyEvent.VK_NUMPAD2, InputHandler.SQUAT);
		DEFAULT[1].put(KeyEvent.VK_NUMPAD6, InputHandler.EVADE);
		DEFAULT[1].put(KeyEvent.VK_DECIMAL, InputHandler.ATTACK);
	}
	private HashMap<Integer, Integer> keys;

	public KeyMap(HashMap<Integer, Integer> keys, File file)
	{
		this.keys = keys;
		this.file = file;
	}
	
	public KeyMap(HashMap<Integer, Integer> keys)
	{
		this.keys = keys;
	}

	public static KeyMap getDefaultKeyMap(int number)
	{
		return new KeyMap(DEFAULT[number-1]);
	}

	public int getAction(int keyCode)
	{
		Integer action = keys.get(keyCode);
		if (keys.get(keyCode) == null)
			action = -1;
		return action;
	}

	public int getKeyCode(int action)
	{
		for (Map.Entry<Integer, Integer> e : keys.entrySet())
		{
			if (e.getValue().equals(action))
				return e.getKey();
		}
		return -1;
	}

	public void changeKey(int action, int oldKey, int newKey)
	{
		keys.remove(oldKey);
		keys.put(newKey, action);
		if(file!=null && file.exists())
			KeyMap.saveKeyMapToFile(KeyMap.this, file);
	}

	public boolean getIsAssigned(int keyCode)
	{
		return keys.containsKey(keyCode);
	}

	public int size()
	{
		return keys.size();
	}

	public HashMap<Integer, Integer> getKeyMap()
	{
		return keys;
	}

	public static KeyMap getKeyMapFromFile(File file)
	{
		HashMap<Integer, Integer> keyHash = new HashMap<Integer, Integer>();
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			reader.skip(15);
			while ((line = reader.readLine()) != null)
			{
				if (line.length() > 2)
				{
					String[] put = line.split(":");
					keyHash.put(Integer.parseInt(put[0]),Integer.parseInt(put[1]));// TODO throws exception.
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return new KeyMap(keyHash, file);
	}

	public static void saveKeyMapToFile(KeyMap km, File file)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			Iterator<Entry<Integer,Integer>> it = km.getKeyMap().entrySet().iterator();
			writer.write("KEY CODE|ACTION");
			while (it.hasNext())
			{
				writer.newLine();
				Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) it.next();
				writer.write(entry.getKey() + ":" + entry.getValue());
				//it.remove();
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static KeyMap getPlayerKeysFromFile(int number)
	{
		try
		{
			number++;
			new File(Game.BASE_DIRECTORY + "Controls").mkdirs();
			File controlsFile = new File(Game.BASE_DIRECTORY + "Controls/player"+number + ".txt");

			if (!(controlsFile).exists())
			{
				controlsFile.createNewFile();
				KeyMap.saveKeyMapToFile(KeyMap.getDefaultKeyMap(number), controlsFile);
			}
			return (KeyMap.getKeyMapFromFile(controlsFile));

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return KeyMap.getDefaultKeyMap(number);
		}
	}
	
	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}

}
