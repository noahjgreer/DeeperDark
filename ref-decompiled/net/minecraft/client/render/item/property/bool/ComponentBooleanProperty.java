package net.minecraft.client.render.item.property.bool;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.component.ComponentPredicate;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record ComponentBooleanProperty(ComponentPredicate.Typed predicate) implements BooleanProperty {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(ComponentPredicate.createCodec("predicate").forGetter(ComponentBooleanProperty::predicate)).apply(instance, ComponentBooleanProperty::new);
   });

   public ComponentBooleanProperty(ComponentPredicate.Typed typed) {
      this.predicate = typed;
   }

   public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
      return this.predicate.predicate().test(stack);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   public ComponentPredicate.Typed predicate() {
      return this.predicate;
   }
}
