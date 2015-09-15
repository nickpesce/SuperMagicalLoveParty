package np.supermagicalloveparty.game;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.NumberFormat;

import javax.swing.JOptionPane;

public class GameUpdater implements Runnable
{

	int downloadSize;
	int downloadProgress;
	String status;
	Thread thread;
	boolean updating, failed;
	File file;
	
	public GameUpdater()
	{
		downloadSize = 1;
//		try
//		{
//			String path = GameUpdater.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//			JOptionPane.showMessageDialog(null, path);
//			String decodedPath = URLDecoder.decode(path, "UTF-8");
//			file = new File(decodedPath);
//		}
//		catch (UnsupportedEncodingException e)
//		{
			file = new File("Super Magical Love Party.jar");
//			e.printStackTrace();
//		}
		status = "Preparing to update: " + file.getName() + " (" + file.getPath() + ")";
		failed = false;
	}

    public void run()
    {
        try
        {
            if (file.exists())
            {
            	status = "Deleting old game file";
                file.delete();
            }

            long l = System.currentTimeMillis();
            status = "Connecting...";
            URL url = new URL("https://www.dropbox.com/s/u5qf20nr53zko4v/Super%20Magical%20Love%20Party.jar?raw=1");
            URLConnection urlconnection = url.openConnection();
            downloadSize = urlconnection.getContentLength();

            InputStream inputstream = url.openStream();
            FileOutputStream fileoutputstream1 = new FileOutputStream(file);
            byte abyte1[] = new byte[8196];
            int i = 0;

            do
            {
                int j;

                if ((j = inputstream.read(abyte1)) < 0)
                {
                    break;
                }

                fileoutputstream1.write(abyte1, 0, j);
                i += j;
                downloadProgress = i;
                status = "Updating: " + getPercent();
            }
            while (true);

            status = ("Done. " + i + " bytes read (" + (System.currentTimeMillis() - l) + " millseconds).");
            
            fileoutputstream1.close();
            inputstream.close();
            updating = false;
            reopenGame();
            failed = false;
        }
        catch (Exception exception)
        {
            status = "Failed to update! Error: " + exception;
            exception.printStackTrace();
            updating = false;
            failed = true;
        }
    }

    public void reopenGame()
    {
    	if(failed)
    		return;
    	try
		{
			Desktop.getDesktop().open(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    	System.exit(0);
    }
    
    public void start()
    {
    	updating = true;
    	thread = new Thread(this);
    	thread.start();
    }
    
    public String getStatus()
    {
    	return status;
    }
    
    public String getPercent()
    {
    	return NumberFormat.getPercentInstance().format(getRatioDone());
    }
    
    public double getRatioDone()
    {
    	return downloadProgress/(downloadSize*1.0);
    }
}
