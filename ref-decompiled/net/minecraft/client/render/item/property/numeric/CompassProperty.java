package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CompassProperty implements NumericProperty {
   public static final MapCodec CODEC;
   private final CompassState state;

   public CompassProperty(boolean wobble, CompassState.Target target) {
      this(new CompassState(wobble, target));
   }

   private CompassProperty(CompassState state) {
      this.state = state;
   }

   public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
      return this.state.getValue(stack, world, holder, seed);
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   static {
      CODEC = CompassState.CODEC.xmap(CompassProperty::new, (property) -> {
         return property.state;
      });
   }
}
