/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.ImmutableMap;
import java.lang.runtime.SwitchBootstraps;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientMannequinEntity;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactories;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class EntityRenderManager
implements SynchronousResourceReloader {
    private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
    private Map<PlayerSkinType, PlayerEntityRenderer<AbstractClientPlayerEntity>> playerRenderers = Map.of();
    private Map<PlayerSkinType, PlayerEntityRenderer<ClientMannequinEntity>> mannequinRenderers = Map.of();
    public final TextureManager textureManager;
    public @Nullable Camera camera;
    public Entity targetedEntity;
    private final ItemModelManager itemModelManager;
    private final MapRenderer mapRenderer;
    private final BlockRenderManager blockRenderManager;
    private final HeldItemRenderer heldItemRenderer;
    private final AtlasManager atlasManager;
    private final TextRenderer textRenderer;
    public final GameOptions gameOptions;
    private final Supplier<LoadedEntityModels> entityModelsGetter;
    private final EquipmentModelLoader equipmentModelLoader;
    private final PlayerSkinCache skinCache;

    public <E extends Entity> int getLight(E entity, float tickProgress) {
        return this.getRenderer((EntityRenderState)((Object)entity)).getLight(entity, tickProgress);
    }

    public EntityRenderManager(MinecraftClient client, TextureManager textureManager, ItemModelManager itemModelManager, MapRenderer mapRenderer, BlockRenderManager blockRenderManager, AtlasManager atlasManager, TextRenderer textRenderer, GameOptions gameOptions, Supplier<LoadedEntityModels> entityModelsGetter, EquipmentModelLoader equipmentModelLoader, PlayerSkinCache skinCache) {
        this.textureManager = textureManager;
        this.itemModelManager = itemModelManager;
        this.mapRenderer = mapRenderer;
        this.atlasManager = atlasManager;
        this.skinCache = skinCache;
        this.heldItemRenderer = new HeldItemRenderer(client, this, itemModelManager);
        this.blockRenderManager = blockRenderManager;
        this.textRenderer = textRenderer;
        this.gameOptions = gameOptions;
        this.entityModelsGetter = entityModelsGetter;
        this.equipmentModelLoader = equipmentModelLoader;
    }

    public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity) {
        T t = entity;
        Objects.requireNonNull(t);
        T t2 = t;
        int n = 0;
        return switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{AbstractClientPlayerEntity.class, ClientMannequinEntity.class}, t2, n)) {
            case 0 -> {
                AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)t2;
                yield this.getPlayerRenderer(this.playerRenderers, abstractClientPlayerEntity);
            }
            case 1 -> {
                ClientMannequinEntity clientMannequinEntity = (ClientMannequinEntity)t2;
                yield this.getPlayerRenderer(this.mannequinRenderers, clientMannequinEntity);
            }
            default -> this.renderers.get(entity.getType());
        };
    }

    public PlayerEntityRenderer<AbstractClientPlayerEntity> getPlayerRenderer(AbstractClientPlayerEntity player) {
        return this.getPlayerRenderer(this.playerRenderers, player);
    }

    private <T extends PlayerLikeEntity> PlayerEntityRenderer<T> getPlayerRenderer(Map<PlayerSkinType, PlayerEntityRenderer<T>> skinTypeToRenderer, T player) {
        PlayerSkinType playerSkinType = ((ClientPlayerLikeEntity)((Object)player)).getSkin().model();
        PlayerEntityRenderer<T> playerEntityRenderer = skinTypeToRenderer.get(playerSkinType);
        if (playerEntityRenderer != null) {
            return playerEntityRenderer;
        }
        return skinTypeToRenderer.get(PlayerSkinType.WIDE);
    }

    public <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S state) {
        if (state instanceof PlayerEntityRenderState) {
            PlayerEntityRenderState playerEntityRenderState = (PlayerEntityRenderState)state;
            PlayerSkinType playerSkinType = playerEntityRenderState.skinTextures.model();
            EntityRenderer entityRenderer = this.playerRenderers.get(playerSkinType);
            if (entityRenderer != null) {
                return entityRenderer;
            }
            return this.playerRenderers.get(PlayerSkinType.WIDE);
        }
        return this.renderers.get(state.entityType);
    }

    public void configure(Camera camera, Entity targetedEntity) {
        this.camera = camera;
        this.targetedEntity = targetedEntity;
    }

    public <E extends Entity> boolean shouldRender(E entity, Frustum frustum, double x, double y, double z) {
        EntityRenderer<?, E> entityRenderer = this.getRenderer((EntityRenderState)((Object)entity));
        return entityRenderer.shouldRender(entity, frustum, x, y, z);
    }

    public <E extends Entity> EntityRenderState getAndUpdateRenderState(E entity, float tickProgress) {
        EntityRenderer<?, E> entityRenderer = this.getRenderer((EntityRenderState)((Object)entity));
        try {
            return entityRenderer.getAndUpdateRenderState(entity, tickProgress);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Extracting render state for an entity in world");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being extracted");
            entity.populateCrashReport(crashReportSection);
            CrashReportSection crashReportSection2 = this.addRendererDetails(entityRenderer, crashReport);
            crashReportSection2.add("Delta", Float.valueOf(tickProgress));
            throw new CrashException(crashReport);
        }
    }

    public <S extends EntityRenderState> void render(S renderState, CameraRenderState cameraState, double offsetX, double offsetY, double offsetZ, MatrixStack matrices, OrderedRenderCommandQueue queue) {
        EntityRenderer<?, S> entityRenderer = this.getRenderer(renderState);
        try {
            Vec3d vec3d = entityRenderer.getPositionOffset(renderState);
            double d = offsetX + vec3d.getX();
            double e = offsetY + vec3d.getY();
            double f = offsetZ + vec3d.getZ();
            matrices.push();
            matrices.translate(d, e, f);
            entityRenderer.render(renderState, matrices, queue, cameraState);
            if (renderState.onFire) {
                queue.submitFire(matrices, renderState, MathHelper.rotateAround(MathHelper.Y_AXIS, cameraState.orientation, new Quaternionf()));
            }
            if (renderState instanceof PlayerEntityRenderState) {
                matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
            }
            if (!renderState.shadowPieces.isEmpty()) {
                queue.submitShadowPieces(matrices, renderState.shadowRadius, renderState.shadowPieces);
            }
            if (!(renderState instanceof PlayerEntityRenderState)) {
                matrices.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
            }
            matrices.pop();
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering entity in world");
            CrashReportSection crashReportSection = crashReport.addElement("EntityRenderState being rendered");
            renderState.addCrashReportDetails(crashReportSection);
            this.addRendererDetails(entityRenderer, crashReport);
            throw new CrashException(crashReport);
        }
    }

    private <S extends EntityRenderState> CrashReportSection addRendererDetails(EntityRenderer<?, S> renderer, CrashReport crashReport) {
        CrashReportSection crashReportSection = crashReport.addElement("Renderer details");
        crashReportSection.add("Assigned renderer", renderer);
        return crashReportSection;
    }

    public void clearCamera() {
        this.camera = null;
    }

    public double getSquaredDistanceToCamera(Entity entity) {
        return this.camera.getCameraPos().squaredDistanceTo(entity.getEntityPos());
    }

    public HeldItemRenderer getHeldItemRenderer() {
        return this.heldItemRenderer;
    }

    @Override
    public void reload(ResourceManager manager) {
        EntityRendererFactory.Context context = new EntityRendererFactory.Context(this, this.itemModelManager, this.mapRenderer, this.blockRenderManager, manager, this.entityModelsGetter.get(), this.equipmentModelLoader, this.atlasManager, this.textRenderer, this.skinCache);
        this.renderers = EntityRendererFactories.reloadEntityRenderers(context);
        this.playerRenderers = EntityRendererFactories.reloadPlayerRenderers(context);
        this.mannequinRenderers = EntityRendererFactories.reloadPlayerRenderers(context);
    }
}
