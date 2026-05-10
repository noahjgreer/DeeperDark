package net.noahsarch.deeperdark.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;

public class VaultBERState extends BlockEntityRenderState {
    public ItemClusterRenderState displayItem = null;
    public float spin;
}
