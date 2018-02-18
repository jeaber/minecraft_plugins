package stream.jeaber;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.entity.Player;

public class SpeedChange extends TimerTask  {
	private final Player player;
	private final float speed;
	
	SpeedChange (Player player, float speed) {
		this.player = player;
		this.speed = speed;
	}
    @Override
    public void run() {
    	player.setWalkSpeed(speed);
        // Implement your Code here!
    }
}
