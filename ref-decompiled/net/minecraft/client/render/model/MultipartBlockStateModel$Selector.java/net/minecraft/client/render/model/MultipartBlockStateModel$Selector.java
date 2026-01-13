/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;

@Environment(value=EnvType.CLIENT)
public static final class MultipartBlockStateModel.Selector<T>
extends Record {
    final Predicate<BlockState> condition;
    final T model;

    public MultipartBlockStateModel.Selector(Predicate<BlockState> condition, T model) {
        this.condition = condition;
        this.model = model;
    }

    public <S> MultipartBlockStateModel.Selector<S> build(S model) {
        return new MultipartBlockStateModel.Selector<S>(this.condition, model);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{MultipartBlockStateModel.Selector.class, "condition;model", "condition", "model"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{MultipartBlockStateModel.Selector.class, "condition;model", "condition", "model"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{MultipartBlockStateModel.Selector.class, "condition;model", "condition", "model"}, this, object);
    }

    public Predicate<BlockState> condition() {
        return this.condition;
    }

    public T model() {
        return this.model;
    }
}
