package blue.sparse.robots.task;

import blue.sparse.robots.Robot;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

public class TaskFollowPath extends Task {

	private List<Block> path;
	private int i = 0;
	private float progress = 0f;

	public TaskFollowPath(Robot robot, List<Block> path) {
		super(robot);
		this.path = path;
		robot.teleport(path.get(0).getLocation());
	}

	@Override
	public void onTick() {
		if(i + 1 >= path.size())
			return;
		Block current = path.get(i);
		Block next = path.get(i + 1);

		final Location a = current.getLocation().add(0.5, 0.0, 0.5);
		final Location b = next.getLocation().add(0.5, 0.0, 0.5);

		final Location newLocation = b.clone().subtract(a).multiply(progress).add(a);

		final Vector direction = newLocation
				.toVector()
				.add(new Vector(0.0, 1.62, 0.0))
				.subtract(robot.getEyeLocation().toVector())
				.normalize();

		newLocation.setDirection(direction);
		robot.shortRangeTeleport(newLocation);

		progress += 0.2f;
		if(progress >= 1f) {
			i++;
			progress %= 1f;
		}
	}

	@Override
	public boolean isDone() {
		return i + 1 >= path.size();
	}
}
