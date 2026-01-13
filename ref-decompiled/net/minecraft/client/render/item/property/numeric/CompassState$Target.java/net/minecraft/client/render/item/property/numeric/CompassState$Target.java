/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.GlobalPos;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public static abstract sealed class CompassState.Target
extends Enum<CompassState.Target>
implements StringIdentifiable {
    public static final /* enum */ CompassState.Target NONE = new CompassState.Target("none"){

        @Override
        public @Nullable GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
            return null;
        }
    };
    public static final /* enum */ CompassState.Target LODESTONE = new CompassState.Target("lodestone"){

        @Override
        public @Nullable GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
            LodestoneTrackerComponent lodestoneTrackerComponent = stack.get(DataComponentTypes.LODESTONE_TRACKER);
            return lodestoneTrackerComponent != null ? (GlobalPos)lodestoneTrackerComponent.target().orElse(null) : null;
        }
    };
    public static final /* enum */ CompassState.Target SPAWN = new CompassState.Target("spawn"){

        @Override
        public GlobalPos getPosition(ClientWorld world, ItemStack stack, @Nullable HeldItemContext context) {
            return world.getSpawnPoint().globalPos();
        }
    };
    public static final /* enum */ CompassState.Target RECOVERY = new CompassState.Target("recovery"){

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
    public static final Codec<CompassState.Target> CODEC;
    private final String name;
    private static final /* synthetic */ CompassState.Target[] field_55395;

    public static CompassState.Target[] values() {
        return (CompassState.Target[])field_55395.clone();
    }

    public static CompassState.Target valueOf(String string) {
        return Enum.valueOf(CompassState.Target.class, string);
    }

    CompassState.Target(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    abstract @Nullable GlobalPos getPosition(ClientWorld var1, ItemStack var2, @Nullable HeldItemContext var3);

    private static /* synthetic */ CompassState.Target[] method_65655() {
        return new CompassState.Target[]{NONE, LODESTONE, SPAWN, RECOVERY};
    }

    static {
        field_55395 = CompassState.Target.method_65655();
        CODEC = StringIdentifiable.createCodec(CompassState.Target::values);
    }
}
