/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.entity.state.CatEntityRenderState
 *  net.minecraft.client.render.entity.state.FelineEntityRenderState
 *  net.minecraft.util.DyeColor
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.FelineEntityRenderState;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CatEntityRenderState
extends FelineEntityRenderState {
    private static final Identifier DEFAULT_TEXTURE = Identifier.ofVanilla((String)"textures/entity/cat/tabby.png");
    public Identifier texture = DEFAULT_TEXTURE;
    public boolean nearSleepingPlayer;
    public @Nullable DyeColor collarColor;
}

