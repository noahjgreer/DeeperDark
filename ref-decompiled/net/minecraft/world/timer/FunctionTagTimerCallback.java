package net.minecraft.world.timer;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;

public record FunctionTagTimerCallback(Identifier name) implements TimerCallback {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("Name").forGetter(FunctionTagTimerCallback::name)).apply(instance, FunctionTagTimerCallback::new);
   });

   public FunctionTagTimerCallback(Identifier name) {
      this.name = name;
   }

   public void call(MinecraftServer minecraftServer, Timer timer, long l) {
      CommandFunctionManager commandFunctionManager = minecraftServer.getCommandFunctionManager();
      List list = commandFunctionManager.getTag(this.name);
      Iterator var7 = list.iterator();

      while(var7.hasNext()) {
         CommandFunction commandFunction = (CommandFunction)var7.next();
         commandFunctionManager.execute(commandFunction, commandFunctionManager.getScheduledCommandSource());
      }

   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public Identifier name() {
      return this.name;
   }
}
