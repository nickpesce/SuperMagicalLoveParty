package np.supermagicalloveparty.game;
import java.awt.image.BufferedImage;


public class EntityBird extends Player
{

	public EntityBird(Game g, double x, double y, KeyMap keys, int number)
	{
		super(g, x, y, 3, 3, new Circle(x, y, 1.5), keys, number);
		setJumpHeight(5);
		type = "Bird";
		setAy(getAy()/2);
		if(game.mode.equals(Game.Mode.HEALTH))
		{
			maxPoints = points = 5;
		}else if(game.mode.equals(Game.Mode.SATISFACTION))
		{
			maxPoints = 14;
		}
		maxAttackCd = 200;
	}

	@Override
	public boolean attack()
	{
		super.attack();
		if(!canAttack())
			return false;
		attackCd = maxAttackCd/5;
		Particle.spawnMany(game, bounds.getX() + (direction==LEFT? 0:bounds.getWidth() - 2) , bounds.getY(), 100, Particle.Type.RAINBOW);
		new EntityExplosion(game, x+(width/2.0), y+(height/2.0), 3, this, 3, 3, 1, 4*damageMultiplier, false, "rainbird dust");
		return true;
	}
	
	@Override
	public boolean attackCharged()
	{
		super.attackCharged();
		if(game.multiplayer)
			return false;
		new EntityProjectileEgg(game, this, x, y, 3 *damageMultiplier, hasPowerupSpecialAttack());
		return true;
	}
	@Override
	public void update()
	{
		super.update();
		if(onGround && attackCd > 0)
			attackCd --;
		if(attackCharging)
			Particle.spawnMany(game, bounds.getX() + (bounds.getWidth()/2) , bounds.getY() + bounds.getHeight(), 10, Particle.Type.RAINBOW);
	}
	
	@Override
	public void respawn()
	{
		super.respawn();
		setAy(getAy()/2);
	}
	
	@Override
	public void addAnimations()
	{
		super.addAnimations();
		BufferedImage[] run = game.textures.getBirdRun();
			int[] runTimes = {5, 10, 5, 10};
			animations.addAnimation(new Animation(game, run, runTimes), AnimationManager.RUN);
			
			BufferedImage[] idle = {game.textures.getBirdBase()};
			BufferedImage[] idleS = {game.textures.getBirdBaseSpecial()};

			animations.addAnimation(new Animation(game, idle, null, idleS, null), AnimationManager.IDLE);
			
			BufferedImage[] jump = {game.textures.getBirdJump()};
			animations.addAnimation(new Animation(game,jump, null), AnimationManager.JUMP);
			
			BufferedImage[] squat = {game.textures.getBirdSquat()};
			animations.addAnimation(new Animation(game, squat, null), AnimationManager.SQUAT);
	}
	
	@Override
	public void jump()
	{
		super.jump();
		jumpsLeft = 1;
	}
	
	@Override
	public void squat()
	{
		super.squat();
		setAy(getAy()*2);
	}
	
	@Override
	public void stand()
	{
		super.stand();
		setAy(getAy()/2);
	}
	
	@Override
	public boolean canAttack()
	{
		return super.canAttack();
	}
//	@Override
//	public boolean canCollideBottom(int tile)
//	{
//		if(tile == Level.UP)
//			return false;
//		return true;
//	}
}
