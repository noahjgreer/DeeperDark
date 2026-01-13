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
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.ItemModelTypes;
import net.minecraft.util.dynamic.Codecs;

@Environment(value=EnvType.CLIENT)
public static final class SelectItemModel.SwitchCase<T>
extends Record {
    final List<T> values;
    final ItemModel.Unbaked model;

    public SelectItemModel.SwitchCase(List<T> values, ItemModel.Unbaked model) {
        this.values = values;
        this.model = model;
    }

    public static <T> Codec<SelectItemModel.SwitchCase<T>> createCodec(Codec<T> conditionCodec) {
        return RecordCodecBuilder.create(instance -> instance.group((App)Codecs.nonEmptyList(Codecs.listOrSingle(conditionCodec)).fieldOf("when").forGetter(SelectItemModel.SwitchCase::values), (App)ItemModelTypes.CODEC.fieldOf("model").forGetter(SelectItemModel.SwitchCase::model)).apply((Applicative)instance, SelectItemModel.SwitchCase::new));
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SelectItemModel.SwitchCase.class, "values;model", "values", "model"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SelectItemModel.SwitchCase.class, "values;model", "values", "model"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SelectItemModel.SwitchCase.class, "values;model", "values", "model"}, this, object);
    }

    public List<T> values() {
        return this.values;
    }

    public ItemModel.Unbaked model() {
        return this.model;
    }
}
