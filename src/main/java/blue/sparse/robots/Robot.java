package blue.sparse.robots;

import blue.sparse.robots.task.Task;
import blue.sparse.robots.task.TaskAttackNearby;
import blue.sparse.robots.util.Animation;
import blue.sparse.robots.util.Skin;
import blue.sparse.robots.version.RobotNMS;
import blue.sparse.robots.version.VersionAdapter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Robot {

	public final UUID ownerID;
	private RobotNMS nms;

	private Set<Player> visibleTo = new HashSet<>();

	private Task task;

	private Stream<Player> getNearbyPlayers() {
		final Location location = getLocation();
		final Location playerLocation = getLocation();
		return location.getWorld()
				.getEntitiesByClass(Player.class)
				.stream()
				.filter(player -> player.getLocation(playerLocation).distance(location) < 80.0 * 80.0);
	}

	public Robot(Player owner, String name, Location location) {
		ownerID = owner.getUniqueId();
		nms = VersionAdapter.getInstance().spawnRobot(name, getDefaultSkin(), location);
		robots.add(this);
	}

	public void teleport(Location location) {
		nms.teleport(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	public void shortRangeTeleport(Location location) {
		nms.moveTo(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	private void onTick() {
		handleVisibility();
		handleTasks();
		if(getTask() == null || getTask().isDone())
			setTask(new TaskAttackNearby(this));

	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}


	public Location getLocation() {
		return nms.getLocation();
	}

	public Location getEyeLocation() {
		return nms.getEyeLocation();
	}

	public static Skin getDefaultSkin() {
		try {
			final InputStream resource = RobotsPlugin.getInstance().getResource("default.skin");
			return Skin.fromInputStream(resource);
		} catch (IOException e) {
			RobotsPlugin.error(e);
			return null;
		}
	}

	public void animate(Animation animation) {
		nms.animate(animation);
	}

	private static Set<Robot> robots = new HashSet<>();

	private void handleTasks() {
		if(task != null) {
			if(task.isDone())
				task = null;
			else
				task.onTick();
		}
	}

	private void handleVisibility() {
		final Set<Player> players = getNearbyPlayers().collect(Collectors.toSet());

		final Set<Player> newVisible = new HashSet<>(players);
		newVisible.removeAll(visibleTo);
		newVisible.removeIf(player -> System.currentTimeMillis() - player.getLastPlayed() < 10000L);

		final Set<Player> notVisible = new HashSet<>(visibleTo);
		notVisible.removeAll(players);
		notVisible.removeIf(player -> !player.isOnline());

		newVisible.forEach(player -> nms.setVisible(player));
		visibleTo.addAll(newVisible);

		notVisible.forEach(player -> nms.setInvisible(player));
		visibleTo.removeAll(notVisible);
	}

	static void tickAll() {
		robots.forEach(Robot::onTick);
	}

}
