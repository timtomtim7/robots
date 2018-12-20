package blue.sparse.robots.listener;

import blue.sparse.robots.Robot;
import blue.sparse.robots.RobotItem;
import blue.sparse.robots.RobotsPlugin;
import blue.sparse.robots.util.Skin;
import blue.sparse.robots.version.RobotNMS;
import blue.sparse.robots.version.VersionAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();

		if (!RobotItem.matches(item) || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		event.setCancelled(true);

		player.getInventory().remove(item);
		Location robotSpawnLocation = event.getClickedBlock().getRelative(event.getBlockFace()).getLocation();
		//TODO: Spawn real robot here

		final Robot robot = new Robot(player, "Robot", robotSpawnLocation);

//		Slime slime = robotSpawnLocation.getWorld().spawn(robotSpawnLocation, Slime.class);
//		slime.setSize(5);
//		slime.setCustomNameVisible(true);
//		slime.setCustomName(color("&b&lTOM THE DESTROYER"));
//		event.setCancelled(true);
	}
}
