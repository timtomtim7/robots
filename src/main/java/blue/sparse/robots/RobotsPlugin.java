package blue.sparse.robots;

import blue.sparse.robots.commands.RobotsCommand;
import blue.sparse.robots.listener.PlayerInteractListener;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class RobotsPlugin extends JavaPlugin {

	private static RobotsPlugin instance;
	private YamlConfiguration messagesConfig;

	@Override
	public void onEnable() {
		instance = this;
		messagesConfig = loadConfiguration("messages.yml");

		registerCommand(RobotsCommand.class);
		registerListener(PlayerInteractListener.class);
	}

	public YamlConfiguration getMessagesConfig() {
		return messagesConfig;
	}

	private YamlConfiguration loadConfiguration(String name) {
		File file = new File(getDataFolder(), name);

		if(!file.exists()) {
			saveResource(name, false);
		}

		return YamlConfiguration.loadConfiguration(file);
	}

	private void registerListener(Class<? extends Listener> listenerClass) {
		try {
			Listener listener = listenerClass.newInstance();
			getServer().getPluginManager().registerEvents(listener, this);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void registerCommand(Class<? extends CommandExecutor> commandClass) {
		String command = commandClass.getSimpleName().replace("Command", "");
		try {
			CommandExecutor instance = commandClass.newInstance();
			getCommand(command).setExecutor(instance);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static RobotsPlugin getInstance() {
		return instance;
	}
}
