/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ShieldModelRenderer
implements SpecialModelRenderer<ComponentMap> {
    private final SpriteHolder spriteHolder;
    private final ShieldEntityModel model;

    public ShieldModelRenderer(SpriteHolder spriteHolder, ShieldEntityModel model) {
        this.spriteHolder = spriteHolder;
        this.model = model;
    }

    @Override
    public @Nullable ComponentMap getData(ItemStack itemStack) {
        return itemStack.getImmutableComponents();
    }

    @Override
    public void render(@Nullable ComponentMap componentMap, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        BannerPatternsComponent bannerPatternsComponent = componentMap != null ? componentMap.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT) : BannerPatternsComponent.DEFAULT;
        DyeColor dyeColor = componentMap != null ? componentMap.get(DataComponentTypes.BASE_COLOR) : null;
        boolean bl2 = !bannerPatternsComponent.layers().isEmpty() || dyeColor != null;
        matrixStack.push();
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        SpriteIdentifier spriteIdentifier = bl2 ? ModelBaker.SHIELD_BASE : ModelBaker.SHIELD_BASE_NO_PATTERN;
        orderedRenderCommandQueue.submitModelPart(this.model.getHandle(), matrixStack, this.model.getLayer(spriteIdentifier.getAtlasId()), i, j, this.spriteHolder.getSprite(spriteIdentifier), false, false, -1, null, k);
        if (bl2) {
            BannerBlockEntityRenderer.renderCanvas(this.spriteHolder, matrixStack, orderedRenderCommandQueue, i, j, this.model, Unit.INSTANCE, spriteIdentifier, false, Objects.requireNonNullElse(dyeColor, DyeColor.WHITE), bannerPatternsComponent, bl, null, k);
        } else {
            orderedRenderCommandQueue.submitModelPart(this.model.getPlate(), matrixStack, this.model.getLayer(spriteIdentifier.getAtlasId()), i, j, this.spriteHolder.getSprite(spriteIdentifier), false, bl, -1, null, k);
        }
        matrixStack.pop();
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        MatrixStack matrixStack = new MatrixStack();
        matrixStack.scale(1.0f, -1.0f, -1.0f);
        this.model.getRootPart().collectVertices(matrixStack, consumer);
    }

    @Override
    public /* synthetic */ @Nullable Object getData(ItemStack stack) {
        return this.getData(stack);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final Unbaked INSTANCE = new Unbaked();
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit((Object)INSTANCE);

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new ShieldModelRenderer(context.spriteHolder(), new ShieldEntityModel(context.entityModelSet().getModelPart(EntityModelLayers.SHIELD)));
        }
    }
}
