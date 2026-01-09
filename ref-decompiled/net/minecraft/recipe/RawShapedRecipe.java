package net.minecraft.recipe;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.chars.CharArraySet;
import it.unimi.dsi.fastutil.chars.CharSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public final class RawShapedRecipe {
   private static final int MAX_WIDTH_AND_HEIGHT = 3;
   public static final char SPACE = ' ';
   public static final MapCodec CODEC;
   public static final PacketCodec PACKET_CODEC;
   private final int width;
   private final int height;
   private final List ingredients;
   private final Optional data;
   private final int ingredientCount;
   private final boolean symmetrical;

   public RawShapedRecipe(int width, int height, List ingredients, Optional data) {
      this.width = width;
      this.height = height;
      this.ingredients = ingredients;
      this.data = data;
      this.ingredientCount = (int)ingredients.stream().flatMap(Optional::stream).count();
      this.symmetrical = Util.isSymmetrical(width, height, ingredients);
   }

   private static RawShapedRecipe create(Integer width, Integer height, List ingredients) {
      return new RawShapedRecipe(width, height, ingredients, Optional.empty());
   }

   public static RawShapedRecipe create(Map key, String... pattern) {
      return create(key, List.of(pattern));
   }

   public static RawShapedRecipe create(Map key, List pattern) {
      Data data = new Data(key, pattern);
      return (RawShapedRecipe)fromData(data).getOrThrow();
   }

   private static DataResult fromData(Data data) {
      String[] strings = removePadding(data.pattern);
      int i = strings[0].length();
      int j = strings.length;
      List list = new ArrayList(i * j);
      CharSet charSet = new CharArraySet(data.key.keySet());
      String[] var6 = strings;
      int var7 = strings.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String string = var6[var8];

         for(int k = 0; k < string.length(); ++k) {
            char c = string.charAt(k);
            Optional optional;
            if (c == ' ') {
               optional = Optional.empty();
            } else {
               Ingredient ingredient = (Ingredient)data.key.get(c);
               if (ingredient == null) {
                  return DataResult.error(() -> {
                     return "Pattern references symbol '" + c + "' but it's not defined in the key";
                  });
               }

               optional = Optional.of(ingredient);
            }

            charSet.remove(c);
            list.add(optional);
         }
      }

      if (!charSet.isEmpty()) {
         return DataResult.error(() -> {
            return "Key defines symbols that aren't used in pattern: " + String.valueOf(charSet);
         });
      } else {
         return DataResult.success(new RawShapedRecipe(i, j, list, Optional.of(data)));
      }
   }

   @VisibleForTesting
   static String[] removePadding(List pattern) {
      int i = Integer.MAX_VALUE;
      int j = 0;
      int k = 0;
      int l = 0;

      for(int m = 0; m < pattern.size(); ++m) {
         String string = (String)pattern.get(m);
         i = Math.min(i, findFirstSymbol(string));
         int n = findLastSymbol(string);
         j = Math.max(j, n);
         if (n < 0) {
            if (k == m) {
               ++k;
            }

            ++l;
         } else {
            l = 0;
         }
      }

      if (pattern.size() == l) {
         return new String[0];
      } else {
         String[] strings = new String[pattern.size() - l - k];

         for(int o = 0; o < strings.length; ++o) {
            strings[o] = ((String)pattern.get(o + k)).substring(i, j + 1);
         }

         return strings;
      }
   }

   private static int findFirstSymbol(String line) {
      int i;
      for(i = 0; i < line.length() && line.charAt(i) == ' '; ++i) {
      }

      return i;
   }

   private static int findLastSymbol(String line) {
      int i;
      for(i = line.length() - 1; i >= 0 && line.charAt(i) == ' '; --i) {
      }

      return i;
   }

   public boolean matches(CraftingRecipeInput input) {
      if (input.getStackCount() != this.ingredientCount) {
         return false;
      } else {
         if (input.getWidth() == this.width && input.getHeight() == this.height) {
            if (!this.symmetrical && this.matches(input, true)) {
               return true;
            }

            if (this.matches(input, false)) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean matches(CraftingRecipeInput input, boolean mirrored) {
      for(int i = 0; i < this.height; ++i) {
         for(int j = 0; j < this.width; ++j) {
            Optional optional;
            if (mirrored) {
               optional = (Optional)this.ingredients.get(this.width - j - 1 + i * this.width);
            } else {
               optional = (Optional)this.ingredients.get(j + i * this.width);
            }

            ItemStack itemStack = input.getStackInSlot(j, i);
            if (!Ingredient.matches(optional, itemStack)) {
               return false;
            }
         }
      }

      return true;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public List getIngredients() {
      return this.ingredients;
   }

   static {
      CODEC = RawShapedRecipe.Data.CODEC.flatXmap(RawShapedRecipe::fromData, (recipe) -> {
         return (DataResult)recipe.data.map(DataResult::success).orElseGet(() -> {
            return DataResult.error(() -> {
               return "Cannot encode unpacked recipe";
            });
         });
      });
      PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, (recipe) -> {
         return recipe.width;
      }, PacketCodecs.VAR_INT, (recipe) -> {
         return recipe.height;
      }, Ingredient.OPTIONAL_PACKET_CODEC.collect(PacketCodecs.toList()), (recipe) -> {
         return recipe.ingredients;
      }, RawShapedRecipe::create);
   }

   public static record Data(Map key, List pattern) {
      final Map key;
      final List pattern;
      private static final Codec PATTERN_CODEC;
      private static final Codec KEY_ENTRY_CODEC;
      public static final MapCodec CODEC;

      public Data(Map map, List list) {
         this.key = map;
         this.pattern = list;
      }

      public Map key() {
         return this.key;
      }

      public List pattern() {
         return this.pattern;
      }

      static {
         PATTERN_CODEC = Codec.STRING.listOf().comapFlatMap((pattern) -> {
            if (pattern.size() > 3) {
               return DataResult.error(() -> {
                  return "Invalid pattern: too many rows, 3 is maximum";
               });
            } else if (pattern.isEmpty()) {
               return DataResult.error(() -> {
                  return "Invalid pattern: empty pattern not allowed";
               });
            } else {
               int i = ((String)pattern.getFirst()).length();
               Iterator var2 = pattern.iterator();

               String string;
               do {
                  if (!var2.hasNext()) {
                     return DataResult.success(pattern);
                  }

                  string = (String)var2.next();
                  if (string.length() > 3) {
                     return DataResult.error(() -> {
                        return "Invalid pattern: too many columns, 3 is maximum";
                     });
                  }
               } while(i == string.length());

               return DataResult.error(() -> {
                  return "Invalid pattern: each row must be the same width";
               });
            }
         }, Function.identity());
         KEY_ENTRY_CODEC = Codec.STRING.comapFlatMap((keyEntry) -> {
            if (keyEntry.length() != 1) {
               return DataResult.error(() -> {
                  return "Invalid key entry: '" + keyEntry + "' is an invalid symbol (must be 1 character only).";
               });
            } else {
               return " ".equals(keyEntry) ? DataResult.error(() -> {
                  return "Invalid key entry: ' ' is a reserved symbol.";
               }) : DataResult.success(keyEntry.charAt(0));
            }
         }, String::valueOf);
         CODEC = RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codecs.strictUnboundedMap(KEY_ENTRY_CODEC, Ingredient.CODEC).fieldOf("key").forGetter((data) -> {
               return data.key;
            }), PATTERN_CODEC.fieldOf("pattern").forGetter((data) -> {
               return data.pattern;
            })).apply(instance, Data::new);
         });
      }
   }
}
