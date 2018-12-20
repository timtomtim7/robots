package blue.sparse.robots;

import blue.sparse.robots.util.Skin;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static blue.sparse.robots.util.MessageUtil.color;

public class RobotItem {

	public static ItemStack create() {
		final Skin skin = Robot.getDefaultSkin();
		if(skin == null)
			return new ItemStack(Material.SKULL_ITEM);
		ItemStack item = skin.createHead();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(color("&bRobot Spawner"));
		meta.setLore(Collections.singletonList(color("&b&lRight Click Ground &bto spawn a robot!")));

		//TODO: Add way to tag items.

		item.setItemMeta(meta);
		return item;
	}

	public static boolean matches(ItemStack other) {
		//TODO: Use item tagging system to check instead.
		return other.isSimilar(create());
	}

}
