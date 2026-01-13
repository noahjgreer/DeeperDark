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
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.model.ChestBlockModel;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.item.model.special.SimpleSpecialModelRenderer;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3fc;

@Environment(value=EnvType.CLIENT)
public class ChestModelRenderer
implements SimpleSpecialModelRenderer {
    public static final Identifier CHRISTMAS_ID = Identifier.ofVanilla("christmas");
    public static final Identifier NORMAL_ID = Identifier.ofVanilla("normal");
    public static final Identifier TRAPPED_ID = Identifier.ofVanilla("trapped");
    public static final Identifier ENDER_ID = Identifier.ofVanilla("ender");
    public static final Identifier COPPER_ID = Identifier.ofVanilla("copper");
    public static final Identifier EXPOSED_COPPER_ID = Identifier.ofVanilla("copper_exposed");
    public static final Identifier WEATHERED_COPPER_ID = Identifier.ofVanilla("copper_weathered");
    public static final Identifier OXIDIZED_COPPER_ID = Identifier.ofVanilla("copper_oxidized");
    private final SpriteHolder spriteHolder;
    private final ChestBlockModel model;
    private final SpriteIdentifier textureId;
    private final float openness;

    public ChestModelRenderer(SpriteHolder spriteHolder, ChestBlockModel model, SpriteIdentifier textureId, float openness) {
        this.spriteHolder = spriteHolder;
        this.model = model;
        this.textureId = textureId;
        this.openness = openness;
    }

    @Override
    public void render(ItemDisplayContext displayContext, MatrixStack matrices, OrderedRenderCommandQueue queue, int light, int overlay, boolean glint, int i) {
        queue.submitModel(this.model, Float.valueOf(this.openness), matrices, this.textureId.getRenderLayer(RenderLayers::entitySolid), light, overlay, -1, this.spriteHolder.getSprite(this.textureId), i, null);
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        this.model.setAngles(Float.valueOf(this.openness));
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier texture, float openness) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), (App)Codec.FLOAT.optionalFieldOf("openness", (Object)Float.valueOf(0.0f)).forGetter(Unbaked::openness)).apply((Applicative)instance, Unbaked::new));

        public Unbaked(Identifier texture) {
            this(texture, 0.0f);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            ChestBlockModel chestBlockModel = new ChestBlockModel(context.entityModelSet().getModelPart(EntityModelLayers.CHEST));
            SpriteIdentifier spriteIdentifier = TexturedRenderLayers.CHEST_SPRITE_MAPPER.map(this.texture);
            return new ChestModelRenderer(context.spriteHolder(), chestBlockModel, spriteIdentifier, this.openness);
        }
    }
}
