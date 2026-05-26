package net.noahsarch.deeperdark.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

@Environment(EnvType.CLIENT)
public class PrimedDynamiteRenderState extends EntityRenderState {
    public float fuseRemainingInTicks;
    public final BlockModelRenderState blockState = new BlockModelRenderState();
}
