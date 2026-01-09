package net.minecraft.datafixer.fix;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public class ProtoChunkTickListFix extends DataFix {
   private static final int CHUNK_EDGE_LENGTH = 16;
   private static final ImmutableSet ALWAYS_WATERLOGGED_BLOCK_IDS = ImmutableSet.of("minecraft:bubble_column", "minecraft:kelp", "minecraft:kelp_plant", "minecraft:seagrass", "minecraft:tall_seagrass");

   public ProtoChunkTickListFix(Schema outputSchema) {
      super(outputSchema, false);
   }

   protected TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
      OpticFinder opticFinder = type.findField("Level");
      OpticFinder opticFinder2 = opticFinder.type().findField("Sections");
      OpticFinder opticFinder3 = ((List.ListType)opticFinder2.type()).getElement().finder();
      OpticFinder opticFinder4 = opticFinder3.type().findField("block_states");
      OpticFinder opticFinder5 = opticFinder3.type().findField("biomes");
      OpticFinder opticFinder6 = opticFinder4.type().findField("palette");
      OpticFinder opticFinder7 = opticFinder.type().findField("TileTicks");
      return this.fixTypeEverywhereTyped("ChunkProtoTickListFix", type, (chunkTyped) -> {
         return chunkTyped.updateTyped(opticFinder, (levelTyped) -> {
            levelTyped = levelTyped.update(DSL.remainderFinder(), (levelDynamic) -> {
               return (Dynamic)DataFixUtils.orElse(levelDynamic.get("LiquidTicks").result().map((liquidTicksDynamic) -> {
                  return levelDynamic.set("fluid_ticks", liquidTicksDynamic).remove("LiquidTicks");
               }), levelDynamic);
            });
            Dynamic dynamic = (Dynamic)levelTyped.get(DSL.remainderFinder());
            MutableInt mutableInt = new MutableInt();
            Int2ObjectMap int2ObjectMap = new Int2ObjectArrayMap();
            levelTyped.getOptionalTyped(opticFinder2).ifPresent((sectionsTyped) -> {
               sectionsTyped.getAllTyped(opticFinder3).forEach((sectionTyped) -> {
                  Dynamic dynamic = (Dynamic)sectionTyped.get(DSL.remainderFinder());
                  int i = dynamic.get("Y").asInt(Integer.MAX_VALUE);
                  if (i != Integer.MAX_VALUE) {
                     if (sectionTyped.getOptionalTyped(opticFinder5).isPresent()) {
                        mutableInt.setValue(Math.min(i, mutableInt.getValue()));
                     }

                     sectionTyped.getOptionalTyped(opticFinder4).ifPresent((blockStatesTyped) -> {
                        int2ObjectMap.put(i, Suppliers.memoize(() -> {
                           java.util.List list = (java.util.List)blockStatesTyped.getOptionalTyped(opticFinder6).map((paletteTyped) -> {
                              return (java.util.List)paletteTyped.write().result().map((paletteDynamic) -> {
                                 return paletteDynamic.asList(Function.identity());
                              }).orElse(Collections.emptyList());
                           }).orElse(Collections.emptyList());
                           long[] ls = ((Dynamic)blockStatesTyped.get(DSL.remainderFinder())).get("data").asLongStream().toArray();
                           return new PalettedSection(list, ls);
                        }));
                     });
                  }
               });
            });
            byte b = mutableInt.getValue().byteValue();
            levelTyped = levelTyped.update(DSL.remainderFinder(), (levelDynamic) -> {
               return levelDynamic.update("yPos", (yDynamic) -> {
                  return yDynamic.createByte(b);
               });
            });
            if (!levelTyped.getOptionalTyped(opticFinder7).isPresent() && !dynamic.get("fluid_ticks").result().isPresent()) {
               int i = dynamic.get("xPos").asInt(0);
               int j = dynamic.get("zPos").asInt(0);
               Dynamic dynamic2 = this.fixToBeTicked(dynamic, int2ObjectMap, b, i, j, "LiquidsToBeTicked", ProtoChunkTickListFix::getFluidBlockIdToBeTicked);
               Dynamic dynamic3 = this.fixToBeTicked(dynamic, int2ObjectMap, b, i, j, "ToBeTicked", ProtoChunkTickListFix::getBlockIdToBeTicked);
               Optional optional = opticFinder7.type().readTyped(dynamic3).result();
               if (optional.isPresent()) {
                  levelTyped = levelTyped.set(opticFinder7, (Typed)((Pair)optional.get()).getFirst());
               }

               return levelTyped.update(DSL.remainderFinder(), (levelDynamic) -> {
                  return levelDynamic.remove("ToBeTicked").remove("LiquidsToBeTicked").set("fluid_ticks", dynamic2);
               });
            } else {
               return levelTyped;
            }
         });
      });
   }

   private Dynamic fixToBeTicked(Dynamic levelDynamic, Int2ObjectMap palettedSectionsByY, byte sectionY, int localX, int localZ, String key, Function blockIdGetter) {
      Stream stream = Stream.empty();
      java.util.List list = levelDynamic.get(key).asList(Function.identity());

      for(int i = 0; i < list.size(); ++i) {
         int j = i + sectionY;
         Supplier supplier = (Supplier)palettedSectionsByY.get(j);
         Stream stream2 = ((Dynamic)list.get(i)).asStream().mapToInt((posDynamic) -> {
            return posDynamic.asShort((short)-1);
         }).filter((packedLocalPos) -> {
            return packedLocalPos > 0;
         }).mapToObj((packedLocalPos) -> {
            return this.createTileTickObject(levelDynamic, supplier, localX, j, localZ, packedLocalPos, blockIdGetter);
         });
         stream = Stream.concat(stream, stream2);
      }

      return levelDynamic.createList(stream);
   }

   private static String getBlockIdToBeTicked(@Nullable Dynamic blockStateDynamic) {
      return blockStateDynamic != null ? blockStateDynamic.get("Name").asString("minecraft:air") : "minecraft:air";
   }

   private static String getFluidBlockIdToBeTicked(@Nullable Dynamic blockStateDynamic) {
      if (blockStateDynamic == null) {
         return "minecraft:empty";
      } else {
         String string = blockStateDynamic.get("Name").asString("");
         if ("minecraft:water".equals(string)) {
            return blockStateDynamic.get("Properties").get("level").asInt(0) == 0 ? "minecraft:water" : "minecraft:flowing_water";
         } else if ("minecraft:lava".equals(string)) {
            return blockStateDynamic.get("Properties").get("level").asInt(0) == 0 ? "minecraft:lava" : "minecraft:flowing_lava";
         } else {
            return !ALWAYS_WATERLOGGED_BLOCK_IDS.contains(string) && !blockStateDynamic.get("Properties").get("waterlogged").asBoolean(false) ? "minecraft:empty" : "minecraft:water";
         }
      }
   }

   private Dynamic createTileTickObject(Dynamic levelDynamic, @Nullable Supplier sectionSupplier, int sectionX, int sectionY, int sectionZ, int packedLocalPos, Function blockIdGetter) {
      int i = packedLocalPos & 15;
      int j = packedLocalPos >>> 4 & 15;
      int k = packedLocalPos >>> 8 & 15;
      String string = (String)blockIdGetter.apply(sectionSupplier != null ? ((PalettedSection)sectionSupplier.get()).get(i, j, k) : null);
      return levelDynamic.createMap(ImmutableMap.builder().put(levelDynamic.createString("i"), levelDynamic.createString(string)).put(levelDynamic.createString("x"), levelDynamic.createInt(sectionX * 16 + i)).put(levelDynamic.createString("y"), levelDynamic.createInt(sectionY * 16 + j)).put(levelDynamic.createString("z"), levelDynamic.createInt(sectionZ * 16 + k)).put(levelDynamic.createString("t"), levelDynamic.createInt(0)).put(levelDynamic.createString("p"), levelDynamic.createInt(0)).build());
   }

   public static final class PalettedSection {
      private static final long MIN_UNIT_SIZE = 4L;
      private final java.util.List palette;
      private final long[] data;
      private final int unitSize;
      private final long unitMask;
      private final int unitsPerLong;

      public PalettedSection(java.util.List palette, long[] data) {
         this.palette = palette;
         this.data = data;
         this.unitSize = Math.max(4, ChunkHeightAndBiomeFix.ceilLog2(palette.size()));
         this.unitMask = (1L << this.unitSize) - 1L;
         this.unitsPerLong = (char)(64 / this.unitSize);
      }

      @Nullable
      public Dynamic get(int localX, int localY, int localZ) {
         int i = this.palette.size();
         if (i < 1) {
            return null;
         } else if (i == 1) {
            return (Dynamic)this.palette.get(0);
         } else {
            int j = this.packLocalPos(localX, localY, localZ);
            int k = j / this.unitsPerLong;
            if (k >= 0 && k < this.data.length) {
               long l = this.data[k];
               int m = (j - k * this.unitsPerLong) * this.unitSize;
               int n = (int)(l >> m & this.unitMask);
               return n >= 0 && n < i ? (Dynamic)this.palette.get(n) : null;
            } else {
               return null;
            }
         }
      }

      private int packLocalPos(int localX, int localY, int localZ) {
         return (localY << 4 | localZ) << 4 | localX;
      }

      public java.util.List getPalette() {
         return this.palette;
      }

      public long[] getData() {
         return this.data;
      }
   }
}
