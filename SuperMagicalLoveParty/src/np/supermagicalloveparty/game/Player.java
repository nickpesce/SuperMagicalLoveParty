package np.supermagicalloveparty.game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import np.supermagicalloveparty.server.ExtraAction;
import np.supermagicalloveparty.server.ExtraPhysics;
import np.supermagicalloveparty.server.Packet;
import np.supermagicalloveparty.server.ServerGame;


public abstract class Player extends EntityLiving{

	private Point spawn;
	int number;
	KeyMap keys;
	protected String name, type;
	public static final int MAX_HEIGHT = 8, MAX_WIDTH = 8;
	
	public Player(Game g, double x, double y, double width, double height, Shape bounds, KeyMap keys, int number)
	{
		super(g, x, y, width, height, bounds);
		if(game.mode.equals(Game.Mode.SATISFACTION))
			this.lives = 1;
		else if(game.mode.equals(Game.Mode.HEALTH))
			this.lives = 3;
		this.keys = keys;
		this.number = number;
		this.name = game.playerNames[number];
		spawn = new Point(x, y);
	}
	
	/**
	 * used for multiplayer player creation. (No key binds).
	 * @param g the game instance
	 * @param x x coord
	 * @param y y coord
	 * @param width width in tiles
	 * @param height height in tiles
	 * @param bounds shape for collisions
	 * @param number player number. must be unique.
	 */
	public Player(Game g, double x, double y, double width, double height, Shape bounds, int number)
	{
		this(g, x, y, width, height, bounds, null, number);
	}
	
	public void setKeyMap(KeyMap keys)
	{
		game.input.changeKeyMap(this, keys);
	}
	@Override
	public void onPointsFull()
	{
		super.onPointsFull();
		if(game.mode.equals(Game.Mode.SATISFACTION) && game.state.equals(Game.State.PLAYING) && !game.multiplayer)
		{
			if(lives==1)
			{
				game.gameOver(this, false);
			}
			else
			{
				lives--;
				respawn();
			}
		}
	}
	
	@Override
	public void draw(Graphics2D g, double interpolation)
	{
		super.draw(g, interpolation);
		double scale = game.canvas.getScaleWithCamera();
		int drawYStatusBar = (int)((bounds.getY()+(getVy()*interpolation))*scale);//Only different for unicorn.
		//sprites[spriteIndex].getWidth(null) OLD. if want to scale based on original
		if(direction == LEFT)
			g.drawImage(animations.getFrame(), drawX+drawWidth, drawY, -drawWidth, drawHeight, null);
		else
			g.drawImage(animations.getFrame(), drawX, drawY, drawWidth, drawHeight, null);
		g.setColor(Color.BLACK);
		g.drawRect((int)(drawX), (int)((drawYStatusBar)-scale*3), drawWidth, (int)(scale));
		
		int nP = 0;
		for(int i = 0; i < powerups.length; i++)
		{
			if(powerups[i] > 0)
			{
				switch(i)
				{
					case DAMAGE:
						g.setColor(new Color(0, 0, 0, 200));
						break;
					case SPEED:
						g.setColor(new Color(255, 255, 255, 200));
						break;
					case SPECIAL_ATTACK:
						g.setColor(new Color(0, 0, 255, 200));
						break;
					case INVINCIBLE:
						g.setColor(new Color(255, 255, 0, 200));
						break;
				}
				g.fillRect(drawX+1, (int)((drawYStatusBar)-scale * (3.5 + (nP*.5))) +1, (int)(((width*scale)-1)*((double)powerups[i]/maxPowerups[i])), (int)(scale/2)-1);
				nP++;
			}
		}
		
		//Health Bar
		int healthGreen = (int)(255*(getPoints()/getMaxPoints()));
		g.setColor(new Color(255-healthGreen, healthGreen, 0, 200));
		g.fillRect((int)drawX+1, (int)((drawYStatusBar)-scale * 3)+1, (int)(((width*scale)-1)*(getPoints()/getMaxPoints())), (int)(scale)-1);
		g.setColor(new Color(0, 0, 0, 200));
		
		//Name
		g.setFont(new Font("ARIAL", Font.PLAIN, (int)(scale)));
		g.drawString(name, (int)drawX+1, (int)((drawYStatusBar)-scale * 2.2));
		g.setFont(new Font("ARIAL", Font.PLAIN, (int)(scale*2)));
		
		//cooldowns
		g.setColor(new Color(150, 0, 150, 200));
		g.fillRect(drawX+1, (int)((drawYStatusBar)-scale * 2)+1, (int)(((width*scale)-1)*((double)attackCd/maxAttackCd)), (int)(scale/2)-1);
		g.setColor(new Color(255, 255, 255, 200));
		g.fillRect(drawX+1, (int)((drawYStatusBar)-scale * 1.5)+1, (int)(((width*scale)-1)*((double)evadeCd/maxEvadeCd)), (int)(scale/2)-1);		
		g.setColor(new Color(255, 0, 0, 200));
		g.fillRect(drawX+1, (int)((drawYStatusBar)-scale * 1)+1, (int)(((width*scale)-1)*((double)evadeTimer/maxEvadeTimer)), (int)(scale/2)-1);
		if(isEvading() || isStunned())
		{
			if(direction == LEFT)
				g.drawImage(filter, drawX+drawWidth, drawY, -drawWidth, drawHeight, null);
			else
				g.drawImage(filter, drawX, drawY, drawWidth, drawHeight, null);
		}
	}
	
	@Override
	public void addAnimations()
	{
		super.addAnimations();
	}

	@Override
	public void onHit(Collidable c)
	{		
		super.onHit(c);
	}
	
	public void removeFromGame()
	{
		game.removePlayer(number);
	}
	
	public void respawn()
	{
		visible = true;
		if(game.mode.equals(Game.Mode.HEALTH))
		{
			lives--;
			if(lives <= 0)
			{
				game.consoleLog("[LOSS] " + this + " has lost all of their lives!", Color.RED, true);
				x = -6969;
				y = -6969;
				vY = 0;
				vX = 0;
				aX = 0;
				aY = 0;
				visible = false;
				if(!game.multiplayer)
				{
					int index = -1;
					for(int i = 0; i < game.players.length; i++)
					{
						Player p = game.players[i];
						if(p != null && p.lives > 0)
						{
							if(index != -1)
								return;
							index = i;
						}
					}
					if(index != -1)
						game.gameOver(game.players[index], false);
				}
				return;
			}
			setPoints(maxPoints);
		}
		else if(game.mode.equals(Game.Mode.SATISFACTION))
		{
			setPoints(0);
		}
		setNoClip(false);
		setX(spawn.x);
		setY(spawn.y);
		collidedLeft = collidedRight = false;
		setVx(0);
		setVy(0);
		setAy(game.settings.getGravity()/Math.pow(GameLoop.BASE_SPEED, 2));
		setAx(0);
		defaultBounds();
		attackCd = 0;
		evadeCd = 0;
		state.clear();
		stopAllPowerups();
		jumpsLeft = maxJumps;
		animations.play(AnimationManager.IDLE);
	}

	public void updateRunning()
	{
		//If multiplayer player, keys will be null. Server will update run.
		if(keys == null)return;
		
		boolean left = game.input.pressedKeys.contains(keys.getKeyCode(InputHandler.LEFT));
		boolean right = game.input.pressedKeys.contains(keys.getKeyCode(InputHandler.RIGHT));
		if(left)
		{
			if(!right)
				runLeft();
		}
		else if(right)
		{
			if(!left)
				runRight();
		}
	}
	
	@Override
	public void stopStunned()
	{
		super.stopStunned();
		updateRunning();
	}
	
	@Override
	public void evade()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.EVADE)));
		super.evade();
	}
	
	@Override
	public void runLeft()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.RUN_LEFT)));
		super.runLeft();
	}
	
	@Override
	public void runRight()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.RUN_RIGHT)));
		super.runRight();
	}
	
	@Override
	public void stand()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.STAND)));
		super.stand();
	}
	
	public void land()
	{
		if(game instanceof ServerGame)
			((ServerGame)game).server.sendAll(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.LAND)));
		super.land();
	}
	@Override
	public void stopEvading()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.STOP_EVADING)));
		super.stopEvading();
	}
	
	@Override
	public void stopLeft()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.STOP_LEFT)));
		super.stopLeft();
	}
	
	@Override
	public void stopRight()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.STOP_RIGHT)));
		super.stopRight();
	}
	
	@Override
	public void startAttack()
	{
		if(!canAttack())
			return;
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.ATTACK_START)));
		super.startAttack();
	}
	
	@Override
	public void doAttack()
	{
		if(!canAttack())
			return;
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.ATTACK, attackChargeTime)));
		super.doAttack();
	}
	@Override
	public boolean attackCharged()
	{
		if(!canAttack())
			return false;
		return super.attackCharged();
	}
	
	@Override
	public boolean attack()
	{
		if(!canAttack())
			return false;
		return super.attack();
	}
	
	@Override
	public void update()
	{
		super.update();
		if(attackCd>0)
			attackCd--;
		/*Client reports physics*/
		/*
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
		{
			((GameMP)game).packetManager.send(new Packet(Packet.PHYSICS, new ExtraPhysics(number, x, y, vX, vY, aX, aY, points, direction)));
		}
		*/
	}

	@Override
	public void jump()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.JUMP)));
		super.jump();
	}
	
	@Override
	public void squat()
	{
		if(game.multiplayer && ((GameMP)game).myPlayerNumber == number)
			((GameMP)game).getPacketManager().send(new Packet(Packet.ACTION, new ExtraAction(number, ExtraAction.SQUAT)));
		super.squat();
		//animations.play(AnimationManager.SQUAT);
	}
	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public KeyMap getKeys()
	{
		return keys;
	}

	public void setKeys(KeyMap keys)
	{
		this.keys = keys;
	}

	public Point getSpawn()
	{
		return spawn;
	}

	public void setSpawn(Point spawn)
	{
		this.spawn = spawn;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getType()
	{
		return type;
	}

}
