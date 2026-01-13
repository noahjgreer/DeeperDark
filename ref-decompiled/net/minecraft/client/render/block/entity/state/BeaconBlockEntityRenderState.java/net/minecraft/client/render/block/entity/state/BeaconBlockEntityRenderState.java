/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderState
extends BlockEntityRenderState {
    public float beamRotationDegrees;
    public float beamScale;
    public List<BeamSegment> beamSegments = new ArrayList<BeamSegment>();

    @Environment(value=EnvType.CLIENT)
    public record BeamSegment(int color, int height) {
    }
}
