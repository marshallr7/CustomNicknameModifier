package me.marshall.marshallteldaria;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;


public class EventListener implements Listener {

    private HashMap<UUID, String> playerNick = new HashMap<>();

    private final String[] nickItemLore = {"&7Click with this item and follow", "&7the instructions to modify your", "&7nickname"};

    @EventHandler
    public void onModifierItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!(item.getType().equals(Material.NAME_TAG))) {
            return;
        }
        if (!(item.hasItemMeta())) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(MarshallTeldaria.getInstance(), "CustomModifier");
        if (!(container.has(key, PersistentDataType.STRING))) {
            return;
        }
        String foundValue = container.get(key, PersistentDataType.STRING);
        if (!(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            event.setCancelled(true);
            return;
        }
        if (!(playerNick.containsKey(player.getUniqueId()))) {
            if (foundValue.equalsIgnoreCase("&2nick")) {
                int itemAmount = item.getAmount();

                if (itemAmount > 1) {
                    item.setAmount(itemAmount -1);
                } else {
                    item = new ItemStack(Material.AIR);
                }
                player.getInventory().setItemInMainHand(item);
                playerNick.put(player.getUniqueId(), "attempting");
                player.setMetadata("nickModifier", new FixedMetadataValue(MarshallTeldaria.getInstance(), "nick"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Please enter what you would like your nickname to be:"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7(Max 16 characters excluding color codes.)"));
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Type 'Cancel' to cancel."));
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou already have this modifier active."));
        }
    }

    @EventHandler
    public void modifierClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (!(item.getType() == Material.NAME_TAG)) {
            return;
        }
        if (!(item.hasItemMeta())) {
            return;
        }
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta.getDisplayName().contains("Nick Modifier")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!(player.hasMetadata("nickModifier"))) {
            return;
        }
        String input = event.getMessage();
        playerNick.replace(player.getUniqueId(), input);
        if (input.equalsIgnoreCase("cancel")) {
            nickUsage(player,"§2You have successfully cancelled the task.", "nickModifier", true);
            event.setCancelled(true);
            return;
        }
        int colorcodes = 0;
        for (int i = 0; i <= input.length() -1; i++) {
            char c = input.charAt(i);
            if (c == '&') {
                colorcodes += 2;
                if (Character.toLowerCase(input.charAt(i+1)) == 'k') {
                    nickUsage(player,"§cYou can not use &k in your nickname.", "nickModifier", true);
                    event.setCancelled(true);
                    return;
                }
            }
        }
        if (input.length()-colorcodes > 16) {
            nickUsage(player,"§cYour nickname can not be greater than 16 characters in length. §7(Excluding color codes)", "nickModifier", true);
            event.setCancelled(true);
            return;
        }

        if (input.contains(" ")) {
            nickUsage(player,"§cYour nickname can not contain spaces.", "nickModifier", true);
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2You are about to update your nickname to: " + input));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2Is this correct? &7(Yes or No)"));
        player.setMetadata("nickModifierConfirmation", new FixedMetadataValue(MarshallTeldaria.getInstance(), "nickModifierConfirmation"));
        player.removeMetadata("nickModifier", MarshallTeldaria.getInstance());
        event.setCancelled(true);
    }


    @EventHandler
    public void playerConfirmation(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        if (!(player.hasMetadata("nickModifierConfirmation"))) {
            return;
        }
        String message = event.getMessage();
        switch (Character.toLowerCase(message.charAt(0))) {
            case 'y':
                try {
                    Bukkit.getScheduler().callSyncMethod(MarshallTeldaria.getInstance(), () -> Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "nick " + player.getPlayerListName() + " " + playerNick.get(player.getUniqueId()))).get();
                } catch (Exception ignored) { }
                nickUsage(player, "§2You have successfully changed your nickname.", "nickModifierConfirmation", false);
                break;
            case 'n':
                nickUsage(player, "§2You have successfully cancelled the task.", "nickModifierConfirmation", true);
                break;
        }

        event.setCancelled(true);
    }


    private void nickUsage(Player player, String message, String metaKey, boolean addItem) {
        player.sendMessage(message);
        if (addItem) {
            player.getInventory().addItem(Items.modifierItem("&2Nick", nickItemLore));
        }
        player.removeMetadata(metaKey, MarshallTeldaria.getInstance());
        playerNick.remove(player.getUniqueId());
    }

}
