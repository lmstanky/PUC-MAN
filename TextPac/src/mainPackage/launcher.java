package mainPackage;

import java.io.IOException;

public class launcher
{
	public static void main(String[] args) throws IOException
	{
		//Create initial display resources (Frame, Panel etc.)
		TextGame myGame = new TextGame();
		Thread gameThread = new Thread(myGame);
		gameThread.start();
		
	}

}
