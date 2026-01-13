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
import net.minecraft.client.render.model.json.BlockModelDefinition;

@Environment(value=EnvType.CLIENT)
static final class BlockStatesLoader.LoadedBlockStateDefinition
extends Record {
    final String source;
    final BlockModelDefinition contents;

    BlockStatesLoader.LoadedBlockStateDefinition(String source, BlockModelDefinition contents) {
        this.source = source;
        this.contents = contents;
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockStatesLoader.LoadedBlockStateDefinition.class, "source;contents", "source", "contents"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockStatesLoader.LoadedBlockStateDefinition.class, "source;contents", "source", "contents"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockStatesLoader.LoadedBlockStateDefinition.class, "source;contents", "source", "contents"}, this, object);
    }

    public String source() {
        return this.source;
    }

    public BlockModelDefinition contents() {
        return this.contents;
    }
}
