package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.newdawn.slick.util.ResourceLoader;

import main.Game;
import main.Textures;

public class MainMenu
{
	public String[] mainMenuOptions = {"play", "quit", "credits"};
	public int selected = 0;
	public Font font100;
	
	private BufferedImage banner, bigPac;
	private BufferedImage frame = null;
	
	private int playX = (Game.WIDTH/2)-275;
	private int playY = Game.HEIGHT/2;
	
	public MainMenu(Textures textures)
	{
		this.banner = textures.banner;
		this.bigPac = textures.bigPac;
	}

	public void render(Graphics g) throws FileNotFoundException, FontFormatException, IOException
	{
		if(frame == null)
			frame = ImageIO.read(getClass().getResource("/gui/fs.png"));
		g.drawImage(frame, 0, 0, null);
		font100 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 100);
		Font font50 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 50);
		g.setFont(font100);
		g.setColor(Color.yellow);
		g.drawString("PUC-MAN", (Game.WIDTH/2)-150, (Game.HEIGHT/2)-200);
		
		g.setFont(font50);
		g.drawString("PRESS", playX, playY);
		g.setColor(Color.green);
		g.drawString("GREEN", playX+300, playY);
		g.setColor(Color.yellow);
		g.drawString("TO PLAY", playX+600, playY);
		
		g.drawImage(banner, 100, 700, null);
		g.drawImage(bigPac, 1000, 600, null);
		
	}
	
}
