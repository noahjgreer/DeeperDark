package net.noahsarch.deeperdark.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class CrateRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState item = new ItemStackRenderState();
    /** Light in front of each face, by {@link Direction#ordinal()}; -1 where a solid block hides it. */
    public final int[] faceLight = new int[Direction.values().length];
    public Direction facing = Direction.NORTH;
}
