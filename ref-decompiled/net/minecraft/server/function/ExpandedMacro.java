package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import java.util.List;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record ExpandedMacro(Identifier id, List entries) implements CommandFunction, Procedure {
   public ExpandedMacro(Identifier identifier, List list) {
      this.id = identifier;
      this.entries = list;
   }

   public Procedure withMacroReplaced(@Nullable NbtCompound arguments, CommandDispatcher dispatcher) throws MacroException {
      return this;
   }

   public Identifier id() {
      return this.id;
   }

   public List entries() {
      return this.entries;
   }
}
