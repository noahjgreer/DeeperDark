/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
@Environment(value=EnvType.CLIENT)
static interface GameEventDebugRenderer.EventConsumer {
    public void accept(Vec3d var1, int var2);
}
