package blue.sparse.robots.command;

import blue.sparse.robots.RobotItem;
import blue.sparse.robots.RobotsPlugin;
import blue.sparse.robots.util.Skin;
import blue.sparse.robots.version.VersionAdapter;
import com.google.common.primitives.Ints;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;

import static blue.sparse.robots.util.MessageUtil.color;
import static blue.sparse.robots.util.MessageUtil.sendMessage;

public class RobotsCommand implements CommandExecutor {

	private RobotsPlugin plugin = RobotsPlugin.getInstance();
	private YamlConfiguration messages = plugin.getMessagesConfig();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("robots.admin"))
			return true;

		if (args.length <= 0) {
			sendMessage(sender, "command.robot.help-menu");
			return true;
		}

		/*
			args[0] = "give"
			args[1] = playerName?
			args[2] = amount?
		 */
		if (args[0].equalsIgnoreCase("give")) {
			Player player = null;
			if(sender instanceof Player) {
				player = (Player) sender;
			}else if(args.length >= 2) {
				player = plugin.getServer().getPlayer(args[1]);
			}

			if (player == null) {
				sendMessage(sender, "comands.robot.expecting-player");
				return true;
			}

			Integer amount = 1;

			if (args.length >= 3) {
				String amountString = args[2];
				amount = Ints.tryParse(amountString);

				if (amount == null) {
					sendMessage(sender, "commands.robot.give.invalid-amount");
//					sender.sendMessage(color("&c&l'" + amountString + "' &cis not a valid amount."));
					return true;
				}
			}

			Inventory inventory = player.getInventory();

			for (int i = 0; i < amount; i++) {
				inventory.addItem(RobotItem.create());
			}
			sendMessage(
					sender, "commands.robot.give.given",
					"player", player.getName(),
					"amount", amount.toString()
			);
		}

		if (args[0].equalsIgnoreCase("saveSkin")) {
			Player player = (Player) sender;
			try {
				final Skin skin = VersionAdapter.getInstance().getSkin(player);
				if(skin != null) {
					skin.writeToFile(new File(RobotsPlugin.getInstance().getDataFolder(), "test.skin"));
				}
			} catch (IOException e) {
				RobotsPlugin.error(e);
			}
		}

		return true;
	}
}
