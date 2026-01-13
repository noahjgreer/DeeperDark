/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import com.mojang.serialization.MapCodec;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.ContextSwapper;
import net.minecraft.util.HeldItemContext;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface ItemModel {
    public void update(ItemRenderState var1, ItemStack var2, ItemModelManager var3, ItemDisplayContext var4, @Nullable ClientWorld var5, @Nullable HeldItemContext var6, int var7);

    @Environment(value=EnvType.CLIENT)
    public record BakeContext(Baker blockModelBaker, LoadedEntityModels entityModelSet, SpriteHolder spriteHolder, PlayerSkinCache playerSkinRenderCache, ItemModel missingItemModel, @Nullable ContextSwapper contextSwapper) implements SpecialModelRenderer.BakeContext
    {
        @Override
        public final String toString() {
            return ObjectMethods.bootstrap("toString", new MethodHandle[]{BakeContext.class, "blockModelBaker;entityModelSet;materials;playerSkinRenderCache;missingItemModel;contextSwapper", "blockModelBaker", "entityModelSet", "spriteHolder", "playerSkinRenderCache", "missingItemModel", "contextSwapper"}, this);
        }

        @Override
        public final int hashCode() {
            return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{BakeContext.class, "blockModelBaker;entityModelSet;materials;playerSkinRenderCache;missingItemModel;contextSwapper", "blockModelBaker", "entityModelSet", "spriteHolder", "playerSkinRenderCache", "missingItemModel", "contextSwapper"}, this);
        }

        @Override
        public final boolean equals(Object object) {
            return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{BakeContext.class, "blockModelBaker;entityModelSet;materials;playerSkinRenderCache;missingItemModel;contextSwapper", "blockModelBaker", "entityModelSet", "spriteHolder", "playerSkinRenderCache", "missingItemModel", "contextSwapper"}, this, object);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Unbaked
    extends ResolvableModel {
        public MapCodec<? extends Unbaked> getCodec();

        public ItemModel bake(BakeContext var1);
    }
}
