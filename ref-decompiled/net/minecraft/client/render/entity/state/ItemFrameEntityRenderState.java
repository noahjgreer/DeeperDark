/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.MapRenderState
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.ItemFrameEntityRenderState
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.component.type.MapIdComponent
 *  net.minecraft.util.math.Direction
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.util.math.Direction;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemFrameEntityRenderState
extends EntityRenderState {
    public Direction facing = Direction.NORTH;
    public final ItemRenderState itemRenderState = new ItemRenderState();
    public int rotation;
    public boolean glow;
    public @Nullable MapIdComponent mapId;
    public final MapRenderState mapRenderState = new MapRenderState();
}

