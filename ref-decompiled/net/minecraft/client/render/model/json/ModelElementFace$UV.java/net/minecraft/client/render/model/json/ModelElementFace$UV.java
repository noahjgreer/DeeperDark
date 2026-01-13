/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public record ModelElementFace.UV(float minU, float minV, float maxU, float maxV) {
    public float getUVertices(int i) {
        return i == 0 || i == 1 ? this.minU : this.maxU;
    }

    public float getVVertices(int i) {
        return i == 0 || i == 3 ? this.minV : this.maxV;
    }
}
