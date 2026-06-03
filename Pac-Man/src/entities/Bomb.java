package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import collision.Entity;
import main.Game;

public class Bomb implements Entity
{

	BufferedImage icon;
	
	// Need to check if we're able to draw a particular tile. If we need to stop, store the coordinate we reached
	boolean right = true;	int rightX;
	boolean down = true;	int downX;
	boolean left = true;	int leftX;
	boolean up = true;		int upX;
	public String state = "unexploded";
	
	int iconState = 0;
	int iconStateCount = 5;
	int explodedState = 0;
	int explosionCount = 60;
	boolean finished = false;
	int currentState;
	int fuse = 240;
	
	Game game;
	
	int x, y, i, j;
	
	public Bomb(Game game)
	{
		this.game = game;
		i = game.pacMan.i;
		j = game.pacMan.j;
		x = game.maze.map[j][i].x-4;
		y = game.maze.map[j][i].y-4;
		icon = game.textures.bomb0;
	}
	
	
	public void tick()
	{
		if(state == "unexploded")
		{
			if(iconStateCount == 0)
			{
				if(iconState == 0)
				{
					icon = game.textures.bomb0;
					iconState = 1;
				}
				else if(iconState == 1)
				{
					icon = game.textures.bomb1;
					iconState = 2;
				}
				else if(iconState == 2)
				{
					icon = game.textures.bomb2;
					iconState = 0;
				}
				iconStateCount = 5;
			}
			iconStateCount--;
			fuse--;
			if(fuse == 0)
			{
				game.audio.soundEffects.get("explosion").playAsSoundEffect(1.0f, 1.0f, false);
				state = "exploding";
			}
		}
	}

	public void render(Graphics g)
	{
		if(state != "exploding")
			g.drawImage(icon, x, y, null);
		
		// Animate the explosion
		else
		{
			// Check for boundaries to see if we can keep rendering in each direction
			if(i+explodedState+1 <= 27 && right && !(game.maze.map[j][i+explodedState+1].isDot || game.maze.map[j][i+explodedState+1].isBlank)){right = false; rightX = 24*explodedState;}
			else if(right) rightX = 24*explodedState;
			if(j+explodedState+1 <= 30 && down && !(game.maze.map[j+explodedState+1][i].isDot || game.maze.map[j+explodedState+1][i].isBlank)){down = false; downX = 24*explodedState;}
			else if(down) downX = 24*explodedState;
			if(i-explodedState-1 >= 0 && left && !(game.maze.map[j][i-explodedState-1].isDot || game.maze.map[j][i-explodedState-1].isBlank)){left = false; leftX = 24*explodedState;}
			else if(left) leftX = 24*explodedState;
			if(j-explodedState-1 >= 0 && up && !(game.maze.map[j-explodedState-1][i].isDot || game.maze.map[j-explodedState-1][i].isBlank)){up = false; upX = 24*explodedState;}
			else if(up) upX = 24*explodedState;
			
			
			if(explodedState == 0)
			{
				if(right && down && left && up)	icon = game.textures.bombExplosionCross;
				else if(right && up && down)	icon = game.textures.bombExplosionTRight;
				else if(right && down && left)	icon = game.textures.bombExplosionTDown;
				else if(left && up && down)		icon = game.textures.bombExplosionTLeft;
				else if(left && right && up)	icon = game.textures.bombExplosionTUp;
				
				else if(left && right)			icon = game.textures.bombExplosionMiddleHoriz;
				else if(up && down)				icon = game.textures.bombExplosionMiddleVert;
				
				else if(down && right)			icon = game.textures.bombExplosionCornerTLeft;
				else if(left && down)			icon = game.textures.bombExplosionCornerTRight;
				else if(up && left)				icon = game.textures.bombExplosionCornerBRight;
				else if(up && right)				icon = game.textures.bombExplosionCornerBLeft;
				
				g.drawImage(icon, x, y, null);
				explodedState++;
			}
			
			currentState = 0;
			while(currentState < explodedState)
			{
				if(x+(24*currentState) < x+rightX){g.drawImage(game.textures.bombExplosionMiddleHoriz, x+(24*currentState), y, null);}
				if(!right){g.drawImage(game.textures.bombExplosionEndRight, x+rightX, y, null);}
				
				if(y+(24*currentState) < y+downX){g.drawImage(game.textures.bombExplosionMiddleVert, x, y+(24*currentState), null);}
				if(!down){g.drawImage(game.textures.bombExplosionEndDown, x, y+downX, null);}
				
				if(x-(24*currentState) > x-leftX){g.drawImage(game.textures.bombExplosionMiddleHoriz, x-(24*currentState), y, null);}
				if(!left){g.drawImage(game.textures.bombExplosionEndLeft, x-leftX, y, null);}
				
				if(y-(24*currentState) > y-upX){g.drawImage(game.textures.bombExplosionMiddleVert, x, y-(24*currentState), null);}
				if(!up){g.drawImage(game.textures.bombExplosionEndUp, x, y-upX, null);}
				
				g.drawImage(icon, x, y, null);
				
				checkCollision();
				currentState++;
			}
			if(explodedState < 3)
				explodedState++;
			else
			{
				finished = true;
				g.drawImage(icon, x, y, null);
				if(right){g.drawImage(game.textures.bombExplosionEndRight, x+(24*explodedState), y, null); rightX = x+(24*explodedState);} 
				if (down){g.drawImage(game.textures.bombExplosionEndDown, x, y+(24*explodedState), null); downX = y+(24*explodedState);} 
				if (left){g.drawImage(game.textures.bombExplosionEndLeft, x-(24*explodedState), y, null); leftX = x-(24*explodedState);}
				if (up){g.drawImage(game.textures.bombExplosionEndUp, x, y-(24*explodedState), null); upX = y-(24*explodedState);}
				checkCollision();
			}
			
			if(finished)
			{
				explosionCount--;
				if(explosionCount == 0)
					game.c.entities.remove(this);
			}
		}
	}

	private void checkCollision()
	{
		int width = Math.abs(rightX - leftX);
		int height = Math.abs(upX - downX);
		
		Rectangle crossX = new Rectangle(leftX, y, width, 32);
		Rectangle crossY = new Rectangle(upX, y, 32, height);
		
		Rectangle blinkyBounds = new Rectangle((int)game.blinky.x+16, (int)game.blinky.y+16, 8, 8);
		Rectangle pinkyBounds = new Rectangle((int)game.pinky.x+16, (int)game.pinky.y+16, 8, 8);
		Rectangle inkyBounds = new Rectangle((int)game.inky.x+16, (int)game.inky.y+16, 8, 8);
		Rectangle clydeBounds = new Rectangle((int)game.clyde.x+16, (int)game.clyde.y+16, 8, 8);
		
		if(blinkyBounds.intersects(crossX) || blinkyBounds.intersects(crossY))
		{
			if(!game.audio.getSound("ghostReturning").isPlaying())
			{
				game.stopBackgroundNoise();
				game.audio.getSound("ghostReturning").playAsSoundEffect(1.0f, 1.0f, true);
			}
			game.score+=500;
			game.blinky.state = "returning";
			game.blinky.newReturningPath();
		}
		
		if(pinkyBounds.intersects(crossX) || pinkyBounds.intersects(crossY))
		{
			if(!game.audio.getSound("ghostReturning").isPlaying())
			{
				game.stopBackgroundNoise();
				game.audio.getSound("ghostReturning").playAsSoundEffect(1.0f, 1.0f, true);
			}
			game.score+=500;
			game.pinky.state = "returning";
			game.pinky.newReturningPath();
		}
		
		if(inkyBounds.intersects(crossX) || inkyBounds.intersects(crossY))
		{
			if(!game.audio.getSound("ghostReturning").isPlaying())
			{
				game.stopBackgroundNoise();
				game.audio.getSound("ghostReturning").playAsSoundEffect(1.0f, 1.0f, true);
			}
			game.score+=500;
			game.inky.state = "returning";
			game.inky.newReturningPath();
		}
		
		if(clydeBounds.intersects(crossX) || clydeBounds.intersects(crossY))
		{
			if(!game.audio.getSound("ghostReturning").isPlaying())
			{
				game.stopBackgroundNoise();
				game.audio.getSound("ghostReturning").playAsSoundEffect(1.0f, 1.0f, true);
			}
			game.score+=500;
			game.clyde.state = "returning";
			game.clyde.newReturningPath();
		}
	}


	@Override
	public double getX(){return x;}
	@Override
	public double getY(){return y;}
	@Override
	public Rectangle getBounds(){return null;}
}
