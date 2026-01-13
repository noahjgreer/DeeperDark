/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block.entity;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;

@Environment(value=EnvType.CLIENT)
public record BlockEntityRendererFactory.Context(BlockEntityRenderManager renderDispatcher, BlockRenderManager renderManager, ItemModelManager itemModelManager, ItemRenderer itemRenderer, EntityRenderManager entityRenderDispatcher, LoadedEntityModels loadedEntityModels, TextRenderer textRenderer, SpriteHolder spriteHolder, PlayerSkinCache playerSkinRenderCache) {
    public ModelPart getLayerModelPart(EntityModelLayer modelLayer) {
        return this.loadedEntityModels.getModelPart(modelLayer);
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{BlockEntityRendererFactory.Context.class, "blockEntityRenderDispatcher;blockRenderDispatcher;itemModelResolver;itemRenderer;entityRenderer;entityModelSet;font;materials;playerSkinRenderCache", "renderDispatcher", "renderManager", "itemModelManager", "itemRenderer", "entityRenderDispatcher", "loadedEntityModels", "textRenderer", "spriteHolder", "playerSkinRenderCache"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BlockEntityRendererFactory.Context.class, "blockEntityRenderDispatcher;blockRenderDispatcher;itemModelResolver;itemRenderer;entityRenderer;entityModelSet;font;materials;playerSkinRenderCache", "renderDispatcher", "renderManager", "itemModelManager", "itemRenderer", "entityRenderDispatcher", "loadedEntityModels", "textRenderer", "spriteHolder", "playerSkinRenderCache"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BlockEntityRendererFactory.Context.class, "blockEntityRenderDispatcher;blockRenderDispatcher;itemModelResolver;itemRenderer;entityRenderer;entityModelSet;font;materials;playerSkinRenderCache", "renderDispatcher", "renderManager", "itemModelManager", "itemRenderer", "entityRenderDispatcher", "loadedEntityModels", "textRenderer", "spriteHolder", "playerSkinRenderCache"}, this, object);
    }
}
