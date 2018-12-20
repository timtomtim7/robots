package blue.sparse.robots.task;

import blue.sparse.robots.Robot;

public abstract class Task {

	public final Robot robot;

	public Task(Robot robot) {
		this.robot = robot;
	}

	public abstract void onTick();

	public abstract boolean isDone();

}
