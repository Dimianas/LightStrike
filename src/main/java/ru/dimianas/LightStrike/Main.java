package ru.dimianas.LightStrike;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Random;

public final class Main extends JavaPlugin implements Listener {
    private final Random random = new Random();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        int ticks = getConfig().getInt("LightStrike.ticks");
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnLightning();
            }
        }.runTaskTimer(this, 0, ticks);
    }

    private void spawnLightning() {
        if (getConfig().getBoolean("LightStrike.enable")) {
            int radius = getConfig().getInt("LightStrike.radius");
            Location center = new Location(getServer().getWorlds().get(0), 0, 0, 0);
            int x = random.nextInt(radius * 2) - radius;
            int z = random.nextInt(radius * 2) - radius;
            Location location = center.clone().add(x, 0, z);

            int highestBlockY = Objects.requireNonNull(location.getWorld()).getHighestBlockYAt(location);
            for (int i = 0; i < 32; i++) {
                int y = highestBlockY - i;
                Location blockLocation = new Location(location.getWorld(), location.getBlockX(), y, location.getBlockZ());
                if (blockLocation.getBlock().getType().isSolid()) {
                    highestBlockY = y;
                    break;
                }
            }

            if (highestBlockY >= 32) {
                Location lightningLocation = new Location(location.getWorld(), location.getBlockX(), highestBlockY, location.getBlockZ());
                location.getWorld().strikeLightning(lightningLocation);
            }
        }
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lights") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            reloadConfig();
            saveDefaultConfig();
            Permission permission = new Permission("lights.reload");
            String message = getConfig().getString("reload-message");
            assert message != null;
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            return true;
        }
        return false;
    }
}
