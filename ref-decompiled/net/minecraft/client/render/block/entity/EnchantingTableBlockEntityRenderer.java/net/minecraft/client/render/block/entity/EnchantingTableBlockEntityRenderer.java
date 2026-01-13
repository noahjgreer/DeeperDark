/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.EnchantingTableBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer
implements BlockEntityRenderer<EnchantingTableBlockEntity, EnchantingTableBlockEntityRenderState> {
    public static final SpriteIdentifier BOOK_TEXTURE = TexturedRenderLayers.ENTITY_SPRITE_MAPPER.mapVanilla("enchanting_table_book");
    private final SpriteHolder spriteHolder;
    private final BookModel book;

    public EnchantingTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.spriteHolder = ctx.spriteHolder();
        this.book = new BookModel(ctx.getLayerModelPart(EntityModelLayers.BOOK));
    }

    @Override
    public EnchantingTableBlockEntityRenderState createRenderState() {
        return new EnchantingTableBlockEntityRenderState();
    }

    @Override
    public void updateRenderState(EnchantingTableBlockEntity enchantingTableBlockEntity, EnchantingTableBlockEntityRenderState enchantingTableBlockEntityRenderState, float f, Vec3d vec3d,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        float g;
        BlockEntityRenderer.super.updateRenderState(enchantingTableBlockEntity, enchantingTableBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        enchantingTableBlockEntityRenderState.pageAngle = MathHelper.lerp(f, enchantingTableBlockEntity.pageAngle, enchantingTableBlockEntity.nextPageAngle);
        enchantingTableBlockEntityRenderState.pageTurningSpeed = MathHelper.lerp(f, enchantingTableBlockEntity.pageTurningSpeed, enchantingTableBlockEntity.nextPageTurningSpeed);
        enchantingTableBlockEntityRenderState.ticks = (float)enchantingTableBlockEntity.ticks + f;
        for (g = enchantingTableBlockEntity.bookRotation - enchantingTableBlockEntity.lastBookRotation; g >= (float)Math.PI; g -= (float)Math.PI * 2) {
        }
        while (g < (float)(-Math.PI)) {
            g += (float)Math.PI * 2;
        }
        enchantingTableBlockEntityRenderState.bookRotationDegrees = enchantingTableBlockEntity.lastBookRotation + g * f;
    }

    @Override
    public void render(EnchantingTableBlockEntityRenderState enchantingTableBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.translate(0.5f, 0.75f, 0.5f);
        matrixStack.translate(0.0f, 0.1f + MathHelper.sin(enchantingTableBlockEntityRenderState.ticks * 0.1f) * 0.01f, 0.0f);
        float f = enchantingTableBlockEntityRenderState.bookRotationDegrees;
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(-f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(80.0f));
        float g = MathHelper.fractionalPart(enchantingTableBlockEntityRenderState.pageAngle + 0.25f) * 1.6f - 0.3f;
        float h = MathHelper.fractionalPart(enchantingTableBlockEntityRenderState.pageAngle + 0.75f) * 1.6f - 0.3f;
        BookModel.BookModelState bookModelState = new BookModel.BookModelState(enchantingTableBlockEntityRenderState.ticks, MathHelper.clamp(g, 0.0f, 1.0f), MathHelper.clamp(h, 0.0f, 1.0f), enchantingTableBlockEntityRenderState.pageTurningSpeed);
        orderedRenderCommandQueue.submitModel(this.book, bookModelState, matrixStack, BOOK_TEXTURE.getRenderLayer(RenderLayers::entitySolid), enchantingTableBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, this.spriteHolder.getSprite(BOOK_TEXTURE), 0, enchantingTableBlockEntityRenderState.crumblingOverlay);
        matrixStack.pop();
    }

    @Override
    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}
