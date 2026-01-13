/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.shaders.ShaderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
record ShaderLoader.ShaderSourceKey(Identifier id, ShaderType type) {
    @Override
    public String toString() {
        return String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
    }
}
