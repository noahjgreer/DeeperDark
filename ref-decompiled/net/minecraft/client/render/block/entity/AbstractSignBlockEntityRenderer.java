/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.AbstractSignBlock
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.WoodType
 *  net.minecraft.block.entity.BlockEntity
 *  net.minecraft.block.entity.SignBlockEntity
 *  net.minecraft.block.entity.SignText
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.font.TextRenderer$TextLayerType
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.Model$SinglePartModel
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.block.entity.AbstractSignBlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRenderer
 *  net.minecraft.client.render.block.entity.BlockEntityRendererFactory$Context
 *  net.minecraft.client.render.block.entity.state.BlockEntityRenderState
 *  net.minecraft.client.render.block.entity.state.SignBlockEntityRenderState
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.SpriteIdentifier
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.text.OrderedText
 *  net.minecraft.text.StringVisitable
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Unit
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WoodType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.block.entity.state.SignBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class AbstractSignBlockEntityRenderer
implements BlockEntityRenderer<SignBlockEntity, SignBlockEntityRenderState> {
    private static final int GLOWING_BLACK_TEXT_COLOR = -988212;
    private static final int MAX_COLORED_TEXT_OUTLINE_RENDER_DISTANCE = MathHelper.square((int)16);
    private final TextRenderer textRenderer;
    private final SpriteHolder spriteHolder;

    public AbstractSignBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.textRenderer = context.textRenderer();
        this.spriteHolder = context.spriteHolder();
    }

    protected abstract Model.SinglePartModel getModel(BlockState var1, WoodType var2);

    protected abstract SpriteIdentifier getTextureId(WoodType var1);

    protected abstract float getSignScale();

    protected abstract float getTextScale();

    protected abstract Vec3d getTextOffset();

    protected abstract void applyTransforms(MatrixStack var1, float var2, BlockState var3);

    public void render(SignBlockEntityRenderState signBlockEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        BlockState blockState = signBlockEntityRenderState.blockState;
        AbstractSignBlock abstractSignBlock = (AbstractSignBlock)blockState.getBlock();
        Model.SinglePartModel singlePartModel = this.getModel(blockState, abstractSignBlock.getWoodType());
        this.render(signBlockEntityRenderState, matrixStack, blockState, abstractSignBlock, abstractSignBlock.getWoodType(), singlePartModel, signBlockEntityRenderState.crumblingOverlay, orderedRenderCommandQueue);
    }

    private void render(SignBlockEntityRenderState renderState, MatrixStack matrices, BlockState blockState, AbstractSignBlock block, WoodType woodType, Model.SinglePartModel model, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, OrderedRenderCommandQueue queue) {
        matrices.push();
        this.applyTransforms(matrices, -block.getRotationDegrees(blockState), blockState);
        this.renderSign(matrices, renderState.lightmapCoordinates, woodType, model, crumblingOverlay, queue);
        this.renderText(renderState, matrices, queue, true);
        this.renderText(renderState, matrices, queue, false);
        matrices.pop();
    }

    protected void renderSign(MatrixStack matrices, int lightmapCoords, WoodType woodType, Model.SinglePartModel model, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, OrderedRenderCommandQueue queue) {
        matrices.push();
        float f = this.getSignScale();
        matrices.scale(f, -f, -f);
        SpriteIdentifier spriteIdentifier = this.getTextureId(woodType);
        RenderLayer renderLayer = spriteIdentifier.getRenderLayer(arg_0 -> ((Model.SinglePartModel)model).getLayer(arg_0));
        queue.submitModel((Model)model, (Object)Unit.INSTANCE, matrices, renderLayer, lightmapCoords, OverlayTexture.DEFAULT_UV, -1, this.spriteHolder.getSprite(spriteIdentifier), 0, crumblingOverlay);
        matrices.pop();
    }

    private void renderText(SignBlockEntityRenderState renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, boolean front) {
        int l;
        boolean bl;
        int k;
        SignText signText;
        SignText signText2 = signText = front ? renderState.frontText : renderState.backText;
        if (signText == null) {
            return;
        }
        matrices.push();
        this.applyTextTransforms(matrices, front, this.getTextOffset());
        int i = AbstractSignBlockEntityRenderer.getTextColor((SignText)signText);
        int j = 4 * renderState.textLineHeight / 2;
        OrderedText[] orderedTexts = signText.getOrderedMessages(renderState.filterText, textx -> {
            List list = this.textRenderer.wrapLines((StringVisitable)textx, signBlockEntityRenderState.maxTextWidth);
            return list.isEmpty() ? OrderedText.EMPTY : (OrderedText)list.get(0);
        });
        if (signText.isGlowing()) {
            k = signText.getColor().getSignColor();
            bl = k == DyeColor.BLACK.getSignColor() || renderState.renderTextOutline;
            l = 0xF000F0;
        } else {
            k = i;
            bl = false;
            l = renderState.lightmapCoordinates;
        }
        for (int m = 0; m < 4; ++m) {
            OrderedText orderedText = orderedTexts[m];
            float f = -this.textRenderer.getWidth(orderedText) / 2;
            queue.submitText(matrices, f, (float)(m * renderState.textLineHeight - j), orderedText, false, TextRenderer.TextLayerType.POLYGON_OFFSET, l, k, 0, bl ? i : 0);
        }
        matrices.pop();
    }

    private void applyTextTransforms(MatrixStack matrices, boolean front, Vec3d textOffset) {
        if (!front) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        }
        float f = 0.015625f * this.getTextScale();
        matrices.translate(textOffset);
        matrices.scale(f, -f, f);
    }

    private static boolean shouldRenderTextOutline(BlockPos pos) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        if (clientPlayerEntity != null && minecraftClient.options.getPerspective().isFirstPerson() && clientPlayerEntity.isUsingSpyglass()) {
            return true;
        }
        Entity entity = minecraftClient.getCameraEntity();
        return entity != null && entity.squaredDistanceTo(Vec3d.ofCenter((Vec3i)pos)) < (double)MAX_COLORED_TEXT_OUTLINE_RENDER_DISTANCE;
    }

    public static int getTextColor(SignText text) {
        int i = text.getColor().getSignColor();
        if (i == DyeColor.BLACK.getSignColor() && text.isGlowing()) {
            return -988212;
        }
        return ColorHelper.scaleRgb((int)i, (float)0.4f);
    }

    public SignBlockEntityRenderState createRenderState() {
        return new SignBlockEntityRenderState();
    }

    public void updateRenderState(SignBlockEntity signBlockEntity, SignBlockEntityRenderState signBlockEntityRenderState, float f, Vec3d vec3d, // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlayCommand) {
        super.updateRenderState((BlockEntity)signBlockEntity, (BlockEntityRenderState)signBlockEntityRenderState, f, vec3d, crumblingOverlayCommand);
        signBlockEntityRenderState.maxTextWidth = signBlockEntity.getMaxTextWidth();
        signBlockEntityRenderState.textLineHeight = signBlockEntity.getTextLineHeight();
        signBlockEntityRenderState.frontText = signBlockEntity.getFrontText();
        signBlockEntityRenderState.backText = signBlockEntity.getBackText();
        signBlockEntityRenderState.filterText = MinecraftClient.getInstance().shouldFilterText();
        signBlockEntityRenderState.renderTextOutline = AbstractSignBlockEntityRenderer.shouldRenderTextOutline((BlockPos)signBlockEntity.getPos());
    }

    public /* synthetic */ BlockEntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

