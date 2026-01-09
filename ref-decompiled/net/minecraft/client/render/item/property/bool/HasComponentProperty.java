package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record HasComponentProperty(ComponentType componentType, boolean ignoreDefault) implements BooleanProperty {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Registries.DATA_COMPONENT_TYPE.getCodec().fieldOf("component").forGetter(HasComponentProperty::componentType), Codec.BOOL.optionalFieldOf("ignore_default", false).forGetter(HasComponentProperty::ignoreDefault)).apply(instance, HasComponentProperty::new);
   });

   public HasComponentProperty(ComponentType componentType, boolean bl) {
      this.componentType = componentType;
      this.ignoreDefault = bl;
   }

   public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
      return this.ignoreDefault ? stack.hasChangedComponent(this.componentType) : stack.contains(this.componentType);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public ComponentType componentType() {
      return this.componentType;
   }

   public boolean ignoreDefault() {
      return this.ignoreDefault;
   }
}
