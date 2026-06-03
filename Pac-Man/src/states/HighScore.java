package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.newdawn.slick.util.ResourceLoader;

import main.Textures;
import misc.SoundSystem;

public class HighScore
{
	public String[] alphabet = {
	"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
	
	public int selectedInitial = 0;
	public int selectedValue0 = 0;
	public int selectedValue1 = 0;
	public int selectedValue2 = 0;
	int score;
	public int savingCount = 180;
	Font font100;
	Font font30;
	public boolean saving = false;
	SoundSystem audio;
	Textures textures;
	BufferedImage banner;
	BufferedImage bigPac;
	
	public HighScore(Textures textures, SoundSystem audio, int score)
	{
		this.audio = audio;
		this.score = score;
		this.textures = textures;
		try
		{
			font100 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 100);
			font30 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 30);
		} catch (FontFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void render(Graphics g)
	{
		if(!audio.soundEffects.get("highScore").isPlaying())
			audio.soundEffects.get("highScore").playAsMusic(1.0f, 2.0f, true);
		
		g.setFont(font30);
		g.drawImage(textures.fs, 0, 0, null);
		g.setColor(Color.yellow);
			g.drawString("HIGH SCORE: " + score, 650, 200);
			g.drawString("Enter Initials:", 650, 300);
			g.setFont(font100);
			g.drawString(alphabet[selectedValue0], 700, 600);
			g.drawString(alphabet[selectedValue1], 825, 600);
			g.drawString(alphabet[selectedValue2], 950, 600);
		
			if(selectedInitial == 0 && !saving)
			{
				g.drawImage(textures.upArrow, 725, 425, null);
				g.drawImage(textures.downArrow, 725, 625, null);
			}
			
			else if(selectedInitial == 1 && !saving)
			{
				g.drawImage(textures.upArrow, 850, 425, null);
				g.drawImage(textures.downArrow, 850, 625, null);
			}
			
			else if(selectedInitial == 2 && !saving)
			{
				g.drawImage(textures.upArrow, 975, 425, null);
				g.drawImage(textures.downArrow, 975, 625, null);
			}
		// Hang in there, Michael. You will be ok. I promise. Stay strong, carry on <3 
	}
	
	public void saveScore()
	{
		if(!saving)
		{
			saving = true;
			audio.soundEffects.get("confirm").playAsSoundEffect(1.0f, 1.0f, false);
			
			try
			{
			File file = new File("");
			String path = file.getAbsolutePath();
			OutputStream outputStream = new FileOutputStream(path + "/highScore.txt");
			//OutputStream outputStream = new FileOutputStream("C:/Users/mikgo/Desktop/Dissertation/Pac-Man/highScore.txt");
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);
			outputStreamWriter.write(alphabet[selectedValue0] + alphabet[selectedValue1] + alphabet[selectedValue2]
					+ " " + score);
			outputStreamWriter.close();
			}catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
