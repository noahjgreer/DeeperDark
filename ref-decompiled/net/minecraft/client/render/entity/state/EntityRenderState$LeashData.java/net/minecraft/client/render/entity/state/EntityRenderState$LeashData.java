/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public static class EntityRenderState.LeashData {
    public Vec3d offset = Vec3d.ZERO;
    public Vec3d startPos = Vec3d.ZERO;
    public Vec3d endPos = Vec3d.ZERO;
    public int leashedEntityBlockLight = 0;
    public int leashHolderBlockLight = 0;
    public int leashedEntitySkyLight = 15;
    public int leashHolderSkyLight = 15;
    public boolean slack = true;
}
