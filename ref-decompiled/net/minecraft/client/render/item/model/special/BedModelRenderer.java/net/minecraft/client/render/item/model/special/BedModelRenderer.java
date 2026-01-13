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
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BedBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class BedModelRenderer
implements SimpleSpecialModelRenderer {
    private final BedBlockEntityRenderer blockEntityRenderer;
    private final SpriteIdentifier textureId;

    public BedModelRenderer(BedBlockEntityRenderer blockEntityRenderer, SpriteIdentifier textureId) {
        this.blockEntityRenderer = blockEntityRenderer;
        this.textureId = textureId;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        this.blockEntityRenderer.renderAsItem(matrices, queue, light, overlay, this.textureId, i);
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        this.blockEntityRenderer.collectVertices(consumer);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier texture) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(DyeColor color) {
            this(TexturedRenderLayers.createColorId(color));
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new BedModelRenderer(new BedBlockEntityRenderer(context), TexturedRenderLayers.BED_SPRITE_MAPPER.map(this.texture));
        }
    }
}
