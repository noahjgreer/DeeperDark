/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.item.model.special;

import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.SpriteHolder;

@Environment(value=EnvType.CLIENT)
public record SpecialModelRenderer.BakeContext.Simple(LoadedEntityModels entityModelSet, SpriteHolder spriteHolder, PlayerSkinCache playerSkinRenderCache) implements SpecialModelRenderer.BakeContext
{
    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SpecialModelRenderer.BakeContext.Simple.class, "entityModelSet;materials;playerSkinRenderCache", "entityModelSet", "spriteHolder", "playerSkinRenderCache"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SpecialModelRenderer.BakeContext.Simple.class, "entityModelSet;materials;playerSkinRenderCache", "entityModelSet", "spriteHolder", "playerSkinRenderCache"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SpecialModelRenderer.BakeContext.Simple.class, "entityModelSet;materials;playerSkinRenderCache", "entityModelSet", "spriteHolder", "playerSkinRenderCache"}, this, object);
    }
}
