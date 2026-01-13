/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface BlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState> {
    public S createRenderState();

    default public void updateRenderState(T blockEntity, S state, float tickProgress, Vec3d cameraPos,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderState.updateBlockEntityRenderState(blockEntity, state, crumblingOverlay);
    }

    public void render(S var1, MatrixStack var2, OrderedRenderCommandQueue var3, CameraRenderState var4);

    default public boolean rendersOutsideBoundingBox() {
        return false;
    }

    default public int getRenderDistance() {
        return 64;
    }

    default public boolean isInRenderDistance(T blockEntity, Vec3d pos) {
        return Vec3d.ofCenter(((BlockEntity)blockEntity).getPos()).isInRange(pos, this.getRenderDistance());
    }
}
