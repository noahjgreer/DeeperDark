package net.minecraft.world.timer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public record FunctionTimerCallback(Identifier name) implements TimerCallback {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("Name").forGetter(FunctionTimerCallback::name)).apply(instance, FunctionTimerCallback::new);
   });

   public FunctionTimerCallback(Identifier name) {
      this.name = name;
   }

   public void call(MinecraftServer minecraftServer, Timer timer, long l) {
      CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
      commandFunctionManager.getFunction(this.name).ifPresent((function) -> {
         commandFunctionManager.execute(function, commandFunctionManager.getScheduledCommandSource());
      });
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Identifier name() {
      return this.name;
   }
}
