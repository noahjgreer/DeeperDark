/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderState
extends BipedEntityRenderState {
    public SkinTextures skinTextures = DefaultSkinHelper.getSteve();
    public float field_53536;
    public float field_53537;
    public float field_53538;
    public int stuckArrowCount;
    public int stingerCount;
    public boolean spectator;
    public boolean hatVisible = true;
    public boolean jacketVisible = true;
    public boolean leftPantsLegVisible = true;
    public boolean rightPantsLegVisible = true;
    public boolean leftSleeveVisible = true;
    public boolean rightSleeveVisible = true;
    public boolean capeVisible = true;
    public float glidingTicks;
    public boolean applyFlyingRotation;
    public float flyingRotation;
    public @Nullable Text playerName;
    public  @Nullable ParrotEntity.Variant leftShoulderParrotVariant;
    public  @Nullable ParrotEntity.Variant rightShoulderParrotVariant;
    public int id;
    public boolean extraEars = false;
    public final ItemRenderState spyglassState = new ItemRenderState();

    public float getGlidingProgress() {
        return MathHelper.clamp(this.glidingTicks * this.glidingTicks / 100.0f, 0.0f, 1.0f);
    }
}
