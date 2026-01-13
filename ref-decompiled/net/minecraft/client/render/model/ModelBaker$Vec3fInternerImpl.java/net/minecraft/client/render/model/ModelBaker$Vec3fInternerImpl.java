/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Interner
 *  com.google.common.collect.Interners
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.Baker;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
static class ModelBaker.Vec3fInternerImpl
implements Baker.Vec3fInterner {
    private final Interner<Vector3fc> INTERNER = Interners.newStrongInterner();

    ModelBaker.Vec3fInternerImpl() {
    }

    @Override
    public Vector3fc intern(Vector3fc vec) {
        return (Vector3fc)this.INTERNER.intern((Object)vec);
    }
}
