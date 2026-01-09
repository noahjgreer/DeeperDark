package net.minecraft.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;

public interface Forkable {
   void execute(Object baseSource, List sources, ContextChain contextChain, ExecutionFlags flags, ExecutionControl control);

   public interface RedirectModifier extends com.mojang.brigadier.RedirectModifier, Forkable {
      default Collection apply(CommandContext context) throws CommandSyntaxException {
         throw new UnsupportedOperationException("This function should not run");
      }
   }
}
