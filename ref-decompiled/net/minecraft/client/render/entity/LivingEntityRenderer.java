/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.block.AbstractSkullBlock
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.EntityRendererFactory$Context
 *  net.minecraft.client.render.entity.LivingEntityRenderer
 *  net.minecraft.client.render.entity.LivingEntityRenderer$1
 *  net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRenderer
 *  net.minecraft.client.render.entity.feature.FeatureRendererContext
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.client.render.state.CameraRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ProfileComponent
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.scoreboard.AbstractTeam
 *  net.minecraft.scoreboard.AbstractTeam$VisibilityRule
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Quaternionfc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
extends EntityRenderer<T, S>
implements FeatureRendererContext<S, M> {
    private static final float field_32939 = 0.1f;
    protected M model;
    protected final ItemModelManager itemModelResolver;
    protected final List<FeatureRenderer<S, M>> features = Lists.newArrayList();

    public LivingEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx);
        this.itemModelResolver = ctx.getItemModelManager();
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    protected final boolean addFeature(FeatureRenderer<S, M> feature) {
        return this.features.add(feature);
    }

    public M getModel() {
        return (M)this.model;
    }

    protected Box getBoundingBox(T livingEntity) {
        Box box = super.getBoundingBox(livingEntity);
        if (livingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
            float f = 0.5f;
            return box.expand(0.5, 0.5, 0.5);
        }
        return box;
    }

    public void render(S livingEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        Direction direction;
        matrixStack.push();
        if (livingEntityRenderState.isInPose(EntityPose.SLEEPING) && (direction = ((LivingEntityRenderState)livingEntityRenderState).sleepingDirection) != null) {
            float f = ((LivingEntityRenderState)livingEntityRenderState).standingEyeHeight - 0.1f;
            matrixStack.translate((float)(-direction.getOffsetX()) * f, 0.0f, (float)(-direction.getOffsetZ()) * f);
        }
        float g = ((LivingEntityRenderState)livingEntityRenderState).baseScale;
        matrixStack.scale(g, g, g);
        this.setupTransforms(livingEntityRenderState, matrixStack, ((LivingEntityRenderState)livingEntityRenderState).bodyYaw, g);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.scale(livingEntityRenderState, matrixStack);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        boolean bl = this.isVisible(livingEntityRenderState);
        boolean bl2 = !bl && !((LivingEntityRenderState)livingEntityRenderState).invisibleToPlayer;
        RenderLayer renderLayer = this.getRenderLayer(livingEntityRenderState, bl, bl2, livingEntityRenderState.hasOutline());
        if (renderLayer != null) {
            int i = LivingEntityRenderer.getOverlay(livingEntityRenderState, (float)this.getAnimationCounter(livingEntityRenderState));
            int j = bl2 ? 0x26FFFFFF : -1;
            int k = ColorHelper.mix((int)j, (int)this.getMixColor(livingEntityRenderState));
            orderedRenderCommandQueue.submitModel((Model)this.model, livingEntityRenderState, matrixStack, renderLayer, ((LivingEntityRenderState)livingEntityRenderState).light, i, k, null, ((LivingEntityRenderState)livingEntityRenderState).outlineColor, null);
        }
        if (this.shouldRenderFeatures(livingEntityRenderState) && !this.features.isEmpty()) {
            this.model.setAngles(livingEntityRenderState);
            for (FeatureRenderer featureRenderer : this.features) {
                featureRenderer.render(matrixStack, orderedRenderCommandQueue, ((LivingEntityRenderState)livingEntityRenderState).light, livingEntityRenderState, ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw, ((LivingEntityRenderState)livingEntityRenderState).pitch);
            }
        }
        matrixStack.pop();
        super.render(livingEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState);
    }

    protected boolean shouldRenderFeatures(S state) {
        return true;
    }

    protected int getMixColor(S state) {
        return -1;
    }

    public abstract Identifier getTexture(S var1);

    protected @Nullable RenderLayer getRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier identifier = this.getTexture(state);
        if (translucent) {
            return RenderLayers.itemEntityTranslucentCull((Identifier)identifier);
        }
        if (showBody) {
            return this.model.getLayer(identifier);
        }
        if (showOutline) {
            return RenderLayers.outlineNoCull((Identifier)identifier);
        }
        return null;
    }

    public static int getOverlay(LivingEntityRenderState state, float whiteOverlayProgress) {
        return OverlayTexture.packUv((int)OverlayTexture.getU((float)whiteOverlayProgress), (int)OverlayTexture.getV((boolean)state.hurt));
    }

    protected boolean isVisible(S state) {
        return !((LivingEntityRenderState)state).invisible;
    }

    private static float getYaw(Direction direction) {
        switch (1.field_18227[direction.ordinal()]) {
            case 1: {
                return 90.0f;
            }
            case 2: {
                return 0.0f;
            }
            case 3: {
                return 270.0f;
            }
            case 4: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected boolean isShaking(S state) {
        return ((LivingEntityRenderState)state).shaking;
    }

    protected void setupTransforms(S state, MatrixStack matrices, float bodyYaw, float baseHeight) {
        if (this.isShaking(state)) {
            bodyYaw += (float)(Math.cos((float)MathHelper.floor((float)((LivingEntityRenderState)state).age) * 3.25f) * Math.PI * (double)0.4f);
        }
        if (!state.isInPose(EntityPose.SLEEPING)) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        }
        if (((LivingEntityRenderState)state).deathTime > 0.0f) {
            float f = (((LivingEntityRenderState)state).deathTime - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt((float)f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f * this.getLyingPositionRotationDegrees()));
        } else if (((LivingEntityRenderState)state).usingRiptide) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - ((LivingEntityRenderState)state).pitch));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(((LivingEntityRenderState)state).age * -75.0f));
        } else if (state.isInPose(EntityPose.SLEEPING)) {
            Direction direction = ((LivingEntityRenderState)state).sleepingDirection;
            float g = direction != null ? LivingEntityRenderer.getYaw((Direction)direction) : bodyYaw;
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(this.getLyingPositionRotationDegrees()));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        } else if (((LivingEntityRenderState)state).flipUpsideDown) {
            matrices.translate(0.0f, (((LivingEntityRenderState)state).height + 0.1f) / baseHeight, 0.0f);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        }
    }

    protected float getLyingPositionRotationDegrees() {
        return 90.0f;
    }

    protected float getAnimationCounter(S state) {
        return 0.0f;
    }

    protected void scale(S state, MatrixStack matrices) {
    }

    protected boolean hasLabel(T livingEntity, double d) {
        boolean bl;
        if (livingEntity.isSneaky()) {
            float f = 32.0f;
            if (d >= 1024.0) {
                return false;
            }
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        boolean bl2 = bl = !livingEntity.isInvisibleTo((PlayerEntity)clientPlayerEntity);
        if (livingEntity != clientPlayerEntity) {
            Team abstractTeam = livingEntity.getScoreboardTeam();
            Team abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
            if (abstractTeam != null) {
                AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
                switch (1.field_4743[visibilityRule.ordinal()]) {
                    case 1: {
                        return bl;
                    }
                    case 2: {
                        return false;
                    }
                    case 3: {
                        return abstractTeam2 == null ? bl : abstractTeam.isEqual((AbstractTeam)abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
                    }
                    case 4: {
                        return abstractTeam2 == null ? bl : !abstractTeam.isEqual((AbstractTeam)abstractTeam2) && bl;
                    }
                }
                return true;
            }
        }
        return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !livingEntity.hasPassengers();
    }

    public boolean shouldFlipUpsideDown(T entity) {
        Text text = entity.getCustomName();
        return text != null && LivingEntityRenderer.shouldFlipUpsideDown((String)text.getString());
    }

    protected static boolean shouldFlipUpsideDown(String name) {
        return "Dinnerbone".equals(name) || "Grumm".equals(name);
    }

    protected float getShadowRadius(S livingEntityRenderState) {
        return super.getShadowRadius(livingEntityRenderState) * ((LivingEntityRenderState)livingEntityRenderState).baseScale;
    }

    public void updateRenderState(T livingEntity, S livingEntityRenderState, float f) {
        BlockItem blockItem;
        super.updateRenderState(livingEntity, livingEntityRenderState, f);
        float g = MathHelper.lerpAngleDegrees((float)f, (float)((LivingEntity)livingEntity).lastHeadYaw, (float)((LivingEntity)livingEntity).headYaw);
        ((LivingEntityRenderState)livingEntityRenderState).bodyYaw = LivingEntityRenderer.clampBodyYaw(livingEntity, (float)g, (float)f);
        ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw = MathHelper.wrapDegrees((float)(g - ((LivingEntityRenderState)livingEntityRenderState).bodyYaw));
        ((LivingEntityRenderState)livingEntityRenderState).pitch = livingEntity.getLerpedPitch(f);
        ((LivingEntityRenderState)livingEntityRenderState).flipUpsideDown = this.shouldFlipUpsideDown(livingEntity);
        if (((LivingEntityRenderState)livingEntityRenderState).flipUpsideDown) {
            ((LivingEntityRenderState)livingEntityRenderState).pitch *= -1.0f;
            ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw *= -1.0f;
        }
        if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress = ((LivingEntity)livingEntity).limbAnimator.getAnimationProgress(f);
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude = ((LivingEntity)livingEntity).limbAnimator.getAmplitude(f);
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress = 0.0f;
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude = 0.0f;
        }
        Entity entity = livingEntity.getVehicle();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            ((LivingEntityRenderState)livingEntityRenderState).headItemAnimationProgress = livingEntity2.limbAnimator.getAnimationProgress(f);
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).headItemAnimationProgress = ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress;
        }
        ((LivingEntityRenderState)livingEntityRenderState).baseScale = livingEntity.getScale();
        ((LivingEntityRenderState)livingEntityRenderState).ageScale = livingEntity.getScaleFactor();
        ((LivingEntityRenderState)livingEntityRenderState).pose = livingEntity.getPose();
        ((LivingEntityRenderState)livingEntityRenderState).sleepingDirection = livingEntity.getSleepingDirection();
        if (((LivingEntityRenderState)livingEntityRenderState).sleepingDirection != null) {
            ((LivingEntityRenderState)livingEntityRenderState).standingEyeHeight = livingEntity.getEyeHeight(EntityPose.STANDING);
        }
        ((LivingEntityRenderState)livingEntityRenderState).shaking = livingEntity.isFrozen();
        ((LivingEntityRenderState)livingEntityRenderState).baby = livingEntity.isBaby();
        ((LivingEntityRenderState)livingEntityRenderState).touchingWater = livingEntity.isTouchingWater();
        ((LivingEntityRenderState)livingEntityRenderState).usingRiptide = livingEntity.isUsingRiptide();
        ((LivingEntityRenderState)livingEntityRenderState).timeSinceLastKineticAttack = livingEntity.getTimeSinceLastKineticAttack(f);
        ((LivingEntityRenderState)livingEntityRenderState).hurt = ((LivingEntity)livingEntity).hurtTime > 0 || ((LivingEntity)livingEntity).deathTime > 0;
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
        Item item = itemStack.getItem();
        if (item instanceof BlockItem && (item = (blockItem = (BlockItem)item).getBlock()) instanceof AbstractSkullBlock) {
            AbstractSkullBlock abstractSkullBlock = (AbstractSkullBlock)item;
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType = abstractSkullBlock.getSkullType();
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullProfile = (ProfileComponent)itemStack.get(DataComponentTypes.PROFILE);
            ((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.clear();
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType = null;
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullProfile = null;
            if (!ArmorFeatureRenderer.hasModel((ItemStack)itemStack, (EquipmentSlot)EquipmentSlot.HEAD)) {
                this.itemModelResolver.updateForLivingEntity(((LivingEntityRenderState)livingEntityRenderState).headItemRenderState, itemStack, ItemDisplayContext.HEAD, livingEntity);
            } else {
                ((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.clear();
            }
        }
        ((LivingEntityRenderState)livingEntityRenderState).deathTime = ((LivingEntity)livingEntity).deathTime > 0 ? (float)((LivingEntity)livingEntity).deathTime + f : 0.0f;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ((LivingEntityRenderState)livingEntityRenderState).invisibleToPlayer = ((LivingEntityRenderState)livingEntityRenderState).invisible && livingEntity.isInvisibleTo((PlayerEntity)minecraftClient.player);
    }

    private static float clampBodyYaw(LivingEntity entity, float degrees, float tickProgress) {
        Entity entity2 = entity.getVehicle();
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            float f = MathHelper.lerpAngleDegrees((float)tickProgress, (float)livingEntity.lastBodyYaw, (float)livingEntity.bodyYaw);
            float g = 85.0f;
            float h = MathHelper.clamp((float)MathHelper.wrapDegrees((float)(degrees - f)), (float)-85.0f, (float)85.0f);
            f = degrees - h;
            if (Math.abs(h) > 50.0f) {
                f += h * 0.2f;
            }
            return f;
        }
        return MathHelper.lerpAngleDegrees((float)tickProgress, (float)entity.lastBodyYaw, (float)entity.bodyYaw);
    }

    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((LivingEntityRenderState)state);
    }
}

