package mainPackage;

import java.util.TimerTask;
import mainPackage.TextGame;

public class inedibleGhostsTask extends TimerTask
{

	@Override
	public void run()
	{
		for(int i = 0; i < 4; i++)
			TextGame.Ghosts[i].edible = false;
		TextGame.Ghosts[0].icon = "B";
		TextGame.Ghosts[1].icon = "P";
		TextGame.Ghosts[2].icon = "I";
		TextGame.Ghosts[3].icon = "C";
	}

}
