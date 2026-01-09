package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.StructureTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ExplorationMapLootFunction extends ConditionalLootFunction {
   public static final TagKey DEFAULT_DESTINATION;
   public static final RegistryEntry DEFAULT_DECORATION;
   public static final byte DEFAULT_ZOOM = 2;
   public static final int DEFAULT_SEARCH_RADIUS = 50;
   public static final boolean DEFAULT_SKIP_EXISTING_CHUNKS = true;
   public static final MapCodec CODEC;
   private final TagKey destination;
   private final RegistryEntry decoration;
   private final byte zoom;
   private final int searchRadius;
   private final boolean skipExistingChunks;

   ExplorationMapLootFunction(List conditions, TagKey destination, RegistryEntry decoration, byte zoom, int searchRadius, boolean skipExistingChunks) {
      super(conditions);
      this.destination = destination;
      this.decoration = decoration;
      this.zoom = zoom;
      this.searchRadius = searchRadius;
      this.skipExistingChunks = skipExistingChunks;
   }

   public LootFunctionType getType() {
      return LootFunctionTypes.EXPLORATION_MAP;
   }

   public Set getAllowedParameters() {
      return Set.of(LootContextParameters.ORIGIN);
   }

   public ItemStack process(ItemStack stack, LootContext context) {
      if (!stack.isOf(Items.MAP)) {
         return stack;
      } else {
         Vec3d vec3d = (Vec3d)context.get(LootContextParameters.ORIGIN);
         if (vec3d != null) {
            ServerWorld serverWorld = context.getWorld();
            BlockPos blockPos = serverWorld.locateStructure(this.destination, BlockPos.ofFloored(vec3d), this.searchRadius, this.skipExistingChunks);
            if (blockPos != null) {
               ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), this.zoom, true, true);
               FilledMapItem.fillExplorationMap(serverWorld, itemStack);
               MapState.addDecorationsNbt(itemStack, blockPos, "+", this.decoration);
               return itemStack;
            }
         }

         return stack;
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   static {
      DEFAULT_DESTINATION = StructureTags.ON_TREASURE_MAPS;
      DEFAULT_DECORATION = MapDecorationTypes.MANSION;
      CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return addConditionsField(instance).and(instance.group(TagKey.unprefixedCodec(RegistryKeys.STRUCTURE).optionalFieldOf("destination", DEFAULT_DESTINATION).forGetter((function) -> {
            return function.destination;
         }), MapDecorationType.CODEC.optionalFieldOf("decoration", DEFAULT_DECORATION).forGetter((function) -> {
            return function.decoration;
         }), Codec.BYTE.optionalFieldOf("zoom", (byte)2).forGetter((function) -> {
            return function.zoom;
         }), Codec.INT.optionalFieldOf("search_radius", 50).forGetter((function) -> {
            return function.searchRadius;
         }), Codec.BOOL.optionalFieldOf("skip_existing_chunks", true).forGetter((function) -> {
            return function.skipExistingChunks;
         }))).apply(instance, ExplorationMapLootFunction::new);
      });
   }

   public static class Builder extends ConditionalLootFunction.Builder {
      private TagKey destination;
      private RegistryEntry decoration;
      private byte zoom;
      private int searchRadius;
      private boolean skipExistingChunks;

      public Builder() {
         this.destination = ExplorationMapLootFunction.DEFAULT_DESTINATION;
         this.decoration = ExplorationMapLootFunction.DEFAULT_DECORATION;
         this.zoom = 2;
         this.searchRadius = 50;
         this.skipExistingChunks = true;
      }

      protected Builder getThisBuilder() {
         return this;
      }

      public Builder withDestination(TagKey destination) {
         this.destination = destination;
         return this;
      }

      public Builder withDecoration(RegistryEntry decoration) {
         this.decoration = decoration;
         return this;
      }

      public Builder withZoom(byte zoom) {
         this.zoom = zoom;
         return this;
      }

      public Builder searchRadius(int searchRadius) {
         this.searchRadius = searchRadius;
         return this;
      }

      public Builder withSkipExistingChunks(boolean skipExistingChunks) {
         this.skipExistingChunks = skipExistingChunks;
         return this;
      }

      public LootFunction build() {
         return new ExplorationMapLootFunction(this.getConditions(), this.destination, this.decoration, this.zoom, this.searchRadius, this.skipExistingChunks);
      }

      // $FF: synthetic method
      protected ConditionalLootFunction.Builder getThisBuilder() {
         return this.getThisBuilder();
      }
   }
}
