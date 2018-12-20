package blue.sparse.robots.version;

import blue.sparse.robots.util.Animation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public interface RobotNMS {

	String getName();

	int getID();

	void setVisible(Player player);
	void setInvisible(Player player);

	boolean isSneaking();
	void setSneaking(boolean sneaking);

	boolean isSprinting();
	void setSprinting(boolean sprinting);

//	@Nullable ItemStack getHelmet();
//	void setHelmet(@Nullable ItemStack helmet);
//
//	@Nullable ItemStack getChestplate();
//	void setChestplate(@Nullable ItemStack chestplate);
//
//	@Nullable ItemStack getLeggings();
//	void setLeggings(@Nullable ItemStack leggings);
//
//	@Nullable ItemStack getBoots();
//	void setBoots(@Nullable ItemStack boots);

	@Nullable ItemStack getHeldItem();
	void setHeldItem(@Nullable ItemStack heldItem);

	Location getLocation();
	Location getEyeLocation();

	void teleport(double x, double y, double z);
	void teleport(double x, double y, double z, float yaw, float pitch);

	void moveTo(double x, double y, double z);
	void moveTo(double x, double y, double z, float yaw, float pitch);

	void look(float yaw, float pitch);

	void animate(Animation animation);

}
