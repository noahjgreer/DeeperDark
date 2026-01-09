package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CompassState extends NeedleAngleState {
   public static final MapCodec CODEC = RecordCodecBuilder.mapCodec((instance) -> {
      return instance.group(Codec.BOOL.optionalFieldOf("wobble", true).forGetter(NeedleAngleState::hasWobble), CompassState.Target.CODEC.fieldOf("target").forGetter(CompassState::getTarget)).apply(instance, CompassState::new);
   });
   private final NeedleAngleState.Angler aimedAngler = this.createAngler(0.8F);
   private final NeedleAngleState.Angler aimlessAngler = this.createAngler(0.8F);
   private final Target target;
   private final Random random = Random.create();

   public CompassState(boolean wobble, Target target) {
      super(wobble);
      this.target = target;
   }

   protected float getAngle(ItemStack stack, ClientWorld world, int seed, Entity user) {
      GlobalPos globalPos = this.target.getPosition(world, stack, user);
      long l = world.getTime();
      return !canPointTo(user, globalPos) ? this.getAimlessAngle(seed, l) : this.getAngleTo(user, l, globalPos.pos());
   }

   private float getAimlessAngle(int seed, long time) {
      if (this.aimlessAngler.shouldUpdate(time)) {
         this.aimlessAngler.update(time, this.random.nextFloat());
      }

      float f = this.aimlessAngler.getAngle() + (float)scatter(seed) / 2.1474836E9F;
      return MathHelper.floorMod(f, 1.0F);
   }

   private float getAngleTo(Entity entity, long time, BlockPos pos) {
      float f = (float)getAngleTo(entity, pos);
      float g = getBodyYaw(entity);
      float h;
      if (entity instanceof PlayerEntity playerEntity) {
         if (playerEntity.isMainPlayer() && playerEntity.getWorld().getTickManager().shouldTick()) {
            if (this.aimedAngler.shouldUpdate(time)) {
               this.aimedAngler.update(time, 0.5F - (g - 0.25F));
            }

            h = f + this.aimedAngler.getAngle();
            return MathHelper.floorMod(h, 1.0F);
         }
      }

      h = 0.5F - (g - 0.25F - f);
      return MathHelper.floorMod(h, 1.0F);
   }

   private static boolean canPointTo(Entity entity, @Nullable GlobalPos pos) {
      return pos != null && pos.dimension() == entity.getWorld().getRegistryKey() && !(pos.pos().getSquaredDistance(entity.getPos()) < 9.999999747378752E-6);
   }

   private static double getAngleTo(Entity entity, BlockPos pos) {
      Vec3d vec3d = Vec3d.ofCenter(pos);
      return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX()) / 6.2831854820251465;
   }

   private static float getBodyYaw(Entity entity) {
      return MathHelper.floorMod(entity.getBodyYaw() / 360.0F, 1.0F);
   }

   private static int scatter(int seed) {
      return seed * 1327217883;
   }

   protected Target getTarget() {
      return this.target;
   }

   @Environment(EnvType.CLIENT)
   public static enum Target implements StringIdentifiable {
      NONE("none") {
         @Nullable
         public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
            return null;
         }
      },
      LODESTONE("lodestone") {
         @Nullable
         public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
            LodestoneTrackerComponent lodestoneTrackerComponent = (LodestoneTrackerComponent)stack.get(DataComponentTypes.LODESTONE_TRACKER);
            return lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse((Object)null) : null;
         }
      },
      SPAWN("spawn") {
         public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
            return GlobalPos.create(world.getRegistryKey(), world.getSpawnPos());
         }
      },
      RECOVERY("recovery") {
         @Nullable
         public GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder) {
            GlobalPos var10000;
            if (holder instanceof PlayerEntity playerEntity) {
               var10000 = (GlobalPos)playerEntity.getLastDeathPos().orElse((Object)null);
            } else {
               var10000 = null;
            }

            return var10000;
         }
      };

      public static final Codec CODEC = StringIdentifiable.createCodec(Target::values);
      private final String name;

      Target(final String name) {
         this.name = name;
      }

      public String asString() {
         return this.name;
      }

      @Nullable
      abstract GlobalPos getPosition(ClientWorld world, ItemStack stack, Entity holder);

      // $FF: synthetic method
      private static Target[] method_65655() {
         return new Target[]{NONE, LODESTONE, SPAWN, RECOVERY};
      }
   }
}
