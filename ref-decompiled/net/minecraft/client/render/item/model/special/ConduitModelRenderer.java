/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.ModelPart
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.ConduitModelRenderer
 *  net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class ConduitModelRenderer
implements SimpleSpecialModelRenderer {
    private final SpriteHolder spriteHolder;
    private final ModelPart shell;

    public ConduitModelRenderer(SpriteHolder spriteHolder, ModelPart shell) {
        this.spriteHolder = spriteHolder;
        this.shell = shell;
    }

    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0.5f);
        queue.submitModelPart(this.shell, matrices, ConduitBlockEntityRenderer.BASE_TEXTURE.getRenderLayer(RenderLayers::entitySolid), light, overlay, this.spriteHolder.getSprite(ConduitBlockEntityRenderer.BASE_TEXTURE), false, false, -1, null, i);
        matrices.pop();
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.5f, 0.5f, 0.5f);
        this.shell.collectVertices(matrixStack, consumer);
    }
}

