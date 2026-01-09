package net.minecraft.text;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public record StorageNbtDataSource(Identifier id) implements NbtDataSource {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Identifier.CODEC.fieldOf("storage").forGetter(StorageNbtDataSource::id)).apply(instance, StorageNbtDataSource::new);
   });
   public static final NbtDataSource.Type TYPE;

   public StorageNbtDataSource(Identifier identifier) {
      this.id = identifier;
   }

   public Stream get(ServerCommandSource source) {
      NbtCompound nbtCompound = source.getServer().getDataCommandStorage().get(this.id);
      return Stream.of(nbtCompound);
   }

   public NbtDataSource.Type getType() {
      return TYPE;
   }

   public String toString() {
      return "storage=" + String.valueOf(this.id);
   }

   public Identifier id() {
      return this.id;
   }

   static {
      TYPE = new NbtDataSource.Type(CODEC, "storage");
   }
}
