/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.registry.ContextSwapper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ItemModel.BakeContext(Baker blockModelBaker, LoadedEntityModels entityModelSet, SpriteHolder spriteHolder, PlayerSkinCache playerSkinRenderCache, ItemModel missingItemModel, @Nullable ContextSwapper contextSwapper) implements SpecialModelRenderer.BakeContext
{
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{ItemModel.BakeContext.class, "blockModelBaker;entityModelSet;materials;playerSkinRenderCache;missingItemModel;contextSwapper", "blockModelBaker", "entityModelSet", "spriteHolder", "playerSkinRenderCache", "missingItemModel", "contextSwapper"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{ItemModel.BakeContext.class, "blockModelBaker;entityModelSet;materials;playerSkinRenderCache;missingItemModel;contextSwapper", "blockModelBaker", "entityModelSet", "spriteHolder", "playerSkinRenderCache", "missingItemModel", "contextSwapper"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{ItemModel.BakeContext.class, "blockModelBaker;entityModelSet;materials;playerSkinRenderCache;missingItemModel;contextSwapper", "blockModelBaker", "entityModelSet", "spriteHolder", "playerSkinRenderCache", "missingItemModel", "contextSwapper"}, this, object);
    }
}
