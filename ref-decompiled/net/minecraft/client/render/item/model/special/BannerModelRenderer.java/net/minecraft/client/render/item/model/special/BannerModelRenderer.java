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
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model.special;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BannerModelRenderer
implements SpecialModelRenderer<BannerPatternsComponent> {
    private final BannerBlockEntityRenderer blockEntityRenderer;
    private final DyeColor baseColor;

    public BannerModelRenderer(DyeColor baseColor, BannerBlockEntityRenderer blockEntityRenderer) {
        this.blockEntityRenderer = blockEntityRenderer;
        this.baseColor = baseColor;
    }

    @Override
    public @Nullable BannerPatternsComponent getData(ItemStack itemStack) {
        return itemStack.get(DataComponentTypes.BANNER_PATTERNS);
    }

    @Override
    public void render(@Nullable BannerPatternsComponent bannerPatternsComponent, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        this.blockEntityRenderer.renderAsItem(matrixStack, orderedRenderCommandQueue, i, j, this.baseColor, Objects.requireNonNullElse(bannerPatternsComponent, BannerPatternsComponent.DEFAULT), k);
    }

    @Override
    public void collectVertices(Consumer<Vector3fc> consumer) {
        this.blockEntityRenderer.collectVertices(consumer);
    }

    @Override
    public /* synthetic */ @Nullable Object getData(ItemStack stack) {
        return this.getData(stack);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(DyeColor baseColor) implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)DyeColor.CODEC.fieldOf("color").forGetter(Unbaked::baseColor)).apply((Applicative)instance, Unbaked::new));

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new BannerModelRenderer(this.baseColor, new BannerBlockEntityRenderer(context));
        }
    }
}
