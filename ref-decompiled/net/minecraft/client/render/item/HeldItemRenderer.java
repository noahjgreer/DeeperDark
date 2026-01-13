/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.item.ItemModelManager
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.render.MapRenderState
 *  net.minecraft.client.render.MapRenderer
 *  net.minecraft.client.render.OverlayTexture
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.client.render.RenderLayers
 *  net.minecraft.client.render.command.OrderedRenderCommandQueue
 *  net.minecraft.client.render.entity.EntityRenderManager
 *  net.minecraft.client.render.entity.PlayerEntityRenderer
 *  net.minecraft.client.render.entity.state.Lancing
 *  net.minecraft.client.render.item.HeldItemRenderer
 *  net.minecraft.client.render.item.HeldItemRenderer$1
 *  net.minecraft.client.render.item.HeldItemRenderer$HandRenderType
 *  net.minecraft.client.render.item.ItemRenderState
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.ComponentType
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.MapIdComponent
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.player.PlayerModelPart
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.FilledMapItem
 *  net.minecraft.item.ItemDisplayContext
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.item.ShieldItem
 *  net.minecraft.item.consume.UseAction
 *  net.minecraft.item.map.MapState
 *  net.minecraft.util.Arm
 *  net.minecraft.util.Hand
 *  net.minecraft.util.HeldItemContext
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.world.World
 *  org.joml.Quaternionfc
 */
package net.minecraft.client.render.item;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.Lancing;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.joml.Quaternionfc;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class HeldItemRenderer {
    private static final RenderLayer MAP_BACKGROUND = RenderLayers.text((Identifier)Identifier.ofVanilla((String)"textures/map/map_background.png"));
    private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayers.text((Identifier)Identifier.ofVanilla((String)"textures/map/map_background_checkerboard.png"));
    private static final float field_32735 = -0.4f;
    private static final float field_32736 = 0.2f;
    private static final float field_32737 = -0.2f;
    private static final float field_32738 = -0.6f;
    private static final float EQUIP_OFFSET_TRANSLATE_X = 0.56f;
    private static final float EQUIP_OFFSET_TRANSLATE_Y = -0.52f;
    private static final float EQUIP_OFFSET_TRANSLATE_Z = -0.72f;
    private static final float field_32742 = 45.0f;
    private static final float field_32743 = -80.0f;
    private static final float field_32744 = -20.0f;
    private static final float field_32745 = -20.0f;
    private static final float EAT_OR_DRINK_X_ANGLE_MULTIPLIER = 10.0f;
    private static final float EAT_OR_DRINK_Y_ANGLE_MULTIPLIER = 90.0f;
    private static final float EAT_OR_DRINK_Z_ANGLE_MULTIPLIER = 30.0f;
    private static final float field_32749 = 0.6f;
    private static final float field_32750 = -0.5f;
    private static final float field_32751 = 0.0f;
    private static final double field_32752 = 27.0;
    private static final float field_32753 = 0.8f;
    private static final float field_32754 = 0.1f;
    private static final float field_32755 = -0.3f;
    private static final float field_32756 = 0.4f;
    private static final float field_32757 = -0.4f;
    private static final float ARM_HOLDING_ITEM_SECOND_Y_ANGLE_MULTIPLIER = 70.0f;
    private static final float ARM_HOLDING_ITEM_FIRST_Z_ANGLE_MULTIPLIER = -20.0f;
    private static final float field_32690 = -0.6f;
    private static final float field_32691 = 0.8f;
    private static final float field_32692 = 0.8f;
    private static final float field_32693 = -0.75f;
    private static final float field_32694 = -0.9f;
    private static final float field_32695 = 45.0f;
    private static final float field_32696 = -1.0f;
    private static final float field_32697 = 3.6f;
    private static final float field_32698 = 3.5f;
    private static final float ARM_HOLDING_ITEM_TRANSLATE_X = 5.6f;
    private static final int ARM_HOLDING_ITEM_X_ANGLE_MULTIPLIER = 200;
    private static final int ARM_HOLDING_ITEM_THIRD_Y_ANGLE_MULTIPLIER = -135;
    private static final int ARM_HOLDING_ITEM_SECOND_Z_ANGLE_MULTIPLIER = 120;
    private static final float field_32703 = -0.4f;
    private static final float field_32704 = -0.2f;
    private static final float field_32705 = 0.0f;
    private static final float field_32706 = 0.04f;
    private static final float field_32707 = -0.72f;
    private static final float field_32708 = -1.2f;
    private static final float field_32709 = -0.5f;
    private static final float field_32710 = 45.0f;
    private static final float field_32711 = -85.0f;
    private static final float ARM_X_ANGLE_MULTIPLIER = 45.0f;
    private static final float ARM_Y_ANGLE_MULTIPLIER = 92.0f;
    private static final float ARM_Z_ANGLE_MULTIPLIER = -41.0f;
    private static final float ARM_TRANSLATE_X = 0.3f;
    private static final float ARM_TRANSLATE_Y = -1.1f;
    private static final float ARM_TRANSLATE_Z = 0.45f;
    private static final float field_32718 = 20.0f;
    private static final float FIRST_PERSON_MAP_FIRST_SCALE = 0.38f;
    private static final float FIRST_PERSON_MAP_TRANSLATE_X = -0.5f;
    private static final float FIRST_PERSON_MAP_TRANSLATE_Y = -0.5f;
    private static final float FIRST_PERSON_MAP_TRANSLATE_Z = 0.0f;
    private static final float FIRST_PERSON_MAP_SECOND_SCALE = 0.0078125f;
    private static final int field_32724 = 7;
    private static final int field_32725 = 128;
    private static final int field_32726 = 128;
    private static final float field_32727 = 0.0f;
    private static final float field_32728 = 0.0f;
    private static final float field_32729 = 0.04f;
    private static final float field_32730 = 0.0f;
    private static final float field_32731 = 0.004f;
    private static final float field_32732 = 0.0f;
    private static final float field_32733 = 0.2f;
    private static final float field_32734 = 0.1f;
    private final MinecraftClient client;
    private final MapRenderState mapRenderState = new MapRenderState();
    private ItemStack mainHand = ItemStack.EMPTY;
    private ItemStack offHand = ItemStack.EMPTY;
    private float equipProgressMainHand;
    private float lastEquipProgressMainHand;
    private float equipProgressOffHand;
    private float lastEquipProgressOffHand;
    private final EntityRenderManager entityRenderDispatcher;
    private final ItemModelManager itemModelManager;

    public HeldItemRenderer(MinecraftClient client, EntityRenderManager entityRenderDispatcher, ItemModelManager itemModelManager) {
        this.client = client;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.itemModelManager = itemModelManager;
    }

    public void renderItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light) {
        if (stack.isEmpty()) {
            return;
        }
        ItemRenderState itemRenderState = new ItemRenderState();
        this.itemModelManager.clearAndUpdate(itemRenderState, stack, renderMode, entity.getEntityWorld(), (HeldItemContext)entity, entity.getId() + renderMode.ordinal());
        itemRenderState.render(matrices, orderedRenderCommandQueue, light, OverlayTexture.DEFAULT_UV, 0);
    }

    private float getMapAngle(float tickProgress) {
        float f = 1.0f - tickProgress / 45.0f + 0.1f;
        f = MathHelper.clamp((float)f, (float)0.0f, (float)1.0f);
        f = -MathHelper.cos((double)(f * (float)Math.PI)) * 0.5f + 0.5f;
        return f;
    }

    private void renderArm(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, Arm arm) {
        PlayerEntityRenderer playerEntityRenderer = this.entityRenderDispatcher.getPlayerRenderer((AbstractClientPlayerEntity)this.client.player);
        matrices.push();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(92.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(45.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f * -41.0f));
        matrices.translate(f * 0.3f, -1.1f, 0.45f);
        Identifier identifier = this.client.player.getSkin().body().texturePath();
        if (arm == Arm.RIGHT) {
            playerEntityRenderer.renderRightArm(matrices, orderedRenderCommandQueue, light, identifier, this.client.player.isModelPartVisible(PlayerModelPart.RIGHT_SLEEVE));
        } else {
            playerEntityRenderer.renderLeftArm(matrices, orderedRenderCommandQueue, light, identifier, this.client.player.isModelPartVisible(PlayerModelPart.LEFT_SLEEVE));
        }
        matrices.pop();
    }

    private void renderMapInOneHand(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack) {
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrices.translate(f * 0.125f, -0.125f, 0.0f);
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f * 10.0f));
            this.renderArmHoldingItem(matrices, orderedRenderCommandQueue, light, equipProgress, swingProgress, arm);
            matrices.pop();
        }
        matrices.push();
        matrices.translate(f * 0.51f, -0.08f + equipProgress * -1.2f, -0.75f);
        float g = MathHelper.sqrt((float)swingProgress);
        float h = MathHelper.sin((double)(g * (float)Math.PI));
        float i = -0.5f * h;
        float j = 0.4f * MathHelper.sin((double)(g * ((float)Math.PI * 2)));
        float k = -0.3f * MathHelper.sin((double)(swingProgress * (float)Math.PI));
        matrices.translate(f * i, j - 0.3f * h, k);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(h * -45.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f * h * -30.0f));
        this.renderFirstPersonMap(matrices, orderedRenderCommandQueue, light, stack);
        matrices.pop();
    }

    private void renderMapInBothHands(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light, float pitch, float equipProgress, float swingProgress) {
        float f = MathHelper.sqrt((float)swingProgress);
        float g = -0.2f * MathHelper.sin((double)(swingProgress * (float)Math.PI));
        float h = -0.4f * MathHelper.sin((double)(f * (float)Math.PI));
        matrices.translate(0.0f, -g / 2.0f, h);
        float i = this.getMapAngle(pitch);
        matrices.translate(0.0f, 0.04f + equipProgress * -1.2f + i * -0.5f, -0.72f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(i * -85.0f));
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            this.renderArm(matrices, orderedRenderCommandQueue, light, Arm.RIGHT);
            this.renderArm(matrices, orderedRenderCommandQueue, light, Arm.LEFT);
            matrices.pop();
        }
        float j = MathHelper.sin((double)(f * (float)Math.PI));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(j * 20.0f));
        matrices.scale(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(matrices, orderedRenderCommandQueue, light, this.mainHand);
    }

    private void renderFirstPersonMap(MatrixStack matrices, OrderedRenderCommandQueue queue, int swingProgress, ItemStack stack) {
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.scale(0.38f, 0.38f, 0.38f);
        matrices.translate(-0.5f, -0.5f, 0.0f);
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
        MapIdComponent mapIdComponent = (MapIdComponent)stack.get(DataComponentTypes.MAP_ID);
        MapState mapState = FilledMapItem.getMapState((MapIdComponent)mapIdComponent, (World)this.client.world);
        RenderLayer renderLayer = mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD;
        queue.submitCustom(matrices, renderLayer, (matricesEntry, vertexConsumer) -> {
            vertexConsumer.vertex(matricesEntry, -7.0f, 135.0f, 0.0f).color(-1).texture(0.0f, 1.0f).light(swingProgress);
            vertexConsumer.vertex(matricesEntry, 135.0f, 135.0f, 0.0f).color(-1).texture(1.0f, 1.0f).light(swingProgress);
            vertexConsumer.vertex(matricesEntry, 135.0f, -7.0f, 0.0f).color(-1).texture(1.0f, 0.0f).light(swingProgress);
            vertexConsumer.vertex(matricesEntry, -7.0f, -7.0f, 0.0f).color(-1).texture(0.0f, 0.0f).light(swingProgress);
        });
        if (mapState != null) {
            MapRenderer mapRenderer = this.client.getMapRenderer();
            mapRenderer.update(mapIdComponent, mapState, this.mapRenderState);
            mapRenderer.draw(this.mapRenderState, matrices, queue, false, swingProgress);
        }
    }

    private void renderArmHoldingItem(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, float equipProgress, float swingProgress, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float f = bl ? 1.0f : -1.0f;
        float g = MathHelper.sqrt((float)swingProgress);
        float h = -0.3f * MathHelper.sin((double)(g * (float)Math.PI));
        float i = 0.4f * MathHelper.sin((double)(g * ((float)Math.PI * 2)));
        float j = -0.4f * MathHelper.sin((double)(swingProgress * (float)Math.PI));
        matrices.translate(f * (h + 0.64000005f), i + -0.6f + equipProgress * -0.6f, j + -0.71999997f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f * 45.0f));
        float k = MathHelper.sin((double)(swingProgress * swingProgress * (float)Math.PI));
        float l = MathHelper.sin((double)(g * (float)Math.PI));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f * l * 70.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f * k * -20.0f));
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        matrices.translate(f * -1.0f, 3.6f, 3.5f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(f * 120.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(200.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(f * -135.0f));
        matrices.translate(f * 5.6f, 0.0f, 0.0f);
        PlayerEntityRenderer playerEntityRenderer = this.entityRenderDispatcher.getPlayerRenderer((AbstractClientPlayerEntity)abstractClientPlayerEntity);
        Identifier identifier = abstractClientPlayerEntity.getSkin().body().texturePath();
        if (bl) {
            playerEntityRenderer.renderRightArm(matrices, queue, light, identifier, abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.RIGHT_SLEEVE));
        } else {
            playerEntityRenderer.renderLeftArm(matrices, queue, light, identifier, abstractClientPlayerEntity.isModelPartVisible(PlayerModelPart.LEFT_SLEEVE));
        }
    }

    private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickProgress, Arm arm, ItemStack stack, PlayerEntity player) {
        float h;
        float f = (float)player.getItemUseTimeLeft() - tickProgress + 1.0f;
        float g = f / (float)stack.getMaxUseTime((LivingEntity)player);
        if (g < 0.8f) {
            h = MathHelper.abs((float)(MathHelper.cos((double)(f / 4.0f * (float)Math.PI)) * 0.1f));
            matrices.translate(0.0f, h, 0.0f);
        }
        h = 1.0f - (float)Math.pow(g, 27.0);
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate(h * 0.6f * (float)i, h * -0.5f, h * 0.0f);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)i * h * 90.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(h * 10.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)i * h * 30.0f));
    }

    private void applyBrushTransformation(MatrixStack matrices, float tickProgress, Arm arm, PlayerEntity playerEntity) {
        float f = playerEntity.getItemUseTimeLeft() % 10;
        float g = f - tickProgress + 1.0f;
        float h = 1.0f - g / 10.0f;
        float i = -90.0f;
        float j = 60.0f;
        float k = 150.0f;
        float l = -15.0f;
        int m = 2;
        float n = -15.0f + 75.0f * MathHelper.cos((double)(h * 2.0f * (float)Math.PI));
        if (arm != Arm.RIGHT) {
            matrices.translate(0.1, 0.83, 0.35);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(-90.0f));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(n));
            matrices.translate(-0.3, 0.22, 0.35);
        } else {
            matrices.translate(-0.25, 0.22, 0.35);
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-80.0f));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees(0.0f));
            matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(n));
        }
    }

    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float f = MathHelper.sin((double)(swingProgress * swingProgress * (float)Math.PI));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)i * (45.0f + f * -20.0f)));
        float g = MathHelper.sin((double)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)i * g * -20.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(g * -80.0f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)i * -45.0f));
    }

    private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
    }

    public void renderItem(float tickProgress, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, ClientPlayerEntity player, int light) {
        float k;
        float j;
        float f = player.getHandSwingProgress(tickProgress);
        Hand hand = (Hand)MoreObjects.firstNonNull((Object)player.preferredHand, (Object)Hand.MAIN_HAND);
        float g = player.getLerpedPitch(tickProgress);
        HandRenderType handRenderType = HeldItemRenderer.getHandRenderType((ClientPlayerEntity)player);
        float h = MathHelper.lerp((float)tickProgress, (float)player.lastRenderPitch, (float)player.renderPitch);
        float i = MathHelper.lerp((float)tickProgress, (float)player.lastRenderYaw, (float)player.renderYaw);
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees((player.getPitch(tickProgress) - h) * 0.1f));
        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((player.getYaw(tickProgress) - i) * 0.1f));
        if (handRenderType.renderMainHand) {
            j = hand == Hand.MAIN_HAND ? f : 0.0f;
            k = this.itemModelManager.getSwapAnimationScale(this.mainHand) * (1.0f - MathHelper.lerp((float)tickProgress, (float)this.lastEquipProgressMainHand, (float)this.equipProgressMainHand));
            this.renderFirstPersonItem((AbstractClientPlayerEntity)player, tickProgress, g, Hand.MAIN_HAND, j, this.mainHand, k, matrices, orderedRenderCommandQueue, light);
        }
        if (handRenderType.renderOffHand) {
            j = hand == Hand.OFF_HAND ? f : 0.0f;
            k = this.itemModelManager.getSwapAnimationScale(this.offHand) * (1.0f - MathHelper.lerp((float)tickProgress, (float)this.lastEquipProgressOffHand, (float)this.equipProgressOffHand));
            this.renderFirstPersonItem((AbstractClientPlayerEntity)player, tickProgress, g, Hand.OFF_HAND, j, this.offHand, k, matrices, orderedRenderCommandQueue, light);
        }
        this.client.gameRenderer.getEntityRenderDispatcher().render();
        this.client.getBufferBuilders().getEntityVertexConsumers().draw();
    }

    @VisibleForTesting
    static HandRenderType getHandRenderType(ClientPlayerEntity player) {
        boolean bl2;
        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = player.getOffHandStack();
        boolean bl = itemStack.isOf(Items.BOW) || itemStack2.isOf(Items.BOW);
        boolean bl3 = bl2 = itemStack.isOf(Items.CROSSBOW) || itemStack2.isOf(Items.CROSSBOW);
        if (!bl && !bl2) {
            return HandRenderType.RENDER_BOTH_HANDS;
        }
        if (player.isUsingItem()) {
            return HeldItemRenderer.getUsingItemHandRenderType((ClientPlayerEntity)player);
        }
        if (HeldItemRenderer.isChargedCrossbow((ItemStack)itemStack)) {
            return HandRenderType.RENDER_MAIN_HAND_ONLY;
        }
        return HandRenderType.RENDER_BOTH_HANDS;
    }

    private static HandRenderType getUsingItemHandRenderType(ClientPlayerEntity player) {
        ItemStack itemStack = player.getActiveItem();
        Hand hand = player.getActiveHand();
        if (itemStack.isOf(Items.BOW) || itemStack.isOf(Items.CROSSBOW)) {
            return HandRenderType.shouldOnlyRender((Hand)hand);
        }
        return hand == Hand.MAIN_HAND && HeldItemRenderer.isChargedCrossbow((ItemStack)player.getOffHandStack()) ? HandRenderType.RENDER_MAIN_HAND_ONLY : HandRenderType.RENDER_BOTH_HANDS;
    }

    private static boolean isChargedCrossbow(ItemStack stack) {
        return stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged((ItemStack)stack);
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickProgress, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, int light) {
        if (player.isUsingSpyglass()) {
            return;
        }
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
        matrices.push();
        if (item.isEmpty()) {
            if (bl && !player.isInvisible()) {
                this.renderArmHoldingItem(matrices, orderedRenderCommandQueue, light, equipProgress, swingProgress, arm);
            }
        } else if (item.contains(DataComponentTypes.MAP_ID)) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(matrices, orderedRenderCommandQueue, light, pitch, equipProgress, swingProgress);
            } else {
                this.renderMapInOneHand(matrices, orderedRenderCommandQueue, light, equipProgress, arm, swingProgress, item);
            }
        } else if (item.isOf(Items.CROSSBOW)) {
            int i;
            this.applyEquipOffset(matrices, arm, equipProgress);
            boolean bl2 = CrossbowItem.isCharged((ItemStack)item);
            boolean bl3 = arm == Arm.RIGHT;
            int n = i = bl3 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand && !bl2) {
                matrices.translate((float)i * -0.4785682f, -0.094387f, 0.05731531f);
                matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-11.935f));
                matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 65.3f));
                matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)i * -9.785f));
                float f = (float)item.getMaxUseTime((LivingEntity)player) - ((float)player.getItemUseTimeLeft() - tickProgress + 1.0f);
                float g = f / (float)CrossbowItem.getPullTime((ItemStack)item, (LivingEntity)player);
                if (g > 1.0f) {
                    g = 1.0f;
                }
                if (g > 0.1f) {
                    float h = MathHelper.sin((double)((f - 0.1f) * 1.3f));
                    float j = g - 0.1f;
                    float k = h * j;
                    matrices.translate(k * 0.0f, k * 0.004f, k * 0.0f);
                }
                matrices.translate(g * 0.0f, g * 0.0f, g * 0.04f);
                matrices.scale(1.0f, 1.0f, 1.0f + g * 0.2f);
                matrices.multiply((Quaternionfc)RotationAxis.NEGATIVE_Y.rotationDegrees((float)i * 45.0f));
            } else {
                this.swingArm(swingProgress, matrices, i, arm);
                if (bl2 && swingProgress < 0.001f && bl) {
                    matrices.translate((float)i * -0.641864f, 0.0f, 0.0f);
                    matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 10.0f));
                }
            }
            this.renderItem((LivingEntity)player, item, bl3 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, matrices, orderedRenderCommandQueue, light);
        } else {
            int l;
            boolean bl2 = arm == Arm.RIGHT;
            int n = l = bl2 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                UseAction useAction = item.getUseAction();
                if (!useAction.hasNoOffset()) {
                    this.applyEquipOffset(matrices, arm, equipProgress);
                }
                switch (1.field_4054[useAction.ordinal()]) {
                    case 1: {
                        break;
                    }
                    case 2: 
                    case 3: {
                        this.applyEatOrDrinkTransformation(matrices, tickProgress, arm, item, (PlayerEntity)player);
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        break;
                    }
                    case 4: {
                        if (item.getItem() instanceof ShieldItem) break;
                        matrices.translate((float)l * -0.14142136f, 0.08f, 0.14142136f);
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-102.25f));
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 13.365f));
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)l * 78.05f));
                        break;
                    }
                    case 5: {
                        matrices.translate((float)l * -0.2785682f, 0.18344387f, 0.15731531f);
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-13.935f));
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3f));
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785f));
                        float f = (float)item.getMaxUseTime((LivingEntity)player) - ((float)player.getItemUseTimeLeft() - tickProgress + 1.0f);
                        float g = f / 20.0f;
                        g = (g * g + g * 2.0f) / 3.0f;
                        if (g > 1.0f) {
                            g = 1.0f;
                        }
                        if (g > 0.1f) {
                            float h = MathHelper.sin((double)((f - 0.1f) * 1.3f));
                            float j = g - 0.1f;
                            float k = h * j;
                            matrices.translate(k * 0.0f, k * 0.004f, k * 0.0f);
                        }
                        matrices.translate(g * 0.0f, g * 0.0f, g * 0.04f);
                        matrices.scale(1.0f, 1.0f, 1.0f + g * 0.2f);
                        matrices.multiply((Quaternionfc)RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0f));
                        break;
                    }
                    case 6: {
                        matrices.translate((float)l * -0.5f, 0.7f, 0.1f);
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_X.rotationDegrees(-55.0f));
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3f));
                        matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785f));
                        float f = (float)item.getMaxUseTime((LivingEntity)player) - ((float)player.getItemUseTimeLeft() - tickProgress + 1.0f);
                        float g = f / 10.0f;
                        if (g > 1.0f) {
                            g = 1.0f;
                        }
                        if (g > 0.1f) {
                            float h = MathHelper.sin((double)((f - 0.1f) * 1.3f));
                            float j = g - 0.1f;
                            float k = h * j;
                            matrices.translate(k * 0.0f, k * 0.004f, k * 0.0f);
                        }
                        matrices.translate(0.0f, 0.0f, g * 0.2f);
                        matrices.scale(1.0f, 1.0f, 1.0f + g * 0.2f);
                        matrices.multiply((Quaternionfc)RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0f));
                        break;
                    }
                    case 7: {
                        this.applyBrushTransformation(matrices, tickProgress, arm, (PlayerEntity)player);
                        break;
                    }
                    case 8: {
                        this.swingArm(swingProgress, matrices, l, arm);
                        break;
                    }
                    case 9: {
                        matrices.translate((float)l * 0.56f, -0.52f, -0.72f);
                        float f = (float)item.getMaxUseTime((LivingEntity)player) - ((float)player.getItemUseTimeLeft() - tickProgress + 1.0f);
                        Lancing.method_75396((float)player.getTimeSinceLastKineticAttack(tickProgress), (MatrixStack)matrices, (float)f, (Arm)arm, (ItemStack)item);
                        break;
                    }
                }
            } else if (player.isUsingRiptide()) {
                this.applyEquipOffset(matrices, arm, equipProgress);
                matrices.translate((float)l * -0.4f, 0.8f, 0.3f);
                matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 65.0f));
                matrices.multiply((Quaternionfc)RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -85.0f));
            } else {
                this.applyEquipOffset(matrices, arm, equipProgress);
                switch (1.field_63573[item.getSwingAnimation().type().ordinal()]) {
                    case 1: {
                        break;
                    }
                    case 2: {
                        this.swingArm(swingProgress, matrices, l, arm);
                        break;
                    }
                    case 3: {
                        Lancing.method_75391((float)swingProgress, (MatrixStack)matrices, (int)l, (Arm)arm);
                    }
                }
            }
            this.renderItem((LivingEntity)player, item, bl2 ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND, matrices, orderedRenderCommandQueue, light);
        }
        matrices.pop();
    }

    private void swingArm(float swingProgress, MatrixStack matrixStack, int i, Arm arm) {
        float f = -0.4f * MathHelper.sin((double)(MathHelper.sqrt((float)swingProgress) * (float)Math.PI));
        float g = 0.2f * MathHelper.sin((double)(MathHelper.sqrt((float)swingProgress) * ((float)Math.PI * 2)));
        float h = -0.2f * MathHelper.sin((double)(swingProgress * (float)Math.PI));
        matrixStack.translate((float)i * f, g, h);
        this.applySwingOffset(matrixStack, arm, swingProgress);
    }

    private boolean shouldSkipHandAnimationOnSwap(ItemStack from, ItemStack to) {
        if (ItemStack.shouldSkipHandAnimationOnSwap((ItemStack)from, (ItemStack)to, ComponentType::skipsHandAnimation)) {
            return true;
        }
        return !this.itemModelManager.hasHandAnimationOnSwap(to);
    }

    public void updateHeldItems() {
        this.lastEquipProgressMainHand = this.equipProgressMainHand;
        this.lastEquipProgressOffHand = this.equipProgressOffHand;
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
        if (this.shouldSkipHandAnimationOnSwap(this.mainHand, itemStack)) {
            this.mainHand = itemStack;
        }
        if (this.shouldSkipHandAnimationOnSwap(this.offHand, itemStack2)) {
            this.offHand = itemStack2;
        }
        if (clientPlayerEntity.isRiding()) {
            this.equipProgressMainHand = MathHelper.clamp((float)(this.equipProgressMainHand - 0.4f), (float)0.0f, (float)1.0f);
            this.equipProgressOffHand = MathHelper.clamp((float)(this.equipProgressOffHand - 0.4f), (float)0.0f, (float)1.0f);
        } else {
            float f = clientPlayerEntity.getHandEquippingProgress(1.0f);
            float g = this.mainHand != itemStack ? 0.0f : f * f * f;
            float h = this.offHand != itemStack2 ? 0.0f : 1.0f;
            this.equipProgressMainHand += MathHelper.clamp((float)(g - this.equipProgressMainHand), (float)-0.4f, (float)0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(h - this.equipProgressOffHand), (float)-0.4f, (float)0.4f);
        }
        if (this.equipProgressMainHand < 0.1f) {
            this.mainHand = itemStack;
        }
        if (this.equipProgressOffHand < 0.1f) {
            this.offHand = itemStack2;
        }
    }

    public void resetEquipProgress(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.equipProgressMainHand = 0.0f;
        } else {
            this.equipProgressOffHand = 0.0f;
        }
    }
}

