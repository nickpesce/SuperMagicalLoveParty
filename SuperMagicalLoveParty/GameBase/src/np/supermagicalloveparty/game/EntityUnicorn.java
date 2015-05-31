package np.supermagicalloveparty.game;
import java.awt.image.BufferedImage;


public class EntityUnicorn extends Player
{

	public EntityUnicorn(Game g, double x, double y, KeyMap keys, int number)
	{
		super(g, x, y, 6, 6, new Rectangle(x + 1, y +(6/2), 4, 6/2), keys, number);
		maxJumps = jumpsLeft = 2;
		type = "Unicorn";
		maxAttackCd = 30;
		speedX = 40;
	}

	@Override
	public void addAnimations()
	{
		super.addAnimations();
		animations.addAnimation(new Animation(game, game.textures.getUnicornRun(), new int[]{2, 2, 2, 2, 2, 2, 2}, game.textures.getUnicornRunSpecial(), new int[]{4, 4, 4, 4}), AnimationManager.RUN);

		animations.addAnimation(new Animation(game, new BufferedImage[]{game.textures.getUnicornBase()}, null, new BufferedImage[]{game.textures.getUnicornBaseSpecial()}, null), AnimationManager.IDLE);
		
		animations.addAnimation(new Animation(game, new BufferedImage[] {game.textures.getUnicornJump()}, null, new BufferedImage[] {game.textures.getUnicornJumpSpecial()}, null), AnimationManager.JUMP);
		
		animations.addAnimation(new Animation(game, new BufferedImage[]{game.textures.getUnicornSquat()}, null, new BufferedImage[]{game.textures.getUnicornSquatSpecial()}, null), AnimationManager.SQUAT);
	}
	
	@Override
	public void onHit(Collidable c)
	{
		super.onHit(c);
	}
	
	@Override
	public boolean attack()
	{
		if(!canAttack())
			return false;
		super.attack();
		attackCd = maxAttackCd/2;
		Particle.spawnMany(game, bounds.getX() + (direction==LEFT? -1:bounds.getWidth() + 1) , bounds.getY()-1, 300, game.specialMode? Particle.Type.RED_GOO:Particle.Type.FAIRY_DUST);
		/*
		for(Collidable c : collisions)
		{
			if(c instanceof EntityLiving)
			{
				((EntityLiving)c).hit(new DamageEvent(2*damageMultiplier, this, "unicorn dust sprinkle"));
			}
		}
		*/
		new EntityExplosion(game, bounds.getX() + (direction==LEFT? -1:bounds.getWidth() + 1) , bounds.getY()-1, 3, this, 0, 7, 1, 2*damageMultiplier, false, "Unicorn dust sprinkle");
		return true;
		/*
		if(!canAttack())
			return false;
		attackCd = maxAttackCd;
		
		game.soundManager.playSound(SoundManager.POP);

		if(direction == LEFT)
		{
			if(hasPowerupSpecialAttack())
			{
				new EntityProjectileHeart(game, this, bounds.getX(), bounds.getY(), -1, 1, 1.5*damageMultiplier);
				new EntityProjectileHeart(game, this, bounds.getX() , bounds.getY(), -1, 0, 1.5*damageMultiplier);
				new EntityProjectileHeart(game, this, bounds.getX() , bounds.getY(), -1, -1, 1.5*damageMultiplier);
			}else
				new EntityProjectileHeart(game, this, bounds.getX(), bounds.getY(), -1, 1, 2*damageMultiplier);

		}
		else
		{
			if(hasPowerupSpecialAttack())
			{
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() - 2 , bounds.getY(), 1, 1, 1.5*damageMultiplier);
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() - 2 , bounds.getY(), 1, 0, 1.5*damageMultiplier);
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() - 2 , bounds.getY(), 1, -1, 1.5*damageMultiplier);
			}else
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() - 2 , bounds.getY(), 1, 1, 2*damageMultiplier);

		}
		return true;
		*/
	}
	
	@Override
	public boolean attackCharged()
	{
		super.attackCharged();
		game.soundManager.playSound(SoundManager.KISS);
		if(game.multiplayer)
			return false;
		if(attackChargeTime>500)
			attackChargeTime = 500;
		if(direction == LEFT)
		{
			if(hasPowerupSpecialAttack())
			{
				new EntityProjectileHeart(game, this, bounds.getX() - 1, bounds.getY() - 1, 1, -1, 1, 1.5*damageMultiplier);
				new EntityProjectileHeart(game, this, bounds.getX() - 1, bounds.getY() - 1, 1+(attackChargeTime/150.0), -1, 0, 1.5*damageMultiplier+(attackChargeTime/200.0));
				new EntityProjectileHeart(game, this, bounds.getX() - 1, bounds.getY() - 1, 1, -1, -1, 1.5*damageMultiplier);
			}else
				new EntityProjectileHeart(game, this, bounds.getX() - 1, bounds.getY() - 1, 1+ (attackChargeTime/150.0), -1, 1, 2*damageMultiplier+(attackChargeTime/200.0));

		}
		else
		{
			if(hasPowerupSpecialAttack())
			{
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() + 1 ,bounds.getY() - 1, 1, 1, 1, 1.5*damageMultiplier);
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() + 1 ,bounds.getY() - 1, 1+ (attackChargeTime/150.0), 1, 0, 1.5*damageMultiplier+(attackChargeTime/200.0));
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() + 1, bounds.getY() - 1, 1, 1, -1, 1.5*damageMultiplier);
			}else
				new EntityProjectileHeart(game, this, bounds.getX() + bounds.getWidth() + 1, bounds.getY() - 1, 1+ (attackChargeTime/150.0), 1, 1, 2*damageMultiplier+(attackChargeTime/200.0));

		}
		return true;
	}
	
	@Override
	public void update()
	{
		super.update();
		if(attackCharging)
			Particle.spawnMany(game, bounds.getX() + (direction==LEFT? -1:bounds.getWidth() + 1) , bounds.getY()-1, game.specialMode? 1:20, game.specialMode? Particle.Type.RED_GOO:Particle.Type.FAIRY_DUST);
	}
	/*
	@Override
	public boolean attackMelee()
	{
		if(!canAttack())
			return false;
		attackCd = 20;
		for(Collidable c : collisions)
		{
			if(c instanceof EntityLiving)
			{
				((EntityLiving)c).hurt(new DamageEvent(2, this, Strings.attackMelee()));
			}
		}
		return true;
	}
	*/
	
	@Override
	public void jump()
	{
		if(adjustBounds(1, 1.5, 0, -.5))//TODO horse jump same width to make setting it back easier. avoid INSIDE collision. Should make it thin.
			super.jump();
		//adjustBounds(.5, 1, direction==LEFT ? 0:.5, 0);
	}
	
	@Override
	public void stand()
	{
		super.stand();
		if(isJumping())
			adjustBounds(1, 1.5, 0, -.5);
			
	}
	@Override
	public void squat()
	{
		super.squat();
		if(!isStunned())
			adjustBounds(1, .5, 0, .5);
	}

}
