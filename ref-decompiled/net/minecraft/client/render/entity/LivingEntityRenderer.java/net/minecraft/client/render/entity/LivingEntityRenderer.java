/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
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
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemConvertible;
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

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    protected Box getBoundingBox(T livingEntity) {
        Box box = super.getBoundingBox(livingEntity);
        if (((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
            float f = 0.5f;
            return box.expand(0.5, 0.5, 0.5);
        }
        return box;
    }

    @Override
    public void render(S livingEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState) {
        Direction direction;
        matrixStack.push();
        if (((LivingEntityRenderState)livingEntityRenderState).isInPose(EntityPose.SLEEPING) && (direction = ((LivingEntityRenderState)livingEntityRenderState).sleepingDirection) != null) {
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
        RenderLayer renderLayer = this.getRenderLayer(livingEntityRenderState, bl, bl2, ((EntityRenderState)livingEntityRenderState).hasOutline());
        if (renderLayer != null) {
            int i = LivingEntityRenderer.getOverlay(livingEntityRenderState, this.getAnimationCounter(livingEntityRenderState));
            int j = bl2 ? 0x26FFFFFF : -1;
            int k = ColorHelper.mix(j, this.getMixColor(livingEntityRenderState));
            orderedRenderCommandQueue.submitModel(this.model, livingEntityRenderState, matrixStack, renderLayer, ((LivingEntityRenderState)livingEntityRenderState).light, i, k, (Sprite)null, ((LivingEntityRenderState)livingEntityRenderState).outlineColor, (ModelCommandRenderer.CrumblingOverlayCommand)null);
        }
        if (this.shouldRenderFeatures(livingEntityRenderState) && !this.features.isEmpty()) {
            ((Model)this.model).setAngles(livingEntityRenderState);
            for (FeatureRenderer<S, M> featureRenderer : this.features) {
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
            return RenderLayers.itemEntityTranslucentCull(identifier);
        }
        if (showBody) {
            return ((Model)this.model).getLayer(identifier);
        }
        if (showOutline) {
            return RenderLayers.outlineNoCull(identifier);
        }
        return null;
    }

    public static int getOverlay(LivingEntityRenderState state, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(state.hurt));
    }

    protected boolean isVisible(S state) {
        return !((LivingEntityRenderState)state).invisible;
    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
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
            bodyYaw += (float)(Math.cos((float)MathHelper.floor(((LivingEntityRenderState)state).age) * 3.25f) * Math.PI * (double)0.4f);
        }
        if (!((LivingEntityRenderState)state).isInPose(EntityPose.SLEEPING)) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        }
        if (((LivingEntityRenderState)state).deathTime > 0.0f) {
            float f = (((LivingEntityRenderState)state).deathTime - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f * this.getLyingPositionRotationDegrees()));
        } else if (((LivingEntityRenderState)state).usingRiptide) {
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - ((LivingEntityRenderState)state).pitch));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(((LivingEntityRenderState)state).age * -75.0f));
        } else if (((LivingEntityRenderState)state).isInPose(EntityPose.SLEEPING)) {
            Direction direction = ((LivingEntityRenderState)state).sleepingDirection;
            float g = direction != null ? LivingEntityRenderer.getYaw(direction) : bodyYaw;
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

    @Override
    protected boolean hasLabel(T livingEntity, double d) {
        boolean bl;
        if (((Entity)livingEntity).isSneaky()) {
            float f = 32.0f;
            if (d >= 1024.0) {
                return false;
            }
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        boolean bl2 = bl = !((Entity)livingEntity).isInvisibleTo(clientPlayerEntity);
        if (livingEntity != clientPlayerEntity) {
            Team abstractTeam = ((Entity)livingEntity).getScoreboardTeam();
            Team abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
            if (abstractTeam != null) {
                AbstractTeam.VisibilityRule visibilityRule = ((AbstractTeam)abstractTeam).getNameTagVisibilityRule();
                switch (visibilityRule) {
                    case ALWAYS: {
                        return bl;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (((AbstractTeam)abstractTeam).shouldShowFriendlyInvisibles() || bl);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
                    }
                }
                return true;
            }
        }
        return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !((Entity)livingEntity).hasPassengers();
    }

    public boolean shouldFlipUpsideDown(T entity) {
        Text text = ((Entity)entity).getCustomName();
        return text != null && LivingEntityRenderer.shouldFlipUpsideDown(text.getString());
    }

    protected static boolean shouldFlipUpsideDown(String name) {
        return "Dinnerbone".equals(name) || "Grumm".equals(name);
    }

    @Override
    protected float getShadowRadius(S livingEntityRenderState) {
        return super.getShadowRadius(livingEntityRenderState) * ((LivingEntityRenderState)livingEntityRenderState).baseScale;
    }

    @Override
    public void updateRenderState(T livingEntity, S livingEntityRenderState, float f) {
        BlockItem blockItem;
        super.updateRenderState(livingEntity, livingEntityRenderState, f);
        float g = MathHelper.lerpAngleDegrees(f, ((LivingEntity)livingEntity).lastHeadYaw, ((LivingEntity)livingEntity).headYaw);
        ((LivingEntityRenderState)livingEntityRenderState).bodyYaw = LivingEntityRenderer.clampBodyYaw(livingEntity, g, f);
        ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw = MathHelper.wrapDegrees(g - ((LivingEntityRenderState)livingEntityRenderState).bodyYaw);
        ((LivingEntityRenderState)livingEntityRenderState).pitch = ((Entity)livingEntity).getLerpedPitch(f);
        ((LivingEntityRenderState)livingEntityRenderState).flipUpsideDown = this.shouldFlipUpsideDown(livingEntity);
        if (((LivingEntityRenderState)livingEntityRenderState).flipUpsideDown) {
            ((LivingEntityRenderState)livingEntityRenderState).pitch *= -1.0f;
            ((LivingEntityRenderState)livingEntityRenderState).relativeHeadYaw *= -1.0f;
        }
        if (!((Entity)livingEntity).hasVehicle() && ((LivingEntity)livingEntity).isAlive()) {
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress = ((LivingEntity)livingEntity).limbAnimator.getAnimationProgress(f);
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude = ((LivingEntity)livingEntity).limbAnimator.getAmplitude(f);
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress = 0.0f;
            ((LivingEntityRenderState)livingEntityRenderState).limbSwingAmplitude = 0.0f;
        }
        Entity entity = ((Entity)livingEntity).getVehicle();
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)entity;
            ((LivingEntityRenderState)livingEntityRenderState).headItemAnimationProgress = livingEntity2.limbAnimator.getAnimationProgress(f);
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).headItemAnimationProgress = ((LivingEntityRenderState)livingEntityRenderState).limbSwingAnimationProgress;
        }
        ((LivingEntityRenderState)livingEntityRenderState).baseScale = ((LivingEntity)livingEntity).getScale();
        ((LivingEntityRenderState)livingEntityRenderState).ageScale = ((LivingEntity)livingEntity).getScaleFactor();
        ((LivingEntityRenderState)livingEntityRenderState).pose = ((Entity)livingEntity).getPose();
        ((LivingEntityRenderState)livingEntityRenderState).sleepingDirection = ((LivingEntity)livingEntity).getSleepingDirection();
        if (((LivingEntityRenderState)livingEntityRenderState).sleepingDirection != null) {
            ((LivingEntityRenderState)livingEntityRenderState).standingEyeHeight = ((Entity)livingEntity).getEyeHeight(EntityPose.STANDING);
        }
        ((LivingEntityRenderState)livingEntityRenderState).shaking = ((Entity)livingEntity).isFrozen();
        ((LivingEntityRenderState)livingEntityRenderState).baby = ((LivingEntity)livingEntity).isBaby();
        ((LivingEntityRenderState)livingEntityRenderState).touchingWater = ((Entity)livingEntity).isTouchingWater();
        ((LivingEntityRenderState)livingEntityRenderState).usingRiptide = ((LivingEntity)livingEntity).isUsingRiptide();
        ((LivingEntityRenderState)livingEntityRenderState).timeSinceLastKineticAttack = ((LivingEntity)livingEntity).getTimeSinceLastKineticAttack(f);
        ((LivingEntityRenderState)livingEntityRenderState).hurt = ((LivingEntity)livingEntity).hurtTime > 0 || ((LivingEntity)livingEntity).deathTime > 0;
        ItemStack itemStack = ((LivingEntity)livingEntity).getEquippedStack(EquipmentSlot.HEAD);
        ItemConvertible itemConvertible = itemStack.getItem();
        if (itemConvertible instanceof BlockItem && (itemConvertible = (blockItem = (BlockItem)itemConvertible).getBlock()) instanceof AbstractSkullBlock) {
            AbstractSkullBlock abstractSkullBlock = (AbstractSkullBlock)itemConvertible;
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType = abstractSkullBlock.getSkullType();
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullProfile = itemStack.get(DataComponentTypes.PROFILE);
            ((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.clear();
        } else {
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullType = null;
            ((LivingEntityRenderState)livingEntityRenderState).wearingSkullProfile = null;
            if (!ArmorFeatureRenderer.hasModel(itemStack, EquipmentSlot.HEAD)) {
                this.itemModelResolver.updateForLivingEntity(((LivingEntityRenderState)livingEntityRenderState).headItemRenderState, itemStack, ItemDisplayContext.HEAD, (LivingEntity)livingEntity);
            } else {
                ((LivingEntityRenderState)livingEntityRenderState).headItemRenderState.clear();
            }
        }
        ((LivingEntityRenderState)livingEntityRenderState).deathTime = ((LivingEntity)livingEntity).deathTime > 0 ? (float)((LivingEntity)livingEntity).deathTime + f : 0.0f;
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ((LivingEntityRenderState)livingEntityRenderState).invisibleToPlayer = ((LivingEntityRenderState)livingEntityRenderState).invisible && ((Entity)livingEntity).isInvisibleTo(minecraftClient.player);
    }

    private static float clampBodyYaw(LivingEntity entity, float degrees, float tickProgress) {
        Entity entity2 = entity.getVehicle();
        if (entity2 instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)entity2;
            float f = MathHelper.lerpAngleDegrees(tickProgress, livingEntity.lastBodyYaw, livingEntity.bodyYaw);
            float g = 85.0f;
            float h = MathHelper.clamp(MathHelper.wrapDegrees(degrees - f), -85.0f, 85.0f);
            f = degrees - h;
            if (Math.abs(h) > 50.0f) {
                f += h * 0.2f;
            }
            return f;
        }
        return MathHelper.lerpAngleDegrees(tickProgress, entity.lastBodyYaw, entity.bodyYaw);
    }

    @Override
    protected /* synthetic */ float getShadowRadius(EntityRenderState state) {
        return this.getShadowRadius((S)((LivingEntityRenderState)state));
    }
}
