package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class TimeProperty extends NeedleAngleState implements NumericProperty {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleAngleState::hasWobble), TimeProperty.Source.CODEC.fieldOf("source").forGetter((property) -> {
         return property.source;
      })).apply(instance, TimeProperty::new);
   });
   private final Source source;
   private final Random random = Random.create();
   private final NeedleAngleState.Angler angler;

   public TimeProperty(boolean wobble, Source source) {
      super(wobble);
      this.source = source;
      this.angler = this.createAngler(0.9F);
   }

   protected float getAngle(ItemStack stack, ClientWorld world, int seed, Entity user) {
      float f = this.source.getAngle(world, stack, user, this.random);
      long l = world.getTime();
      if (this.angler.shouldUpdate(l)) {
         this.angler.update(l, f);
      }

      return this.angler.getAngle();
   }

   public MapCodec getCodec() {
      return CODEC;
   }

   @Environment(EnvType.CLIENT)
   public static enum Source implements StringIdentifiable {
      RANDOM("random") {
         public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
            return random.nextFloat();
         }
      },
      DAYTIME("daytime") {
         public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
            return world.getSkyAngle(1.0F);
         }
      },
      MOON_PHASE("moon_phase") {
         public float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random) {
            return (float)world.getMoonPhase() / 8.0F;
         }
      };

      public static final Codec CODEC = StringIdentifiable.createCodec(Source::values);
      private final String name;

      Source(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      abstract float getAngle(ClientWorld world, ItemStack stack, Entity user, Random random);

      // $FF: synthetic method
      private static Source[] method_65913() {
         return new Source[]{RANDOM, DAYTIME, MOON_PHASE};
      }
   }
}
