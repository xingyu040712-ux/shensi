import java.lang.reflect.*;

/** Isolated branch/verification probe; no server, players or database are opened. */
public final class VerifyCouponsJoin {
    public static void main(String[] args) throws Exception {
        Class<?> settingsType = Class.forName("com.mchim.Coupons.Config.Settings");
        Object settings = settingsType.getConstructor().newInstance();
        Field usePoints = settingsType.getField("usePlayerPoint");
        Class<?> listenerType = Class.forName("com.mchim.Coupons.Listeners.InventoryListener");
        Object listener = listenerType.getConstructor().newInstance();
        Method join = listenerType.getMethod("join", Class.forName("org.bukkit.event.player.PlayerJoinEvent"));
        usePoints.setBoolean(settings, true);
        join.invoke(listener, new Object[]{null});
        System.out.println("PASS PlayerPoints mode: join(null) returns before event/database access");
        usePoints.setBoolean(settings, false);
        try {
            join.invoke(listener, new Object[]{null});
            throw new AssertionError("SQL branch was unexpectedly bypassed");
        } catch (InvocationTargetException e) {
            if (!(e.getCause() instanceof NullPointerException)) throw e;
            System.out.println("PASS SQL mode: original branch reaches event access (expected null-fixture NPE)");
        }
    }
}
