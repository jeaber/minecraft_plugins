package stream.jeaber;

import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
public class BlazeRodReducePlugin extends JavaPlugin implements Listener {
	// Fired when plugin is first enabled
	private static final Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {
		// log.info(String.format("[%s] Disabled Version %s",
		// getDescription().getName(), getDescription().getVersion()));
	}

	@EventHandler
	public void modifyBlazeRodRate(ItemSpawnEvent event) {
		if (event == null) {
			return;
		}
		Item item = event.getEntity();
		Material stack = item.getItemStack().getType();
		if (stack == Material.BLAZE_ROD) {
			Random generator = new Random();
			int number = generator.nextInt(101);
			if (number < 75) {
				event.setCancelled(true);
			}
		}
	}

}