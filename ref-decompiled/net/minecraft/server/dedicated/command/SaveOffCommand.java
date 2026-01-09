package net.minecraft.server.dedicated.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Iterator;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public class SaveOffCommand {
   private static final SimpleCommandExceptionType ALREADY_OFF_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.save.alreadyOff"));

   public static void register(CommandDispatcher dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("save-off").requires(CommandManager.requirePermissionLevel(4))).executes((context) -> {
         ServerCommandSource serverCommandSource = (ServerCommandSource)context.getSource();
         boolean bl = false;
         Iterator var3 = serverCommandSource.getServer().getWorlds().iterator();

         while(var3.hasNext()) {
            ServerWorld serverWorld = (ServerWorld)var3.next();
            if (serverWorld != null && !serverWorld.savingDisabled) {
               serverWorld.savingDisabled = true;
               bl = true;
            }
         }

         if (!bl) {
            throw ALREADY_OFF_EXCEPTION.create();
         } else {
            serverCommandSource.sendFeedback(() -> {
               return Text.translatable("commands.save.disabled");
            }, true);
            return 1;
         }
      }));
   }
}
