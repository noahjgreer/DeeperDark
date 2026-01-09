package net.minecraft.structure.pool;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.Orientation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureLiquidSettings;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.PlacedFeature;

public class FeaturePoolElement extends StructurePoolElement {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(PlacedFeature.REGISTRY_CODEC.fieldOf("feature").forGetter((pool) -> {
         return pool.feature;
      }), projectionGetter()).apply(instance, FeaturePoolElement::new);
   });
   private static final Identifier DEFAULT_NAME = Identifier.ofVanilla("bottom");
   private final RegistryEntry feature;
   private final NbtCompound nbt;

   protected FeaturePoolElement(RegistryEntry feature, StructurePool.Projection projection) {
      super(projection);
      this.feature = feature;
      this.nbt = this.createDefaultJigsawNbt();
   }

   private NbtCompound createDefaultJigsawNbt() {
      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.put("name", Identifier.CODEC, DEFAULT_NAME);
      nbtCompound.putString("final_state", "minecraft:air");
      nbtCompound.put("pool", JigsawBlockEntity.STRUCTURE_POOL_KEY_CODEC, StructurePools.EMPTY);
      nbtCompound.put("target", Identifier.CODEC, JigsawBlockEntity.DEFAULT_NAME);
      nbtCompound.put("joint", JigsawBlockEntity.Joint.CODEC, JigsawBlockEntity.Joint.ROLLABLE);
      return nbtCompound;
   }

   public Vec3i getStart(StructureTemplateManager structureTemplateManager, BlockRotation rotation) {
      return Vec3i.ZERO;
   }

   public List getStructureBlockInfos(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random) {
      return List.of(StructureTemplate.JigsawBlockInfo.of(new StructureTemplate.StructureBlockInfo(pos, (BlockState)Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, Orientation.byDirections(Direction.DOWN, Direction.SOUTH)), this.nbt)));
   }

   public BlockBox getBoundingBox(StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation) {
      Vec3i vec3i = this.getStart(structureTemplateManager, rotation);
      return new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + vec3i.getX(), pos.getY() + vec3i.getY(), pos.getZ() + vec3i.getZ());
   }

   public boolean generate(StructureTemplateManager structureTemplateManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos pivot, BlockRotation rotation, BlockBox box, Random random, StructureLiquidSettings liquidSettings, boolean keepJigsaws) {
      return ((PlacedFeature)this.feature.value()).generateUnregistered(world, chunkGenerator, random, pos);
   }

   public StructurePoolElementType getType() {
      return StructurePoolElementType.FEATURE_POOL_ELEMENT;
   }

   public String toString() {
      return "Feature[" + String.valueOf(this.feature) + "]";
   }
}
