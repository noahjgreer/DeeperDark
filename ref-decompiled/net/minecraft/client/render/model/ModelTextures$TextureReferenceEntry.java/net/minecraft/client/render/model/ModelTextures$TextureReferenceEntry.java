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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelTextures;

@Environment(value=EnvType.CLIENT)
static final class ModelTextures.TextureReferenceEntry
extends Record
implements ModelTextures.Entry {
    final String target;

    ModelTextures.TextureReferenceEntry(String target) {
        this.target = target;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ModelTextures.TextureReferenceEntry.class, "target", "target"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ModelTextures.TextureReferenceEntry.class, "target", "target"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ModelTextures.TextureReferenceEntry.class, "target", "target"}, this, object);
    }

    public String target() {
        return this.target;
    }
}
