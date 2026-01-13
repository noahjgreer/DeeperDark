/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState$BeamSegment
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 */
package net.minecraft.client.render.block.entity.state;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderState
extends BlockEntityRenderState {
    public float beamRotationDegrees;
    public float beamScale;
    public List<BeamSegment> beamSegments = new ArrayList();
}

