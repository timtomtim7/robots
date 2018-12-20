package blue.sparse.robots.version;

import blue.sparse.robots.util.Skin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public interface VersionAdapter {

	@Nullable
	Skin getSkin(Player player);

	ItemStack createHead(Skin skin);

	RobotNMS spawnRobot(String name, @Nullable Skin skin, Location location);

}
