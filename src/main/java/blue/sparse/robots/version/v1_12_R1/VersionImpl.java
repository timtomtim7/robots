package blue.sparse.robots.version.v1_12_R1;

import blue.sparse.robots.RobotsPlugin;
import blue.sparse.robots.util.Skin;
import blue.sparse.robots.version.RobotNMS;
import blue.sparse.robots.version.VersionAdapter;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

public final class VersionImpl implements VersionAdapter {

	private final Method nbtMethod;

	public VersionImpl() throws ClassNotFoundException, NoSuchMethodException {
		Class<?> craftMetaItemClass = Class.forName("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaItem");
		nbtMethod = craftMetaItemClass.getDeclaredMethod("applyToItem", NBTTagCompound.class);
		nbtMethod.setAccessible(true);
	}

	private NBTTagCompound getNBT(ItemStack item) {
		final NBTTagCompound tag = new NBTTagCompound();

		try {
			nbtMethod.invoke(item.getItemMeta(), tag);
		} catch (IllegalAccessException | InvocationTargetException e) {
			RobotsPlugin.error(e);
		}

		return tag;
	}

	private void setNBT(ItemStack item, NBTTagCompound tag) {
		final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		nmsItem.setTag(tag);

		item.setItemMeta(CraftItemStack.getItemMeta(nmsItem));
	}

	@Override
	@Nullable
	public Skin getSkin(Player player) {
		final GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
		final Collection<Property> textures = profile.getProperties().get("textures");
		if (textures == null)
			return null;

		final Iterator<Property> iterator = textures.iterator();
		if (!iterator.hasNext())
			return null;

		final Property property = iterator.next();
		return new Skin(property.getValue(), property.getSignature());
	}

	@Override
	public ItemStack createHead(Skin skin) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		final NBTTagCompound nbt = getNBT(head);
		final NBTTagCompound skullOwner = new NBTTagCompound();
		final NBTTagCompound properties = new NBTTagCompound();
		final NBTTagList textures = new NBTTagList();
		final NBTTagCompound texture = new NBTTagCompound();
		texture.setString("Value", skin.value);
		texture.setString("Signature", skin.signature);
		textures.add(texture);
		properties.set("textures", textures);
		skullOwner.set("Properties", properties);
		skullOwner.setString("Id", UUID.randomUUID().toString()); // TODO: This might require valid UUID

		nbt.set("SkullOwner", skullOwner);
		setNBT(head, nbt);
		return head;
	}

	@Override
	public RobotNMS spawnRobot(String name, @Nullable Skin skin, Location location) {
		return new RobotImpl(name, skin, location);
	}
}
