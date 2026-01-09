package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.Pool;
import net.minecraft.world.biome.SpawnSettings;

public record StructureSpawns(BoundingBox boundingBox, Pool spawns) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(StructureSpawns.BoundingBox.CODEC.fieldOf("bounding_box").forGetter(StructureSpawns::boundingBox), Pool.createCodec(SpawnSettings.SpawnEntry.CODEC).fieldOf("spawns").forGetter(StructureSpawns::spawns)).apply(instance, StructureSpawns::new);
   });

   public StructureSpawns(BoundingBox boundingBox, Pool pool) {
      this.boundingBox = boundingBox;
      this.spawns = pool;
   }

   public BoundingBox boundingBox() {
      return this.boundingBox;
   }

   public Pool spawns() {
      return this.spawns;
   }

   public static enum BoundingBox implements StringIdentifiable {
      PIECE("piece"),
      STRUCTURE("full");

      public static final Codec CODEC = StringIdentifiable.createCodec(BoundingBox::values);
      private final String name;

      private BoundingBox(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static BoundingBox[] method_41152() {
         return new BoundingBox[]{PIECE, STRUCTURE};
      }
   }
}
