package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import collision.Entity;
import main.Game;
import main.SpriteSheet;

public class Bullet extends GameObject implements Entity
{
	
	private BufferedImage icon;
	Game game;
	SpriteSheet ss;
	String direction;
	int points;
	Rectangle bounds, blinkyBounds, pinkyBounds, inkyBounds, clydeBounds, ufoBounds;
	boolean hostile;
	
	public Bullet(double x, double y, double speed, String direction, BufferedImage icon, int points, boolean hostile, Game game)
	{
		super(x,y, speed);
		this.direction = direction;
		this.icon = icon;
		this.points = points;
		this.hostile = hostile;
		this.game = game;
	}
	
	public void tick()
	{
		checkCollision();
		
		if(direction == "right")
			x += 10;
		else if(direction == "rUp" || direction == "uRight")
		{
			x += 10;
			y -= 10;
		}
		else if(direction == "down")
			y += 10;
		
		else if(direction == "rDown" || direction == "dRight")
		{
			x += 10;
			y += 10;
		}
		else if(direction == "left")
			x -= 10;
		
		else if(direction == "lDown" || direction == "dLeft")
		{
			x -= 10;
			y += 10;
		}
		else if(direction == "up")
			y -= 10;
		
		else if(direction == "lUp" || direction == "uLeft")
		{
			x -= 10;
			y -= 10;
		}
		
	}
	
	public void checkCollision()
	{
		Rectangle pacBounds = new Rectangle((int)game.pacMan.x+16, (int)game.pacMan.y+16, 8, 8);
		blinkyBounds = new Rectangle((int)game.blinky.x+16, (int)game.blinky.y+16, 8, 8);
		pinkyBounds = new Rectangle((int)game.pinky.x+16, (int)game.pinky.y+16, 8, 8);
		inkyBounds = new Rectangle((int)game.inky.x+16, (int)game.inky.y+16, 8, 8);
		clydeBounds = new Rectangle((int)game.clyde.x+16, (int)game.clyde.y+16, 8, 8);
		if(game.ufoActive)
			ufoBounds = new Rectangle(game.ufo.x, 80, 64, 27);
		bounds = getBounds();
		if(bounds.intersects(pacBounds) && hostile)
		{
			game.stopBackgroundNoise();
			game.stopFrightenedNoise();
			game.stopReturningNoise();
			game.pacDeath = true;
			game.laserDeath = true; 
		}
		
		else if(bounds.intersects(blinkyBounds) && (game.blinky.state == "scatter" || game.blinky.state == "chase"
				|| game.blinky.state == "frightened") && !hostile)
		{
			game.c.entities.remove(this);
			game.score += points;
			game.blinky.state = "exploded";
			game.blinky.speed = 0;
			game.blinky.icon = game.blinky.getNextIcon();
			game.audio.soundEffects.get("pacDeath_laser").playAsSoundEffect(1.0f, 1.0f, false);
		}
		
		else if(bounds.intersects(pinkyBounds) && (game.pinky.state == "scatter" || 
				game.pinky.state == "chase" || game.pinky.state == "frightened") && !hostile)
		{
			game.c.entities.remove(this);
			game.score += points;
			game.pinky.state = "exploded";
			game.pinky.speed = 0;
			game.pinky.icon = game.pinky.getNextIcon();
			game.audio.soundEffects.get("pacDeath_laser").playAsSoundEffect(1.0f, 1.0f, false);
		}
		
		else if(bounds.intersects(inkyBounds) && (game.inky.state == "scatter" || game.inky.state == "chase" 
				|| game.inky.state == "frightened") && !hostile)
		{
			game.c.entities.remove(this);
			game.score += points;
			game.inky.state = "exploded";
			game.inky.speed = 0;
			game.inky.icon = game.inky.getNextIcon();
			game.audio.soundEffects.get("pacDeath_laser").playAsSoundEffect(1.0f, 1.0f, false);
		}
		
		else if(bounds.intersects(clydeBounds) && (game.clyde.state == "scatter" || game.clyde.state == "chase"
				|| game.clyde.state == "frightened") && !hostile)
		{
			game.c.entities.remove(this);
			game.score += points;
			game.clyde.state = "exploded";
			game.clyde.speed = 0;
			game.clyde.icon = game.clyde.getNextIcon();
			game.audio.soundEffects.get("pacDeath_laser").playAsSoundEffect(1.0f, 1.0f, false);
		}
		
		
		else if(game.ufoActive && bounds.intersects(ufoBounds))
		{
			game.c.entities.remove(this);
			game.score += 1000;
			game.ufo.isShot = true;
			game.audio.soundEffects.get("mysteryShipDeath").playAsSoundEffect(1.0f, 1.0f, false);
		}
		
	}
	
	public void render(Graphics g)
	{
		g.drawImage(icon, (int)x, (int) y, null);
	}
	
	public double getX(){return x;}
	public double getY(){return y;}

	@Override
	public Rectangle getBounds()
	{
		Rectangle newRec = new Rectangle((int)x+4, (int)y+4, 24, 24);
		return newRec;
	}
}
