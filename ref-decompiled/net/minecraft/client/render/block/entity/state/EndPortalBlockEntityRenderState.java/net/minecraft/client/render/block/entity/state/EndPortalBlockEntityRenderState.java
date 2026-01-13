/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class EndPortalBlockEntityRenderState
extends BlockEntityRenderState {
    public EnumSet<Direction> sides = EnumSet.noneOf(Direction.class);
}
