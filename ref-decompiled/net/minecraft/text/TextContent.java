package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.Nullable;

public interface TextContent {
   default Optional visit(StringVisitable.StyledVisitor visitor, Style style) {
      return Optional.empty();
   }

   default Optional visit(StringVisitable.Visitor visitor) {
      return Optional.empty();
   }

   default MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
      return MutableText.of(this);
   }

   Type getType();

   public static record Type(MapCodec codec, String id) implements StringIdentifiable {
      public Type(MapCodec mapCodec, String string) {
         this.codec = mapCodec;
         this.id = string;
      }

      public String asString() {
         return this.id;
      }

      public MapCodec codec() {
         return this.codec;
      }

      public String id() {
         return this.id;
      }
   }
}
