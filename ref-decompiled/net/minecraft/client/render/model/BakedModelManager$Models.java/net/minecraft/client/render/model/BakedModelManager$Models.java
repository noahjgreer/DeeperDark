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
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static final class BakedModelManager.Models
extends Record {
    private final BakedSimpleModel missing;
    final Map<Identifier, BakedSimpleModel> models;

    BakedModelManager.Models(BakedSimpleModel missing, Map<Identifier, BakedSimpleModel> models) {
        this.missing = missing;
        this.models = models;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakedModelManager.Models.class, "missing;models", "missing", "models"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakedModelManager.Models.class, "missing;models", "missing", "models"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakedModelManager.Models.class, "missing;models", "missing", "models"}, this, object);
    }

    public BakedSimpleModel missing() {
        return this.missing;
    }

    public Map<Identifier, BakedSimpleModel> models() {
        return this.models;
    }
}
