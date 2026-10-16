import java.lang.reflect.*;
public final class VerifyShield {
 public static void main(String[] args) throws Exception {
  Class<?> c=Class.forName("com.mchim.Intensify.Utils.SecretChatShieldCompat",false,VerifyShield.class.getClassLoader());
  Method selector=c.getDeclaredMethod("sendingHandler");selector.setAccessible(true);
  Method m=(Method)selector.invoke(null);
  if(m.getReturnType()!=Void.TYPE || m.getParameterCount()!=1 || m.getParameterTypes()[0]!=Object.class)throw new AssertionError(m);
  System.out.println("Verified adapter classes and selected original void sending handler.");
 }
}