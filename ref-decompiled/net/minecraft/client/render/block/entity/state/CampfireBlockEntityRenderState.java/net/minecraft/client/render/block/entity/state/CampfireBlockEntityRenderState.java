/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity.state;

import java.util.Collections;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class CampfireBlockEntityRenderState
extends BlockEntityRenderState {
    public List<ItemRenderState> cookedItemStates = Collections.emptyList();
    public Direction facing = Direction.NORTH;
}
