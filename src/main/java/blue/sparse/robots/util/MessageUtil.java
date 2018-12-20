package blue.sparse.robots.util;

import blue.sparse.robots.RobotsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import static blue.sparse.robots.util.StringUtil.color;

public class MessageUtil {

    public static void sendMessage(CommandSender sender, String key, String... placeholders) {
        YamlConfiguration messages = RobotsPlugin.getInstance().getMessagesConfig();

        if(messages.getString(key) == null) {
            for(String m : messages.getStringList(key)) {

                sender.sendMessage(color(m));
            }
        } else {
            String value = messages.getString(key);

            sender.sendMessage(color(messages.getString(key)));
        }

    }

}
