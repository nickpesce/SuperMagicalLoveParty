package np.supermagicalloveparty.game;



public interface Collidable {

	public boolean collided(Collidable c);
	public boolean alreadyCollided(Collidable c);
	/**
	 * The attacker/projectile should be the one doing the damage to the c that it hits
	 * @param c
	 */
	public void onHit(Collidable c);
	public void onUnCollided(Collidable c);
	public Shape getBounds();
}
