package net.minecraft.world.gen.feature;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;
import org.slf4j.Logger;

public class DungeonFeature extends Feature {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityType[] MOB_SPAWNER_ENTITIES;
   private static final BlockState AIR;

   public DungeonFeature(Codec codec) {
      super(codec);
   }

   public boolean generate(FeatureContext context) {
      Predicate predicate = Feature.notInBlockTagPredicate(BlockTags.FEATURES_CANNOT_REPLACE);
      BlockPos blockPos = context.getOrigin();
      Random random = context.getRandom();
      StructureWorldAccess structureWorldAccess = context.getWorld();
      int i = true;
      int j = random.nextInt(2) + 2;
      int k = -j - 1;
      int l = j + 1;
      int m = true;
      int n = true;
      int o = random.nextInt(2) + 2;
      int p = -o - 1;
      int q = o + 1;
      int r = 0;

      int s;
      int t;
      int u;
      BlockPos blockPos2;
      for(s = k; s <= l; ++s) {
         for(t = -1; t <= 4; ++t) {
            for(u = p; u <= q; ++u) {
               blockPos2 = blockPos.add(s, t, u);
               boolean bl = structureWorldAccess.getBlockState(blockPos2).isSolid();
               if (t == -1 && !bl) {
                  return false;
               }

               if (t == 4 && !bl) {
                  return false;
               }

               if ((s == k || s == l || u == p || u == q) && t == 0 && structureWorldAccess.isAir(blockPos2) && structureWorldAccess.isAir(blockPos2.up())) {
                  ++r;
               }
            }
         }
      }

      if (r >= 1 && r <= 5) {
         for(s = k; s <= l; ++s) {
            for(t = 3; t >= -1; --t) {
               for(u = p; u <= q; ++u) {
                  blockPos2 = blockPos.add(s, t, u);
                  BlockState blockState = structureWorldAccess.getBlockState(blockPos2);
                  if (s != k && t != -1 && u != p && s != l && t != 4 && u != q) {
                     if (!blockState.isOf(Blocks.CHEST) && !blockState.isOf(Blocks.SPAWNER)) {
                        this.setBlockStateIf(structureWorldAccess, blockPos2, AIR, predicate);
                     }
                  } else if (blockPos2.getY() >= structureWorldAccess.getBottomY() && !structureWorldAccess.getBlockState(blockPos2.down()).isSolid()) {
                     structureWorldAccess.setBlockState(blockPos2, AIR, 2);
                  } else if (blockState.isSolid() && !blockState.isOf(Blocks.CHEST)) {
                     if (t == -1 && random.nextInt(4) != 0) {
                        this.setBlockStateIf(structureWorldAccess, blockPos2, Blocks.MOSSY_COBBLESTONE.getDefaultState(), predicate);
                     } else {
                        this.setBlockStateIf(structureWorldAccess, blockPos2, Blocks.COBBLESTONE.getDefaultState(), predicate);
                     }
                  }
               }
            }
         }

         for(s = 0; s < 2; ++s) {
            for(t = 0; t < 3; ++t) {
               u = blockPos.getX() + random.nextInt(j * 2 + 1) - j;
               int v = blockPos.getY();
               int w = blockPos.getZ() + random.nextInt(o * 2 + 1) - o;
               BlockPos blockPos3 = new BlockPos(u, v, w);
               if (structureWorldAccess.isAir(blockPos3)) {
                  int x = 0;
                  Iterator var23 = Direction.Type.HORIZONTAL.iterator();

                  while(var23.hasNext()) {
                     Direction direction = (Direction)var23.next();
                     if (structureWorldAccess.getBlockState(blockPos3.offset(direction)).isSolid()) {
                        ++x;
                     }
                  }

                  if (x == 1) {
                     this.setBlockStateIf(structureWorldAccess, blockPos3, StructurePiece.orientateChest(structureWorldAccess, blockPos3, Blocks.CHEST.getDefaultState()), predicate);
                     LootableInventory.setLootTable(structureWorldAccess, random, blockPos3, LootTables.SIMPLE_DUNGEON_CHEST);
                     break;
                  }
               }
            }
         }

         this.setBlockStateIf(structureWorldAccess, blockPos, Blocks.SPAWNER.getDefaultState(), predicate);
         BlockEntity blockEntity = structureWorldAccess.getBlockEntity(blockPos);
         if (blockEntity instanceof MobSpawnerBlockEntity) {
            MobSpawnerBlockEntity mobSpawnerBlockEntity = (MobSpawnerBlockEntity)blockEntity;
            mobSpawnerBlockEntity.setEntityType(this.getMobSpawnerEntity(random), random);
         } else {
            LOGGER.error("Failed to fetch mob spawner entity at ({}, {}, {})", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
         }

         return true;
      } else {
         return false;
      }
   }

   private EntityType getMobSpawnerEntity(Random random) {
      return (EntityType)Util.getRandom((Object[])MOB_SPAWNER_ENTITIES, random);
   }

   static {
      MOB_SPAWNER_ENTITIES = new EntityType[]{EntityType.SKELETON, EntityType.ZOMBIE, EntityType.ZOMBIE, EntityType.SPIDER};
      AIR = Blocks.CAVE_AIR.getDefaultState();
   }
}
