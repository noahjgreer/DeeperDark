package net.minecraft.recipe.book;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.UnaryOperator;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public final class RecipeBookOptions {
   public static final PacketCodec PACKET_CODEC;
   public static final MapCodec CODEC;
   private CategoryOption crafting;
   private CategoryOption furnace;
   private CategoryOption blastFurnace;
   private CategoryOption smoker;

   public RecipeBookOptions() {
      this(RecipeBookOptions.CategoryOption.DEFAULT, RecipeBookOptions.CategoryOption.DEFAULT, RecipeBookOptions.CategoryOption.DEFAULT, RecipeBookOptions.CategoryOption.DEFAULT);
   }

   private RecipeBookOptions(CategoryOption crafting, CategoryOption furnace, CategoryOption blastFurnace, CategoryOption smoker) {
      this.crafting = crafting;
      this.furnace = furnace;
      this.blastFurnace = blastFurnace;
      this.smoker = smoker;
   }

   @VisibleForTesting
   public CategoryOption getOption(RecipeBookType type) {
      CategoryOption var10000;
      switch (type) {
         case CRAFTING:
            var10000 = this.crafting;
            break;
         case FURNACE:
            var10000 = this.furnace;
            break;
         case BLAST_FURNACE:
            var10000 = this.blastFurnace;
            break;
         case SMOKER:
            var10000 = this.smoker;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   private void apply(RecipeBookType type, UnaryOperator modifier) {
      switch (type) {
         case CRAFTING:
            this.crafting = (CategoryOption)modifier.apply(this.crafting);
            break;
         case FURNACE:
            this.furnace = (CategoryOption)modifier.apply(this.furnace);
            break;
         case BLAST_FURNACE:
            this.blastFurnace = (CategoryOption)modifier.apply(this.blastFurnace);
            break;
         case SMOKER:
            this.smoker = (CategoryOption)modifier.apply(this.smoker);
      }

   }

   public boolean isGuiOpen(RecipeBookType category) {
      return this.getOption(category).guiOpen;
   }

   public void setGuiOpen(RecipeBookType category, boolean open) {
      this.apply(category, (option) -> {
         return option.withGuiOpen(open);
      });
   }

   public boolean isFilteringCraftable(RecipeBookType category) {
      return this.getOption(category).filteringCraftable;
   }

   public void setFilteringCraftable(RecipeBookType category, boolean filtering) {
      this.apply(category, (option) -> {
         return option.withFilteringCraftable(filtering);
      });
   }

   public RecipeBookOptions copy() {
      return new RecipeBookOptions(this.crafting, this.furnace, this.blastFurnace, this.smoker);
   }

   public void copyFrom(RecipeBookOptions other) {
      this.crafting = other.crafting;
      this.furnace = other.furnace;
      this.blastFurnace = other.blastFurnace;
      this.smoker = other.smoker;
   }

   static {
      PACKET_CODEC = PacketCodec.tuple(RecipeBookOptions.CategoryOption.PACKET_CODEC, (options) -> {
         return options.crafting;
      }, RecipeBookOptions.CategoryOption.PACKET_CODEC, (options) -> {
         return options.furnace;
      }, RecipeBookOptions.CategoryOption.PACKET_CODEC, (options) -> {
         return options.blastFurnace;
      }, RecipeBookOptions.CategoryOption.PACKET_CODEC, (options) -> {
         return options.smoker;
      }, RecipeBookOptions::new);
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(RecipeBookOptions.CategoryOption.CRAFTING.forGetter((options) -> {
            return options.crafting;
         }), RecipeBookOptions.CategoryOption.FURNACE.forGetter((options) -> {
            return options.furnace;
         }), RecipeBookOptions.CategoryOption.BLAST_FURNACE.forGetter((options) -> {
            return options.blastFurnace;
         }), RecipeBookOptions.CategoryOption.SMOKER.forGetter((options) -> {
            return options.smoker;
         })).apply(instance, RecipeBookOptions::new);
      });
   }

   public static record CategoryOption(boolean guiOpen, boolean filteringCraftable) {
      final boolean guiOpen;
      final boolean filteringCraftable;
      public static final CategoryOption DEFAULT = new CategoryOption(false, false);
      public static final MapCodec CRAFTING = createCodec("isGuiOpen", "isFilteringCraftable");
      public static final MapCodec FURNACE = createCodec("isFurnaceGuiOpen", "isFurnaceFilteringCraftable");
      public static final MapCodec BLAST_FURNACE = createCodec("isBlastingFurnaceGuiOpen", "isBlastingFurnaceFilteringCraftable");
      public static final MapCodec SMOKER = createCodec("isSmokerGuiOpen", "isSmokerFilteringCraftable");
      public static final PacketCodec PACKET_CODEC;

      public CategoryOption(boolean guiOpen, boolean filteringCraftable) {
         this.guiOpen = guiOpen;
         this.filteringCraftable = filteringCraftable;
      }

      public String toString() {
         return "[open=" + this.guiOpen + ", filtering=" + this.filteringCraftable + "]";
      }

      public CategoryOption withGuiOpen(boolean guiOpen) {
         return new CategoryOption(guiOpen, this.filteringCraftable);
      }

      public CategoryOption withFilteringCraftable(boolean filteringCraftable) {
         return new CategoryOption(this.guiOpen, filteringCraftable);
      }

      private static MapCodec createCodec(String guiOpenField, String filteringCraftableField) {
         return RecordCodecBuilder.mapCodec((instance) -> {
            return instance.group(Codec.BOOL.optionalFieldOf(guiOpenField, false).forGetter(CategoryOption::guiOpen), Codec.BOOL.optionalFieldOf(filteringCraftableField, false).forGetter(CategoryOption::filteringCraftable)).apply(instance, CategoryOption::new);
         });
      }

      public boolean guiOpen() {
         return this.guiOpen;
      }

      public boolean filteringCraftable() {
         return this.filteringCraftable;
      }

      static {
         PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOLEAN, CategoryOption::guiOpen, PacketCodecs.BOOLEAN, CategoryOption::filteringCraftable, CategoryOption::new);
      }
   }
}
