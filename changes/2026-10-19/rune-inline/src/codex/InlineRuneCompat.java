package codex;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/** Changes only Lore line grouping. YeeGem owns all inlay state and attributes. */
public final class InlineRuneCompat extends JavaPlugin implements Listener {
    public static final String HOLE = "§7「§8✰§7」";
    private final Set<UUID> pending = new HashSet<UUID>();
    private final Set<String> backed = new HashSet<String>();

    @Override public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Inline rune layout 1.2 enabled; original items are backed up before a Lore change.");
    }
    private boolean page(InventoryView view) { return "§3§l符文页面".equals(view.getTitle()); }
    private static String plain(String s) { return s.replaceAll("§.", "").trim(); }
    private static boolean fragment(String s) {
        String p = plain(s);
        if (!p.matches("(?:「[^」\\r\\n]+」\\s*)+")) return false;
        return s.contains(HOLE) || s.contains("§(") || p.matches("(?:「[^」\\p{IsHan}]+」\\s*)+");
    }
    /** No NBT, gem data, stats, slot count, or text inside a token is changed. */
    public static List<String> compactLore(List<String> input) {
        List<String> out = new ArrayList<String>();
        for (int i = 0; i < input.size(); i++) {
            String line = input.get(i);
            if (!line.contains("符文➱")) { out.add(line); continue; }
            StringBuilder row = new StringBuilder(line);
            int end = i;
            for (int j = i + 1; j < input.size(); j++) {
                String next = input.get(j);
                if (plain(next).isEmpty()) {
                    if (j + 1 < input.size() && fragment(input.get(j + 1))) { end = j; continue; }
                    break;
                }
                if (!fragment(next)) break;
                row.append(next.trim()); end = j;
            }
            out.add(row.toString()); i = end;
        }
        return out;
    }
    private boolean backup(ItemStack original, String player) {
        try {
            YamlConfiguration y = new YamlConfiguration();
            y.set("player", player);
            y.set("reason", "Inline rune layout: only Lore line grouping changed");
            y.set("item", original.clone());
            byte[] bytes = y.saveToString().getBytes(StandardCharsets.UTF_8);
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(bytes);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) hex.append(String.format("%02x", b & 255));
            String key = player + ":" + hex;
            if (backed.contains(key)) return true;
            File folder = new File(getDataFolder(), "inline-backups");
            if (!folder.isDirectory() && !folder.mkdirs()) throw new IllegalStateException("Cannot create backup directory");
            File dst = new File(folder, player.replaceAll("[^A-Za-z0-9_\\-]", "_") + "_" + hex + ".yml");
            if (!dst.exists()) y.save(dst);
            if (!YamlConfiguration.loadConfiguration(dst).getItemStack("item").equals(original))
                throw new IllegalStateException("Item backup verification failed");
            backed.add(key); return true;
        } catch (Exception e) {
            getLogger().warning("Rune layout unchanged: backup failed for " + player + ": " + e);
            return false;
        }
    }
    public ItemStack compact(ItemStack item, String player) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return item;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return item;
        List<String> old = meta.getLore(), next = compactLore(old);
        if (old.equals(next)) return item;
        if (!backup(item, player)) return item;
        ItemStack result = item.clone();
        meta.setLore(next); result.setItemMeta(meta);
        return result;
    }
    private void inventory(Inventory inventory, String name) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack before = inventory.getItem(i), after = compact(before, name);
            if (before != after) inventory.setItem(i, after);
        }
    }
    private void returned(Player p) {
        inventory(p.getInventory(), p.getName());
        ItemStack before = p.getItemOnCursor(), after = compact(before, p.getName());
        if (before != after) p.setItemOnCursor(after);
    }
    private void later(final Player p) {
        if (!pending.add(p.getUniqueId())) return;
        Bukkit.getScheduler().runTask(this, new Runnable() { public void run() {
            pending.remove(p.getUniqueId());
            if (!p.isOnline()) return;
            if (page(p.getOpenInventory())) {
                Inventory top = p.getOpenInventory().getTopInventory();
                if (top.getSize() > 40) {
                    ItemStack before = top.getItem(40), after = compact(before, p.getName());
                    if (before != after) top.setItem(40, after);
                }
            }
            returned(p);
            if (!page(p.getOpenInventory())) refreshAttributes(p);
        }});
    }
    private void refreshAttributes(Player p) {
        try {
            org.bukkit.plugin.Plugin gem = Bukkit.getPluginManager().getPlugin("YeeGem");
            if (gem == null || !gem.isEnabled()) return;
            Class<?> type = Class.forName("me.yeezhi.yeegem.YeeGem", false, gem.getClass().getClassLoader());
            Object api = type.getField("API").get(null);
            api.getClass().getMethod("updateAttribute", Player.class).invoke(api, p);
        } catch (Exception e) {
            getLogger().warning("Unable to refresh YeeGem attributes after rune menu close: " + e);
        }
    }
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void incoming(InventoryClickEvent e) {
        if (!page(e.getView())) return;
        String name = e.getWhoClicked().getName();
        if (e.getRawSlot() == 40) {
            ItemStack before = e.getCursor(), after = compact(before, name);
            if (before != after) e.setCursor(after);
            before = e.getCurrentItem(); after = compact(before, name);
            if (before != after) e.setCurrentItem(after);
        }
        if (e.isShiftClick() && e.getRawSlot() >= e.getView().getTopInventory().getSize()) {
            ItemStack before = e.getCurrentItem(), after = compact(before, name);
            if (before != after) e.setCurrentItem(after);
        }
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void clicked(InventoryClickEvent e) {
        if (page(e.getView()) && e.getWhoClicked() instanceof Player) later((Player)e.getWhoClicked());
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void dragged(InventoryDragEvent e) {
        if (page(e.getView()) && e.getWhoClicked() instanceof Player) later((Player)e.getWhoClicked());
    }
    @EventHandler(priority=EventPriority.HIGHEST)
    public void closing(InventoryCloseEvent e) {
        if (!page(e.getView()) || !(e.getPlayer() instanceof Player)) return;
        if (e.getInventory().getSize() > 40) {
            ItemStack before=e.getInventory().getItem(40), after=compact(before,e.getPlayer().getName());
            if(before!=after)e.getInventory().setItem(40,after);
        }
        later((Player)e.getPlayer());
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void quit(PlayerQuitEvent e) {
        if (page(e.getPlayer().getOpenInventory())) returned(e.getPlayer());
    }
}
