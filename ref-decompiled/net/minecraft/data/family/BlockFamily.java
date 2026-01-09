package net.minecraft.data.family;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.util.StringHelper;
import org.jetbrains.annotations.Nullable;

public class BlockFamily {
   private final Block baseBlock;
   final Map variants = Maps.newHashMap();
   boolean generateModels = true;
   boolean generateRecipes = true;
   @Nullable
   String group;
   @Nullable
   String unlockCriterionName;

   BlockFamily(Block baseBlock) {
      this.baseBlock = baseBlock;
   }

   public Block getBaseBlock() {
      return this.baseBlock;
   }

   public Map getVariants() {
      return this.variants;
   }

   public Block getVariant(Variant variant) {
      return (Block)this.variants.get(variant);
   }

   public boolean shouldGenerateModels() {
      return this.generateModels;
   }

   public boolean shouldGenerateRecipes() {
      return this.generateRecipes;
   }

   public Optional getGroup() {
      return StringHelper.isBlank(this.group) ? Optional.empty() : Optional.of(this.group);
   }

   public Optional getUnlockCriterionName() {
      return StringHelper.isBlank(this.unlockCriterionName) ? Optional.empty() : Optional.of(this.unlockCriterionName);
   }

   public static class Builder {
      private final BlockFamily family;

      public Builder(Block baseBlock) {
         this.family = new BlockFamily(baseBlock);
      }

      public BlockFamily build() {
         return this.family;
      }

      public Builder button(Block block) {
         this.family.variants.put(BlockFamily.Variant.BUTTON, block);
         return this;
      }

      public Builder chiseled(Block block) {
         this.family.variants.put(BlockFamily.Variant.CHISELED, block);
         return this;
      }

      public Builder mosaic(Block block) {
         this.family.variants.put(BlockFamily.Variant.MOSAIC, block);
         return this;
      }

      public Builder cracked(Block block) {
         this.family.variants.put(BlockFamily.Variant.CRACKED, block);
         return this;
      }

      public Builder cut(Block block) {
         this.family.variants.put(BlockFamily.Variant.CUT, block);
         return this;
      }

      public Builder door(Block block) {
         this.family.variants.put(BlockFamily.Variant.DOOR, block);
         return this;
      }

      public Builder customFence(Block block) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE, block);
         return this;
      }

      public Builder fence(Block block) {
         this.family.variants.put(BlockFamily.Variant.FENCE, block);
         return this;
      }

      public Builder customFenceGate(Block block) {
         this.family.variants.put(BlockFamily.Variant.CUSTOM_FENCE_GATE, block);
         return this;
      }

      public Builder fenceGate(Block block) {
         this.family.variants.put(BlockFamily.Variant.FENCE_GATE, block);
         return this;
      }

      public Builder sign(Block block, Block wallBlock) {
         this.family.variants.put(BlockFamily.Variant.SIGN, block);
         this.family.variants.put(BlockFamily.Variant.WALL_SIGN, wallBlock);
         return this;
      }

      public Builder slab(Block block) {
         this.family.variants.put(BlockFamily.Variant.SLAB, block);
         return this;
      }

      public Builder stairs(Block block) {
         this.family.variants.put(BlockFamily.Variant.STAIRS, block);
         return this;
      }

      public Builder pressurePlate(Block block) {
         this.family.variants.put(BlockFamily.Variant.PRESSURE_PLATE, block);
         return this;
      }

      public Builder polished(Block block) {
         this.family.variants.put(BlockFamily.Variant.POLISHED, block);
         return this;
      }

      public Builder trapdoor(Block block) {
         this.family.variants.put(BlockFamily.Variant.TRAPDOOR, block);
         return this;
      }

      public Builder wall(Block block) {
         this.family.variants.put(BlockFamily.Variant.WALL, block);
         return this;
      }

      public Builder noGenerateModels() {
         this.family.generateModels = false;
         return this;
      }

      public Builder noGenerateRecipes() {
         this.family.generateRecipes = false;
         return this;
      }

      public Builder group(String group) {
         this.family.group = group;
         return this;
      }

      public Builder unlockCriterionName(String unlockCriterionName) {
         this.family.unlockCriterionName = unlockCriterionName;
         return this;
      }
   }

   public static enum Variant {
      BUTTON("button"),
      CHISELED("chiseled"),
      CRACKED("cracked"),
      CUT("cut"),
      DOOR("door"),
      CUSTOM_FENCE("fence"),
      FENCE("fence"),
      CUSTOM_FENCE_GATE("fence_gate"),
      FENCE_GATE("fence_gate"),
      MOSAIC("mosaic"),
      SIGN("sign"),
      SLAB("slab"),
      STAIRS("stairs"),
      PRESSURE_PLATE("pressure_plate"),
      POLISHED("polished"),
      TRAPDOOR("trapdoor"),
      WALL("wall"),
      WALL_SIGN("wall_sign");

      private final String name;

      private Variant(final String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Variant[] method_36938() {
         return new Variant[]{BUTTON, CHISELED, CRACKED, CUT, DOOR, CUSTOM_FENCE, FENCE, CUSTOM_FENCE_GATE, FENCE_GATE, MOSAIC, SIGN, SLAB, STAIRS, PRESSURE_PLATE, POLISHED, TRAPDOOR, WALL, WALL_SIGN};
      }
   }
}
