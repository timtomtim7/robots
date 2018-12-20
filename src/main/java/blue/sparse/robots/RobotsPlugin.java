package blue.sparse.robots;

import blue.sparse.robots.command.RobotsCommand;
import blue.sparse.robots.listener.PlayerInteractListener;
import blue.sparse.robots.util.ErrorHandler;
import blue.sparse.robots.version.VersionAdapter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class RobotsPlugin extends JavaPlugin {

	private static VersionAdapter versionAdapter;
	private static ErrorHandler errorHandler;
	private static RobotsPlugin instance;
	private YamlConfiguration messagesConfig;

	private static String getVersion() {
		return Bukkit
				.getServer()
				.getClass()
				.getPackage()
				.getName()
				.split("\\.")[3];

	}

	public static VersionAdapter getVersionAdapter() {
		return versionAdapter;
	}

	public static RobotsPlugin getInstance() {
		return instance;
	}

	public static void error(Throwable error) {
		errorHandler.logError(error);
	}

	@Override
	public void onEnable() {
		instance = this;
		errorHandler = new ErrorHandler(new File(getDataFolder(), "errors"), getLogger());

		final String version = getVersion();
		try {
			versionAdapter = (VersionAdapter) Class
					.forName(String.format(
							"blue.sparse.robots.version.%s.VersionImpl",
							version
					))
					.getDeclaredConstructor()
					.newInstance();
			getLogger().info("Plugin enabled with version adapter " + version);
		} catch (ReflectiveOperationException e) {
			errorHandler.logError(e);
			getLogger().severe(String.format(
					"Plugin does not support this version (%s), disabling.",
					version
			));
			getServer().getPluginManager().disablePlugin(this);
		}

		messagesConfig = loadConfiguration("messages.yml");

		registerCommand(RobotsCommand.class);
		registerListener(PlayerInteractListener.class);

		getServer().getScheduler().scheduleSyncRepeatingTask(this, Robot::tickAll, 1L, 1L);
	}

	public YamlConfiguration getMessagesConfig() {
		return messagesConfig;
	}

	private YamlConfiguration loadConfiguration(String name) {
		File file = new File(getDataFolder(), name);

		if (!file.exists()) {
			saveResource(name, false);
		}

		return YamlConfiguration.loadConfiguration(file);
	}

	private void registerListener(Class<? extends Listener> listenerClass) {
		try {
			Listener listener = listenerClass.getDeclaredConstructor().newInstance();
			getServer().getPluginManager().registerEvents(listener, this);
		} catch (ReflectiveOperationException e) {
			error(e);
		}
	}

	private void registerCommand(Class<? extends CommandExecutor> commandClass) {
		String command = commandClass.getSimpleName().replace("Command", "");
		try {
			CommandExecutor instance = commandClass.getDeclaredConstructor().newInstance();
			getCommand(command).setExecutor(instance);
		} catch (ReflectiveOperationException e) {
			error(e);
		}
	}

}
