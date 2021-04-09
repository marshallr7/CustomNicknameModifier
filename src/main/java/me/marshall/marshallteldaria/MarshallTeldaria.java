package me.marshall.marshallteldaria;

import org.bukkit.plugin.java.JavaPlugin;

public final class MarshallTeldaria extends JavaPlugin {

    private static MarshallTeldaria plugin;

    public static MarshallTeldaria getInstance() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        getCommand("nickitem").setExecutor(new Commands());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
