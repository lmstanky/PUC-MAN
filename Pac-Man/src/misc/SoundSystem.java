package misc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 * This class is used for loading and playing all the game's sounds, using the Slick2D game development library
 * @author Michael Tonks
 */
public class SoundSystem
{
	public Map<String, Audio> soundEffects = new HashMap<String, Audio>();
	
	// Load all sounds into a HashMap data structure
	public void load()
	{
		try
		{
			soundEffects.put("pacDot_up", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/pacDot_up.wav")));
			soundEffects.put("pacDot_down", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/pacDot_down.wav")));
			soundEffects.put("menu_scroll", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/menu_scroll.wav")));
			soundEffects.put("game_start", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/game_start.wav")));
			soundEffects.put("pacDeath", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/pacDeath.wav")));
			soundEffects.put("pacDeath_dk", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/pacDeath_dk.wav")));
			soundEffects.put("pacDeath_laser", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/pacDeath_laser.wav")));
			soundEffects.put("ghostEaten", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/ghostEaten.wav")));
			soundEffects.put("fruit", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/fruit.wav")));
			soundEffects.put("bg1", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/bg1.wav")));
			soundEffects.put("bg2", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/bg2.wav")));
			soundEffects.put("bg3", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/bg3.wav")));
			soundEffects.put("bg4", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/bg4.wav")));
			soundEffects.put("bg5", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/bg5.wav")));
			soundEffects.put("ghostReturning", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/ghostReturning.wav")));
			soundEffects.put("ghostsFrightened", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/ghostsFrightened.wav")));
			soundEffects.put("extraLife", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/extraLife.wav")));
			soundEffects.put("powerUp", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/powerUp.wav")));
			soundEffects.put("laser_fire", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/laser_fire.wav")));
			soundEffects.put("explosion", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/explosion.wav")));
			soundEffects.put("infect", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/infect.wav")));
			soundEffects.put("highScore", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/highScore.wav")));
			soundEffects.put("confirm", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/confirm.wav")));
			soundEffects.put("mysteryShip", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/mysteryShip.wav")));
			soundEffects.put("mysteryShipDeath", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/mysteryShipDeath.wav")));
			soundEffects.put("titleMusic", AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream("sounds/titleMusic.wav")));
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Get a specific sound via its key (Which is just the file name minus the WAV extension)
	 * @param sound The String associated with the sound.
	 * @param audio The specific resource to load via a file path
	 * @return Returns the sound
	 */
	public Audio getSound(String sound)
	{
		return soundEffects.get(sound);
	}
}
