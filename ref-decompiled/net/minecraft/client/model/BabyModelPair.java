/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.BabyModelPair
 *  net.minecraft.client.model.Model
 */
package net.minecraft.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(value=EnvType.CLIENT)
public record BabyModelPair<T extends Model>(T adultModel, T babyModel) {
    private final T adultModel;
    private final T babyModel;

    public BabyModelPair(T adultModel, T babyModel) {
        this.adultModel = adultModel;
        this.babyModel = babyModel;
    }

    public T get(boolean baby) {
        return (T)(baby ? this.babyModel : this.adultModel);
    }

    public T adultModel() {
        return (T)this.adultModel;
    }

    public T babyModel() {
        return (T)this.babyModel;
    }
}

