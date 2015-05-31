package np.supermagicalloveparty.game;
import java.awt.Graphics2D;

public interface Drawable {

	public void draw(Graphics2D g, double interpolation);
	public boolean getVisible();
}