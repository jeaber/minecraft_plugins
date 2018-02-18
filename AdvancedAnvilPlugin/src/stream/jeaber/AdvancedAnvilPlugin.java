package stream.jeaber;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class AdvancedAnvilPlugin extends JavaPlugin implements Listener {
	// Fired when plugin is first enabled
	private static final Logger log = Logger.getLogger("Minecraft");
	private static Economy econ = null;
	private static Permission perms = null;
	private static Chat chat = null;

	@Override
	public void onEnable() {
		// log.info("SUPER ANVIL ENABLED!");
		if (!setupEconomy()) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		// setupPermissions();
		// setupChat();
		getServer().getPluginManager().registerEvents(this, this);
	}

	// Fired when plugin is disabled
	@Override
	public void onDisable() {
		// log.info(String.format("[%s] Disabled Version %s",
		// getDescription().getName(), getDescription().getVersion()));
	}

	@EventHandler
	public void useCurrencyLevelChange(PlayerLevelChangeEvent event) {

	}

	@EventHandler
	public void useCurrencyOnAnvil(PrepareAnvilEvent event) {
		if (event.getInventory() == null) {
			return;
		}
		if (event.getInventory().getItem(0) == null) {
			return;
		}
		if (event.getInventory().getItem(1) == null) {
			return;
		}
		AnvilInventory inventory = (AnvilInventory) event.getInventory();

		List<HumanEntity> viewers = event.getViewers();
		if (viewers.isEmpty()) {
			return;
		}
		Player player = (Player) viewers.get(0);
		int balance = (int) econ.getBalance(player.getName());
		int cost = ((inventory.getRepairCost() + 30) / 5) + 1;
		inventory.setRepairCost(cost);
		cost = getRepairCost(inventory);
		// System.out.println("getSize(): " + inventory.getSize() + " /
		// getContents()[1].getAmount(): " + inventory.getContents()[1].getAmount());
		if (inventory.getContents()[1].getAmount() == 1) {
			// player.sendMessage(ChatColor.GREEN + "Items cost currency (FE) also!");

		}
		if (inventory.getContents()[1].getAmount() == 1) {
			// player.sendMessage(String.format("Costs " + cost + " FE to repair."));
			player.sendMessage(String.format("Costs " + cost + " FE to repair."));
		}

		if (balance < cost) {
			player.sendMessage(String.format("Costs too much. You only have " + balance + " FE."));
			inventory.setRepairCost(99);
		}

	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() == null) {
			return;
		}
		if (event.getInventory().getItem(0) == null) {
			return;
		}
		if (event.getInventory().getItem(1) == null) {
			return;
		}
		// // log.info(String.format("InventoryClickEvent Fired"));
		if ((event.getWhoClicked() instanceof Player)) {

			Player player = (Player) event.getWhoClicked();
			// // log.info(String.format(player.toString()));
			if ((event.getClickedInventory() instanceof AnvilInventory)) {
				AnvilInventory inventory = (AnvilInventory) event.getClickedInventory();
				int cost = ((inventory.getRepairCost() + 30) / 5);
				cost = getRepairCost(inventory);
				if (event.getSlot() == 0) {
					player.sendMessage(ChatColor.GREEN + "Items cost currency (FE) also!");
				}
				if (event.getSlot() == 2) {
					int balance = (int) econ.getBalance(player.getName());
					int level = (int) player.getLevel();
					int levelcost = inventory.getRepairCost();
					// player.sendMessage(String.format("XP:" + xp));
					if (balance >= cost && level >= levelcost) {
						EconomyResponse r = econ.withdrawPlayer(player, cost);
						if (r.transactionSuccess()) {
							player.sendMessage(String.format("You were charged %s and now have %s",
									econ.format(r.amount), econ.format(r.balance)));
						} else {
							player.sendMessage(String.format("An error occured: %s", r.errorMessage));
						}
					}
				}
			}
		}
	}

	public int getRepairCost(AnvilInventory inventory) {
		int cost = ((inventory.getRepairCost() + 30) / 5) + 1;
		// log.info(String.format("Basic anvil cost PREPARING!: " +
		// inventory.getRepairCost()));
		int levels = 0;
		int baseLevels = 0;
		int enchants = 0;
		int[] itemEnchCost = { 0, 0 };
		for (int i = 0; i < 2; i++) {
			ItemStack item = inventory.getContents()[i];
			Map<Enchantment, Integer> enchs = item.getEnchantments();
			int itemCost = 0;
			if (enchs.size() > 0) {
				for (Map.Entry<Enchantment, Integer> entry : enchs.entrySet()) {
					double base = (double) entry.getValue() + 2.0;
					double enchCost = Math.pow(base, 3.0) / 3;
					baseLevels += base;
					itemCost += (int) enchCost;
					enchants += enchs.size();
				}
			}
			itemEnchCost[i] = itemCost;
		}
		if (itemEnchCost[0] > itemEnchCost[1]) {
			itemEnchCost[1] = itemEnchCost[1] / 2;
		} else {
			itemEnchCost[0] = itemEnchCost[0] / 2;
		}
		cost += (itemEnchCost[0] + itemEnchCost[1]) / 2;
		// cost = cost - (enchants*baseLevels)/4;
		// cost = cost - (baseLevels*3);
		return cost;
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}
}