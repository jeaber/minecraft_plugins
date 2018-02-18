package stream.jeaber;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.PlayerInventory;
public class AdvancedArmorPlugin extends JavaPlugin implements Listener {
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
	public void modifySneakSpeed(PlayerToggleSneakEvent event) {
		if (event == null) {
			return;
		}
		Player player = event.getPlayer();
		if (player.getInventory() == null) {
			return;
		}
		PlayerInventory inventory = player.getInventory();
		double increase = 0.0;
		if (inventory.getHelmet() != null) {
			Material helm = inventory.getHelmet().getType();
			if (helm != null && helm == Material.LEATHER_HELMET) {
				//System.out.println("LEATHER HELM");
				increase = increase + .03;
			}
		}
		if (inventory.getChestplate() != null) {
			Material chest = inventory.getChestplate().getType();
			if (chest != null && chest == Material.LEATHER_CHESTPLATE) {
				//System.out.println("LEATHER CHESTPLATE");
				increase = increase + .06;
			}
		}
		if (inventory.getLeggings() != null) {
			Material legs = inventory.getLeggings().getType();
			if (legs != null && legs == Material.LEATHER_LEGGINGS) {
				//System.out.println("LEATHER LEGGINGS");
				increase = increase + .05;
			}
		}
		if (inventory.getBoots() != null) {
			Material boots = inventory.getBoots().getType();
			if (boots != null && boots == Material.LEATHER_BOOTS) {
				//System.out.println("LEATHER_BOOTS");
				increase = increase + .04;
			}
		}
		if (event.isSneaking()) {
			//System.out.println("PlayerToggleSneakEvent! Player IS SNEAKING!");
			float speed = player.getWalkSpeed();
			if (increase > 0.0 && speed < .4F) {
				float newSpeed = (float) (speed + increase);
				////System.out.println("New walk speed! " + speed + " -> " + newSpeed);
				player.setWalkSpeed(newSpeed);
			}
		} else {
			//System.out.println("PlayerToggleSneakEvent! Player IS NOT sneaking!");
			player.setWalkSpeed(0.2F);
		}
	}

	@EventHandler
	public void modifyDamage(EntityDamageEvent event) {
		// String cause = event.getCause();
		if (event.getCause() == DamageCause.PROJECTILE) {
			////System.out.println("Cause getCause(): " + event.getCause() + event.getCause().toString());
			Entity entity = event.getEntity();
			if (entity instanceof Player) {
				Player player = (Player) event.getEntity();
				PlayerInventory inventory = player.getInventory();
				double reduction = 0.0;
				if (inventory.getHelmet() != null) {
					Material helm = inventory.getHelmet().getType();
					if (helm != null && (helm == Material.IRON_HELMET || helm == Material.GOLD_HELMET || helm == Material.DIAMOND_HELMET)) {
						////System.out.println("HELM");
						reduction = reduction + .07;
					}
				}
				if (inventory.getChestplate() != null) {
					Material chest = inventory.getChestplate().getType();
					if (chest != null && (chest == Material.IRON_CHESTPLATE || chest == Material.GOLD_CHESTPLATE
							|| chest == Material.DIAMOND_CHESTPLATE)) {
						////System.out.println("LEATHER CHESTPLATE");
						reduction = reduction + .1;
					}
				}
				if (inventory.getLeggings() != null) {
					Material legs = inventory.getLeggings().getType();
					if (legs != null && (legs == Material.IRON_LEGGINGS || legs == Material.GOLD_LEGGINGS
							|| legs == Material.DIAMOND_LEGGINGS)) {
						////System.out.println("LEATHER LEGGINGS");
						reduction = reduction + .08;
					}
				}
				if (inventory.getBoots() != null) {
					Material boots = inventory.getBoots().getType();
					if (boots != null && (boots == Material.IRON_BOOTS || boots == Material.GOLD_BOOTS || boots == Material.DIAMOND_BOOTS)) {
						////System.out.println("LEATHER_BOOTS");
						reduction = reduction + .06;
					}
				}
				if (reduction > 0.0) {
					double damage = event.getDamage();
					double newDamage = damage - (damage * reduction);
					////System.out.println("New Damage : " + damage + " -> " + newDamage);
					event.setDamage(newDamage);
				}
			}
		}
	}

}