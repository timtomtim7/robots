package blue.sparse.robots.commands;

import blue.sparse.robots.RobotItem;
import blue.sparse.robots.RobotsPlugin;
import com.google.common.primitives.Ints;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

import static blue.sparse.robots.util.MessageUtil.sendMessage;
import static blue.sparse.robots.util.StringUtil.color;

public class RobotsCommand implements CommandExecutor {

    private RobotsPlugin plugin = RobotsPlugin.getInstance();
    private YamlConfiguration messages = plugin.getMessagesConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("robots.admin"))
            return true;

        if(args.length <= 0) {
            sendMessage(sender, "commands.robot.help-menu");
            return true;
        }

        if(args[0].equalsIgnoreCase("give")) {
            if(args.length < 2) {
                sendMessage(sender ,"comands.robot.expecting-player");
                return true;
            }

            String playerName = args[1];
            Player player = plugin.getServer().getPlayer(playerName);

            if(player == null) {
                sender.sendMessage(color("&c&l" + playerName + " &cisn't online."));
                return true;
            }

            Integer amount = 1;

            if(args.length >= 3) {
                String amountString = args[2];
                amount = Ints.tryParse(amountString);

                if(amount == null) {
                    sender.sendMessage(color("&c&l'" + amountString + "' &cis not a valid amount."));
                    return true;
                }
            }

            Inventory inventory = player.getInventory();

            for(int i = 0; i < amount; i++) {
                inventory.addItem(RobotItem.create());
            }
            if(amount == 1)
                sender.sendMessage(color("&bGiven " + playerName + " " + amount + " robot!"));
            else
                sender.sendMessage(color("&bGiven " + playerName + " " + amount + " robots!"));
        }

        return true;
    }
}
