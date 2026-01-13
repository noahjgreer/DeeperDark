/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.block.entity.model.CopperGolemStatueModel
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.model.special.CopperGolemStatueModelRenderer
 *  net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Direction
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.block.entity.model.CopperGolemStatueModel;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.joml.Vector3fc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class CopperGolemStatueModelRenderer
implements SimpleSpecialModelRenderer {
    private static final Direction field_64689 = Direction.SOUTH;
    private final CopperGolemStatueModel model;
    private final Identifier texture;

    public CopperGolemStatueModelRenderer(CopperGolemStatueModel model, Identifier texture) {
        this.model = model;
        this.texture = texture;
    }

    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        CopperGolemStatueModelRenderer.setAngles((MatrixStack)matrices);
        queue.submitModel((Model)this.model, (Object)Direction.SOUTH, matrices, RenderLayers.entityCutoutNoCull((Identifier)this.texture), light, overlay, -1, null, i, null);
    }

    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        CopperGolemStatueModelRenderer.setAngles((MatrixStack)matrixStack);
        this.model.setAngles(field_64689);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    private static void setAngles(MatrixStack matrices) {
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.scale(-1.0f, -1.0f, 1.0f);
    }
}

