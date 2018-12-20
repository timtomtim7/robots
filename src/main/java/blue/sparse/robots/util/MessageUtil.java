package blue.sparse.robots.util;

import blue.sparse.robots.RobotsPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MessageUtil {
	private MessageUtil() {}

	public static String color(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public static void sendMessage(CommandSender sender, String key, String... placeholders) {
		YamlConfiguration messageConfig = RobotsPlugin.getInstance().getMessagesConfig();
		List<String> messages = messageConfig.getStringList(key);

		if(messages == null) {
			final String message = messageConfig.getString(key);
			if(message == null)
				return;
			messages = Collections.singletonList(message);
		}

		for(String message : messages) {
			sendMessageWithPlaceholders(sender, message, placeholders);
		}
	}

	private static void sendMessageWithPlaceholders(
			CommandSender sender,
			String rawMessage,
			String... placeholders
	) {
		for(int i = 0; i < placeholders.length; i += 2) {
			String key = placeholders[i];
			String value = placeholders[i + 1];
			rawMessage = rawMessage.replace(String.format("[%s]", key), value);
		}

		sender.sendMessage(color(rawMessage));
	}
}
