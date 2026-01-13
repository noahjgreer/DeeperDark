/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PistonBlockEntityRenderState
extends BlockEntityRenderState {
    public @Nullable MovingBlockRenderState pushedState;
    public @Nullable MovingBlockRenderState extendedPistonState;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
}
