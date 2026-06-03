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

public class ModeSelect
{
	public String[] modeSelectOptions = {"classic", "custom"};
	public String selected = "classic";
	
	private BufferedImage cursor;
	private BufferedImage frame = null;
	private BufferedImage splash;
	
	Font font75 = null;
	Font font50 = null;
	public Font font20 = null;
	
	private int classicX = (Game.WIDTH/2)-475;
	private int classicY = Game.HEIGHT/2-50;
	
	private int customX = (Game.WIDTH/2)-475;
	private int customY = (Game.HEIGHT/2)+250;
	
	public ModeSelect(Textures textures)
	{
		this.cursor = textures.cursor;
		try
		{
			font75 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 75);
			font50 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 50);
			font20 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 20);
		} catch (FontFormatException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void render(Graphics g) throws FileNotFoundException, FontFormatException, IOException
	{
		if(frame == null)
			frame = ImageIO.read(getClass().getResource("/gui/modeselect.png"));
		if(selected == modeSelectOptions[0])
			splash = ImageIO.read(getClass().getResource("/gui/classicSplash.png"));
		else
			splash = ImageIO.read(getClass().getResource("/gui/customSplash.png"));
		g.drawImage(frame, 0, 0, null);
		g.drawImage(splash, 855, 12, null);
		
		g.setFont(font75);
		g.setColor(Color.yellow);
		g.drawString("CLASSIC", classicX, classicY);
		g.drawString("CUSTOM", customX, customY);
		g.setFont(font50);
		
		if(selected == modeSelectOptions[0])
		{
			g.drawImage(cursor, classicX-70, classicY-60, null);
			g.drawString("Play a standard", 900, 625);
			g.drawString("game of PAC-MAN", 900, 700);
			g.drawString("like it's 1980", 900, 775);
			
		}
		else if(selected == modeSelectOptions[1])
		{
			g.drawImage(cursor, customX-70, customY-55, null);
			g.drawString("Experience", 900, 600);
			g.drawString("PUC-MAN by", 900, 675);
			g.drawString("playing with", 900, 750);
			g.drawString("custom rules", 900, 825);
			g.setFont(font20);
			g.drawString("(NOTE: High Scores are not saved", 900, 900);
			g.drawString("for custom rules)", 900, 930);
		}
	}
}
