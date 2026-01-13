/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.property.numeric;

import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.MoonPhase;
import net.minecraft.world.attribute.EnvironmentAttributes;

@Environment(value=EnvType.CLIENT)
public static abstract sealed class TimeProperty.Source
extends Enum<TimeProperty.Source>
implements StringIdentifiable {
    public static final /* enum */ TimeProperty.Source RANDOM = new TimeProperty.Source("random"){

        @Override
        public float getAngle(ClientWorld world, ItemStack stack, HeldItemContext heldItemContext, Random random) {
            return random.nextFloat();
        }
    };
    public static final /* enum */ TimeProperty.Source DAYTIME = new TimeProperty.Source("daytime"){

        @Override
        public float getAngle(ClientWorld world, ItemStack stack, HeldItemContext heldItemContext, Random random) {
            return world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.SUN_ANGLE_VISUAL, heldItemContext.getEntityPos()).floatValue() / 360.0f;
        }
    };
    public static final /* enum */ TimeProperty.Source MOON_PHASE = new TimeProperty.Source("moon_phase"){

        @Override
        public float getAngle(ClientWorld world, ItemStack stack, HeldItemContext heldItemContext, Random random) {
            return (float)world.getEnvironmentAttributes().getAttributeValue(EnvironmentAttributes.MOON_PHASE_VISUAL, heldItemContext.getEntityPos()).getIndex() / (float)MoonPhase.COUNT;
        }
    };
    public static final Codec<TimeProperty.Source> CODEC;
    private final String name;
    private static final /* synthetic */ TimeProperty.Source[] field_55562;

    public static TimeProperty.Source[] values() {
        return (TimeProperty.Source[])field_55562.clone();
    }

    public static TimeProperty.Source valueOf(String string) {
        return Enum.valueOf(TimeProperty.Source.class, string);
    }

    TimeProperty.Source(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    abstract float getAngle(ClientWorld var1, ItemStack var2, HeldItemContext var3, Random var4);

    private static /* synthetic */ TimeProperty.Source[] method_65913() {
        return new TimeProperty.Source[]{RANDOM, DAYTIME, MOON_PHASE};
    }

    static {
        field_55562 = TimeProperty.Source.method_65913();
        CODEC = StringIdentifiable.createCodec(TimeProperty.Source::values);
    }
}
