/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;

@Environment(value=EnvType.CLIENT)
public static final class RangeDispatchItemModel.Entry
extends Record {
    final float threshold;
    final ItemModel.Unbaked model;
    public static final Codec<RangeDispatchItemModel.Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.FLOAT.fieldOf("threshold").forGetter(RangeDispatchItemModel.Entry::threshold), (App)ItemModelTypes.CODEC.fieldOf("model").forGetter(RangeDispatchItemModel.Entry::model)).apply((Applicative)instance, RangeDispatchItemModel.Entry::new));
    public static final Comparator<RangeDispatchItemModel.Entry> COMPARATOR = Comparator.comparingDouble(RangeDispatchItemModel.Entry::threshold);

    public RangeDispatchItemModel.Entry(float threshold, ItemModel.Unbaked model) {
        this.threshold = threshold;
        this.model = model;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{RangeDispatchItemModel.Entry.class, "threshold;model", "threshold", "model"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{RangeDispatchItemModel.Entry.class, "threshold;model", "threshold", "model"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{RangeDispatchItemModel.Entry.class, "threshold;model", "threshold", "model"}, this, object);
    }

    public float threshold() {
        return this.threshold;
    }

    public ItemModel.Unbaked model() {
        return this.model;
    }
}
