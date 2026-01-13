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
 *  net.minecraft.client.render.item.property.numeric.NeedleAngleState
 *  net.minecraft.client.render.item.property.numeric.NeedleAngleState$Angler
 *  net.minecraft.client.render.item.property.numeric.NumericProperty
 *  net.minecraft.client.render.item.property.numeric.TimeProperty
 *  net.minecraft.client.render.item.property.numeric.TimeProperty$Source
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.util.math.random.Random
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
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.numeric.TimeProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public class TimeProperty
extends NeedleAngleState
implements NumericProperty {
    public static final MapCodec<TimeProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Codec.BOOL.optionalFieldOf("wobble", (Object)true).forGetter(NeedleAngleState::hasWobble), (App)Source.CODEC.fieldOf("source").forGetter(property -> property.source)).apply((Applicative)instance, TimeProperty::new));
    private final Source source;
    private final Random random = Random.create();
    private final NeedleAngleState.Angler angler;

    public TimeProperty(boolean wobble, Source source) {
        super(wobble);
        this.source = source;
        this.angler = this.createAngler(0.9f);
    }

    protected float getAngle(ItemStack stack, ClientWorld world, int seed, HeldItemContext context) {
        float f = this.source.getAngle(world, stack, context, this.random);
        long l = world.getTime();
        if (this.angler.shouldUpdate(l)) {
            this.angler.update(l, f);
        }
        return this.angler.getAngle();
    }

    public MapCodec<TimeProperty> getCodec() {
        return CODEC;
    }
}

