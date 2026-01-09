package net.minecraft.block.entity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MultifaceBlock;
import net.minecraft.block.SculkSpreadable;
import net.minecraft.block.SculkVeinBlock;
import net.minecraft.entity.Entity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class SculkSpreadManager {
   public static final int field_37609 = 24;
   public static final int MAX_CHARGE = 1000;
   public static final float field_37611 = 0.5F;
   private static final int MAX_CURSORS = 32;
   public static final int field_37612 = 11;
   public static final int MAX_CURSOR_DISTANCE = 1024;
   final boolean worldGen;
   private final TagKey replaceableTag;
   private final int extraBlockChance;
   private final int maxDistance;
   private final int spreadChance;
   private final int decayChance;
   private List cursors = new ArrayList();

   public SculkSpreadManager(boolean worldGen, TagKey replaceableTag, int extraBlockChance, int maxDistance, int spreadChance, int decayChance) {
      this.worldGen = worldGen;
      this.replaceableTag = replaceableTag;
      this.extraBlockChance = extraBlockChance;
      this.maxDistance = maxDistance;
      this.spreadChance = spreadChance;
      this.decayChance = decayChance;
   }

   public static SculkSpreadManager create() {
      return new SculkSpreadManager(false, BlockTags.SCULK_REPLACEABLE, 10, 4, 10, 5);
   }

   public static SculkSpreadManager createWorldGen() {
      return new SculkSpreadManager(true, BlockTags.SCULK_REPLACEABLE_WORLD_GEN, 50, 1, 5, 10);
   }

   public TagKey getReplaceableTag() {
      return this.replaceableTag;
   }

   public int getExtraBlockChance() {
      return this.extraBlockChance;
   }

   public int getMaxDistance() {
      return this.maxDistance;
   }

   public int getSpreadChance() {
      return this.spreadChance;
   }

   public int getDecayChance() {
      return this.decayChance;
   }

   public boolean isWorldGen() {
      return this.worldGen;
   }

   @VisibleForTesting
   public List getCursors() {
      return this.cursors;
   }

   public void clearCursors() {
      this.cursors.clear();
   }

   public void readData(ReadView view) {
      this.cursors.clear();
      ((List)view.read("cursors", SculkSpreadManager.Cursor.CODEC.sizeLimitedListOf(32)).orElse(List.of())).forEach(this::addCursor);
   }

   public void writeData(WriteView view) {
      view.put("cursors", SculkSpreadManager.Cursor.CODEC.listOf(), this.cursors);
   }

   public void spread(BlockPos pos, int charge) {
      while(charge > 0) {
         int i = Math.min(charge, 1000);
         this.addCursor(new Cursor(pos, i));
         charge -= i;
      }

   }

   private void addCursor(Cursor cursor) {
      if (this.cursors.size() < 32) {
         this.cursors.add(cursor);
      }
   }

   public void tick(WorldAccess world, BlockPos pos, Random random, boolean shouldConvertToBlock) {
      if (!this.cursors.isEmpty()) {
         List list = new ArrayList();
         Map map = new HashMap();
         Object2IntMap object2IntMap = new Object2IntOpenHashMap();
         Iterator var8 = this.cursors.iterator();

         while(true) {
            while(true) {
               Cursor cursor;
               BlockPos blockPos;
               do {
                  if (!var8.hasNext()) {
                     ObjectIterator var16 = object2IntMap.object2IntEntrySet().iterator();

                     while(var16.hasNext()) {
                        Object2IntMap.Entry entry = (Object2IntMap.Entry)var16.next();
                        blockPos = (BlockPos)entry.getKey();
                        int i = entry.getIntValue();
                        Cursor cursor3 = (Cursor)map.get(blockPos);
                        Collection collection = cursor3 == null ? null : cursor3.getFaces();
                        if (i > 0 && collection != null) {
                           int j = (int)(Math.log1p((double)i) / 2.299999952316284) + 1;
                           int k = (j << 6) + MultifaceBlock.directionsToFlag(collection);
                           world.syncWorldEvent(3006, blockPos, k);
                        }
                     }

                     this.cursors = list;
                     return;
                  }

                  cursor = (Cursor)var8.next();
               } while(cursor.isTooFarFrom(pos));

               cursor.spread(world, pos, random, this, shouldConvertToBlock);
               if (cursor.charge <= 0) {
                  world.syncWorldEvent(3006, cursor.getPos(), 0);
               } else {
                  blockPos = cursor.getPos();
                  object2IntMap.computeInt(blockPos, (posx, charge) -> {
                     return (charge == null ? 0 : charge) + cursor.charge;
                  });
                  Cursor cursor2 = (Cursor)map.get(blockPos);
                  if (cursor2 == null) {
                     map.put(blockPos, cursor);
                     list.add(cursor);
                  } else if (!this.isWorldGen() && cursor.charge + cursor2.charge <= 1000) {
                     cursor2.merge(cursor);
                  } else {
                     list.add(cursor);
                     if (cursor.charge < cursor2.charge) {
                        map.put(blockPos, cursor);
                     }
                  }
               }
            }
         }
      }
   }

   // $FF: synthetic method
   private static Integer method_51355(Cursor cursor) {
      return 1;
   }

   public static class Cursor {
      private static final ObjectArrayList OFFSETS = (ObjectArrayList)Util.make(new ObjectArrayList(18), (list) -> {
         Stream var10000 = BlockPos.stream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1)).filter((pos) -> {
            return (pos.getX() == 0 || pos.getY() == 0 || pos.getZ() == 0) && !pos.equals(BlockPos.ORIGIN);
         }).map(BlockPos::toImmutable);
         Objects.requireNonNull(list);
         var10000.forEach(list::add);
      });
      public static final int field_37622 = 1;
      private BlockPos pos;
      int charge;
      private int update;
      private int decay;
      @Nullable
      private Set faces;
      private static final Codec DIRECTION_SET_CODEC;
      public static final Codec CODEC;

      private Cursor(BlockPos pos, int charge, int decay, int update, Optional faces) {
         this.pos = pos;
         this.charge = charge;
         this.decay = decay;
         this.update = update;
         this.faces = (Set)faces.orElse((Object)null);
      }

      public Cursor(BlockPos pos, int charge) {
         this(pos, charge, 1, 0, Optional.empty());
      }

      public BlockPos getPos() {
         return this.pos;
      }

      boolean isTooFarFrom(BlockPos pos) {
         return this.pos.getChebyshevDistance(pos) > 1024;
      }

      public int getCharge() {
         return this.charge;
      }

      public int getDecay() {
         return this.decay;
      }

      @Nullable
      public Set getFaces() {
         return this.faces;
      }

      private boolean canSpread(WorldAccess world, BlockPos pos, boolean worldGen) {
         if (this.charge <= 0) {
            return false;
         } else if (worldGen) {
            return true;
         } else if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld)world;
            return serverWorld.shouldTickBlockPos(pos);
         } else {
            return false;
         }
      }

      public void spread(WorldAccess world, BlockPos pos, Random random, SculkSpreadManager spreadManager, boolean shouldConvertToBlock) {
         if (this.canSpread(world, pos, spreadManager.worldGen)) {
            if (this.update > 0) {
               --this.update;
            } else {
               BlockState blockState = world.getBlockState(this.pos);
               SculkSpreadable sculkSpreadable = getSpreadable(blockState);
               if (shouldConvertToBlock && sculkSpreadable.spread(world, this.pos, blockState, this.faces, spreadManager.isWorldGen())) {
                  if (sculkSpreadable.shouldConvertToSpreadable()) {
                     blockState = world.getBlockState(this.pos);
                     sculkSpreadable = getSpreadable(blockState);
                  }

                  world.playSound((Entity)null, this.pos, SoundEvents.BLOCK_SCULK_SPREAD, SoundCategory.BLOCKS, 1.0F, 1.0F);
               }

               this.charge = sculkSpreadable.spread(this, world, pos, random, spreadManager, shouldConvertToBlock);
               if (this.charge <= 0) {
                  sculkSpreadable.spreadAtSamePosition(world, blockState, this.pos, random);
               } else {
                  BlockPos blockPos = getSpreadPos(world, this.pos, random);
                  if (blockPos != null) {
                     sculkSpreadable.spreadAtSamePosition(world, blockState, this.pos, random);
                     this.pos = blockPos.toImmutable();
                     if (spreadManager.isWorldGen() && !this.pos.isWithinDistance(new Vec3i(pos.getX(), this.pos.getY(), pos.getZ()), 15.0)) {
                        this.charge = 0;
                        return;
                     }

                     blockState = world.getBlockState(blockPos);
                  }

                  if (blockState.getBlock() instanceof SculkSpreadable) {
                     this.faces = MultifaceBlock.collectDirections(blockState);
                  }

                  this.decay = sculkSpreadable.getDecay(this.decay);
                  this.update = sculkSpreadable.getUpdate();
               }
            }
         }
      }

      void merge(Cursor cursor) {
         this.charge += cursor.charge;
         cursor.charge = 0;
         this.update = Math.min(this.update, cursor.update);
      }

      private static SculkSpreadable getSpreadable(BlockState state) {
         Block var2 = state.getBlock();
         SculkSpreadable var10000;
         if (var2 instanceof SculkSpreadable sculkSpreadable) {
            var10000 = sculkSpreadable;
         } else {
            var10000 = SculkSpreadable.VEIN_ONLY_SPREADER;
         }

         return var10000;
      }

      private static List shuffleOffsets(Random random) {
         return Util.copyShuffled(OFFSETS, random);
      }

      @Nullable
      private static BlockPos getSpreadPos(WorldAccess world, BlockPos pos, Random random) {
         BlockPos.Mutable mutable = pos.mutableCopy();
         BlockPos.Mutable mutable2 = pos.mutableCopy();
         Iterator var5 = shuffleOffsets(random).iterator();

         while(var5.hasNext()) {
            Vec3i vec3i = (Vec3i)var5.next();
            mutable2.set(pos, (Vec3i)vec3i);
            BlockState blockState = world.getBlockState(mutable2);
            if (blockState.getBlock() instanceof SculkSpreadable && canSpread(world, pos, (BlockPos)mutable2)) {
               mutable.set(mutable2);
               if (SculkVeinBlock.veinCoversSculkReplaceable(world, blockState, mutable2)) {
                  break;
               }
            }
         }

         return mutable.equals(pos) ? null : mutable;
      }

      private static boolean canSpread(WorldAccess world, BlockPos sourcePos, BlockPos targetPos) {
         if (sourcePos.getManhattanDistance(targetPos) == 1) {
            return true;
         } else {
            BlockPos blockPos = targetPos.subtract(sourcePos);
            Direction direction = Direction.from(Direction.Axis.X, blockPos.getX() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction2 = Direction.from(Direction.Axis.Y, blockPos.getY() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            Direction direction3 = Direction.from(Direction.Axis.Z, blockPos.getZ() < 0 ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE);
            if (blockPos.getX() == 0) {
               return canSpread(world, sourcePos, direction2) || canSpread(world, sourcePos, direction3);
            } else if (blockPos.getY() == 0) {
               return canSpread(world, sourcePos, direction) || canSpread(world, sourcePos, direction3);
            } else {
               return canSpread(world, sourcePos, direction) || canSpread(world, sourcePos, direction2);
            }
         }
      }

      private static boolean canSpread(WorldAccess world, BlockPos pos, Direction direction) {
         BlockPos blockPos = pos.offset(direction);
         return !world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction.getOpposite());
      }

      static {
         DIRECTION_SET_CODEC = Direction.CODEC.listOf().xmap((directions) -> {
            return Sets.newEnumSet(directions, Direction.class);
         }, Lists::newArrayList);
         CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(BlockPos.CODEC.fieldOf("pos").forGetter(Cursor::getPos), Codec.intRange(0, 1000).fieldOf("charge").orElse(0).forGetter(Cursor::getCharge), Codec.intRange(0, 1).fieldOf("decay_delay").orElse(1).forGetter(Cursor::getDecay), Codec.intRange(0, Integer.MAX_VALUE).fieldOf("update_delay").orElse(0).forGetter((cursor) -> {
               return cursor.update;
            }), DIRECTION_SET_CODEC.lenientOptionalFieldOf("facings").forGetter((cursor) -> {
               return Optional.ofNullable(cursor.getFaces());
            })).apply(instance, Cursor::new);
         });
      }
   }
}
