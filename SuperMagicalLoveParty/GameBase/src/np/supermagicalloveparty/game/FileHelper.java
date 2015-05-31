package np.supermagicalloveparty.game;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class FileHelper {
	/**
	 * 
	 * @param name The name of the map not including any metadata
	 * @param width
	 * @param height
	 * @return
	 */
	public static char[] loadTerrain(String name, int width, int height)
	{
		new File(Game.BASE_DIRECTORY + "Levels/" + Level.makeFileString(name, ""+width, ""+height)).mkdirs();
		File file = new File(Game.BASE_DIRECTORY + "Levels/" + Level.makeFileString(name, ""+width, ""+height) + "/Terrain.txt");
		return loadTerrain(file, width, height);
	}
	
	/**
	 * Writes each string in the list to a seperate line in the txt file
	 * @param al The strings to write
	 * @param directory The name of the folder after the BASE_DIRECTORY. ex. for a file server/banned-ips.txt, directory would be server/
	 * @param name The name of the file. ex banned-ips.txt
	 */
	public static void saveArrayListToFile(ArrayList<String> al, String directory, String name)
	{
		deleteFile(Game.BASE_DIRECTORY + directory + name);
		File file = createAndOpenFile(Game.BASE_DIRECTORY + directory, name);
		BufferedWriter writer;
		try
		{
			writer = new BufferedWriter(new FileWriter(file));
			for(String s : al)
			{
				writer.write(s);
				writer.newLine();
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	/**
	 * reads each line from the text file and adds it as a string to the list
	 * @param al The list to add the strings to.
	 * @param directory The name of the folder after Base_Directory. ex. for a file server/banned-ips.txt, directory would be server/
	 * @param name The name of the file after the BASE_DIRECTORY + directory. ex. banned-ips.txt
	 */
	public static void readArrayListToFile(ArrayList<String> al, String directory, String name)
	{
		File file = createAndOpenFile(Game.BASE_DIRECTORY + directory, name);
		BufferedReader reader;
		String line;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null)
			{
				al.add(line);
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}
	
	public static char[] loadTerrain(File file, int width, int height)
	{
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
				//writeDefaultFile("Terrain.txt", file.getPath());

				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				for(int y = 0; y < height; y++)
				{
					for(int x = 0; x < width; x++)
					{
						writer.write('E');
					}
					writer.newLine();
				}
				writer.close();
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			return loadTerrain(new FileInputStream(file), width, height);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;	
	}
	
	public static char[] loadEntities(String name, int width, int height)
	{
		new File(Game.BASE_DIRECTORY + "Levels/" + Level.makeFileString(name, ""+width, ""+height)).mkdirs();
		File file = new File(Game.BASE_DIRECTORY + "Levels/" + Level.makeFileString(name, ""+width, ""+height)+ "/Entities.txt");
		return loadEntities(file, width, height);
	}
	
	public static char[] loadTerrain(InputStream input, int width, int height)
	{
		char[] terrain = new char[width*height];
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			int c;
			int index = 0;
			while (index < (width*height) && (c = reader.read()) != -1)
			{
				terrain[index] = (char)c;
				if(c == 13 || c == 10)//new line and carriage return
					continue;
				index++;
			}
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return terrain;
	}
	
	public static char[] loadEntities(InputStream input, int width, int height)
	{
		char[] entities = new char[width*height];
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			int c;
			int index = 0;
			while (index < (width*height) && (c = reader.read()) != -1)
			{
				entities[index] = (char)c;
				if(c == 13 || c == 10)//new line and carriage return
					continue;
				index++;
			}
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return entities;
	}
	
	public static char[] loadEntities(File file, int width, int height)
	{
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
				//writeDefaultFile("Entities.txt", file.getPath());
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				for(int y = 0; y < height; y++)
				{
					for(int x = 0; x < width; x++)
					{
						writer.write('0');
					}
					writer.newLine();
				}
				writer.close();
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			return loadEntities(new FileInputStream(file), width, height);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static void saveLevel(Level level)
	{
		File dir = new File(Game.BASE_DIRECTORY + "Levels/" +level.toFileString());
		deleteDirectory(dir);
		dir.mkdirs();
		BufferedWriter writer;
		File file = new File(Game.BASE_DIRECTORY + "Levels/" + level.toFileString()+"/Terrain.txt");
		//file.delete();
		try
		{
			writer = new BufferedWriter(new FileWriter(file, true));
		    for(int y = 0; y < level.getHeight(); y++)
		    {
		    	for(int x = 0; x < level.getWidth(); x++)
		    	{
		    		writer.write(level.getTileAtLocation(x, y));
		    	}
		    	writer.newLine();
		    }
			writer.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		file = new File(Game.BASE_DIRECTORY + "Levels/" + level.toFileString()+"/Entities.txt");
		//file.delete();
		try
		{
			writer = new BufferedWriter(new FileWriter(file, true));
		    for(int y = 0; y < level.getHeight(); y++)
		    {
		    	for(int x = 0; x < level.getWidth(); x++)
		    	{
		    		writer.write(level.getEntities()[x+(y*level.getWidth())]);
		    	}
		    	writer.newLine();
		    }
			writer.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(level.hasOwnBackground())
		{
			file = new File(Game.BASE_DIRECTORY + "Levels/" + level.toFileString() +"/Background.png");
			try
			{
				ImageIO.write(level.getBackground(), "png",file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		if(level.hasOwnBackgroundSpecial())
		{
			file = new File(Game.BASE_DIRECTORY + "Levels/" + level.toFileString() +"/BackgroundSpecial.png");
			try
			{
				ImageIO.write(level.getSpecialBackground(), "png",file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void writeDefaultFile(String from, String to)
	{
		InputStream stream = FileHelper.class.getResourceAsStream(from);
	    if (stream == null)
	    {
	    	System.out.println("File write failed");
	    	return;
	    }
	    OutputStream resStreamOut;
	    int readBytes;
	    byte[] buffer = new byte[4096];
	    try 
	    {
	        resStreamOut = new FileOutputStream(new File(to));
	        while ((readBytes = stream.read(buffer)) > 0) 
	        {
	            resStreamOut.write(buffer, 0, readBytes);
	        }
			stream.close();
	        resStreamOut.close();
	    } 
	    catch (IOException e1)
	    {
	        e1.printStackTrace();
	    } 
	}

	public static void writeEmpty(File file, int width, int height, char c)
	{
	    try 
	    {
	    	file.getParentFile().mkdirs();
			file.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
	        for(int y = 0; y < height; y++)
	        {
	        	for(int x = 0; x < width; x++)
	        	{
	        		writer.write(c);
	        	}
	        	writer.newLine();
	        }
	        writer.close();
	    } 
	    catch (IOException e1)
	    {
	        e1.printStackTrace();
	    } 
	}
	
	public static boolean deleteDirectory(File file)
	{
		if(file.isDirectory())
		{
			String[] files = file.list();
			for(String f : files)
			{
				if(deleteDirectory(new File(file, f))==false)
				{
					return false;
				}
			}
		}
		return file.delete();
	}
	
	public static boolean deleteFile(String path)
	{
		return new File(path).delete();
	}
	
	public static BufferedImage loadImage(String name)
	{
		try
		{
			return ImageIO.read(FileHelper.class.getClass().getResource(name));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public static BufferedImage loadImage(File file)
	{
		if(file.exists())
		{
			try
			{
				return ImageIO.read(file);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static BufferedImage loadImage(String name, int width, int height, boolean special)
	{
		new File(Game.BASE_DIRECTORY + "Levels/" + Level.makeFileString(name, ""+width, ""+height));
		File file = special? new File(Game.BASE_DIRECTORY + "Levels/" + Level.makeFileString(name, ""+width, ""+height) + "/BackgroundSpecial.png") : new File(Game.BASE_DIRECTORY + "Levels/" + Level.makeFileString(name, ""+width, ""+height)+ "/Background.png");
		return loadImage(file);
	}

	public static boolean copyImage(File to, File selectedFile)
	{
		try
		{
	    	to.getParentFile().mkdirs();
			Files.copy(selectedFile.toPath(), to.toPath());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return true;
		
		
		/*//TODO actually copy image files the hard/real way.
	    int readBytes;
	    byte[] buffer = new byte[4096];
	    try 
	    {
	    	to.getParentFile().mkdirs();
	    	if(!to.exists())
	    	{
	    		if(!to.createNewFile())
	    			return false;
	    	}
			InputStream streamIn = new FileInputStream(selectedFile);
			OutputStream streamOut = new FileOutputStream(to);
	        while ((readBytes = streamIn.read(buffer)) > 0) 
	        {
	            streamOut.write(buffer, 0, readBytes);
	        }
			streamIn.close();
	        streamOut.close();
	    } 
	    catch (IOException e1)
	    {
	        e1.printStackTrace();
	        return false;
	    } 
	    return true;
	    */
	}
	
	public static void loadSettings(Settings settings)
	{
		try
		{
			File file = new File(settings.getDirectory() + "/Settings.txt");
			if(!file.exists())
			{
				return;
				//saveSettings(settings);
			}
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null)
			{
				String[] split = line.split(":");
				for(Pair p : settings.list)
				{
					if(split[0].equals(p.getLeft()))
					{
						if(split[0].equals("Gravity") || split[0].equals("Sounds Volume") || split[0].equals("Music Volume") || split[0].equals("Powerups") || split[0].equals("Speed"))///XXX find a better way for loading level. Boolean vs int
							p.setRight(Integer.parseInt(split[1]));
						else
							p.setRight(Boolean.parseBoolean(split[1]));
					}
				}
			}
			reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void saveSettings(Settings settings)
	{
		try
		{
			File file = createAndOpenFile(settings.getDirectory(), "/Settings.txt");
			file.delete();
			BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
			for(Pair p: settings.list)
			{
				writer.write(p.getLeft() + ":" + p.getRight());
				writer.newLine();			
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static File createAndOpenFile(String directory, String name)
	{
		File file = new File(directory + name);
		if(!file.exists())
		{
			new File(directory).mkdirs();
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return file;
	}
}
