package blue.sparse.robots.version.v1_12_R1;

import blue.sparse.robots.RobotsPlugin;
import blue.sparse.robots.util.Animation;
import blue.sparse.robots.util.Skin;
import blue.sparse.robots.version.RobotNMS;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Stream;

public class RobotImpl implements RobotNMS {

	private World world;
	private EntityPlayer nms;

	RobotImpl(String name, @Nullable Skin skin, Location location) {
		this.world = location.getWorld();

		final WorldServer nmsWorld = ((CraftWorld) world).getHandle();
		final MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();

		final GameProfile profile = new GameProfile(UUID.randomUUID(), name);
		if (skin != null)
			profile.getProperties().put("textures", new Property("textures", skin.value, skin.signature));

		nms = new EntityPlayer(nmsServer, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
		nms.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	@Override
	public void setVisible(Player player) {
		sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nms));
		sendPacket(player, new PacketPlayOutNamedEntitySpawn(nms));
		createLookPackets().forEach(packet -> sendPacket(player, packet));

		Bukkit.getScheduler().scheduleSyncDelayedTask(RobotsPlugin.getInstance(), () -> {
			sendPacket(player, new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, nms));
		});
	}

	@Override
	public void setInvisible(Player player) {
		sendPacket(player, new PacketPlayOutEntityDestroy(getID()));
	}

	@Override
	public int getID() {
		return nms.getId();
	}

	@Override
	public Location getLocation() {
		return new Location(world, nms.locX, nms.locY, nms.locZ, nms.yaw, nms.pitch);
	}

	@Override
	public Location getEyeLocation() {
		return getLocation().add(0.0, isSneaking() ? 1.54 : 1.62, 0.0);
	}

	@Override
	public String getName() {
		return nms.getName();
	}

	@Override
	public boolean isSneaking() {
		return nms.getFlag(1);
	}

	@Override
	public void setSneaking(boolean sneaking) {
		nms.setFlag(1, sneaking);
	}

	@Override
	public boolean isSprinting() {
		return nms.getFlag(2);
	}

	@Override
	public void setSprinting(boolean sprinting) {
		nms.setFlag(2, sprinting);
	}

	@Nullable
	@Override
	public ItemStack getHeldItem() {
		return CraftItemStack.asBukkitCopy(nms.getEquipment(EnumItemSlot.MAINHAND));
	}

	@Override
	public void setHeldItem(@Nullable ItemStack heldItem) {
		final net.minecraft.server.v1_12_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(heldItem);
		nms.setEquipment(EnumItemSlot.MAINHAND, nmsItem);
		sendPacketToNearbyPlayers(new PacketPlayOutEntityEquipment(getID(), EnumItemSlot.MAINHAND, nmsItem));
	}

	@Override
	public void teleport(double x, double y, double z) {
		nms.locX = x;
		nms.locY = y;
		nms.locZ = z;
		sendPacketToNearbyPlayers(new PacketPlayOutEntityTeleport(nms));
	}

	@Override
	public void teleport(double x, double y, double z, float yaw, float pitch) {
		nms.yaw = yaw;
		nms.yaw = pitch;
		teleport(x, y, z);
	}

	@Override
	public void moveTo(double x, double y, double z) {
		long deltaX = (long) ((x * 32 - nms.locX * 32) * 128);
		long deltaY = (long) ((y * 32 - nms.locY * 32) * 128);
		long deltaZ = (long) ((z * 32 - nms.locZ * 32) * 128);
		nms.locX = x;
		nms.locY = y;
		nms.locZ = z;

		sendPacketToNearbyPlayers(new PacketPlayOutEntity.PacketPlayOutRelEntityMove(getID(), deltaX, deltaY, deltaZ, nms.onGround));
	}

	@Override
	public void moveTo(double x, double y, double z, float yaw, float pitch) {
		long deltaX = (long) ((x * 32 - nms.locX * 32) * 128);
		long deltaY = (long) ((y * 32 - nms.locY * 32) * 128);
		long deltaZ = (long) ((z * 32 - nms.locZ * 32) * 128);
		byte byteYaw = (byte) (yaw * 256.0F / 360.0F);
		byte bytePitch = (byte) (pitch * 256.0F / 360.0F);
		nms.locX = x;
		nms.locY = y;
		nms.locZ = z;
		nms.yaw = yaw;
		nms.pitch = pitch;

		sendPacketToNearbyPlayers(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(getID(), deltaX, deltaY, deltaZ, byteYaw, bytePitch, nms.onGround));
	}

	@Override
	public void look(float yaw, float pitch) {
		nms.yaw = yaw;
		nms.pitch = pitch;
		createLookPackets().forEach(this::sendPacketToNearbyPlayers);
	}

	@Override
	public void animate(Animation animation) {
		if (animation == Animation.DAMAGE) {
			sendPacketToNearbyPlayers(new PacketPlayOutAnimation(nms, 1));
		} else if (animation == Animation.SWING) {
			sendPacketToNearbyPlayers(new PacketPlayOutAnimation(nms, 0));
		}
	}

	private Stream<Player> getNearbyPlayers() {
		final Location location = getLocation();
		final Location playerLocation = getLocation();
		return world
				.getEntitiesByClass(Player.class)
				.stream()
				.filter(player -> player.getLocation(playerLocation).distance(location) < 80.0 * 80.0);
	}

	private void sendPacket(Player player, Packet<?> packet) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	private void sendPacketToNearbyPlayers(Packet<?> packet) {
		getNearbyPlayers().forEach(player -> sendPacket(player, packet));
	}

	private Collection<Packet<?>> createLookPackets() {
		byte byteYaw = (byte) (nms.yaw * 256.0F / 360.0F);
		byte bytePitch = (byte) (nms.pitch * 256.0F / 360.0F);

		return Arrays.asList(
				new PacketPlayOutEntity.PacketPlayOutEntityLook(getID(), byteYaw, bytePitch, true),
				new PacketPlayOutEntityHeadRotation(nms, byteYaw)
		);
	}
}
