package np.supermagicalloveparty.game;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class TextureManager
{
	private BufferedImage[]
			unicornRun, unicornRunSpecial,
			pandaRun, pandaRunSpecial,
			birdRun, birdRunSpecial,
			bunnyRun, bunnyRunSpecial,
			apple, sun;
	private BufferedImage 
	unicornBase, unicornBaseSpecial,
	unicornJump, unicornJumpSpecial,
	unicornSquat, unicornSquatSpecial,
	heart, heartSpecial,	
	birdBase, birdBaseSpecial,
	birdJump, birdJumpSpecial,
	birdSquat, birdSquatSpecial,
	egg, eggSpecial,
	pandaBase, pandaBaseSpecial,
	pandaJump, pandaJumpSpecial,
	pandaSquat, pandaSquatSpecial,
	bunnyBase, bunnyBaseSpecial,
	bunnyJump, bunnyJumpSpecial,
	bunnySquat, bunnySquatSpecial,
	boot, potion, cupcake, carrot, turnip, cabbage;

	public TextureManager(Game game)
	{
		//new File(Game.BASE_DIRECTORY + "levels/" + game.level.getName() + "~" + game.level.getWidth() + "x" + game.level.getHeight()).mkdirs();
	}
	
	/**
	 * loads every texture for the game. Okay(for now) due to low # of textures.
	 */
	public void init()
	{
		try
		{
			unicornBase = ImageIO.read(getClass().getResourceAsStream("/UnicornBase.png"));
			unicornBaseSpecial = ImageIO.read(getClass().getResourceAsStream("/UnicornBaseSpecial.png"));
			unicornRun = new BufferedImage[7];
			for(int i = 0; i < 7; i++)
			{
				unicornRun[i] = ImageIO.read(getClass().getResourceAsStream("/UnicornRun" + (i+1) + ".png"));
			}
			unicornRunSpecial = new BufferedImage[4];
			unicornRunSpecial[0] = ImageIO.read(getClass().getResourceAsStream("/UnicornRun1Special.png"));
			unicornRunSpecial[1] = unicornBaseSpecial;
			unicornRunSpecial[2] = ImageIO.read(getClass().getResourceAsStream("/UnicornRun2Special.png"));
			unicornRunSpecial[3] = unicornBaseSpecial;

			unicornJump = ImageIO.read(getClass().getResourceAsStream("/UnicornJump.png"));
			unicornJumpSpecial = ImageIO.read(getClass().getResourceAsStream("/UnicornJump1Special.png"));
			unicornSquat = ImageIO.read(getClass().getResourceAsStream("/UnicornSquat.png"));
			unicornSquatSpecial = ImageIO.read(getClass().getResourceAsStream("/UnicornSquatSpecial.png"));
			heart = ImageIO.read(getClass().getResourceAsStream("/Heart.png"));
			heartSpecial =  ImageIO.read(getClass().getResourceAsStream("/specialHeart.png"));
			
			birdBase = ImageIO.read(getClass().getResourceAsStream("/BirdBase.png"));
			birdBaseSpecial = ImageIO.read(getClass().getResourceAsStream("/BirdBase.png"));
			birdRun = new BufferedImage[2];
			birdRun[0] = ImageIO.read(getClass().getResourceAsStream("/BirdBase.png"));
			birdRun[1] = ImageIO.read(getClass().getResourceAsStream("/BirdBase.png"));
			birdJump = ImageIO.read(getClass().getResourceAsStream("/BirdBase.png"));
			birdSquat = ImageIO.read(getClass().getResourceAsStream("/BirdBase.png"));
			egg = ImageIO.read(getClass().getResourceAsStream("/Heart.png"));
			
			pandaBase = ImageIO.read(getClass().getResourceAsStream("/PandaBase.png"));
			pandaBaseSpecial = ImageIO.read(getClass().getResourceAsStream("/PandaBase.png"));
			pandaRun = new BufferedImage[2];
			pandaRun[0] = ImageIO.read(getClass().getResourceAsStream("/PandaBase.png"));
			pandaRun[1] = ImageIO.read(getClass().getResourceAsStream("/PandaBase.png"));
			pandaJump = ImageIO.read(getClass().getResourceAsStream("/PandaBase.png"));
			pandaSquat = ImageIO.read(getClass().getResourceAsStream("/PandaSquat.png"));
			
			bunnyBase = ImageIO.read(getClass().getResourceAsStream("/BunnyBase.png"));
			bunnyBaseSpecial = ImageIO.read(getClass().getResourceAsStream("/BunnyBase.png"));
			bunnyRun = new BufferedImage[5];
			bunnyRun[0] = ImageIO.read(getClass().getResourceAsStream("/BunnyRun1.png"));
			bunnyRun[1] = ImageIO.read(getClass().getResourceAsStream("/BunnyRun2.png"));
			bunnyRun[2] = ImageIO.read(getClass().getResourceAsStream("/BunnyRun3.png"));
			bunnyRun[3] = ImageIO.read(getClass().getResourceAsStream("/BunnyRun4.png"));
			bunnyRun[4] = ImageIO.read(getClass().getResourceAsStream("/BunnyRun5.png"));

			bunnyJump = ImageIO.read(getClass().getResourceAsStream("/BunnyBase.png"));
			bunnySquat = ImageIO.read(getClass().getResourceAsStream("/BunnyBase.png"));
			
			cabbage =ImageIO.read(getClass().getResourceAsStream("/Cabbage.png"));
			turnip = ImageIO.read(getClass().getResourceAsStream("/Turnip.png"));
			carrot = ImageIO.read(getClass().getResourceAsStream("/Carrot.png"));
			boot = ImageIO.read(getClass().getResourceAsStream("/Speed.png"));
			potion = ImageIO.read(getClass().getResourceAsStream("/Strength.png"));
			cupcake = ImageIO.read(getClass().getResourceAsStream("/Coopcake.png"));
			
			apple = new BufferedImage[8];
			apple[0] = ImageIO.read(getClass().getResourceAsStream("/Apple.png"));
			apple[1] = ImageIO.read(getClass().getResourceAsStream("/AppleSparkle1.png"));
			apple[2] = ImageIO.read(getClass().getResourceAsStream("/AppleSparkle2.png"));
			apple[3] = ImageIO.read(getClass().getResourceAsStream("/AppleSparkle3.png"));
			apple[4] = ImageIO.read(getClass().getResourceAsStream("/AppleSparkle4.png"));
			apple[5] = apple[3];
			apple[6] = apple[2];
			apple[7] = apple[1];
			
			sun = new BufferedImage[3];
			sun[0] = ImageIO.read(getClass().getResourceAsStream("/Sun.png"));
			sun[1] = ImageIO.read(getClass().getResourceAsStream("/Sun2.png"));
			sun[2] = ImageIO.read(getClass().getResourceAsStream("/Sun3.png"));

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public BufferedImage getUnicornBase()
	{
		return unicornBase;
	}

	public BufferedImage getUnicornBaseSpecial()
	{
		return unicornBaseSpecial;
	}

	public BufferedImage[] getUnicornRun()
	{
		return unicornRun;
	}

	public BufferedImage[] getUnicornRunSpecial()
	{
		return unicornRunSpecial;
	}

	public BufferedImage getUnicornJump()
	{
		return unicornJump;
	}

	public BufferedImage getUnicornJumpSpecial()
	{
		return unicornJumpSpecial;
	}

	public BufferedImage getUnicornSquat()
	{
		return unicornSquat;
	}

	public BufferedImage getUnicornSquatSpecial()
	{
		return unicornSquatSpecial;
	}

	public BufferedImage getHeart()
	{
		return heart;
	}

	public BufferedImage getHeartSpecial()
	{
		return heartSpecial;
	}
	
	//BIRD
	public BufferedImage getBirdBase()
	{
		return birdBase;
	}

	public BufferedImage getBirdBaseSpecial()
	{
		return birdBaseSpecial;
	}

	public BufferedImage[] getBirdRun()
	{
		return birdRun;
	}

	public BufferedImage[] getBirdRunSpecial()
	{
		return birdRunSpecial;
	}

	public BufferedImage getBirdJump()
	{
		return birdJump;
	}

	public BufferedImage getBirdJumpSpecial()
	{
		return birdJumpSpecial;
	}

	public BufferedImage getBirdSquat()
	{
		return birdSquat;
	}

	public BufferedImage getBirdSquatSpecial()
	{
		return birdSquatSpecial;
	}

	public BufferedImage getEgg()
	{
		return egg;
	}

	public BufferedImage getEggSpecial()
	{
		return eggSpecial;
	}
	
	//PANDA
	public BufferedImage getPandaBase()
	{
		return pandaBase;
	}

	public BufferedImage getPandaBaseSpecial()
	{
		return pandaBaseSpecial;
	}

	public BufferedImage[] getPandaRun()
	{
		return pandaRun;
	}

	public BufferedImage[] getPandaRunSpecial()
	{
		return pandaRunSpecial;
	}

	public BufferedImage getPandaJump()
	{
		return pandaJump;
	}

	public BufferedImage getPandaJumpSpecial()
	{
		return pandaJumpSpecial;
	}

	public BufferedImage getPandaSquat()
	{
		return pandaSquat;
	}

	public BufferedImage getPandaSquatSpecial()
	{
		return pandaSquatSpecial;
	}
	
	//BUNNY
	public BufferedImage getBunnyBase()
	{
		return bunnyBase;
	}

	public BufferedImage getBunnyBaseSpecial()
	{
		return bunnyBaseSpecial;
	}

	public BufferedImage[] getBunnyRun()
	{
		return bunnyRun;
	}

	public BufferedImage[] getBunnyRunSpecial()
	{
		return bunnyRunSpecial;
	}

	public BufferedImage getBunnyJump()
	{
		return bunnyJump;
	}

	public BufferedImage getBunnyJumpSpecial()
	{
		return bunnyJumpSpecial;
	}

	public BufferedImage getBunnySquat()
	{
		return bunnySquat;
	}

	public BufferedImage getBunnySquatSpecial()
	{
		return bunnySquatSpecial;
	}
	
	public BufferedImage getCarrot()
	{
		return carrot;
	}
	
	public BufferedImage getBoot()
	{
		return boot;
	}

	public BufferedImage getPotion()
	{
		return potion;
	}

	public BufferedImage getCupcake()
	{
		return cupcake;
	}

	public BufferedImage[] getApple()
	{
		return apple;
	}
	
	public BufferedImage[] getSun()
	{
		return sun;
	}

	public BufferedImage getTurnip()
	{
		return turnip;
	}

	public Image getCabbage()
	{
		return cabbage;
	}
}
