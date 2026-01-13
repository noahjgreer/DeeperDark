/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.render.TexturedRenderLayers
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.MobEntityRenderer
 *  net.minecraft.client.render.entity.ShulkerEntityRenderer
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.ShulkerEntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.entity.state.ShulkerEntityRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.ShulkerEntity
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.ShulkerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ShulkerEntityRenderer
extends MobEntityRenderer<ShulkerEntity, ShulkerEntityRenderState, ShulkerEntityModel> {
    private static final Identifier TEXTURE = TexturedRenderLayers.SHULKER_TEXTURE_ID.getTextureId().withPath(string -> "textures/" + string + ".png");
    private static final Identifier[] COLORED_TEXTURES = (Identifier[])TexturedRenderLayers.COLORED_SHULKER_BOXES_TEXTURES.stream().map(spriteId -> spriteId.getTextureId().withPath(string -> "textures/" + string + ".png")).toArray(Identifier[]::new);

    public ShulkerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, (EntityModel)new ShulkerEntityModel(context.getPart(EntityModelLayers.SHULKER)), 0.0f);
    }

    public Vec3d getPositionOffset(ShulkerEntityRenderState shulkerEntityRenderState) {
        return shulkerEntityRenderState.renderPositionOffset;
    }

    public boolean shouldRender(ShulkerEntity shulkerEntity, Frustum frustum, double d, double e, double f) {
        if (super.shouldRender((Entity)shulkerEntity, frustum, d, e, f)) {
            return true;
        }
        Vec3d vec3d = shulkerEntity.getRenderPositionOffset(0.0f);
        if (vec3d == null) {
            return false;
        }
        EntityType entityType = shulkerEntity.getType();
        float g = entityType.getHeight() / 2.0f;
        float h = entityType.getWidth() / 2.0f;
        Vec3d vec3d2 = Vec3d.ofBottomCenter((Vec3i)shulkerEntity.getBlockPos());
        return frustum.isVisible(new Box(vec3d.x, vec3d.y + (double)g, vec3d.z, vec3d2.x, vec3d2.y + (double)g, vec3d2.z).expand((double)h, (double)g, (double)h));
    }

    public Identifier getTexture(ShulkerEntityRenderState shulkerEntityRenderState) {
        return ShulkerEntityRenderer.getTexture((DyeColor)shulkerEntityRenderState.color);
    }

    public ShulkerEntityRenderState createRenderState() {
        return new ShulkerEntityRenderState();
    }

    public void updateRenderState(ShulkerEntity shulkerEntity, ShulkerEntityRenderState shulkerEntityRenderState, float f) {
        super.updateRenderState((LivingEntity)shulkerEntity, (LivingEntityRenderState)shulkerEntityRenderState, f);
        shulkerEntityRenderState.renderPositionOffset = Objects.requireNonNullElse(shulkerEntity.getRenderPositionOffset(f), Vec3d.ZERO);
        shulkerEntityRenderState.color = shulkerEntity.getColor();
        shulkerEntityRenderState.openProgress = shulkerEntity.getOpenProgress(f);
        shulkerEntityRenderState.headYaw = shulkerEntity.headYaw;
        shulkerEntityRenderState.shellYaw = shulkerEntity.bodyYaw;
        shulkerEntityRenderState.facing = shulkerEntity.getAttachedFace();
    }

    public static Identifier getTexture(@Nullable DyeColor shulkerColor) {
        if (shulkerColor == null) {
            return TEXTURE;
        }
        return COLORED_TEXTURES[shulkerColor.getIndex()];
    }

    protected void setupTransforms(ShulkerEntityRenderState shulkerEntityRenderState, MatrixStack matrixStack, float f, float g) {
        super.setupTransforms((LivingEntityRenderState)shulkerEntityRenderState, matrixStack, f + 180.0f, g);
        matrixStack.multiply((Quaternionfc)shulkerEntityRenderState.facing.getOpposite().getRotationQuaternion(), 0.0f, 0.5f, 0.0f);
    }

    public /* synthetic */ EntityRenderState createRenderState() {
        return this.createRenderState();
    }
}

