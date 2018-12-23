package blue.sparse.robots.task;

import blue.sparse.robots.Robot;
import blue.sparse.robots.pathfinding.Pathfinder;
import blue.sparse.robots.util.Animation;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.List;

public class TaskAttackNearby extends Task {

    private Creature currentTarget = null;
    private TaskFollowPath currentTask = null;
    private boolean isDone = false;

    public TaskAttackNearby(Robot robot) {
        super(robot);
    }

    private Creature getNearestVisibleCreature(double radius) {
        Location location = robot.getLocation();

        Creature nearbyCreature = null;
        double distance = Double.MAX_VALUE;

        for (Entity entity : location.getWorld().getEntities()) {
            if (!(entity instanceof Monster))
                continue;

//            if(!canSee(entity)) {
//                System.out.println("Could not see the entity.");
//                continue;
//            }

            double newDistance = entity.getLocation().distanceSquared(location);

            if (newDistance > radius * radius) {
                System.out.println("Too far away.");
                continue;
            }

            if (nearbyCreature != null && newDistance > distance) {
                System.out.println("Strange.");
                continue;
            }

            nearbyCreature = (Creature) entity;
            distance = newDistance;
        }

        return nearbyCreature;
    }

    private boolean canSee(Entity entity, int maxDistance) {
        World world = robot.getLocation().getWorld();
        Vector eyeLocation = robot.getEyeLocation().toVector();
        Vector direction = entity.getLocation().subtract(eyeLocation).toVector().normalize();
//        Vector direction = eyeLocation.subtract(entity.getLocation().toVector());

        BlockIterator iterator = new BlockIterator(world, eyeLocation, direction, 0.0, maxDistance);
        boolean canSee = true;

        while (iterator.hasNext()) {
            Block next = iterator.next();
            if (next.getType().isSolid()) {
                canSee = false;
                break;
            }
        }

        return canSee;
    }

    @Override
    public void onTick() {
        Creature creature = getNearestVisibleCreature(25.0);
        if(creature == null) {
            System.out.println("Creature was null.");
            return;
        }


        double distance = robot.getLocation().distanceSquared(creature.getLocation());

        if (distance > 2.0 * 2.0) {
            List<Block> path = Pathfinder.find(robot.getLocation().getBlock(), creature.getLocation().getBlock());
            if (!path.isEmpty() && currentTask == null || currentTask.isDone()) {
                System.out.println("Creating new task to the creature.");
                currentTask = new TaskFollowPath(robot, path);
                currentTarget = creature;
            }
        } else {
            currentTask = null;
        }

        if (distance <= 4.0 * 4.0) {
            if(creature.getNoDamageTicks() >= 1)
            System.out.println("Attacking the creature.");
            robot.animate(Animation.SWING);
            creature.setVelocity(robot.getEyeLocation().subtract(creature.getEyeLocation()).toVector().normalize());
            creature.damage(1.0);
        }

        if(currentTask != null)
            currentTask.onTick();
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
}
