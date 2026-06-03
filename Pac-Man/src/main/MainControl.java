package main;

import java.awt.Graphics;
import java.util.LinkedList;

import collision.Entity;
import entities.Ghost;

public class MainControl
{
	public LinkedList<Entity> entities = new LinkedList<Entity>();
	public LinkedList<Ghost> ghosts = new LinkedList<Ghost>();

	
	Entity enta;
	Ghost ghost;
	Game game;
	
	public MainControl(Game game)
	{
		this.game = game;
	}
	
	public void tick()
	{
		// Tick A Classes
		for(int i = 0; i < entities.size(); i++)
		{
			enta = entities.get(i);
			enta.tick();
			if(enta.getX() < -30 || enta.getY() < -30 || enta.getX() > 1800 || enta.getY() > 1000)
				entities.remove(i);
		}
		
		// Tick B Classes
		for(int i = 0; i < ghosts.size(); i++)
		{
			/* The primary tick affects all the counters, so the entity still stays in each state for the appropriate amount of time
			* The secondary tick does everything the primary one does, only it doesn't decrease the counters
			* This is so the Ghosts can move robustly at speeds higher than 2, simply just by doing more ticks
			*/
			ghost = ghosts.get(i);
			for(int j = 0; j < ghost.speed; j++)
			{
				ghost.tick();
				ghost.secondaryTick = true;
			}
			ghost.secondaryTick = false;
		}			
	}
	
	public void render(Graphics g)
	{
		// Render A Classes
		for(int i = 0; i < entities.size(); i++)
		{
			enta = entities.get(i);
			if(enta != null)
				enta.render(g);
		}
		
		// Render B Classes
		for(int i = 0; i < ghosts.size(); i++)
		{
			ghost = ghosts.get(i);
			ghost.render(g);
		}
	}
}
