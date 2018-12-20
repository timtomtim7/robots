package blue.sparse.robots;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;

import static blue.sparse.robots.util.StringUtil.color;

public class RobotItem {

    public static ItemStack create() {
        ItemStack item = new ItemStack(Material.SKULL_ITEM);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(color("&bRobot Spawner"));
        meta.setLore(Arrays.asList(color("&b&lRight Click Ground &bto spawn a robot!")));

        //TODO: Add way to tag items.

        item.setItemMeta(meta);
        return item;
    }

    public static boolean matches(ItemStack other) {
        //TODO: Use item tagging system to check instead.
        return other.isSimilar(create());
    }

}
