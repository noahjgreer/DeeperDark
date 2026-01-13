/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.block.entity.state.EndGatewayBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.EndPortalBlockEntityRenderState
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.EndPortalBlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class EndGatewayBlockEntityRenderState
extends EndPortalBlockEntityRenderState {
    public int beamSpan;
    public float beamHeight;
    public int beamColor;
    public float beamRotationDegrees;
}

