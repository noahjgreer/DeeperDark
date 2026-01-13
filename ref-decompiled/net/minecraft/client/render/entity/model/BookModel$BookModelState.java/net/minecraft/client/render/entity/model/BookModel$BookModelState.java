/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public static final class BookModel.BookModelState
extends Record {
    final float pageTurnAmount;
    final float leftFlipAmount;
    final float rightFlipAmount;
    final float pageTurnSpeed;

    public BookModel.BookModelState(float pageTurnAmount, float leftFlipAmount, float rightFlipAmount, float pageTurnSpeed) {
        this.pageTurnAmount = pageTurnAmount;
        this.leftFlipAmount = leftFlipAmount;
        this.rightFlipAmount = rightFlipAmount;
        this.pageTurnSpeed = pageTurnSpeed;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BookModel.BookModelState.class, "animationPos;pageFlip1;pageFlip2;open", "pageTurnAmount", "leftFlipAmount", "rightFlipAmount", "pageTurnSpeed"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BookModel.BookModelState.class, "animationPos;pageFlip1;pageFlip2;open", "pageTurnAmount", "leftFlipAmount", "rightFlipAmount", "pageTurnSpeed"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BookModel.BookModelState.class, "animationPos;pageFlip1;pageFlip2;open", "pageTurnAmount", "leftFlipAmount", "rightFlipAmount", "pageTurnSpeed"}, this, object);
    }

    public float pageTurnAmount() {
        return this.pageTurnAmount;
    }

    public float leftFlipAmount() {
        return this.leftFlipAmount;
    }

    public float rightFlipAmount() {
        return this.rightFlipAmount;
    }

    public float pageTurnSpeed() {
        return this.pageTurnSpeed;
    }
}
