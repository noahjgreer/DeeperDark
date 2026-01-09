package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.visitor.NbtOrderedStringFormatter;
import net.minecraft.nbt.visitor.NbtTextFormatter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class NbtHelper {
   private static final Comparator BLOCK_POS_COMPARATOR = Comparator.comparingInt((nbt) -> {
      return nbt.getInt(1, 0);
   }).thenComparingInt((nbt) -> {
      return nbt.getInt(0, 0);
   }).thenComparingInt((nbt) -> {
      return nbt.getInt(2, 0);
   });
   private static final Comparator ENTITY_POS_COMPARATOR = Comparator.comparingDouble((nbt) -> {
      return nbt.getDouble(1, 0.0);
   }).thenComparingDouble((nbt) -> {
      return nbt.getDouble(0, 0.0);
   }).thenComparingDouble((nbt) -> {
      return nbt.getDouble(2, 0.0);
   });
   private static final Codec BLOCK_KEY_CODEC;
   public static final String DATA_KEY = "data";
   private static final char LEFT_CURLY_BRACKET = '{';
   private static final char RIGHT_CURLY_BRACKET = '}';
   private static final String COMMA = ",";
   private static final char COLON = ':';
   private static final Splitter COMMA_SPLITTER;
   private static final Splitter COLON_SPLITTER;
   private static final Logger LOGGER;
   private static final int field_33229 = 2;
   private static final int field_33230 = -1;

   private NbtHelper() {
   }

   @VisibleForTesting
   public static boolean matches(@Nullable NbtElement standard, @Nullable NbtElement subject, boolean ignoreListOrder) {
      if (standard == subject) {
         return true;
      } else if (standard == null) {
         return true;
      } else if (subject == null) {
         return false;
      } else if (!standard.getClass().equals(subject.getClass())) {
         return false;
      } else {
         Iterator var6;
         if (standard instanceof NbtCompound) {
            NbtCompound nbtCompound = (NbtCompound)standard;
            NbtCompound nbtCompound2 = (NbtCompound)subject;
            if (nbtCompound2.getSize() < nbtCompound.getSize()) {
               return false;
            } else {
               var6 = nbtCompound.entrySet().iterator();

               Map.Entry entry;
               NbtElement nbtElement;
               do {
                  if (!var6.hasNext()) {
                     return true;
                  }

                  entry = (Map.Entry)var6.next();
                  nbtElement = (NbtElement)entry.getValue();
               } while(matches(nbtElement, nbtCompound2.get((String)entry.getKey()), ignoreListOrder));

               return false;
            }
         } else {
            if (standard instanceof NbtList) {
               NbtList nbtList = (NbtList)standard;
               if (ignoreListOrder) {
                  NbtList nbtList2 = (NbtList)subject;
                  if (nbtList.isEmpty()) {
                     return nbtList2.isEmpty();
                  }

                  if (nbtList2.size() < nbtList.size()) {
                     return false;
                  }

                  var6 = nbtList.iterator();

                  boolean bl;
                  do {
                     if (!var6.hasNext()) {
                        return true;
                     }

                     NbtElement nbtElement2 = (NbtElement)var6.next();
                     bl = false;
                     Iterator var9 = nbtList2.iterator();

                     while(var9.hasNext()) {
                        NbtElement nbtElement3 = (NbtElement)var9.next();
                        if (matches(nbtElement2, nbtElement3, ignoreListOrder)) {
                           bl = true;
                           break;
                        }
                     }
                  } while(bl);

                  return false;
               }
            }

            return standard.equals(subject);
         }
      }
   }

   public static BlockState toBlockState(RegistryEntryLookup blockLookup, NbtCompound nbt) {
      Optional var10000 = nbt.get("Name", BLOCK_KEY_CODEC);
      Objects.requireNonNull(blockLookup);
      Optional optional = var10000.flatMap(blockLookup::getOptional);
      if (optional.isEmpty()) {
         return Blocks.AIR.getDefaultState();
      } else {
         Block block = (Block)((RegistryEntry)optional.get()).value();
         BlockState blockState = block.getDefaultState();
         Optional optional2 = nbt.getCompound("Properties");
         if (optional2.isPresent()) {
            StateManager stateManager = block.getStateManager();
            Iterator var7 = ((NbtCompound)optional2.get()).getKeys().iterator();

            while(var7.hasNext()) {
               String string = (String)var7.next();
               Property property = stateManager.getProperty(string);
               if (property != null) {
                  blockState = (BlockState)withProperty(blockState, property, string, (NbtCompound)optional2.get(), nbt);
               }
            }
         }

         return blockState;
      }
   }

   private static State withProperty(State state, Property property, String key, NbtCompound properties, NbtCompound root) {
      Optional var10000 = properties.getString(key);
      Objects.requireNonNull(property);
      Optional optional = var10000.flatMap(property::parse);
      if (optional.isPresent()) {
         return (State)state.with(property, (Comparable)optional.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for blockstate: {}", new Object[]{key, properties.get(key), root});
         return state;
      }
   }

   public static NbtCompound fromBlockState(BlockState state) {
      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.putString("Name", Registries.BLOCK.getId(state.getBlock()).toString());
      Map map = state.getEntries();
      if (!map.isEmpty()) {
         NbtCompound nbtCompound2 = new NbtCompound();
         Iterator var4 = map.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            Property property = (Property)entry.getKey();
            nbtCompound2.putString(property.getName(), nameValue(property, (Comparable)entry.getValue()));
         }

         nbtCompound.put("Properties", nbtCompound2);
      }

      return nbtCompound;
   }

   public static NbtCompound fromFluidState(FluidState state) {
      NbtCompound nbtCompound = new NbtCompound();
      nbtCompound.putString("Name", Registries.FLUID.getId(state.getFluid()).toString());
      Map map = state.getEntries();
      if (!map.isEmpty()) {
         NbtCompound nbtCompound2 = new NbtCompound();
         Iterator var4 = map.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry entry = (Map.Entry)var4.next();
            Property property = (Property)entry.getKey();
            nbtCompound2.putString(property.getName(), nameValue(property, (Comparable)entry.getValue()));
         }

         nbtCompound.put("Properties", nbtCompound2);
      }

      return nbtCompound;
   }

   private static String nameValue(Property property, Comparable value) {
      return property.name(value);
   }

   public static String toFormattedString(NbtElement nbt) {
      return toFormattedString(nbt, false);
   }

   public static String toFormattedString(NbtElement nbt, boolean withArrayContents) {
      return appendFormattedString(new StringBuilder(), nbt, 0, withArrayContents).toString();
   }

   public static StringBuilder appendFormattedString(StringBuilder stringBuilder, NbtElement nbt, int depth, boolean withArrayContents) {
      Objects.requireNonNull(nbt);
      byte var5 = 0;
      int o;
      StringBuilder var10000;
      int i;
      int j;
      int m;
      switch (nbt.typeSwitch<invokedynamic>(nbt, var5)) {
         case 0:
            NbtPrimitive nbtPrimitive = (NbtPrimitive)nbt;
            var10000 = stringBuilder.append(nbtPrimitive);
            break;
         case 1:
            NbtEnd nbtEnd = (NbtEnd)nbt;
            var10000 = stringBuilder;
            break;
         case 2:
            NbtByteArray nbtByteArray = (NbtByteArray)nbt;
            byte[] bs = nbtByteArray.getByteArray();
            i = bs.length;
            appendIndent(depth, stringBuilder).append("byte[").append(i).append("] {\n");
            if (!withArrayContents) {
               appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               appendIndent(depth + 1, stringBuilder);

               for(j = 0; j < bs.length; ++j) {
                  if (j != 0) {
                     stringBuilder.append(',');
                  }

                  if (j % 16 == 0 && j / 16 > 0) {
                     stringBuilder.append('\n');
                     if (j < bs.length) {
                        appendIndent(depth + 1, stringBuilder);
                     }
                  } else if (j != 0) {
                     stringBuilder.append(' ');
                  }

                  stringBuilder.append(String.format(Locale.ROOT, "0x%02X", bs[j] & 255));
               }
            }

            stringBuilder.append('\n');
            appendIndent(depth, stringBuilder).append('}');
            var10000 = stringBuilder;
            break;
         case 3:
            NbtList nbtList = (NbtList)nbt;
            i = nbtList.size();
            appendIndent(depth, stringBuilder).append("list").append("[").append(i).append("] [");
            if (i != 0) {
               stringBuilder.append('\n');
            }

            for(j = 0; j < i; ++j) {
               if (j != 0) {
                  stringBuilder.append(",\n");
               }

               appendIndent(depth + 1, stringBuilder);
               appendFormattedString(stringBuilder, nbtList.method_10534(j), depth + 1, withArrayContents);
            }

            if (i != 0) {
               stringBuilder.append('\n');
            }

            appendIndent(depth, stringBuilder).append(']');
            var10000 = stringBuilder;
            break;
         case 4:
            NbtIntArray nbtIntArray = (NbtIntArray)nbt;
            int[] is = nbtIntArray.getIntArray();
            int k = 0;
            int[] var28 = is;
            int n = is.length;

            for(o = 0; o < n; ++o) {
               int l = var28[o];
               k = Math.max(k, String.format(Locale.ROOT, "%X", l).length());
            }

            m = is.length;
            appendIndent(depth, stringBuilder).append("int[").append(m).append("] {\n");
            if (!withArrayContents) {
               appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               appendIndent(depth + 1, stringBuilder);

               for(n = 0; n < is.length; ++n) {
                  if (n != 0) {
                     stringBuilder.append(',');
                  }

                  if (n % 16 == 0 && n / 16 > 0) {
                     stringBuilder.append('\n');
                     if (n < is.length) {
                        appendIndent(depth + 1, stringBuilder);
                     }
                  } else if (n != 0) {
                     stringBuilder.append(' ');
                  }

                  stringBuilder.append(String.format(Locale.ROOT, "0x%0" + k + "X", is[n]));
               }
            }

            stringBuilder.append('\n');
            appendIndent(depth, stringBuilder).append('}');
            var10000 = stringBuilder;
            break;
         case 5:
            NbtCompound nbtCompound = (NbtCompound)nbt;
            List list = Lists.newArrayList(nbtCompound.getKeys());
            Collections.sort(list);
            appendIndent(depth, stringBuilder).append('{');
            if (stringBuilder.length() - stringBuilder.lastIndexOf("\n") > 2 * (depth + 1)) {
               stringBuilder.append('\n');
               appendIndent(depth + 1, stringBuilder);
            }

            m = list.stream().mapToInt(String::length).max().orElse(0);
            String string = Strings.repeat(" ", m);

            for(o = 0; o < list.size(); ++o) {
               if (o != 0) {
                  stringBuilder.append(",\n");
               }

               String string2 = (String)list.get(o);
               appendIndent(depth + 1, stringBuilder).append('"').append(string2).append('"').append(string, 0, string.length() - string2.length()).append(": ");
               appendFormattedString(stringBuilder, nbtCompound.get(string2), depth + 1, withArrayContents);
            }

            if (!list.isEmpty()) {
               stringBuilder.append('\n');
            }

            appendIndent(depth, stringBuilder).append('}');
            var10000 = stringBuilder;
            break;
         case 6:
            NbtLongArray nbtLongArray = (NbtLongArray)nbt;
            long[] ls = nbtLongArray.getLongArray();
            long p = 0L;
            long[] var16 = ls;
            int var17 = ls.length;

            int s;
            for(s = 0; s < var17; ++s) {
               long q = var16[s];
               p = Math.max(p, (long)String.format(Locale.ROOT, "%X", q).length());
            }

            long r = (long)ls.length;
            appendIndent(depth, stringBuilder).append("long[").append(r).append("] {\n");
            if (!withArrayContents) {
               appendIndent(depth + 1, stringBuilder).append(" // Skipped, supply withBinaryBlobs true");
            } else {
               appendIndent(depth + 1, stringBuilder);

               for(s = 0; s < ls.length; ++s) {
                  if (s != 0) {
                     stringBuilder.append(',');
                  }

                  if (s % 16 == 0 && s / 16 > 0) {
                     stringBuilder.append('\n');
                     if (s < ls.length) {
                        appendIndent(depth + 1, stringBuilder);
                     }
                  } else if (s != 0) {
                     stringBuilder.append(' ');
                  }

                  stringBuilder.append(String.format(Locale.ROOT, "0x%0" + p + "X", ls[s]));
               }
            }

            stringBuilder.append('\n');
            appendIndent(depth, stringBuilder).append('}');
            var10000 = stringBuilder;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private static StringBuilder appendIndent(int depth, StringBuilder stringBuilder) {
      int i = stringBuilder.lastIndexOf("\n") + 1;
      int j = stringBuilder.length() - i;

      for(int k = 0; k < 2 * depth - j; ++k) {
         stringBuilder.append(' ');
      }

      return stringBuilder;
   }

   public static Text toPrettyPrintedText(NbtElement element) {
      return (new NbtTextFormatter("")).apply(element);
   }

   public static String toNbtProviderString(NbtCompound compound) {
      return (new NbtOrderedStringFormatter()).apply(toNbtProviderFormat(compound));
   }

   public static NbtCompound fromNbtProviderString(String string) throws CommandSyntaxException {
      return fromNbtProviderFormat(StringNbtReader.readCompound(string));
   }

   @VisibleForTesting
   static NbtCompound toNbtProviderFormat(NbtCompound compound) {
      Optional optional = compound.getList("palettes");
      NbtList nbtList;
      if (optional.isPresent()) {
         nbtList = ((NbtList)optional.get()).getListOrEmpty(0);
      } else {
         nbtList = compound.getListOrEmpty("palette");
      }

      NbtList nbtList2 = (NbtList)nbtList.streamCompounds().map(NbtHelper::toNbtProviderFormattedPalette).map(NbtString::of).collect(Collectors.toCollection(NbtList::new));
      compound.put("palette", nbtList2);
      if (optional.isPresent()) {
         NbtList nbtList3 = new NbtList();
         ((NbtList)optional.get()).stream().flatMap((nbt) -> {
            return nbt.asNbtList().stream();
         }).forEach((nbt) -> {
            NbtCompound nbtCompound = new NbtCompound();

            for(int i = 0; i < nbt.size(); ++i) {
               nbtCompound.putString((String)nbtList2.getString(i).orElseThrow(), toNbtProviderFormattedPalette((NbtCompound)nbt.getCompound(i).orElseThrow()));
            }

            nbtList3.add(nbtCompound);
         });
         compound.put("palettes", nbtList3);
      }

      Optional optional2 = compound.getList("entities");
      NbtList nbtList4;
      if (optional2.isPresent()) {
         nbtList4 = (NbtList)((NbtList)optional2.get()).streamCompounds().sorted(Comparator.comparing((nbt) -> {
            return nbt.getList("pos");
         }, Comparators.emptiesLast(ENTITY_POS_COMPARATOR))).collect(Collectors.toCollection(NbtList::new));
         compound.put("entities", nbtList4);
      }

      nbtList4 = (NbtList)compound.getList("blocks").stream().flatMap(NbtList::streamCompounds).sorted(Comparator.comparing((nbt) -> {
         return nbt.getList("pos");
      }, Comparators.emptiesLast(BLOCK_POS_COMPARATOR))).peek((nbt) -> {
         nbt.putString("state", (String)nbtList2.getString(nbt.getInt("state", 0)).orElseThrow());
      }).collect(Collectors.toCollection(NbtList::new));
      compound.put("data", nbtList4);
      compound.remove("blocks");
      return compound;
   }

   @VisibleForTesting
   static NbtCompound fromNbtProviderFormat(NbtCompound compound) {
      NbtList nbtList = compound.getListOrEmpty("palette");
      Map map = (Map)nbtList.stream().flatMap((nbt) -> {
         return nbt.asString().stream();
      }).collect(ImmutableMap.toImmutableMap(Function.identity(), NbtHelper::fromNbtProviderFormattedPalette));
      Optional optional = compound.getList("palettes");
      if (optional.isPresent()) {
         compound.put("palettes", (NbtElement)((NbtList)optional.get()).streamCompounds().map((nbt) -> {
            return (NbtList)map.keySet().stream().map((key) -> {
               return (String)nbt.getString(key).orElseThrow();
            }).map(NbtHelper::fromNbtProviderFormattedPalette).collect(Collectors.toCollection(NbtList::new));
         }).collect(Collectors.toCollection(NbtList::new)));
         compound.remove("palette");
      } else {
         compound.put("palette", (NbtElement)map.values().stream().collect(Collectors.toCollection(NbtList::new)));
      }

      Optional optional2 = compound.getList("data");
      if (optional2.isPresent()) {
         Object2IntMap object2IntMap = new Object2IntOpenHashMap();
         object2IntMap.defaultReturnValue(-1);

         for(int i = 0; i < nbtList.size(); ++i) {
            object2IntMap.put((String)nbtList.getString(i).orElseThrow(), i);
         }

         NbtList nbtList2 = (NbtList)optional2.get();

         for(int j = 0; j < nbtList2.size(); ++j) {
            NbtCompound nbtCompound = (NbtCompound)nbtList2.getCompound(j).orElseThrow();
            String string = (String)nbtCompound.getString("state").orElseThrow();
            int k = object2IntMap.getInt(string);
            if (k == -1) {
               throw new IllegalStateException("Entry " + string + " missing from palette");
            }

            nbtCompound.putInt("state", k);
         }

         compound.put("blocks", nbtList2);
         compound.remove("data");
      }

      return compound;
   }

   @VisibleForTesting
   static String toNbtProviderFormattedPalette(NbtCompound compound) {
      StringBuilder stringBuilder = new StringBuilder((String)compound.getString("Name").orElseThrow());
      compound.getCompound("Properties").ifPresent((properties) -> {
         String string = (String)properties.entrySet().stream().sorted(Entry.comparingByKey()).map((entry) -> {
            String var10000 = (String)entry.getKey();
            return var10000 + ":" + (String)((NbtElement)entry.getValue()).asString().orElseThrow();
         }).collect(Collectors.joining(","));
         stringBuilder.append('{').append(string).append('}');
      });
      return stringBuilder.toString();
   }

   @VisibleForTesting
   static NbtCompound fromNbtProviderFormattedPalette(String string) {
      NbtCompound nbtCompound = new NbtCompound();
      int i = string.indexOf(123);
      String string2;
      if (i >= 0) {
         string2 = string.substring(0, i);
         NbtCompound nbtCompound2 = new NbtCompound();
         if (i + 2 <= string.length()) {
            String string3 = string.substring(i + 1, string.indexOf(125, i));
            COMMA_SPLITTER.split(string3).forEach((property) -> {
               List list = COLON_SPLITTER.splitToList(property);
               if (list.size() == 2) {
                  nbtCompound2.putString((String)list.get(0), (String)list.get(1));
               } else {
                  LOGGER.error("Something went wrong parsing: '{}' -- incorrect gamedata!", string);
               }

            });
            nbtCompound.put("Properties", nbtCompound2);
         }
      } else {
         string2 = string;
      }

      nbtCompound.putString("Name", string2);
      return nbtCompound;
   }

   public static NbtCompound putDataVersion(NbtCompound nbt) {
      int i = SharedConstants.getGameVersion().dataVersion().id();
      return putDataVersion(nbt, i);
   }

   public static NbtCompound putDataVersion(NbtCompound nbt, int dataVersion) {
      nbt.putInt("DataVersion", dataVersion);
      return nbt;
   }

   public static void writeDataVersion(WriteView view) {
      int i = SharedConstants.getGameVersion().dataVersion().id();
      writeDataVersion(view, i);
   }

   public static void writeDataVersion(WriteView view, int dataVersion) {
      view.putInt("DataVersion", dataVersion);
   }

   public static int getDataVersion(NbtCompound nbt, int fallback) {
      return nbt.getInt("DataVersion", fallback);
   }

   public static int getDataVersion(Dynamic dynamic, int fallback) {
      return dynamic.get("DataVersion").asInt(fallback);
   }

   static {
      BLOCK_KEY_CODEC = RegistryKey.createCodec(RegistryKeys.BLOCK);
      COMMA_SPLITTER = Splitter.on(",");
      COLON_SPLITTER = Splitter.on(':').limit(2);
      LOGGER = LogUtils.getLogger();
   }
}
