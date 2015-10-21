package np.supermagicalloveparty.game;

import java.io.File;
import java.io.FilenameFilter;

import np.supermagicalloveparty.game.Game.Mode;
import np.supermagicalloveparty.server.Server;

public class Driver
{
	public static void main(String[] args)
	{
		if(args.length > 0)
		{
			if(args[0].equalsIgnoreCase("-server"))
			{
				File levelDirectory = new File(Game.BASE_DIRECTORY +"Levels/");
				levelDirectory.mkdirs();
				String[] folders = levelDirectory.list(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String name)
					{
						if(name.contains("."))
							return false;
						return true;
					}
					
				});		
				if(folders.length == 0)
				{
					FileHelper.createDefaultLevel();
				}
				Server server = new Server(null, 6969, FileHelper.loadDefaultLevel(), 9, Mode.SATISFACTION);
				server.start();
				return;
			}
		}
		Frame frame = new Frame();
		GuiOutOfGame gui = new GuiOutOfGame(frame);
	}
	
	//TODO command line server options.
//	private static Object[] getServerArgs(String[] args)
//	{
//		Object[] ret = new Object[4];
//		if(args.length == 5)
//		{
//			String levelString = "ERROR";
//			int levelWidth = 0, levelHeight = 0;
//			String[] chunks = args[1].split("~");
//			levelString=chunks[0];
//			if(chunks.length>1)
//			{
//				String[] dimensions = chunks[1].split("x");
//				if(dimensions.length>1)
//				{
//					levelWidth = Integer.parseInt(dimensions[0]);
//					levelHeight = Integer.parseInt(dimensions[1]);
//				}else
//				{
//					System.out.println("ERROR~ Missing a dimension!");
//					ret[0] = 
//				}
//			}else
//			{
//				System.out.println("ERROR: Level Name is missing dimensions!");
//			}
//			ret[0] = new Level(levelString, levelWidth, levelHeight, 
//					FileHelper.loadTerrain(levelString, levelWidth, levelHeight), 
//					FileHelper.loadEntities(levelString, levelWidth, levelHeight), 
//					FileHelper.loadImage(levelString, levelWidth, levelHeight, false),
//					FileHelper.loadImage(levelString, levelWidth, levelHeight, true));
//		}
//		
//		return ret;
//	}
}
