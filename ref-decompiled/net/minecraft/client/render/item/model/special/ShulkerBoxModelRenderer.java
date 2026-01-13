/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.ShulkerBoxModelRenderer
 *  net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.util.math.Direction
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.ShulkerBoxBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.math.Direction;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class ShulkerBoxModelRenderer
implements SimpleSpecialModelRenderer {
    private final ShulkerBoxBlockEntityRenderer blockEntityRenderer;
    private final float openness;
    private final Direction facing;
    private final SpriteIdentifier textureId;

    public ShulkerBoxModelRenderer(ShulkerBoxBlockEntityRenderer blockEntityRenderer, float openness, Direction facing, SpriteIdentifier textureId) {
        this.blockEntityRenderer = blockEntityRenderer;
        this.openness = openness;
        this.facing = facing;
        this.textureId = textureId;
    }

    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        this.blockEntityRenderer.render(matrices, queue, light, overlay, this.facing, this.openness, null, this.textureId, i);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        this.blockEntityRenderer.collectVertices(this.facing, this.openness, consumer);
    }
}

