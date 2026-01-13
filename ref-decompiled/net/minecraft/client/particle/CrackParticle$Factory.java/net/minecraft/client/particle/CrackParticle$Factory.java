/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.Atlases;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
public static abstract class CrackParticle.Factory<T extends ParticleEffect>
implements ParticleFactory<T> {
    private final ItemRenderState itemRenderState = new ItemRenderState();

    protected Sprite getSprite(ItemStack stack, ClientWorld world, Random random) {
        MinecraftClient.getInstance().getItemModelManager().clearAndUpdate(this.itemRenderState, stack, ItemDisplayContext.GROUND, world, null, 0);
        Sprite sprite = this.itemRenderState.getParticleSprite(random);
        return sprite != null ? sprite : MinecraftClient.getInstance().getAtlasManager().getAtlasTexture(Atlases.ITEMS).getMissingSprite();
    }
}
