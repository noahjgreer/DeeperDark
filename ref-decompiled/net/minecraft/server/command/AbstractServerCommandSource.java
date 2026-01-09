package net.minecraft.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.PermissionLevelSource;
import net.minecraft.command.ReturnValueConsumer;
import net.minecraft.server.function.Tracer;
import org.jetbrains.annotations.Nullable;

public interface AbstractServerCommandSource extends PermissionLevelSource {
   AbstractServerCommandSource withReturnValueConsumer(ReturnValueConsumer returnValueConsumer);

   ReturnValueConsumer getReturnValueConsumer();

   default AbstractServerCommandSource withDummyReturnValueConsumer() {
      return this.withReturnValueConsumer(ReturnValueConsumer.EMPTY);
   }

   CommandDispatcher getDispatcher();

   void handleException(CommandExceptionType type, Message message, boolean silent, @Nullable Tracer tracer);

   boolean isSilent();

   default void handleException(CommandSyntaxException exception, boolean silent, @Nullable Tracer tracer) {
      this.handleException(exception.getType(), exception.getRawMessage(), silent, tracer);
   }

   static ResultConsumer asResultConsumer() {
      return (context, success, result) -> {
         ((AbstractServerCommandSource)context.getSource()).getReturnValueConsumer().onResult(success, result);
      };
   }
}
