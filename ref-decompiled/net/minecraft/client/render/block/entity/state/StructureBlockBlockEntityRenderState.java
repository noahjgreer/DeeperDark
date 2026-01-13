/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.StructureBoxRendering$RenderMode
 *  net.minecraft.block.entity.StructureBoxRendering$StructureBox
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.StructureBlockBlockEntityRenderState
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.StructureBoxRendering;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class StructureBlockBlockEntityRenderState
extends BlockEntityRenderState {
    public boolean visible;
    public StructureBoxRendering.RenderMode renderMode;
    public StructureBoxRendering.StructureBox structureBox;
    public // Could not load outer class - annotation placement on inner may be incorrect
    @Nullable StructureBlockBlockEntityRenderState.InvisibleRenderType @Nullable [] invisibleBlocks;
    public boolean @Nullable [] field_62682;
}

