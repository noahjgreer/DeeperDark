/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.model.EntityModelLayer
 *  net.minecraft.util.Identifier
 */
package net.minecraft.client.render.entity.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public record EntityModelLayer(Identifier id, String name) {
    private final Identifier id;
    private final String name;

    public EntityModelLayer(Identifier id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.valueOf(this.id) + "#" + this.name;
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{EntityModelLayer.class, "model;layer", "id", "name"}, this);
    }

    @Override
    public final boolean equals(Object o) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{EntityModelLayer.class, "model;layer", "id", "name"}, this, o);
    }

    public Identifier id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }
}

