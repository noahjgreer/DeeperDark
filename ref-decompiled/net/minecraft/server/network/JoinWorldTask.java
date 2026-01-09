package net.minecraft.server.network;

import java.util.function.Consumer;
import net.minecraft.network.packet.s2c.config.ReadyS2CPacket;

public class JoinWorldTask implements ServerPlayerConfigurationTask {
   public static final ServerPlayerConfigurationTask.Key KEY = new ServerPlayerConfigurationTask.Key("join_world");

   public void sendPacket(Consumer sender) {
      sender.accept(ReadyS2CPacket.INSTANCE);
   }

   public ServerPlayerConfigurationTask.Key getKey() {
      return KEY;
   }
}
