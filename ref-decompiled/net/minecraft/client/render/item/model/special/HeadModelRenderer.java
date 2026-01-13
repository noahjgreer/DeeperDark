/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel
 *  net.minecraft.client.render.block.entity.SkullBlockEntityModel$SkullModelState
 *  net.minecraft.client.render.block.entity.SkullBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.HeadModelRenderer
 *  net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class HeadModelRenderer
implements SimpleSpecialModelRenderer {
    private final SkullBlockEntityModel model;
    private final float animation;
    private final RenderLayer renderLayer;

    public HeadModelRenderer(SkullBlockEntityModel model, float animation, RenderLayer renderLayer) {
        this.model = model;
        this.animation = animation;
        this.renderLayer = renderLayer;
    }

    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        SkullBlockEntityRenderer.render(null, (float)180.0f, (float)this.animation, (MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, (SkullBlockEntityModel)this.model, (RenderLayer)this.renderLayer, (int)i, null);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.5f, 0.0f, 0.5f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        SkullBlockEntityModel.SkullModelState skullModelState = new SkullBlockEntityModel.SkullModelState();
        skullModelState.poweredTicks = this.animation;
        skullModelState.yaw = 180.0f;
        this.model.setAngles((Object)skullModelState);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }
}

