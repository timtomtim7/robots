package blue.sparse.robots.pathfinding;

import blue.sparse.robots.util.SortedList;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Crops;

import java.util.*;

public class Pathfinder {

	private static final BlockFace[] faces = BlockFace.values();

	private static float heuristic(Block a, Block b) {
		return heuristic(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
	}

	private static float heuristic(int ax, int ay, int az, int bx, int by, int bz) {
		int dx = Math.abs(ax - bx);
		int dy = Math.abs(ay - by);
		int dz = Math.abs(az - bz);

		return dx + dy + dz;
	}

	public static List<Block> find(Block origin, Block goal) {
//		final Set<Node> open = new HashSet<>();
		final long startTime = System.currentTimeMillis();
		if (origin.getWorld() != goal.getWorld())
			return Collections.emptyList();

		final SortedList<Node> open = new SortedList<>();
		final Node start = new Node(heuristic(origin, goal), 0f, origin, null);
		open.add(start);

		final Set<Node> closed = new HashSet<>();

		while (open.size() > 0) {
			if(System.currentTimeMillis() - startTime > 500L)
				return Collections.emptyList();

			final Node current = open.pop();
			if (current.block.equals(goal))
				return current.reconstructPath(origin.getWorld());

			closed.add(current);
			final Node[] neighbors = current.getNeighbors(goal);
			for (Node n : neighbors) {
				if (n == null)
					continue;

				if (closed.contains(n))
					continue;

				final Node old = open.get(n);
				if (old == null) {
					open.add(n);
					continue;
				} else if (n.g > old.g) {
					continue;
				}

				old.parent = current;
				old.g = n.g;
				open.update(open.indexOf(old));
			}
		}

		return Collections.emptyList();
	}

	private static final class Node implements Comparable<Node> {
		public final Block block;

		public final float h;
		public float g;

		public Node parent;

		public Node(float h, float g, Block block, Node parent) {
			this.h = h;
			this.g = g;
			this.block = block;
			this.parent = parent;
		}

		public float getF() {
			return h + g;
		}

		public Node[] getNeighbors(Block goal) {
			Node[] result = new Node[6];
			for (int i = 0; i < 6; i++) {
				Block relative = block.getRelative(faces[i]);
				if (relative.getType().isSolid())
					continue;

				if(relative.getRelative(BlockFace.UP).getType().isSolid())
					continue;

				if(
						!block.getRelative(BlockFace.DOWN).getType().isSolid()
								&& !relative.getRelative(BlockFace.DOWN).getType().isSolid()
								&& faces[i] != BlockFace.DOWN
				)
						continue;

				float cost = 1f;

				if(relative.getType() == Material.WATER || relative.getType() == Material.STATIONARY_WATER)
					cost *= 100f;

				if(relative.getType() == Material.LAVA || relative.getType() == Material.STATIONARY_LAVA)
					cost *= 100f;

				if (parent != null) {
					BlockFace face = parent.block.getFace(block);
					if (face != faces[i])
						cost *= 1.5f;
				}

				result[i] = new Node(heuristic(block, goal), g + cost, relative, this);
			}

			return result;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Node)) return false;
			return block.equals(((Node) o).block);
		}

		@Override
		public int hashCode() {
			return block.hashCode();
		}

		@Override
		public int compareTo(Node o) {
			return Float.compare(getF(), o.getF());
		}

		public List<Block> reconstructPath(World world) {
			final ArrayList<Block> target = new ArrayList<>();
			Node current = this;
			while(current != null) {
				target.add(current.block);
				current = current.parent;
			}
			Collections.reverse(target);
			return target;
		}

//		private void reconstructPath(World world, List<Block> target) {
//			if (parent != null)
//				parent.reconstructPath(world, target);
//
//			target.add(block);
//		}
	}

}
