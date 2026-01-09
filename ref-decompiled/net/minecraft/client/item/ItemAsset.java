package net.minecraft.client.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.registry.ContextSwapper;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ItemAsset(ItemModel.Unbaked model, Properties properties, @Nullable ContextSwapper registrySwapper) {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ItemModelTypes.CODEC.fieldOf("model").forGetter(ItemAsset::model), ItemAsset.Properties.CODEC.forGetter(ItemAsset::properties)).apply(instance, ItemAsset::new);
   });

   public ItemAsset(ItemModel.Unbaked model, Properties properties) {
      this(model, properties, (ContextSwapper)null);
   }

   public ItemAsset(ItemModel.Unbaked unbaked, Properties properties, @Nullable ContextSwapper contextSwapper) {
      this.model = unbaked;
      this.properties = properties;
      this.registrySwapper = contextSwapper;
   }

   public ItemAsset withContextSwapper(ContextSwapper contextSwapper) {
      return new ItemAsset(this.model, this.properties, contextSwapper);
   }

   public ItemModel.Unbaked model() {
      return this.model;
   }

   public Properties properties() {
      return this.properties;
   }

   @Nullable
   public ContextSwapper registrySwapper() {
      return this.registrySwapper;
   }

   @Environment(EnvType.CLIENT)
   public static record Properties(boolean handAnimationOnSwap, boolean oversizedInGui) {
      public static final Properties DEFAULT = new Properties(true, false);
      public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
         return instance.group(Codec.BOOL.optionalFieldOf("hand_animation_on_swap", true).forGetter(Properties::handAnimationOnSwap), Codec.BOOL.optionalFieldOf("oversized_in_gui", false).forGetter(Properties::oversizedInGui)).apply(instance, Properties::new);
      });

      public Properties(boolean bl, boolean bl2) {
         this.handAnimationOnSwap = bl;
         this.oversizedInGui = bl2;
      }

      public boolean handAnimationOnSwap() {
         return this.handAnimationOnSwap;
      }

      public boolean oversizedInGui() {
         return this.oversizedInGui;
      }
   }
}
