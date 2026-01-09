package net.minecraft.world.gen.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class MineshaftStructure extends Structure {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(configCodecBuilder(instance), MineshaftStructure.Type.CODEC.fieldOf("mineshaft_type").forGetter((mineshaftStructure) -> {
         return mineshaftStructure.type;
      })).apply(instance, MineshaftStructure::new);
   });
   private final Type type;

   public MineshaftStructure(Structure.Config config, Type type) {
      super(config);
      this.type = type;
   }

   public Optional getStructurePosition(Structure.Context context) {
      context.random().nextDouble();
      ChunkPos chunkPos = context.chunkPos();
      BlockPos blockPos = new BlockPos(chunkPos.getCenterX(), 50, chunkPos.getStartZ());
      StructurePiecesCollector structurePiecesCollector = new StructurePiecesCollector();
      int i = this.addPieces(structurePiecesCollector, context);
      return Optional.of(new Structure.StructurePosition(blockPos.add(0, i, 0), Either.right(structurePiecesCollector)));
   }

   private int addPieces(StructurePiecesCollector collector, Structure.Context context) {
      ChunkPos chunkPos = context.chunkPos();
      ChunkRandom chunkRandom = context.random();
      ChunkGenerator chunkGenerator = context.chunkGenerator();
      MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(0, chunkRandom, chunkPos.getOffsetX(2), chunkPos.getOffsetZ(2), this.type);
      collector.addPiece(mineshaftRoom);
      mineshaftRoom.fillOpenings(mineshaftRoom, collector, chunkRandom);
      int i = chunkGenerator.getSeaLevel();
      if (this.type == MineshaftStructure.Type.MESA) {
         BlockPos blockPos = collector.getBoundingBox().getCenter();
         int j = chunkGenerator.getHeight(blockPos.getX(), blockPos.getZ(), Heightmap.Type.WORLD_SURFACE_WG, context.world(), context.noiseConfig());
         int k = j <= i ? i : MathHelper.nextBetween(chunkRandom, i, j);
         int l = k - blockPos.getY();
         collector.shift(l);
         return l;
      } else {
         return collector.shiftInto(i, chunkGenerator.getMinimumY(), chunkRandom, 10);
      }
   }

   public StructureType getType() {
      return StructureType.MINESHAFT;
   }

   public static enum Type implements StringIdentifiable {
      NORMAL("normal", Blocks.OAK_LOG, Blocks.OAK_PLANKS, Blocks.OAK_FENCE),
      MESA("mesa", Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS, Blocks.DARK_OAK_FENCE);

      public static final Codec CODEC = StringIdentifiable.createCodec(Type::values);
      private static final IntFunction BY_ID = ValueLists.createIndexToValueFunction(Enum::ordinal, values(), (ValueLists.OutOfBoundsHandling)ValueLists.OutOfBoundsHandling.ZERO);
      private final String name;
      private final BlockState log;
      private final BlockState planks;
      private final BlockState fence;

      private Type(final String name, final Block log, final Block planks, final Block fence) {
         this.name = name;
         this.log = log.getDefaultState();
         this.planks = planks.getDefaultState();
         this.fence = fence.getDefaultState();
      }

      public String getName() {
         return this.name;
      }

      public static Type byId(int id) {
         return (Type)BY_ID.apply(id);
      }

      public BlockState getLog() {
         return this.log;
      }

      public BlockState getPlanks() {
         return this.planks;
      }

      public BlockState getFence() {
         return this.fence;
      }

      public String asString() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] method_36755() {
         return new Type[]{NORMAL, MESA};
      }
   }
}
