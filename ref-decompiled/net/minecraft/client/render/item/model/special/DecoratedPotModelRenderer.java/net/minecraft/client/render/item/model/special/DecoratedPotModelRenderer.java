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
import net.minecraft.block.entity.Sherds;
import net.minecraft.client.render.block.entity.DecoratedPotBlockEntityRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class DecoratedPotModelRenderer
implements SpecialModelRenderer<Sherds> {
    private final DecoratedPotBlockEntityRenderer blockEntityRenderer;

    public DecoratedPotModelRenderer(DecoratedPotBlockEntityRenderer blockEntityRenderer) {
        this.blockEntityRenderer = blockEntityRenderer;
    }

    @Override
    public @Nullable Sherds getData(ItemStack itemStack) {
        return itemStack.get(DataComponentTypes.POT_DECORATIONS);
    }

    @Override
    public void render(@Nullable Sherds sherds, ItemDisplayContext itemDisplayContext, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, int j, boolean bl, int k) {
        this.blockEntityRenderer.render(matrixStack, orderedRenderCommandQueue, i, j, Objects.requireNonNullElse(sherds, Sherds.DEFAULT), k);
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
    public record Unbaked() implements SpecialModelRenderer.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = MapCodec.unit((Object)new Unbaked());

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }

        @Override
        public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakeContext context) {
            return new DecoratedPotModelRenderer(new DecoratedPotBlockEntityRenderer(context));
        }
    }
}
