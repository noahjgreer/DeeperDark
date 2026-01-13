/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BlockEntityRenderManager
implements SynchronousResourceReloader {
    private Map<BlockEntityType<?>, BlockEntityRenderer<?, ?>> renderers = ImmutableMap.of();
    private final TextRenderer textRenderer;
    private final Supplier<LoadedEntityModels> entityModelsGetter;
    private Vec3d cameraPos;
    private final BlockRenderManager blockRenderManager;
    private final ItemModelManager itemModelManager;
    private final ItemRenderer itemRenderer;
    private final EntityRenderManager entityRenderDispatcher;
    private final SpriteHolder spriteHolder;
    private final PlayerSkinCache playerSkinCache;

    public BlockEntityRenderManager(TextRenderer textRenderer, Supplier<LoadedEntityModels> entityModelsGetter, BlockRenderManager blockRenderManager, ItemModelManager itemModelManager, ItemRenderer itemRenderer, EntityRenderManager entityRenderDispatcher, SpriteHolder spriteHolder, PlayerSkinCache playerSkinCache) {
        this.itemRenderer = itemRenderer;
        this.itemModelManager = itemModelManager;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.textRenderer = textRenderer;
        this.entityModelsGetter = entityModelsGetter;
        this.blockRenderManager = blockRenderManager;
        this.spriteHolder = spriteHolder;
        this.playerSkinCache = playerSkinCache;
    }

    public <E extends BlockEntity, S extends BlockEntityRenderState> @Nullable BlockEntityRenderer<E, S> get(E blockEntity) {
        return this.renderers.get(blockEntity.getType());
    }

    public <E extends BlockEntity, S extends BlockEntityRenderState> @Nullable BlockEntityRenderer<E, S> getByRenderState(S renderState) {
        return this.renderers.get(renderState.type);
    }

    public void configure(Camera camera) {
        this.cameraPos = camera.getCameraPos();
    }

    public <E extends BlockEntity, S extends BlockEntityRenderState> @Nullable S getRenderState(E blockEntity, float tickProgress,  @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer<E, S> blockEntityRenderer = this.get(blockEntity);
        if (blockEntityRenderer == null) {
            return null;
        }
        if (!blockEntity.hasWorld() || !blockEntity.getType().supports(blockEntity.getCachedState())) {
            return null;
        }
        if (!blockEntityRenderer.isInRenderDistance(blockEntity, this.cameraPos)) {
            return null;
        }
        Vec3d vec3d = this.cameraPos;
        S blockEntityRenderState = blockEntityRenderer.createRenderState();
        blockEntityRenderer.updateRenderState(blockEntity, blockEntityRenderState, tickProgress, vec3d, crumblingOverlay);
        return blockEntityRenderState;
    }

    public <S extends BlockEntityRenderState> void render(S renderState, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraRenderState) {
        BlockEntityRenderer blockEntityRenderer = this.getByRenderState(renderState);
        if (blockEntityRenderer == null) {
            return;
        }
        try {
            blockEntityRenderer.render(renderState, matrices, queue, cameraRenderState);
        }
        catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering Block Entity");
            CrashReportSection crashReportSection = crashReport.addElement("Block Entity Details");
            renderState.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void reload(ResourceManager manager) {
        BlockEntityRendererFactory.Context context = new BlockEntityRendererFactory.Context(this, this.blockRenderManager, this.itemModelManager, this.itemRenderer, this.entityRenderDispatcher, this.entityModelsGetter.get(), this.textRenderer, this.spriteHolder, this.playerSkinCache);
        this.renderers = BlockEntityRendererFactories.reload(context);
    }
}
