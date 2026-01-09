package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class Feature {
   public static final Feature NO_OP;
   public static final Feature TREE;
   public static final Feature FALLEN_TREE;
   public static final Feature FLOWER;
   public static final Feature NO_BONEMEAL_FLOWER;
   public static final Feature RANDOM_PATCH;
   public static final Feature BLOCK_PILE;
   public static final Feature SPRING_FEATURE;
   public static final Feature CHORUS_PLANT;
   public static final Feature REPLACE_SINGLE_BLOCK;
   public static final Feature VOID_START_PLATFORM;
   public static final Feature DESERT_WELL;
   public static final Feature FOSSIL;
   public static final Feature HUGE_RED_MUSHROOM;
   public static final Feature HUGE_BROWN_MUSHROOM;
   public static final Feature ICE_SPIKE;
   public static final Feature GLOWSTONE_BLOB;
   public static final Feature FREEZE_TOP_LAYER;
   public static final Feature VINES;
   public static final Feature BLOCK_COLUMN;
   public static final Feature VEGETATION_PATCH;
   public static final Feature WATERLOGGED_VEGETATION_PATCH;
   public static final Feature ROOT_SYSTEM;
   public static final Feature MULTIFACE_GROWTH;
   public static final Feature UNDERWATER_MAGMA;
   public static final Feature MONSTER_ROOM;
   public static final Feature BLUE_ICE;
   public static final Feature ICEBERG;
   public static final Feature FOREST_ROCK;
   public static final Feature DISK;
   public static final Feature LAKE;
   public static final Feature ORE;
   public static final Feature END_PLATFORM;
   public static final Feature END_SPIKE;
   public static final Feature END_ISLAND;
   public static final Feature END_GATEWAY;
   public static final SeagrassFeature SEAGRASS;
   public static final Feature KELP;
   public static final Feature CORAL_TREE;
   public static final Feature CORAL_MUSHROOM;
   public static final Feature CORAL_CLAW;
   public static final Feature SEA_PICKLE;
   public static final Feature SIMPLE_BLOCK;
   public static final Feature BAMBOO;
   public static final Feature HUGE_FUNGUS;
   public static final Feature NETHER_FOREST_VEGETATION;
   public static final Feature WEEPING_VINES;
   public static final Feature TWISTING_VINES;
   public static final Feature BASALT_COLUMNS;
   public static final Feature DELTA_FEATURE;
   public static final Feature NETHERRACK_REPLACE_BLOBS;
   public static final Feature FILL_LAYER;
   public static final BonusChestFeature BONUS_CHEST;
   public static final Feature BASALT_PILLAR;
   public static final Feature SCATTERED_ORE;
   public static final Feature RANDOM_SELECTOR;
   public static final Feature SIMPLE_RANDOM_SELECTOR;
   public static final Feature RANDOM_BOOLEAN_SELECTOR;
   public static final Feature GEODE;
   public static final Feature DRIPSTONE_CLUSTER;
   public static final Feature LARGE_DRIPSTONE;
   public static final Feature POINTED_DRIPSTONE;
   public static final Feature SCULK_PATCH;
   private final MapCodec codec;

   private static Feature register(String name, Feature feature) {
      return (Feature)Registry.register(Registries.FEATURE, (String)name, feature);
   }

   public Feature(Codec configCodec) {
      this.codec = configCodec.fieldOf("config").xmap((config) -> {
         return new ConfiguredFeature(this, config);
      }, ConfiguredFeature::config);
   }

   public MapCodec getCodec() {
      return this.codec;
   }

   protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
      world.setBlockState(pos, state, 3);
   }

   public static Predicate notInBlockTagPredicate(TagKey tag) {
      return (state) -> {
         return !state.isIn(tag);
      };
   }

   protected void setBlockStateIf(StructureWorldAccess world, BlockPos pos, BlockState state, Predicate predicate) {
      if (predicate.test(world.getBlockState(pos))) {
         world.setBlockState(pos, state, 2);
      }

   }

   public abstract boolean generate(FeatureContext context);

   public boolean generateIfValid(FeatureConfig config, StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos) {
      return world.isValidForSetBlock(pos) ? this.generate(new FeatureContext(Optional.empty(), world, chunkGenerator, random, pos, config)) : false;
   }

   protected static boolean isStone(BlockState state) {
      return state.isIn(BlockTags.BASE_STONE_OVERWORLD);
   }

   public static boolean isSoil(BlockState state) {
      return state.isIn(BlockTags.DIRT);
   }

   public static boolean isSoil(TestableWorld world, BlockPos pos) {
      return world.testBlockState(pos, Feature::isSoil);
   }

   public static boolean testAdjacentStates(Function posToState, BlockPos pos, Predicate predicate) {
      BlockPos.Mutable mutable = new BlockPos.Mutable();
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction direction = var4[var6];
         mutable.set(pos, (Direction)direction);
         if (predicate.test((BlockState)posToState.apply(mutable))) {
            return true;
         }
      }

      return false;
   }

   public static boolean isExposedToAir(Function posToState, BlockPos pos) {
      return testAdjacentStates(posToState, pos, AbstractBlock.AbstractBlockState::isAir);
   }

   protected void markBlocksAboveForPostProcessing(StructureWorldAccess world, BlockPos pos) {
      BlockPos.Mutable mutable = pos.mutableCopy();

      for(int i = 0; i < 2; ++i) {
         mutable.move(Direction.UP);
         if (world.getBlockState(mutable).isAir()) {
            return;
         }

         world.getChunk(mutable).markBlockForPostProcessing(mutable);
      }

   }

   static {
      NO_OP = register("no_op", new NoOpFeature(DefaultFeatureConfig.CODEC));
      TREE = register("tree", new TreeFeature(TreeFeatureConfig.CODEC));
      FALLEN_TREE = register("fallen_tree", new FallenTreeFeature(FallenTreeFeatureConfig.CODEC));
      FLOWER = register("flower", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
      NO_BONEMEAL_FLOWER = register("no_bonemeal_flower", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
      RANDOM_PATCH = register("random_patch", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
      BLOCK_PILE = register("block_pile", new BlockPileFeature(BlockPileFeatureConfig.CODEC));
      SPRING_FEATURE = register("spring_feature", new SpringFeature(SpringFeatureConfig.CODEC));
      CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig.CODEC));
      REPLACE_SINGLE_BLOCK = register("replace_single_block", new EmeraldOreFeature(EmeraldOreFeatureConfig.CODEC));
      VOID_START_PLATFORM = register("void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig.CODEC));
      DESERT_WELL = register("desert_well", new DesertWellFeature(DefaultFeatureConfig.CODEC));
      FOSSIL = register("fossil", new FossilFeature(FossilFeatureConfig.CODEC));
      HUGE_RED_MUSHROOM = register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig.CODEC));
      HUGE_BROWN_MUSHROOM = register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig.CODEC));
      ICE_SPIKE = register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig.CODEC));
      GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig.CODEC));
      FREEZE_TOP_LAYER = register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig.CODEC));
      VINES = register("vines", new VinesFeature(DefaultFeatureConfig.CODEC));
      BLOCK_COLUMN = register("block_column", new BlockColumnFeature(BlockColumnFeatureConfig.CODEC));
      VEGETATION_PATCH = register("vegetation_patch", new VegetationPatchFeature(VegetationPatchFeatureConfig.CODEC));
      WATERLOGGED_VEGETATION_PATCH = register("waterlogged_vegetation_patch", new WaterloggedVegetationPatchFeature(VegetationPatchFeatureConfig.CODEC));
      ROOT_SYSTEM = register("root_system", new RootSystemFeature(RootSystemFeatureConfig.CODEC));
      MULTIFACE_GROWTH = register("multiface_growth", new MultifaceGrowthFeature(MultifaceGrowthFeatureConfig.CODEC));
      UNDERWATER_MAGMA = register("underwater_magma", new UnderwaterMagmaFeature(UnderwaterMagmaFeatureConfig.CODEC));
      MONSTER_ROOM = register("monster_room", new DungeonFeature(DefaultFeatureConfig.CODEC));
      BLUE_ICE = register("blue_ice", new BlueIceFeature(DefaultFeatureConfig.CODEC));
      ICEBERG = register("iceberg", new IcebergFeature(SingleStateFeatureConfig.CODEC));
      FOREST_ROCK = register("forest_rock", new ForestRockFeature(SingleStateFeatureConfig.CODEC));
      DISK = register("disk", new DiskFeature(DiskFeatureConfig.CODEC));
      LAKE = register("lake", new LakeFeature(LakeFeature.Config.CODEC));
      ORE = register("ore", new OreFeature(OreFeatureConfig.CODEC));
      END_PLATFORM = register("end_platform", new EndPlatformFeature(DefaultFeatureConfig.CODEC));
      END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig.CODEC));
      END_ISLAND = register("end_island", new EndIslandFeature(DefaultFeatureConfig.CODEC));
      END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig.CODEC));
      SEAGRASS = (SeagrassFeature)register("seagrass", new SeagrassFeature(ProbabilityConfig.CODEC));
      KELP = register("kelp", new KelpFeature(DefaultFeatureConfig.CODEC));
      CORAL_TREE = register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig.CODEC));
      CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig.CODEC));
      CORAL_CLAW = register("coral_claw", new CoralClawFeature(DefaultFeatureConfig.CODEC));
      SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(CountConfig.CODEC));
      SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig.CODEC));
      BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig.CODEC));
      HUGE_FUNGUS = register("huge_fungus", new HugeFungusFeature(HugeFungusFeatureConfig.CODEC));
      NETHER_FOREST_VEGETATION = register("nether_forest_vegetation", new NetherForestVegetationFeature(NetherForestVegetationFeatureConfig.VEGETATION_CODEC));
      WEEPING_VINES = register("weeping_vines", new WeepingVinesFeature(DefaultFeatureConfig.CODEC));
      TWISTING_VINES = register("twisting_vines", new TwistingVinesFeature(TwistingVinesFeatureConfig.CODEC));
      BASALT_COLUMNS = register("basalt_columns", new BasaltColumnsFeature(BasaltColumnsFeatureConfig.CODEC));
      DELTA_FEATURE = register("delta_feature", new DeltaFeature(DeltaFeatureConfig.CODEC));
      NETHERRACK_REPLACE_BLOBS = register("netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceBlobsFeatureConfig.CODEC));
      FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig.CODEC));
      BONUS_CHEST = (BonusChestFeature)register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig.CODEC));
      BASALT_PILLAR = register("basalt_pillar", new BasaltPillarFeature(DefaultFeatureConfig.CODEC));
      SCATTERED_ORE = register("scattered_ore", new ScatteredOreFeature(OreFeatureConfig.CODEC));
      RANDOM_SELECTOR = register("random_selector", new RandomFeature(RandomFeatureConfig.CODEC));
      SIMPLE_RANDOM_SELECTOR = register("simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig.CODEC));
      RANDOM_BOOLEAN_SELECTOR = register("random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig.CODEC));
      GEODE = register("geode", new GeodeFeature(GeodeFeatureConfig.CODEC));
      DRIPSTONE_CLUSTER = register("dripstone_cluster", new DripstoneClusterFeature(DripstoneClusterFeatureConfig.CODEC));
      LARGE_DRIPSTONE = register("large_dripstone", new LargeDripstoneFeature(LargeDripstoneFeatureConfig.CODEC));
      POINTED_DRIPSTONE = register("pointed_dripstone", new SmallDripstoneFeature(SmallDripstoneFeatureConfig.CODEC));
      SCULK_PATCH = register("sculk_patch", new SculkPatchFeature(SculkPatchFeatureConfig.CODEC));
   }
}
