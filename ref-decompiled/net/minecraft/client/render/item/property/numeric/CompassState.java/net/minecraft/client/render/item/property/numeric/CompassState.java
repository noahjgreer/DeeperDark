/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.property.numeric.NeedleAngleState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CompassState
extends NeedleAngleState {
    public static final MapCodec<CompassState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("wobble", (Object)true).forGetter(NeedleAngleState::hasWobble), (App)Target.CODEC.fieldOf("target").forGetter(CompassState::getTarget)).apply((Applicative)instance, CompassState::new));
    private final NeedleAngleState.Angler aimedAngler;
    private final NeedleAngleState.Angler aimlessAngler;
    private final Target target;
    private final Random random = Random.create();

    public CompassState(boolean wobble, Target target) {
        super(wobble);
        this.aimedAngler = this.createAngler(0.8f);
        this.aimlessAngler = this.createAngler(0.8f);
        this.target = target;
    }

    @Override
    protected float getAngle(ItemStack stack, ClientWorld world, int seed, HeldItemContext context) {
        GlobalPos globalPos = this.target.getPosition(world, stack, context);
        long l = world.getTime();
        if (!CompassState.canPointTo(context, globalPos)) {
            return this.getAimlessAngle(seed, l);
        }
        return this.getAngleTo(context, l, globalPos.pos());
    }

    private float getAimlessAngle(int seed, long time) {
        if (this.aimlessAngler.shouldUpdate(time)) {
            this.aimlessAngler.update(time, this.random.nextFloat());
        }
        float f = this.aimlessAngler.getAngle() + (float)CompassState.scatter(seed) / 2.1474836E9f;
        return MathHelper.floorMod(f, 1.0f);
    }

    private float getAngleTo(HeldItemContext from, long time, BlockPos to) {
        float h;
        PlayerEntity playerEntity;
        float f = (float)CompassState.getAngleTo(from, to);
        float g = CompassState.getBodyYaw(from);
        LivingEntity livingEntity = from.getEntity();
        if (livingEntity instanceof PlayerEntity && (playerEntity = (PlayerEntity)livingEntity).isMainPlayer() && playerEntity.getEntityWorld().getTickManager().shouldTick()) {
            if (this.aimedAngler.shouldUpdate(time)) {
                this.aimedAngler.update(time, 0.5f - (g - 0.25f));
            }
            h = f + this.aimedAngler.getAngle();
        } else {
            h = 0.5f - (g - 0.25f - f);
        }
        return MathHelper.floorMod(h, 1.0f);
    }

    private static boolean canPointTo(HeldItemContext from, @Nullable GlobalPos to) {
        return to != null && to.dimension() == from.getEntityWorld().getRegistryKey() && !(to.pos().getSquaredDistance(from.getEntityPos()) < (double)1.0E-5f);
    }

    private static double getAngleTo(HeldItemContext from, BlockPos to) {
        Vec3d vec3d = Vec3d.ofCenter(to);
        Vec3d vec3d2 = from.getEntityPos();
        return Math.atan2(vec3d.getZ() - vec3d2.getZ(), vec3d.getX() - vec3d2.getX()) / 6.2831854820251465;
    }

    private static float getBodyYaw(HeldItemContext context) {
        return MathHelper.floorMod(context.getBodyYaw() / 360.0f, 1.0f);
    }

    private static int scatter(int seed) {
        return seed * 1327217883;
    }

    protected Target getTarget() {
        return this.target;
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract sealed class Target
    extends Enum<Target>
    implements StringIdentifiable {
        public static final /* enum */ Target NONE = new Target("none"){

            @Override
            public @Nullable GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
                return null;
            }
        };
        public static final /* enum */ Target LODESTONE = new Target("lodestone"){

            @Override
            public @Nullable GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
                LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
                return lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse(null) : null;
            }
        };
        public static final /* enum */ Target SPAWN = new Target("spawn"){

            @Override
            public GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
                return world.getSpawnPoint().globalPos();
            }
        };
        public static final /* enum */ Target RECOVERY = new Target("recovery"){

            @Override
            public @Nullable GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
                GlobalPos globalPos;
                LivingEntity livingEntity;
                LivingEntity livingEntity2 = livingEntity = context == null ? null : context.getEntity();
                if (livingEntity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity)livingEntity;
                    globalPos = playerEntity.getLastDeathPos().orElse(null);
                } else {
                    globalPos = null;
                }
                return globalPos;
            }
        };
        public static final Codec<Target> CODEC;
        private final String name;
        private static final /* synthetic */ Target[] field_55395;

        public static Target[] values() {
            return (Target[])field_55395.clone();
        }

        public static Target valueOf(String string) {
            return Enum.valueOf(Target.class, string);
        }

        Target(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        abstract @Nullable GlobalPos getPosition(ClientWorld var1, ItemStack var2, @Nullable HeldItemContext var3);

        private static /* synthetic */ Target[] method_65655() {
            return new Target[]{NONE, LODESTONE, SPAWN, RECOVERY};
        }

        static {
            field_55395 = Target.method_65655();
            CODEC = StringIdentifiable.createCodec(Target::values);
        }
    }
}
