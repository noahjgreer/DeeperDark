/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

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

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        SkullBlockEntityRenderer.render(null, 180.0f, this.animation, matrices, queue, light, this.model, this.renderLayer, i, null);
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.translate(0.5f, 0.0f, 0.5f);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        SkullBlockEntityModel.SkullModelState skullModelState = new SkullBlockEntityModel.SkullModelState();
        skullModelState.poweredTicks = this.animation;
        skullModelState.yaw = 180.0f;
        this.model.setAngles(skullModelState);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(SkullBlock.SkullType kind, Optional<Identifier> textureOverride, float animation) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)SkullBlock.SkullType.CODEC.fieldOf("kind").forGetter(Unbaked::kind), (App)Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::textureOverride), (App)Codec.FLOAT.optionalFieldOf("animation", (Object)Float.valueOf(0.0f)).forGetter(Unbaked::animation)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(SkullBlock.SkullType kind) {
            this(kind, Optional.empty(), 0.0f);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public @Nullable SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            SkullBlockEntityModel skullBlockEntityModel = SkullBlockEntityRenderer.getModels(context.entityModelSet(), this.kind);
            Identifier identifier = this.textureOverride.map(id -> id.withPath(texture -> "textures/entity/" + texture + ".png")).orElse(null);
            if (skullBlockEntityModel == null) {
                return null;
            }
            RenderLayer renderLayer = SkullBlockEntityRenderer.getCutoutRenderLayer(this.kind, identifier);
            return new HeadModelRenderer(skullBlockEntityModel, this.animation, renderLayer);
        }
    }
}
