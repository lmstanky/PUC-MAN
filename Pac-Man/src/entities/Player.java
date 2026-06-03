package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.newdawn.slick.openal.Audio;

import collision.Entity;
import collision.Physics;
import main.Game;
import main.Textures;

public class Player extends GameObject implements Entity
{
	public String prevDirection;
	public int dirCount;
	private String direction;
	public String name;
	private Textures textures;
	private Game game;
	public BufferedImage icon;
	int iconState;
	int prevIconState;
	int iconTickCount = 3;
	public boolean inTunnel;
	public String weapon = null;
	public Bomb bomb = null;
	public int ammo = 0;
	
	public boolean slowed = false;
	int slowedCount = 120; // Number of ticks Pac-Man is slowed for after eating an infected Power Pellet
	
	public int dotSound = 0;
	
	public Player(double x, double y, double speed, Textures textures, Game game, Audio sound)
	{
		super(x,y,speed);
		this.direction = "left";
		this.textures = textures;
		this.game = game;
		icon = textures.rPlayer2;
		iconState = 1;
		prevIconState = 0;
	}
	
	public void tick()
	{		
		if(iconTickCount == 0)
		{
			iconTickCount = 3;
			if(speed != 0)
				icon = getNextIcon();
		}
		
		if(((i <= 5 || i >= 22) && j == 14) || x < 480 || x > 1120)
			inTunnel = true;
		else
			inTunnel = false;
		
		
		if(x >= 1200)
			x = 360;
		else if(x <= 360)
			x = 1200;
		
		// These conditionals check if Pac-Man should carry on moving after turning a corner
		if(speed == 0 && (direction == "lUp" || direction == "rUp"))
		{
			speed = 1;
			direction = "up";
		}
		else if(speed == 0 && (direction == "uRight" || direction == "dRight"))
		{
			speed = 1;
			direction = "right";
		}
		else if(speed == 0 && (direction == "uLeft" || direction == "dLeft"))
		{
			speed = 1;
			direction = "left";
		}
		else if(speed == 0 && (direction == "lDown" || direction == "rDown"))
		{
			speed = 1;
			direction = "down";
		}
		
		if(slowed)
		{
			speed = 0.5;
			slowedCount--;
			if(slowedCount == 0)
			{
				quantise();
				slowedCount = 120;
				slowed = false;
			}
		}
		
		if(Physics.mapCollision(this, "pac-man", direction, game.maze, game))
			speed = 0;		
		else if(!slowed)
			speed = 2;
		
		if(direction == "right")
			x+=speed;
		else if(direction == "rUp" || direction == "uRight")
		{
			x+=speed;
			y-=speed;
		}
		else if(direction == "lUp" || direction == "uLeft")
		{
			x-=speed;
			y-=speed;
		}
		else if(direction == "dRight" || direction == "rDown")
		{
			x+=speed;
			y+=speed;
		}
		else if(direction == "dLeft" || direction == "lDown")
		{
			x-=speed;
			y+=speed;
		}
		else if(direction == "down")
			y+=speed;
		else if(direction == "left")
			x-=speed;
		else if(direction == "up")
			y-=speed;
		
		
		iconTickCount--;		
		i = getGridX(x);
		j = getGridY(y);
	}

	private void quantise()
	{
		// Re-align x value if it's a decimal, or an odd number
		if((x - (int)x) != 0)
			Math.round(x);
		if((int)x % 2 != 0)
			x++;
		
		
		// Do the same with the y value
		if((y - (int)y) != 0)
			Math.round(y);
		if((int)y % 2 != 0)
			y++;
		
	}

	@SuppressWarnings("static-access")
	public void render(Graphics g)
	{
		g.drawImage(icon, (int) x, (int) y, null);
		if(game.DEBUG)
			drawCollisionBoxes(g);
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle((int)x, (int)y, 32, 32);
	}
	
	public void drawCollisionBoxes(Graphics g)
	{
		g.setColor(Color.RED);
		g.drawRect((int)x+4, (int)y+4, 24, 24);
		g.setColor(Color.GREEN);
		g.drawRect((int)x+10, (int)y+10, 12, 12);
		g.setColor(Color.blue);
		g.drawRect((int)x+23, (int)y+3, 6,6);
		g.drawRect((int)x+3, (int)y+3, 6, 6);
		g.drawRect((int)x+3, (int)y+23, 6, 6);
		g.drawRect((int)x+23, (int)y+23, 6, 6);
	}
	
	public BufferedImage getNextIcon()
	{
		if(direction == "right" || direction == "uRight" || direction == "dRight")
		{
			if(iconState == 0)
			{
				iconState = 1;
				prevIconState = 0;
				return textures.rPlayer1;
			}
				
			else if(iconState == 1 && prevIconState == 0)
			{
				iconState = 2;
				prevIconState = 1;
				return textures.rPlayer2;
			}
			
			else if(iconState == 1 && prevIconState == 2)
			{
				iconState = 0;
				prevIconState = 1;
				return textures.rPlayer0;
			}
				
			else
			{
				iconState = 1;
				prevIconState = 2;
				return textures.rPlayer1;
			}
		}
		
		if(direction == "down" || direction == "rDown" || direction == "lDown")
		{
			if(iconState == 0)
			{
				iconState = 1;
				prevIconState = 0;
				return textures.dPlayer1;
			}
				
			else if(iconState == 1 && prevIconState == 0)
			{
				iconState = 2;
				prevIconState = 1;
				return textures.rPlayer2;
			}
			
			else if(iconState == 1 && prevIconState == 2)
			{
				iconState = 0;
				prevIconState = 1;
				return textures.dPlayer0;
			}
				
			else
			{
				iconState = 1;
				prevIconState = 2;
				return textures.dPlayer1;
			}
		}
		
		if(direction == "left" || direction == "uLeft" || direction == "dLeft")
		{
			if(iconState == 0)
			{
				iconState = 1;
				prevIconState = 0;
				return textures.lPlayer1;
			}
				
			else if(iconState == 1 && prevIconState == 0)
			{
				iconState = 2;
				prevIconState = 1;
				return textures.rPlayer2;
			}
			
			else if(iconState == 1 && prevIconState == 2)
			{
				iconState = 0;
				prevIconState = 1;
				return textures.lPlayer0;
			}
				
			else
			{
				iconState = 1;
				prevIconState = 2;
				return textures.lPlayer1;
			}
		}
		
		else
		{
			if(iconState == 0)
			{
				iconState = 1;
				prevIconState = 0;
				return textures.uPlayer1;
			}
				
			else if(iconState == 1 && prevIconState == 0)
			{
				iconState = 2;
				prevIconState = 1;
				return textures.rPlayer2;
			}
			
			else if(iconState == 1 && prevIconState == 2)
			{
				iconState = 0;
				prevIconState = 1;
				return textures.uPlayer0;
			}
				
			else
			{
				iconState = 1;
				prevIconState = 2;
				return textures.uPlayer1;
			}
		}
			
	}
	
	public double nearest24(double x)
	{
		return Math.round(x / 24) * 24;
	}
	
	public void fire()
	{
		if(weapon == "bomb" && ammo > 0)
		{
			bomb = new Bomb(game);
			game.c.entities.add(bomb);
			ammo--;
		}
		
		else if(weapon == "laser" && ammo > 0)
		{
			game.audio.soundEffects.get("laser_fire").playAsSoundEffect(1.0f, 1.0f, false);
			ammo--;
			if(direction == "right" || direction == "left")
				game.c.entities.add(new Bullet(x, y, 2, direction, textures.bulletHoriz, 100, false, game));
			else if(direction == "down" || direction == "up")
				game.c.entities.add(new Bullet(x, y, 2, direction, textures.bulletVert, 100, false,  game));
			else if(direction == "uRight" || direction == "rUp" || direction == "dLeft" || direction == "lDown")
				game.c.entities.add(new Bullet(x, y, 2, direction, textures.bulletDiagUpRight, 500, false, game));
			else
				game.c.entities.add(new Bullet(x, y, 2, direction, textures.bulletDiagUpLeft, 500, false, game));
		}
	}
	
	public void setDirection(String in){direction = in;}
	public String getDirection(){return direction;}
	public int getGridX(double xIn){return (int)Math.round(((xIn-456)/24));}
	public int getGridY(double yIn){return (int)Math.round(((yIn-96)/24));}
	public double getX(){return x;}
	public double getY(){return y;}
}
