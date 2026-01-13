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
import net.minecraft.block.entity.TestInstanceBlockEntity;
import net.minecraft.client.render.block.entity.state.BeaconBlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState;

@Environment(value=EnvType.CLIENT)
public class TestInstanceBlockEntityRenderState
extends BlockEntityRenderState {
    public BeaconBlockEntityRenderState beaconState;
    public StructureBlockBlockEntityRenderState structureState;
    public final List<TestInstanceBlockEntity.Error> errors = new ArrayList<TestInstanceBlockEntity.Error>();
}
