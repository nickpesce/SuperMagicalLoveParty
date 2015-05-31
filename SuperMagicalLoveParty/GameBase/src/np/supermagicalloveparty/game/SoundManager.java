package np.supermagicalloveparty.game;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class SoundManager {

	public static final byte
	MUSIC_LOOP = 0,
	MUSIC2_LOOP = 1,
	MUSIC_STREAMLINE = 2,
	POP = 3,
	KISS = 4,
	THROW = 5,
	MENU_SPARKLE = 6,
	CRUNCH = 7;
	
	public Clip music;
	int musicVol, sfxVol;
	ArrayList<Clip> playingClips;
	public SoundManager(int musicVolume, int sfxVolume)
	{
		this.playingClips = new ArrayList<Clip>();
		this.musicVol = musicVolume;
		this.sfxVol = sfxVolume;
	}
	
	public void playSound(final byte id)
	{
		if(sfxVol == 0)
			return;
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				try
				{
					final Clip clip = AudioSystem.getClip();
					switch(id)
					{
					case POP:
						clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/Pop.wav")));
						break;
					case KISS:
						clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/Kiss.wav")));
						break;
					case THROW:
						clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/Throw.wav")));
						break;
					case CRUNCH:
						clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/Crunch.wav")));
						break;
					case MENU_SPARKLE:
						clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/MenuSparkle.wav")));
						break;
						
					}
					playingClips.add(clip);
					setVolume(clip, -((50-(sfxVol/2.0f))));
					clip.start();
					clip.flush();
					clip.addLineListener(new LineListener()
					{
						
						@Override
						public void update(LineEvent event)
						{
							if(event.getType()==LineEvent.Type.STOP)
							{
								clip.close();//TODO probably better way  to close small sounds
								playingClips.remove(clip);
							}
						}
					});
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void setMusic(byte id)
	{
		try
		{
			if(music != null)
				music.close();
			else
				music = AudioSystem.getClip();
			switch(id)
			{
			case MUSIC_LOOP:
				music.open(AudioSystem.getAudioInputStream(getClass().getResource("/Music.aiff")));
				break;
			case MUSIC2_LOOP:
				music.open(AudioSystem.getAudioInputStream(getClass().getResource("/Music2.aiff")));
				break;
			case MUSIC_STREAMLINE:
				music.open(AudioSystem.getAudioInputStream(getClass().getResource("/Streamline.wav")));
				break;
			}
			setVolume(music, -((50-(musicVol/2.0f))));
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void pauseMusic()
	{
		if(music!=null)
			music.stop();
	}
	
	public void resumeMusic()
	{
		if(music!=null && music.isOpen())
			music.loop(Clip.LOOP_CONTINUOUSLY);
		setVolume(music, -((50-(musicVol/2.0f))));
	}
	
	public static void setVolume(Clip clip, float level)
	{
		if(level == -50)
			clip.stop();
		else if(!clip.isActive())
			clip.start();
		if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
		{
			FloatControl volume = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
			volume.setValue(level);
		}
	}
	
	public void setSfxVolume(int level)
	{
		this.sfxVol = level;
		for(Clip clip : playingClips)
		{
			setVolume(clip, -((50-(sfxVol/2.0f))));
		}
		playSound(MENU_SPARKLE);
	}
	
	public void setMusicVolume(int level)
	{
		this.musicVol = level;
		if(music!=null)
			setVolume(music,  -((50-(musicVol/2.0f))));
	}
}
