package me.marshall.marshallteldaria;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class Items {

    public static ItemStack modifierItem(String modifierType, String[] loreInfo) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', modifierType + " Modifier"));
        List<String> lore = new ArrayList<>();
        for (int i = 0; i <= loreInfo.length - 1; i++) {
            lore.add(ChatColor.translateAlternateColorCodes('&', loreInfo[i]));
        }
        NamespacedKey key = new NamespacedKey(MarshallTeldaria.getInstance(), "CustomModifier");
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, modifierType);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }
}
