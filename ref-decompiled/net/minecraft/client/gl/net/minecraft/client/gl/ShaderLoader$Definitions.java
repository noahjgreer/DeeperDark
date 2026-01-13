/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.PostEffectPipeline;
import net.minecraft.client.gl.ShaderLoader;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public static final class ShaderLoader.Definitions
extends Record {
    final Map<ShaderLoader.ShaderSourceKey, String> shaderSources;
    final Map<Identifier, PostEffectPipeline> postChains;
    public static final ShaderLoader.Definitions EMPTY = new ShaderLoader.Definitions(Map.of(), Map.of());

    public ShaderLoader.Definitions(Map<ShaderLoader.ShaderSourceKey, String> shaderSources, Map<Identifier, PostEffectPipeline> postChains) {
        this.shaderSources = shaderSources;
        this.postChains = postChains;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ShaderLoader.Definitions.class, "shaderSources;postChains", "shaderSources", "postChains"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ShaderLoader.Definitions.class, "shaderSources;postChains", "shaderSources", "postChains"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ShaderLoader.Definitions.class, "shaderSources;postChains", "shaderSources", "postChains"}, this, object);
    }

    public Map<ShaderLoader.ShaderSourceKey, String> shaderSources() {
        return this.shaderSources;
    }

    public Map<Identifier, PostEffectPipeline> postChains() {
        return this.postChains;
    }
}
