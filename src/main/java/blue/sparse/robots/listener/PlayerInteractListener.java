package blue.sparse.robots.listener;

import blue.sparse.robots.Robot;
import blue.sparse.robots.RobotItem;
import blue.sparse.robots.pathfinding.Pathfinder;
import blue.sparse.robots.task.TaskFollowPath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerInteractListener implements Listener {

	private Block start;

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		ItemStack item = player.getItemInHand();

		if (!RobotItem.matches(item) || event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		event.setCancelled(true);

//		player.getInventory().remove(item);
		Block robotSpawnLocation = event.getClickedBlock().getRelative(event.getBlockFace());

		if (start == null) {
			start = robotSpawnLocation;
		} else {
			Block goal = robotSpawnLocation;
			final List<Block> path = Pathfinder.find(start, goal);

			if(path == null || path.isEmpty()) {
				player.sendMessage("No path");
				return;
			}

			final Robot robot = new Robot(player, "Robot", start.getLocation());
			robot.setTask(new TaskFollowPath(robot, path));
			start = null;

//			path.forEach(block -> player.sendBlockChange(block.getLocation(), Material.STAINED_GLASS, (byte)0));
//			path.forEach(block -> block.setType(Material.STAINED_GLASS));
		}



	}
}
