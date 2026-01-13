/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
class WorldRenderer.1
implements FrameGraphBuilder.Profiler {
    final /* synthetic */ Profiler field_53082;

    WorldRenderer.1() {
        this.field_53082 = profiler;
    }

    @Override
    public void push(String location) {
        this.field_53082.push(location);
    }

    @Override
    public void pop(String location) {
        this.field_53082.pop();
    }
}
