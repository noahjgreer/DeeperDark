/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class TridentModelRenderer
implements SimpleSpecialModelRenderer {
    private final TridentEntityModel model;

    public TridentModelRenderer(TridentEntityModel model) {
        this.model = model;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        matrices.push();
        matrices.scale(1.0f, -1.0f, -1.0f);
        queue.submitModelPart(this.model.getRootPart(), matrices, this.model.getLayer(TridentEntityModel.TEXTURE), light, overlay, null, false, glint, -1, null, i);
        matrices.pop();
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new TridentModelRenderer(new TridentEntityModel(context.entityModelSet().getModelPart(EntityModelLayers.TRIDENT)));
        }
    }
}
