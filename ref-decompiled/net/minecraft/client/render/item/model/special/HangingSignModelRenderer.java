/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model$SinglePartModel
 *  net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.HangingSignModelRenderer
 *  net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.block.entity.HangingSignBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class HangingSignModelRenderer
implements SimpleSpecialModelRenderer {
    private final SpriteHolder spriteHolder;
    private final Model.SinglePartModel model;
    private final SpriteIdentifier texture;

    public HangingSignModelRenderer(SpriteHolder spriteHolder, Model.SinglePartModel model, SpriteIdentifier texture) {
        this.spriteHolder = spriteHolder;
        this.model = model;
        this.texture = texture;
    }

    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        HangingSignBlockEntityRenderer.renderAsItem((SpriteHolder)this.spriteHolder, (MatrixStack)matrices, (OrderedRenderCommandQueue)queue, (int)light, (int)overlay, (Model.SinglePartModel)this.model, (SpriteIdentifier)this.texture);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        HangingSignBlockEntityRenderer.setAngles((MatrixStack)matrixStack, (float)0.0f);
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }
}

