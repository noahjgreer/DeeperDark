/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.BlockRenderType
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.InGameOverlayRenderer
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.DiffuseLighting$Type
 *  net.minecraft.client.render.LightmapTextureManager
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.VertexConsumer
 *  net.minecraft.client.render.VertexConsumerProvider
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.render.model.ModelBaker
 *  net.minecraft.client.texture.Sprite
 *  net.minecraft.client.texture.SpriteHolder
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$Mutable
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.random.Random
 *  net.minecraft.world.BlockView
 *  net.minecraft.world.World
 *  net.minecraft.world.dimension.DimensionType
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class InGameOverlayRenderer {
    private static final Identifier UNDERWATER_TEXTURE = Identifier.ofVanilla((String)"textures/misc/underwater.png");
    private final MinecraftClient client;
    private final SpriteHolder spriteHolder;
    private final VertexConsumerProvider vertexConsumers;
    public static final int field_59969 = 40;
    private @Nullable ItemStack floatingItem;
    private int floatingItemTimer;
    private float floatingItemOffsetX;
    private float floatingItemOffsetY;

    public InGameOverlayRenderer(MinecraftClient client, SpriteHolder spriteHolder, VertexConsumerProvider vertexConsumers) {
        this.client = client;
        this.spriteHolder = spriteHolder;
        this.vertexConsumers = vertexConsumers;
    }

    public void tickFloatingItemTimer() {
        if (this.floatingItemTimer > 0) {
            --this.floatingItemTimer;
            if (this.floatingItemTimer == 0) {
                this.floatingItem = null;
            }
        }
    }

    public void renderOverlays(boolean sleeping, float tickProgress, OrderedRenderCommandQueue queue) {
        MatrixStack matrixStack = new MatrixStack();
        ClientPlayerEntity playerEntity = this.client.player;
        if (this.client.options.getPerspective().isFirstPerson() && !sleeping) {
            BlockState blockState;
            if (!playerEntity.noClip && (blockState = InGameOverlayRenderer.getInWallBlockState((PlayerEntity)playerEntity)) != null) {
                InGameOverlayRenderer.renderInWallOverlay((Sprite)this.client.getBlockRenderManager().getModels().getModelParticleSprite(blockState), (MatrixStack)matrixStack, (VertexConsumerProvider)this.vertexConsumers);
            }
            if (!this.client.player.isSpectator()) {
                if (this.client.player.isSubmergedIn(FluidTags.WATER)) {
                    InGameOverlayRenderer.renderUnderwaterOverlay((MinecraftClient)this.client, (MatrixStack)matrixStack, (VertexConsumerProvider)this.vertexConsumers);
                }
                if (this.client.player.isOnFire()) {
                    Sprite sprite = this.spriteHolder.getSprite(ModelBaker.FIRE_1);
                    InGameOverlayRenderer.renderFireOverlay((MatrixStack)matrixStack, (VertexConsumerProvider)this.vertexConsumers, (Sprite)sprite);
                }
            }
        }
        if (!this.client.options.hudHidden) {
            this.renderFloatingItem(matrixStack, tickProgress, queue);
        }
    }

    private void renderFloatingItem(MatrixStack matrices, float tickProgress, OrderedRenderCommandQueue queue) {
        if (this.floatingItem == null || this.floatingItemTimer <= 0) {
            return;
        }
        int i = 40 - this.floatingItemTimer;
        float f = ((float)i + tickProgress) / 40.0f;
        float g = f * f;
        float h = f * g;
        float j = 10.25f * h * g - 24.95f * g * g + 25.5f * h - 13.8f * g + 4.0f * f;
        float k = j * (float)Math.PI;
        float l = (float)this.client.getWindow().getFramebufferWidth() / (float)this.client.getWindow().getFramebufferHeight();
        float m = this.floatingItemOffsetX * 0.3f * l;
        float n = this.floatingItemOffsetY * 0.3f;
        matrices.push();
        matrices.translate(m * MathHelper.abs((float)MathHelper.sin((double)(k * 2.0f))), n * MathHelper.abs((float)MathHelper.sin((double)(k * 2.0f))), -10.0f + 9.0f * MathHelper.sin((double)k));
        float o = 0.8f;
        matrices.scale(0.8f, 0.8f, 0.8f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(900.0f * MathHelper.abs((float)MathHelper.sin((double)k))));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(6.0f * MathHelper.cos((double)(f * 8.0f))));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(6.0f * MathHelper.cos((double)(f * 8.0f))));
        this.client.gameRenderer.getDiffuseLighting().setShaderLights(DiffuseLighting.Type.ITEMS_3D);
        ItemRenderState itemRenderState = new ItemRenderState();
        this.client.getItemModelManager().clearAndUpdate(itemRenderState, this.floatingItem, ItemDisplayContext.FIXED, (World)this.client.world, null, 0);
        itemRenderState.render(matrices, queue, 0xF000F0, OverlayTexture.DEFAULT_UV, 0);
        matrices.pop();
    }

    public void clearFloatingItem() {
        this.floatingItem = null;
    }

    public void setFloatingItem(ItemStack stack, Random random) {
        this.floatingItem = stack;
        this.floatingItemTimer = 40;
        this.floatingItemOffsetX = random.nextFloat() * 2.0f - 1.0f;
        this.floatingItemOffsetY = random.nextFloat() * 2.0f - 1.0f;
    }

    private static @Nullable BlockState getInWallBlockState(PlayerEntity player) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int i = 0; i < 8; ++i) {
            double d = player.getX() + (double)(((float)((i >> 0) % 2) - 0.5f) * player.getWidth() * 0.8f);
            double e = player.getEyeY() + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f * player.getScale());
            double f = player.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * player.getWidth() * 0.8f);
            mutable.set(d, e, f);
            BlockState blockState = player.getEntityWorld().getBlockState((BlockPos)mutable);
            if (blockState.getRenderType() == BlockRenderType.INVISIBLE || !blockState.shouldBlockVision((BlockView)player.getEntityWorld(), (BlockPos)mutable)) continue;
            return blockState;
        }
        return null;
    }

    private static void renderInWallOverlay(Sprite sprite, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        float f = 0.1f;
        int i = ColorHelper.fromFloats((float)1.0f, (float)0.1f, (float)0.1f, (float)0.1f);
        float g = -1.0f;
        float h = 1.0f;
        float j = -1.0f;
        float k = 1.0f;
        float l = -0.5f;
        float m = sprite.getMinU();
        float n = sprite.getMaxU();
        float o = sprite.getMinV();
        float p = sprite.getMaxV();
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.blockScreenEffect((Identifier)sprite.getAtlasId()));
        vertexConsumer.vertex((Matrix4fc)matrix4f, -1.0f, -1.0f, -0.5f).texture(n, p).color(i);
        vertexConsumer.vertex((Matrix4fc)matrix4f, 1.0f, -1.0f, -0.5f).texture(m, p).color(i);
        vertexConsumer.vertex((Matrix4fc)matrix4f, 1.0f, 1.0f, -0.5f).texture(m, o).color(i);
        vertexConsumer.vertex((Matrix4fc)matrix4f, -1.0f, 1.0f, -0.5f).texture(n, o).color(i);
    }

    private static void renderUnderwaterOverlay(MinecraftClient client, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        BlockPos blockPos = BlockPos.ofFloored((double)client.player.getX(), (double)client.player.getEyeY(), (double)client.player.getZ());
        float f = LightmapTextureManager.getBrightness((DimensionType)client.player.getEntityWorld().getDimension(), (int)client.player.getEntityWorld().getLightLevel(blockPos));
        int i = ColorHelper.fromFloats((float)0.1f, (float)f, (float)f, (float)f);
        float g = 4.0f;
        float h = -1.0f;
        float j = 1.0f;
        float k = -1.0f;
        float l = 1.0f;
        float m = -0.5f;
        float n = -client.player.getYaw() / 64.0f;
        float o = client.player.getPitch() / 64.0f;
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.blockScreenEffect((Identifier)UNDERWATER_TEXTURE));
        vertexConsumer.vertex((Matrix4fc)matrix4f, -1.0f, -1.0f, -0.5f).texture(4.0f + n, 4.0f + o).color(i);
        vertexConsumer.vertex((Matrix4fc)matrix4f, 1.0f, -1.0f, -0.5f).texture(0.0f + n, 4.0f + o).color(i);
        vertexConsumer.vertex((Matrix4fc)matrix4f, 1.0f, 1.0f, -0.5f).texture(0.0f + n, 0.0f + o).color(i);
        vertexConsumer.vertex((Matrix4fc)matrix4f, -1.0f, 1.0f, -0.5f).texture(4.0f + n, 0.0f + o).color(i);
    }

    private static void renderFireOverlay(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Sprite sprite) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayers.fireScreenEffect((Identifier)sprite.getAtlasId()));
        float f = sprite.getMinU();
        float g = sprite.getMaxU();
        float h = sprite.getMinV();
        float i = sprite.getMaxV();
        float j = 1.0f;
        for (int k = 0; k < 2; ++k) {
            matrices.push();
            float l = -0.5f;
            float m = 0.5f;
            float n = -0.5f;
            float o = 0.5f;
            float p = -0.5f;
            matrices.translate((float)(-(k * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)(k * 2 - 1) * 10.0f));
            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            vertexConsumer.vertex((Matrix4fc)matrix4f, -0.5f, -0.5f, -0.5f).texture(g, i).color(1.0f, 1.0f, 1.0f, 0.9f);
            vertexConsumer.vertex((Matrix4fc)matrix4f, 0.5f, -0.5f, -0.5f).texture(f, i).color(1.0f, 1.0f, 1.0f, 0.9f);
            vertexConsumer.vertex((Matrix4fc)matrix4f, 0.5f, 0.5f, -0.5f).texture(f, h).color(1.0f, 1.0f, 1.0f, 0.9f);
            vertexConsumer.vertex((Matrix4fc)matrix4f, -0.5f, 0.5f, -0.5f).texture(g, h).color(1.0f, 1.0f, 1.0f, 0.9f);
            matrices.pop();
        }
    }
}

