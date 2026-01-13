/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.WoodType;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class SignModelRenderer
implements SimpleSpecialModelRenderer {
    private final SpriteHolder spriteHolder;
    private final Model.SinglePartModel model;
    private final SpriteIdentifier texture;

    public SignModelRenderer(SpriteHolder spriteHolder, Model.SinglePartModel model, SpriteIdentifier texture) {
        this.spriteHolder = spriteHolder;
        this.model = model;
        this.texture = texture;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        SignBlockEntityRenderer.renderAsItem(this.spriteHolder, matrices, queue, light, overlay, this.model, this.texture);
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        SignBlockEntityRenderer.setTransformsForItem(matrixStack);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(WoodType woodType, Optional<Identifier> texture) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)WoodType.CODEC.fieldOf("wood_type").forGetter(Unbaked::woodType), (App)Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(WoodType woodType) {
            this(woodType, Optional.empty());
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            Model.SinglePartModel singlePartModel = SignBlockEntityRenderer.createSignModel(context.entityModelSet(), this.woodType, true);
            SpriteIdentifier spriteIdentifier = this.texture.map(TexturedRenderLayers.SIGN_SPRITE_MAPPER::map).orElseGet(() -> TexturedRenderLayers.getSignTextureId(this.woodType));
            return new SignModelRenderer(context.spriteHolder(), singlePartModel, spriteIdentifier);
        }
    }
}
