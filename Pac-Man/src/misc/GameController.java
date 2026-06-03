package misc;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

public class GameController
{
	public Controller sanwa = null;
	public Controller xbox = null;
	public Component[] components;
	public GameController()
	{
		ControllerEnvironment ce = ControllerEnvironment.getDefaultEnvironment();
		Controller[] controllers = ce.getControllers();
		for(Controller c : controllers)
		{
			if(c.getType() == Controller.Type.STICK)
			{
				sanwa = c;
				components = sanwa.getComponents();
				break;
				
				// components[6] = Green button
				// components[7] = Red button
				// components[8] = Right
				// components[9] = Down
				// components[10] = Left
				// components[11] = Up
			}
			
			else if(c.getType() == Controller.Type.GAMEPAD)
			{
				xbox = c;
				components = xbox.getComponents();
				break;
				
				// components[5] = A
				// components[6] = B
				// components[7] = X
				// components[8] = Y
			}
		}
		
	}
	
	public boolean isPressed(GameController gc)
	{
		for(int i = 0; i < gc.components.length; i++)
		{
			if(gc.components[i].getPollData() > 0)
				return true;
		}
		
		return false;
	}
}
