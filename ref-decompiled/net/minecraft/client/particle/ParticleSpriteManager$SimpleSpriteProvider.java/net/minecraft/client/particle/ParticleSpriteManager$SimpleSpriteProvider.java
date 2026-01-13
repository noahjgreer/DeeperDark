/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.random.Random;

@Environment(value=EnvType.CLIENT)
static class ParticleSpriteManager.SimpleSpriteProvider
implements SpriteProvider {
    private List<Sprite> sprites;

    ParticleSpriteManager.SimpleSpriteProvider() {
    }

    @Override
    public Sprite getSprite(int age, int maxAge) {
        return this.sprites.get(age * (this.sprites.size() - 1) / maxAge);
    }

    @Override
    public Sprite getSprite(Random random) {
        return this.sprites.get(random.nextInt(this.sprites.size()));
    }

    @Override
    public Sprite getFirst() {
        return this.sprites.getFirst();
    }

    public void setSprites(List<Sprite> sprites) {
        this.sprites = ImmutableList.copyOf(sprites);
    }
}
