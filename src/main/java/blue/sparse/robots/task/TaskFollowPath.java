package blue.sparse.robots.task;

import blue.sparse.robots.Robot;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;

public class TaskFollowPath extends Task {

	private List<Block> path;
	private int i = 0;
	private int ticks = 0;

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

		float progress = ticks / 5f;
		final Location newLocation = b.clone().subtract(a).multiply(progress).add(a);
		robot.shortRangeTeleport(newLocation);

		ticks++;
		if(progress >= 1f)
			ticks = 0;
	}

	@Override
	public boolean isDone() {
		return i + 1 >= path.size();
	}
}
