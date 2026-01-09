package net.minecraft.text;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.StringIdentifiable;

public interface NbtDataSource {
   MapCodec CODEC = TextCodecs.dispatchingCodec(new Type[]{EntityNbtDataSource.TYPE, BlockNbtDataSource.TYPE, StorageNbtDataSource.TYPE}, Type::codec, NbtDataSource::getType, "source");

   Stream get(ServerCommandSource source) throws CommandSyntaxException;

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
