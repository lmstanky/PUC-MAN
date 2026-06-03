package entities;

import java.awt.Rectangle;

public class GameObject
{
	public double x, y, speed;
	public int i, j;
	
	public GameObject(double x, double y, double speed)
	{
		this.x = x;
		this.y = y;
		this.speed = speed;
	}
	
	public Rectangle getBounds(int off, int width, int height)
	{
		return new Rectangle((int)x+off, (int)this.y+off, width, height);
	}
}
