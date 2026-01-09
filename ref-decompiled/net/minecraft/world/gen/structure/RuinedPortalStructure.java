package net.minecraft.world.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeCoords;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.noise.NoiseConfig;

public class RuinedPortalStructure extends Structure {
   private static final String[] COMMON_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/portal_1", "ruined_portal/portal_2", "ruined_portal/portal_3", "ruined_portal/portal_4", "ruined_portal/portal_5", "ruined_portal/portal_6", "ruined_portal/portal_7", "ruined_portal/portal_8", "ruined_portal/portal_9", "ruined_portal/portal_10"};
   private static final String[] RARE_PORTAL_STRUCTURE_IDS = new String[]{"ruined_portal/giant_portal_1", "ruined_portal/giant_portal_2", "ruined_portal/giant_portal_3"};
   private static final float RARE_PORTAL_CHANCE = 0.05F;
   private static final int MIN_BLOCKS_ABOVE_WORLD_BOTTOM = 15;
   private final List setups;
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(configCodecBuilder(instance), Codecs.nonEmptyList(RuinedPortalStructure.Setup.CODEC.listOf()).fieldOf("setups").forGetter((structure) -> {
         return structure.setups;
      })).apply(instance, RuinedPortalStructure::new);
   });

   public RuinedPortalStructure(Structure.Config config, List setups) {
      super(config);
      this.setups = setups;
   }

   public RuinedPortalStructure(Structure.Config config, Setup setup) {
      this(config, List.of(setup));
   }

   public Optional getStructurePosition(Structure.Context context) {
      RuinedPortalStructurePiece.Properties properties = new RuinedPortalStructurePiece.Properties();
      ChunkRandom chunkRandom = context.random();
      Setup setup = null;
      if (this.setups.size() > 1) {
         float f = 0.0F;

         Setup setup2;
         for(Iterator var6 = this.setups.iterator(); var6.hasNext(); f += setup2.weight()) {
            setup2 = (Setup)var6.next();
         }

         float g = chunkRandom.nextFloat();
         Iterator var22 = this.setups.iterator();

         while(var22.hasNext()) {
            Setup setup3 = (Setup)var22.next();
            g -= setup3.weight() / f;
            if (g < 0.0F) {
               setup = setup3;
               break;
            }
         }
      } else {
         setup = (Setup)this.setups.get(0);
      }

      if (setup == null) {
         throw new IllegalStateException();
      } else {
         properties.airPocket = shouldPlaceAirPocket(chunkRandom, setup.airPocketProbability());
         properties.mossiness = setup.mossiness();
         properties.overgrown = setup.overgrown();
         properties.vines = setup.vines();
         properties.replaceWithBlackstone = setup.replaceWithBlackstone();
         Identifier identifier;
         if (chunkRandom.nextFloat() < 0.05F) {
            identifier = Identifier.ofVanilla(RARE_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(RARE_PORTAL_STRUCTURE_IDS.length)]);
         } else {
            identifier = Identifier.ofVanilla(COMMON_PORTAL_STRUCTURE_IDS[chunkRandom.nextInt(COMMON_PORTAL_STRUCTURE_IDS.length)]);
         }

         StructureTemplate structureTemplate = context.structureTemplateManager().getTemplateOrBlank(identifier);
         BlockRotation blockRotation = (BlockRotation)Util.getRandom((Object[])BlockRotation.values(), chunkRandom);
         BlockMirror blockMirror = chunkRandom.nextFloat() < 0.5F ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
         BlockPos blockPos = new BlockPos(structureTemplate.getSize().getX() / 2, 0, structureTemplate.getSize().getZ() / 2);
         ChunkGenerator chunkGenerator = context.chunkGenerator();
         HeightLimitView heightLimitView = context.world();
         NoiseConfig noiseConfig = context.noiseConfig();
         BlockPos blockPos2 = context.chunkPos().getStartPos();
         BlockBox blockBox = structureTemplate.calculateBoundingBox(blockPos2, blockRotation, blockPos, blockMirror);
         BlockPos blockPos3 = blockBox.getCenter();
         int i = chunkGenerator.getHeight(blockPos3.getX(), blockPos3.getZ(), RuinedPortalStructurePiece.getHeightmapType(setup.placement()), heightLimitView, noiseConfig) - 1;
         int j = getFloorHeight(chunkRandom, chunkGenerator, setup.placement(), properties.airPocket, i, blockBox.getBlockCountY(), blockBox, heightLimitView, noiseConfig);
         BlockPos blockPos4 = new BlockPos(blockPos2.getX(), j, blockPos2.getZ());
         return Optional.of(new Structure.StructurePosition(blockPos4, (collector) -> {
            if (setup.canBeCold()) {
               properties.cold = isColdAt(blockPos4, context.chunkGenerator().getBiomeSource().getBiome(BiomeCoords.fromBlock(blockPos4.getX()), BiomeCoords.fromBlock(blockPos4.getY()), BiomeCoords.fromBlock(blockPos4.getZ()), noiseConfig.getMultiNoiseSampler()), chunkGenerator.getSeaLevel());
            }

            collector.addPiece(new RuinedPortalStructurePiece(context.structureTemplateManager(), blockPos4, setup.placement(), properties, identifier, structureTemplate, blockRotation, blockMirror, blockPos));
         }));
      }
   }

   private static boolean shouldPlaceAirPocket(ChunkRandom random, float probability) {
      if (probability == 0.0F) {
         return false;
      } else if (probability == 1.0F) {
         return true;
      } else {
         return random.nextFloat() < probability;
      }
   }

   private static boolean isColdAt(BlockPos pos, RegistryEntry biome, int seaLevel) {
      return ((Biome)biome.value()).isCold(pos, seaLevel);
   }

   private static int getFloorHeight(Random random, ChunkGenerator chunkGenerator, RuinedPortalStructurePiece.VerticalPlacement verticalPlacement, boolean airPocket, int height, int blockCountY, BlockBox box, HeightLimitView world, NoiseConfig noiseConfig) {
      int i = world.getBottomY() + 15;
      int j;
      if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_NETHER) {
         if (airPocket) {
            j = MathHelper.nextBetween(random, 32, 100);
         } else if (random.nextFloat() < 0.5F) {
            j = MathHelper.nextBetween(random, 27, 29);
         } else {
            j = MathHelper.nextBetween(random, 29, 100);
         }
      } else {
         int k;
         if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.IN_MOUNTAIN) {
            k = height - blockCountY;
            j = choosePlacementHeight(random, 70, k);
         } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.UNDERGROUND) {
            k = height - blockCountY;
            j = choosePlacementHeight(random, i, k);
         } else if (verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.PARTLY_BURIED) {
            j = height - blockCountY + MathHelper.nextBetween(random, 2, 8);
         } else {
            j = height;
         }
      }

      List list = ImmutableList.of(new BlockPos(box.getMinX(), 0, box.getMinZ()), new BlockPos(box.getMaxX(), 0, box.getMinZ()), new BlockPos(box.getMinX(), 0, box.getMaxZ()), new BlockPos(box.getMaxX(), 0, box.getMaxZ()));
      List list2 = (List)list.stream().map((pos) -> {
         return chunkGenerator.getColumnSample(pos.getX(), pos.getZ(), world, noiseConfig);
      }).collect(Collectors.toList());
      Heightmap.Type type = verticalPlacement == RuinedPortalStructurePiece.VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Type.OCEAN_FLOOR_WG : Heightmap.Type.WORLD_SURFACE_WG;

      int l;
      for(l = j; l > i; --l) {
         int m = 0;
         Iterator var16 = list2.iterator();

         while(var16.hasNext()) {
            VerticalBlockSample verticalBlockSample = (VerticalBlockSample)var16.next();
            BlockState blockState = verticalBlockSample.getState(l);
            if (type.getBlockPredicate().test(blockState)) {
               ++m;
               if (m == 3) {
                  return l;
               }
            }
         }
      }

      return l;
   }

   private static int choosePlacementHeight(Random random, int min, int max) {
      return min < max ? MathHelper.nextBetween(random, min, max) : max;
   }

   public StructureType getType() {
      return StructureType.RUINED_PORTAL;
   }

   public static record Setup(RuinedPortalStructurePiece.VerticalPlacement placement, float airPocketProbability, float mossiness, boolean overgrown, boolean vines, boolean canBeCold, boolean replaceWithBlackstone, float weight) {
      public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
         return instance.group(RuinedPortalStructurePiece.VerticalPlacement.CODEC.fieldOf("placement").forGetter(Setup::placement), Codec.floatRange(0.0F, 1.0F).fieldOf("air_pocket_probability").forGetter(Setup::airPocketProbability), Codec.floatRange(0.0F, 1.0F).fieldOf("mossiness").forGetter(Setup::mossiness), Codec.BOOL.fieldOf("overgrown").forGetter(Setup::overgrown), Codec.BOOL.fieldOf("vines").forGetter(Setup::vines), Codec.BOOL.fieldOf("can_be_cold").forGetter(Setup::canBeCold), Codec.BOOL.fieldOf("replace_with_blackstone").forGetter(Setup::replaceWithBlackstone), Codecs.POSITIVE_FLOAT.fieldOf("weight").forGetter(Setup::weight)).apply(instance, Setup::new);
      });

      public Setup(RuinedPortalStructurePiece.VerticalPlacement verticalPlacement, float f, float g, boolean bl, boolean bl2, boolean bl3, boolean bl4, float h) {
         this.placement = verticalPlacement;
         this.airPocketProbability = f;
         this.mossiness = g;
         this.overgrown = bl;
         this.vines = bl2;
         this.canBeCold = bl3;
         this.replaceWithBlackstone = bl4;
         this.weight = h;
      }

      public RuinedPortalStructurePiece.VerticalPlacement placement() {
         return this.placement;
      }

      public float airPocketProbability() {
         return this.airPocketProbability;
      }

      public float mossiness() {
         return this.mossiness;
      }

      public boolean overgrown() {
         return this.overgrown;
      }

      public boolean vines() {
         return this.vines;
      }

      public boolean canBeCold() {
         return this.canBeCold;
      }

      public boolean replaceWithBlackstone() {
         return this.replaceWithBlackstone;
      }

      public float weight() {
         return this.weight;
      }
   }
}
