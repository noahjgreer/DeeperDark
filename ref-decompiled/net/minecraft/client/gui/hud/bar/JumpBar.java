/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.hud.bar.Bar
 *  net.minecraft.client.gui.hud.bar.JumpBar
 *  net.minecraft.client.render.RenderTickCounter
 *  net.minecraft.entity.JumpingMount
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 */
package net.minecraft.client.gui.hud.bar;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.JumpingMount;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class JumpBar
implements Bar {
    private static final Identifier BACKGROUND = Identifier.ofVanilla((String)"hud/jump_bar_background");
    private static final Identifier COOLDOWN = Identifier.ofVanilla((String)"hud/jump_bar_cooldown");
    private static final Identifier PROGRESS = Identifier.ofVanilla((String)"hud/jump_bar_progress");
    private final MinecraftClient client;
    private final JumpingMount jumpingMount;

    public JumpBar(MinecraftClient client) {
        this.client = client;
        this.jumpingMount = Objects.requireNonNull(Objects.requireNonNull(client.player).getJumpingMount());
    }

    public void renderBar(DrawContext context, RenderTickCounter tickCounter) {
        int i = this.getCenterX(this.client.getWindow());
        int j = this.getCenterY(this.client.getWindow());
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND, i, j, 182, 5);
        if (this.jumpingMount.getJumpCooldown() > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, COOLDOWN, i, j, 182, 5);
            return;
        }
        int k = MathHelper.lerpPositive((float)this.client.player.getMountJumpStrength(), (int)0, (int)182);
        if (k > 0) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, PROGRESS, 182, 5, 0, 0, i, j, k, 5);
        }
    }

    public void renderAddons(DrawContext context, RenderTickCounter tickCounter) {
    }
}

