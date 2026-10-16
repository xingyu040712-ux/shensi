package com.mchim.Intensify.Utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.mchim.Intensify.IntensifyPlugin;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.Bukkit;

/** ProtocolLib 4.4 registration adapter. Original secret processing is retained. */
public final class SecretChatShieldCompat {
    private SecretChatShieldCompat() {}

    private static Method sendingHandler() throws NoSuchMethodException {
        for (Method method : SecretChatShield.class.getDeclaredMethods()) {
            if (method.getName().equals("C") && method.getReturnType() == Void.TYPE
                    && java.util.Arrays.equals(method.getParameterTypes(), new Class<?>[]{Object.class}))
                return method;
        }
        throw new NoSuchMethodException("SecretChatShield.C(Object):void");
    }

    public static void install(final IntensifyPlugin plugin) {
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) return;
        ProtocolManager manager = null;
        PacketListener receive = null;
        PacketListener send = null;
        try {
            manager = ProtocolLibrary.getProtocolManager();
            final Method receivingHandler = SecretChatShield.class.getDeclaredMethod(
                    "ALLATORIxDEMO", IntensifyPlugin.class, Object.class);
            final Method sendingHandler = sendingHandler();
            receivingHandler.setAccessible(true);
            sendingHandler.setAccessible(true);
            final IntensifyPlugin owner = plugin;
            final AtomicBoolean receiveWarning = new AtomicBoolean();
            final AtomicBoolean sendWarning = new AtomicBoolean();
            receive = new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.CHAT) {
                @Override public void onPacketReceiving(PacketEvent event) {
                    try {
                        receivingHandler.invoke(null, owner, event);
                    } catch (Exception failure) {
                        event.setCancelled(true);
                        if (receiveWarning.compareAndSet(false, true))
                            plugin.getLogger().warning("SecretChatShield incoming chat cancelled after handler failure: "
                                    + failure.getClass().getName());
                    }
                }
            };
            // The retained sending handler can cancel packets; MONITOR is read-only.
            send = new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT) {
                @Override public void onPacketSending(PacketEvent event) {
                    try {
                        sendingHandler.invoke(null, event);
                    } catch (Exception failure) {
                        event.setCancelled(true);
                        if (sendWarning.compareAndSet(false, true))
                            plugin.getLogger().warning("SecretChatShield outgoing chat cancelled after handler failure: "
                                    + failure.getClass().getName());
                    }
                }
            };
            manager.addPacketListener(receive);
            manager.addPacketListener(send);
            SecretChatGate.ALLATORIxDEMO(true);
            plugin.getLogger().info("SecretChatShield enabled (ProtocolLib 4.4 chat adapters)");
        } catch (Throwable failure) {
            if (manager != null) {
                if (receive != null) manager.removePacketListener(receive);
                if (send != null) manager.removePacketListener(send);
            }
            SecretChatGate.ALLATORIxDEMO(false);
            plugin.getLogger().warning("SecretChatShield unavailable: " + failure.getClass().getName());
        }
    }
}
