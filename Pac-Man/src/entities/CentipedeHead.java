package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

import main.Game;

public class CentipedeHead
{
	Game game;
	String state = "dormant";
	double x = -64;
	double y = 500;
	BufferedImage icon;
	
	int shootCooldown = 0;
	int activeCount = 420;
	
	public CentipedeHead(Game game)
	{
		this.game = game;
		icon = game.textures.centipedeHead;
	}
	
	public void tick()
	{
		if(state == "dormant")
		{
			if(x < 300)
				x+=3;
			else
				state = "active";
		}
		
		else if(state == "active")
		{
			if(y < game.pacMan.y) y+=3;
			else if(y > game.pacMan.y) y-=3;
			else y = game.pacMan.y;
			if(shootCooldown == 0)
			{
				shootCooldown = ThreadLocalRandom.current().nextInt(30, 120);
				shoot();
				game.audio.soundEffects.get("laser_fire").playAsSoundEffect(1.0f, 1.0f, false);
			}
			shootCooldown--;
		}
		
		else if(state == "finished")
		{
			if(x > -64)
				x-=2;
			else game.c.entities.remove(this);
		}
		if(activeCount == 0)
			state = "finished";
		else if(state == "active") activeCount--;
	}
	
	public void render(Graphics g)
	{
		g.drawImage(icon, (int)x, (int) y, null);
	}
	
	private void shoot()
	{
		Bullet bullet = new Bullet(x+4, y, 1, "right", game.textures.bulletHoriz, 0, true, game);
		game.c.entities.add(bullet);
	}
}
