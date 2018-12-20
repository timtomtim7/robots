package blue.sparse.robots.listener;

import blue.sparse.robots.RobotItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static blue.sparse.robots.util.MessageUtil.color;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();

		if (!RobotItem.matches(item) || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;

		player.getInventory().remove(item);
		Location robotSpawnLocation = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
		//TODO: Spawn real robot here

		Slime slime = robotSpawnLocation.getWorld().spawn(robotSpawnLocation, Slime.class);
		slime.setSize(5);
		slime.setCustomNameVisible(true);
		slime.setCustomName(color("&b&lTOM THE DESTROYER"));
		event.setCancelled(true);
	}
}
