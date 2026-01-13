/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.shaders.ShaderType;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
static final class GlBackend.ShaderKey
extends Record {
    final Identifier id;
    final ShaderType type;
    final Defines defines;

    GlBackend.ShaderKey(Identifier id, ShaderType type, Defines defines) {
        this.id = id;
        this.type = type;
        this.defines = defines;
    }

    @Override
    public String toString() {
        String string = String.valueOf(this.id) + " (" + String.valueOf((Object)this.type) + ")";
        if (!this.defines.isEmpty()) {
            return string + " with " + String.valueOf(this.defines);
        }
        return string;
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{GlBackend.ShaderKey.class, "id;type;defines", "id", "type", "defines"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{GlBackend.ShaderKey.class, "id;type;defines", "id", "type", "defines"}, this, object);
    }

    public Identifier id() {
        return this.id;
    }

    public ShaderType type() {
        return this.type;
    }

    public Defines defines() {
        return this.defines;
    }
}
