/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class ConduitBlockEntityRenderState
extends BlockEntityRenderState {
    public float ticks;
    public boolean active;
    public float rotation;
    public int rotationPhase;
    public boolean eyeOpen;
}
