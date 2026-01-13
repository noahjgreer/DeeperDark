/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.EnchantingTableBlockEntity
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.EnchantingTableBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.model.BookModel
 *  net.minecraft.client.render.entity.model.BookModel$BookModelState
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.model.Model;
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

    public EnchantingTableBlockEntityRenderState createRenderState() {
        return new EnchantingTableBlockEntityRenderState();
    }

    public void updateRenderState(EnchantingTableBlockEntity enchantingTableBlockEntity, EnchantingTableBlockEntityRenderState enchantingTableBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        float g;
        super.updateRenderState((BlockEntity)enchantingTableBlockEntity, (BlockEntityRenderState)enchantingTableBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        enchantingTableBlockEntityRenderState.pageAngle = MathHelper.lerp((float)f, (float)enchantingTableBlockEntity.pageAngle, (float)enchantingTableBlockEntity.nextPageAngle);
        enchantingTableBlockEntityRenderState.pageTurningSpeed = MathHelper.lerp((float)f, (float)enchantingTableBlockEntity.pageTurningSpeed, (float)enchantingTableBlockEntity.nextPageTurningSpeed);
        enchantingTableBlockEntityRenderState.ticks = (float)enchantingTableBlockEntity.ticks + f;
        for (g = enchantingTableBlockEntity.bookRotation - enchantingTableBlockEntity.lastBookRotation; g >= (float)Math.PI; g -= (float)Math.PI * 2) {
        }
        while (g < (float)(-Math.PI)) {
            g += (float)Math.PI * 2;
        }
        enchantingTableBlockEntityRenderState.bookRotationDegrees = enchantingTableBlockEntity.lastBookRotation + g * f;
    }

    public void render(EnchantingTableBlockEntityRenderState enchantingTableBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        matrixStack.push();
        matrixStack.translate(0.5f, 0.75f, 0.5f);
        matrixStack.translate(0.0f, 0.1f + MathHelper.sin((double)(enchantingTableBlockEntityRenderState.ticks * 0.1f)) * 0.01f, 0.0f);
        float f = enchantingTableBlockEntityRenderState.bookRotationDegrees;
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotation(-f));
        matrixStack.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(80.0f));
        float g = MathHelper.fractionalPart((float)(enchantingTableBlockEntityRenderState.pageAngle + 0.25f)) * 1.6f - 0.3f;
        float h = MathHelper.fractionalPart((float)(enchantingTableBlockEntityRenderState.pageAngle + 0.75f)) * 1.6f - 0.3f;
        BookModel.BookModelState bookModelState = new BookModel.BookModelState(enchantingTableBlockEntityRenderState.ticks, MathHelper.clamp((float)g, (float)0.0f, (float)1.0f), MathHelper.clamp((float)h, (float)0.0f, (float)1.0f), enchantingTableBlockEntityRenderState.pageTurningSpeed);
        orderedRenderCommandQueue.submitModel((Model)this.book, (Object)bookModelState, matrixStack, BOOK_TEXTURE.getRenderLayer(RenderLayers::entitySolid), enchantingTableBlockEntityRenderState.lightmapCoordinates, OverlayTexture.DEFAULT_UV, -1, this.spriteHolder.getSprite(BOOK_TEXTURE), 0, enchantingTableBlockEntityRenderState.crumblingOverlay);
        matrixStack.pop();
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

