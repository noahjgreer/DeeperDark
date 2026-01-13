/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.ClientPlayerLikeEntity
 *  net.minecraft.client.network.ClientPlayerLikeState
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.text.Text
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerLikeState;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public interface ClientPlayerLikeEntity {
    public ClientPlayerLikeState getState();

    public SkinTextures getSkin();

    public @Nullable Text getMannequinName();

    public // Could not load outer class - annotation placement on inner may be incorrect
     @Nullable ParrotEntity.Variant getShoulderParrotVariant(boolean var1);

    public boolean hasExtraEars();
}

