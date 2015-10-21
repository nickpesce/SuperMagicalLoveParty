package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public abstract class EntityLiving extends Entity
{

	/**
	 * all of the current states of the entity. ex. Running, jumping, evading. Can be multiple.
	 */
	protected ArrayList<Integer> state;
	/**
	 * the time(in ticks) left for each powerup. Use powerup index constant values in class EntityLiving.
	 */
	protected int[] powerups;
	/**
	 * The length(in ticks) of each type of powerup. Use powerup index constant values in class EntityLiving.
	 */
	protected int[] maxPowerups = {600, 600, 300, 800};
	/**
	 * current satisfaction or health of the entity.
	 */
	protected double points;
	/**
	 * Total satisfaction or health the entity can have.
	 */
	protected double maxPoints;
	/**
	 * value to multiply all outgoing damage by.
	 */
	protected double damageMultiplier = 1;
	protected int jumpsLeft, maxJumps;

	/**
	 * attack button is held down
	 */
	protected boolean attackCharging;
	
	/**
	 * how long the attack button has been held down for(in ticks)
	 */
	protected int attackChargeTime;
	
	/**
	 * Time in ticks before player can attack again
	 */
	protected int attackCd;
	/**
	 * How long (in ticks) the attackCd was set last. Used for cd percent
	 */
	protected int maxAttackCd;
	/**
	 * Length(in ticks) until entity can evade again.
	 */
	protected int evadeCd;
	/**
	 * Total ticks between allowed evades.
	 */
	protected int maxEvadeCd;
	/**
	 * Time(in ticks) left of evade.
	 */
	protected int evadeTimer;
	/**
	 * Time(in ticks) that entity will evade.
	 */
	protected int maxEvadeTimer;
	/**
	 * Time(in ticks) that the entity will be stunned for
	 */
	protected int stunTimer;
	/**
	 * Image to draw over player to show evading or stunned
	 */
	protected BufferedImage filter;
	/**
	 * maximum speed to reach when running.
	 */
	double speedX;
	/**
	 * How much vX to add per tick when starting to run.
	 */
	double runAcceleration;
	/**
	 * initial speed of jump in blocks/second
	 */
	double jumpSpeed;
	public  int lives;
	
	public final static Integer
		RUNNING_LEFT = 0,
		RUNNING_RIGHT=1, 
		SQUATTING=3,
		JUMPING=4, 
		EVADING=5,
		STUNNED=6;
	
	
	public static final int
		SPECIAL_ATTACK = 0,
		DAMAGE = 1,
		INVINCIBLE = 2,
		SPEED = 3;

	
	
	public EntityLiving(Game g, double x, double y, double width, double height, Shape bounds)
	{
		super(g, x, y, width, height, bounds);
		speedX = 30;
		runAcceleration = speedX/1.0;
		setJumpHeight(15);
		if(game.mode.equals(Game.Mode.SATISFACTION))
		{
			points = 0;
			maxPoints = 10;
		}
		else if(game.mode.equals(Game.Mode.HEALTH))
		{
			points = maxPoints = 10;
		}
		maxAttackCd = 20;
		attackCd = 0;
		attackChargeTime = 0;
		attackCharging = false;
		jumpsLeft = maxJumps = 1;
		evadeTimer = 0;
		maxEvadeTimer = 20;
		evadeCd = 0;
		maxEvadeCd = 300;
		state = new ArrayList<Integer>();
		powerups = new int[4];
		animations.play(AnimationManager.IDLE);
		
		animations.setChanged(true);
	}
	
	public double getJumpHeight()
	{
		return (jumpSpeed * jumpSpeed)/(2*(Game.DEFAULT_GRAVITY/Math.pow(GameLoop.BASE_SPEED, 2))*Math.pow(GameLoop.BASE_SPEED, 2));
	}
	
	public double getActualJumpHeight()
	{
		return (jumpSpeed * jumpSpeed)/(2*getAy()*Math.pow(GameLoop.BASE_SPEED, 2));
	}
	
	/**
	 * approximate blocks that entity can jump in normal gravity conditions(Game.DEFAULT_GRAVITY). collision for wall jumps is pretty bad. Guess and check pretty much(if you dont want player to be able to jump over a 7 high wall, set to ~6, V.V)
	 * @param h height in tiles
	 */
	public void setJumpHeight(double h)
	{
		jumpSpeed = (Math.pow(2*(Game.DEFAULT_GRAVITY/Math.pow(GameLoop.BASE_SPEED, 2))*h, .5) * GameLoop.BASE_SPEED);
	}
	
	/**
	 * set the height that the entity can jump in the current gravity conditions
	 * @param h height in tiles
	 */
	public void setActualJumpHeight(double h)
	{
		jumpSpeed = (Math.pow(2*getAy()*h, .5) * GameLoop.BASE_SPEED);
	}
	
	/**
	 * Do damage to the entity or give points to attacker as specified by the DamageEvent.
	 * @param de Amount, who, how damage was done.
	 * @return true if hit was succesful
	 */
	public boolean hit(DamageEvent de)
	{
		if(game.mode.equals(Game.Mode.SATISFACTION))
		{
			if(hasPowerupInvincible())
			{
				game.consoleLog("[LOVE] " + de.getSourceString() + " tried to love " + this + " with " + String.format("%.3g", de.getDamage()) + " luvitude using " + de.getAttack() + " but " + this + " is like a rock, shutting out the love!", Color.YELLOW, false);
				return false;
			}
			if(isEvading())
			{
				game.consoleLog("[EVADE] " + de.getSourceString() + " tried to love " + this + " with " + String.format("%.3g", de.getDamage()) + " luvitude using " + de.getAttack() + " but " + this + " used his mad skill to evade it!", Color.YELLOW, false);
				return false;
			}
			if(de.getSource() instanceof Player)
			{
				((Player)de.getSource()).increasePoints(de.getDamage());
				game.consoleLog("[SATISFACTION] " + de.getSource() + " gave " + this + " " + de.getDamage() + " love using " + de.getAttack()+ ", and is feeling the satisfaction.", Color.RED, false);
				return true;
			}
			else
			{
				decreasePoints(de.getDamage());
				game.consoleLog("[DISSATISFACTION] OH NO!! " + this + " got a bobo from " + de.getSourceString() + " when it did " + de.getAttack() + ", and he lost some satisfaction!", Color.RED, false);
				return true;
			}
		}
		else if(game.mode.equals(Game.Mode.HEALTH))
		{
			if(hasPowerupInvincible())
			{
				game.consoleLog("[DAMAGE] " + de.getSourceString() + " tried to do " + String.format("%.3g", de.getDamage()) + " points of damage to " + this + " with " + de.getAttack() + " but " + this + " is invincible!", Color.YELLOW, false);
				return false;
			}
			if(isEvading())
			{
				game.consoleLog("[EVADE] " + de.getSourceString() + " tried to hurt " + this + " with " + String.format("%.3g", de.getDamage()) + " damage using " + de.getAttack() + " but " + this + " used his mad skill to evade it!", Color.YELLOW, false);
				return false;
			}
			points -= de.getDamage();
			if(points <= 0)
			{
				game.consoleLog("[KILL] " + de.getSourceString() + " killed " + this + " with " + de.getAttack(), Color.RED, true);
				respawn();
				return true;
			}
			else
			{
				game.consoleLog("[DAMAGE] " + de.getSourceString() + " did " + String.format("%.3g", de.getDamage()) + " points of damage to " + this + " with " + de.getAttack(), Color.PINK, false);
				return true;
			}
		}
		return false;
	}
	
	public void respawn()
	{
		
	}
	
	@Override
	public void update()
	{
		super.update();
		if(evadeTimer <= 0)
		{
			if(isEvading())
				stopEvading();
		}
		else
			evadeTimer--;
		if(stunTimer<=0)
		{
			if(isStunned())
				stopStunned();
		}
		else
			stunTimer--;
		if(attackCharging)
			attackChargeTime++;
		for(int i = 0; i < powerups.length; i++)
		{
			if(powerups[i]>0)
			{
				if(--powerups[i] == 0)
					onPowerupEnd(i);
			}
		}
		
		if(isOnIce())
			runAcceleration = speedX/5.0;
		else
			runAcceleration = speedX/1.0;
		if(evadeCd > 0)
			evadeCd--;
		
		if(animations.isChanged() || animations.getAnimationIndex() == AnimationManager.IDLE)
		{
			animations.setChanged(false);
			if(isSquatting())
				animations.play(AnimationManager.SQUAT);
			//else if(isEvading())
			//	animations.play(AnimationManager.EVADE);
			else if(isJumping())
				animations.play(AnimationManager.JUMP);
			else if(isRunning())
				animations.play(AnimationManager.RUN);
			else
				animations.play(AnimationManager.IDLE);
			if(isStunned() || isEvading())
				updateFilter();
		}
	}
	
	@Override
	public void updatePhysics()
	{
		super.updatePhysics();
		if(isRunningLeft() && !collidedLeft && !isRunningRight() && getScaledVx() > -speedX)
		{
			addScaledVx(-runAcceleration);
			if(getScaledVx() < -speedX)
				setScaledVx(-speedX);
		}
		else if(isRunningRight() && !collidedRight && !isRunningLeft() && getScaledVx() < speedX)
		{
			addScaledVx(runAcceleration);
			if(getScaledVx() > speedX)
				setScaledVx(speedX);
		}
		
	}
	
	public void updateFilter()
	{
		BufferedImage frame = animations.getFrame();
		filter = new BufferedImage(animations.getFrame().getWidth(), animations.getFrame().getHeight(), BufferedImage.TYPE_INT_ARGB);
		int color = new Color(0, 0, isEvading()? 255 : 0).getRGB() & ((100<<24)| 0x00ffffff);
		for(int w = 0; w < frame.getWidth(); w++)
		{
			for(int h = 0; h < frame.getHeight(); h++)
			{
				if(new Color(frame.getRGB(w, h), true).getAlpha() != 0)
					filter.setRGB(w, h, color);
			}
		}
	}
	
	@Override
	public void onHit(Collidable c)
	{
		super.onHit(c);
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
	}
	
	public void onPointsFull()
	{
	}
	
	public void onPointsEmpty()
	{
		if(game.mode.equals(Game.Mode.HEALTH))
			respawn();
	}
	
	public void increasePoints(double value)
	{
		if(this.points + value > maxPoints)
		{
			this.points = maxPoints;
			onPointsFull();
		}
		else
			this.points += value;
	}
	
	public void decreasePoints(double value)
	{
		if(this.points - value < 0)
		{
			this.points = 0;
			onPointsEmpty();
		}
		else
			this.points -= points;
	}
	
	public void setPoints(double points)
	{
		this.points = points;
		if(this.points <= 0)
		{
			onPointsEmpty();
		}
		else if(this.points >= maxPoints)
		{
			onPointsFull();
		}
	}
	
	public double getPoints()
	{
		return points;
	}
	
	public double getMaxPoints()
	{
		return maxPoints;
	}
	
	public boolean isSquatting()
	{
		return state.contains(SQUATTING);
	}
	
	public boolean isRunningLeft()
	{
		return state.contains(RUNNING_LEFT);
	}
	
	public boolean isRunningRight()
	{
		return state.contains(RUNNING_RIGHT);
	}
	
	public boolean isRunning()
	{
		return state.contains(RUNNING_LEFT) || state.contains(RUNNING_RIGHT);
	}
	
	public boolean isJumping()
	{
		return state.contains(JUMPING);
	}
	
	public boolean isIdle()
	{
		return state.isEmpty();
	}
	
	public boolean isDead()
	{
		return lives <= 0;
	}
	
	protected void setIdle()
	{
		animations.setChanged(true);
		state.clear();
	}
	
	protected void setRunningLeft(boolean running)
	{
		if(running && !state.contains(RUNNING_LEFT))
		{
			//state.clear();
			state.add(RUNNING_LEFT);
		}
		else
			state.remove(RUNNING_LEFT);
	}
	
	private void addRunningLeft()
	{
		if(!state.contains(RUNNING_LEFT))
		{
			state.add(RUNNING_LEFT);
		}
	}
	
	protected void setRunningRight(boolean running)
	{
		if(running && !state.contains(RUNNING_RIGHT))
		{
			//state.clear();
			state.add(RUNNING_RIGHT);
		}
		else
			state.remove(RUNNING_RIGHT);
	}
	
	private void addRunningRight()
	{
		if(!state.contains(RUNNING_RIGHT))
		{
			state.add(RUNNING_RIGHT);
		}
	}
	
	protected void setSquatting(boolean squatting)
	{
		if(squatting && !state.contains(SQUATTING))
		{
			//state.clear();
			state.add(SQUATTING);
		}
		else
			state.remove(SQUATTING);
	}
	
	private void addSquatting()
	{
		if(!state.contains(SQUATTING))
		{
			state.add(SQUATTING);
		}
	}
	
	protected void setJumping(boolean jumping)
	{
		if(jumping && !state.contains(JUMPING))
		{
			//state.clear();
			state.add(JUMPING);
		}
		else
			state.remove(JUMPING);
	}
	
	private void addJumping()
	{
		if(!state.contains(JUMPING))
		{
			state.add(JUMPING);
		}
	}
	
	public void jump()
	{
		animations.setChanged(true);
		addJumping();
		if(!(getScaledVy() < -jumpSpeed))
			setScaledVy(-jumpSpeed);
		jumpsLeft--;
	}
	
	public void land()
	{
		setJumping(false);
		animations.setChanged(true);
		jumpsLeft = maxJumps;
		if(!isSquatting())
			defaultBounds();
	}
	
	public void squat()
	{
		if(isStunned())
			return;
		animations.setChanged(true);
		stopLeft();
		stopRight();
		setAy((game.settings.getGravity()/Math.pow(GameLoop.BASE_SPEED, 2))*2);
		
		addSquatting();
		onGround = false;//TO proc bottom collison and fall through some types of tiles
	}
	
	public void stand()
	{
		defaultBounds();
		setSquatting(false);
		setAy(game.settings.getGravity()/Math.pow(GameLoop.BASE_SPEED, 2));
		animations.setChanged(true);
	}
	
	public void runLeft()
	{
		if(!isSquatting() && !isRunningLeft() && !isStunned())
		{
			animations.setChanged(true);
//			if(!collidedLeft)
//				setScaledVx(-speedX);//TODO add or set for run??
			addRunningLeft();
		}
	}
	
	public void runRight()
	{
		if(!isSquatting() && !isRunningRight() && !isStunned())
		{
			animations.setChanged(true);
//			if(!collidedRight)
//				setScaledVx(speedX);
			addRunningRight();
		}
	}
	
	public void stopLeft()
	{
		animations.setChanged(true);
		/*
		if(isRunningLeft())
			setVx(0);
		if(isRunningRight())
			setScaledVx(speedX);
			*/
		setRunningLeft(false);
	}
	
	public void stopRight()
	{
		animations.setChanged(true);
		/*
		if(isRunningRight())
			setVx(0);
		if(isRunningLeft())
			setScaledVx(-speedX);
		 */
		setRunningRight(false);
	}

	public void stopRunning()
	{
		if(isRunningLeft())
			stopLeft();
		if(isRunningRight())
			stopRight();
	}
	
	public void evade()
	{
		if(!state.contains(EVADING) && canEvade())
		{
			if(isSquatting())
				stand();
			animations.setChanged(true);
			evadeTimer = maxEvadeTimer;
			evadeCd = maxEvadeCd;
			state.add(EVADING);
		}
	}
	
	public void stopEvading()
	{
		state.remove(EVADING);
		animations.setChanged(true);
	}
	
	public boolean isEvading()
	{
		return state.contains(EVADING);
	}
	
	public void startAttack()
	{
		attackCharging = true;
		animations.play(AnimationManager.ATTACK_CHARGING);
	}
	
	public void doAttack()
	{
		if(!canAttack() || !attackCharging)
			return;
		attackCharging = false;
		if(attackChargeTime > 10)
			attackCharged();
		else
			attack();
		attackChargeTime = 0;
	}
	
	public boolean attack()
	{
		animations.play(AnimationManager.ATTACK);
		return false;
	}
	
	public boolean attackCharged()
	{
		attackCd = maxAttackCd;
		animations.play(AnimationManager.ATTACK_CHARGED);
		return true;
	}
	
	
	@Override
	protected void onCollideRight(CollisionEvent ce)
	{
		super.onCollideRight(ce);
		if(ce.getTile() == Level.DAMAGE_AND_KNOCKBACK)
		{
			hit(new DamageEvent(4, "the sun", "Solar flare"));
			addScaledVx(-50);
			collidedRight = true;
		}
//		else if(isRunningRight())
//		{
//			setScaledVx(speedX);
//		}
	}
	
	@Override
	protected void onCollideLeft(CollisionEvent ce)
	{
		super.onCollideLeft(ce);
		if(ce.getTile() == Level.DAMAGE_AND_KNOCKBACK && !bounceable)
		{
			hit(new DamageEvent(4, "the sun", "Solar flare"));
			addScaledVx(50);
			collidedLeft = true;
		}
//		else if(isRunningLeft())
//		{
//			setScaledVx(-speedX);
//		}
	}
	
	@Override
	protected void onCollideBottom(CollisionEvent ce)
	{
		super.onCollideBottom(ce);
		if(ce.getTile() == Level.DAMAGE_AND_KNOCKBACK)
			hit(new DamageEvent(4, "the sun", "Solar flare"));
	}
	
	@Override
	protected void onCollideTop(CollisionEvent ce)
	{
		super.onCollideTop(ce);
		if(ce.getTile() == Level.DAMAGE_AND_KNOCKBACK)
			hit(new DamageEvent(4, "the sun", "Solar flare"));
	}

	public boolean canAttack()
	{
		return attackCd<=0 && !isStunned();
	}
	
	public boolean canJump()
	{
		return jumpsLeft > 0 && /*(getVy() >= 0) &&*/ (!isSquatting()) && (!isStunned());
	}
	
	public boolean canEvade()
	{
		return evadeCd <= 0;
	}
	
	public void powerupSpecialAttack()
	{
		powerups[SPECIAL_ATTACK] = maxPowerups[SPECIAL_ATTACK];
	}
	
	public void powerupDamage()
	{
		if(!hasPowerupDamage())
			damageMultiplier++;
		powerups[DAMAGE] = maxPowerups[DAMAGE];
	}
	
	public void powerupInvincible()
	{
		powerups[INVINCIBLE] = maxPowerups[INVINCIBLE];
	}
	
	public void powerupHealth()
	{
		this.increasePoints((maxPoints - points)*.5);
	}
	
	public void powerupSpeed()
	{
		speedX = 60;
		if(!hasPowerupSpeed())
		{
			if(isRunningLeft())
				setScaledVx(-speedX);
			if(isRunningRight())
				setScaledVx(speedX);
		}
		powerups[SPEED] = maxPowerups[SPEED];
	}
	
	public boolean hasPowerupSpecialAttack()
	{
		return powerups[SPECIAL_ATTACK] > 0;
	}
	
	public boolean hasPowerupDamage()
	{
		return powerups[DAMAGE] > 0;
	}
	
	public boolean hasPowerupInvincible()
	{
		return powerups[INVINCIBLE] > 0;
	}
	
	public boolean hasPowerupSpeed()
	{
		return powerups[SPEED] > 0;
	}
	
	public void onPowerupEnd(int index)
	{
		switch(index)
		{
			case SPEED:
				speedX = 30;
				if(isRunningLeft())
					setScaledVx(speedX);
				if(isRunningRight())
					setScaledVx(-speedX);
				break;
			case DAMAGE:
				damageMultiplier--;
				break;
		}
	}
	
	public void stopAllPowerups()
	{
		if(hasPowerupSpeed())
			onPowerupEnd(SPEED);
		if(hasPowerupDamage())
			onPowerupEnd(DAMAGE);
		for(int i = 0; i < powerups.length; i++)
		{
			powerups[i]=0;
		}
	}
	
	public void stun(int time)
	{
		stunTimer += time;
		stopRunning();
		if(!state.contains(STUNNED))
		{
			animations.setChanged(true);
			state.add(STUNNED);
		}
	}
	
	public boolean isStunned()
	{
		return state.contains(STUNNED);
	}
	
	public void stopStunned()
	{
		state.remove(STUNNED);
		animations.setChanged(true);
	}
	
	public double getDamageMultiplier()
	{
		return damageMultiplier;
	}
	
	public ArrayList<Integer> getState()
	{
		return state;
	}
	
	public void setState (ArrayList<Integer> state)
	{
		this.state = state;
		animations.setChanged(true);
	}
	public boolean isAttackCharging()
	{
		return attackCharging;
	}
	public void setAttackCharging(boolean attackCharging)
	{
		this.attackCharging = attackCharging;
	}
	public int getAttackChargeTime()
	{
		return attackChargeTime;
	}
	public void setAttackChargeTime(int attackChargeTime)
	{
		this.attackChargeTime = attackChargeTime;
	}

}
