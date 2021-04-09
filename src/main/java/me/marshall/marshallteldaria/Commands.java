package me.marshall.marshallteldaria;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
    // CMD nickitem give <player>
    // ARGS null     0      1
    // LEN    0      1      2
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] nickItemLore = {"&7Click with this item and follow", "&7the instructions to modify your", "&7nickname"};
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("teldaria.modifiers.give")) {
                if (args.length != 2) {
                    player.sendMessage("§CCorrect command usage: §f/nickitem give <player>");
                } else {
                    Player target = Bukkit.getPlayer(args[1]);
                    if (!target.isOnline()) {
                        player.sendMessage("§cThat player is not online.");
                    }  else {
                        target.getInventory().addItem(Items.modifierItem("&2Nick", nickItemLore));
                        player.sendMessage("§2You have successfully given a nick modifier to " + target.getPlayerListName());
                    }
                }
            } else {
                player.sendMessage("§cYou do not have permission to use this command.");
            }
        }






        return true;
    }
}
