/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.navigation.GuiNavigation
 *  net.minecraft.client.gui.navigation.GuiNavigationPath
 *  net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
 *  net.minecraft.client.gui.widget.ClickableWidget
 *  net.minecraft.client.gui.widget.PlayerSkinWidget
 *  net.minecraft.client.render.entity.model.EntityModelLayers
 *  net.minecraft.client.render.entity.model.LoadedEntityModels
 *  net.minecraft.client.render.entity.model.PlayerEntityModel
 *  net.minecraft.client.sound.SoundManager
 *  net.minecraft.entity.player.PlayerSkinType
 *  net.minecraft.entity.player.SkinTextures
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.util.math.MathHelper
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.widget;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.math.MathHelper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class PlayerSkinWidget
extends ClickableWidget {
    private static final float field_45997 = 2.125f;
    private static final float field_59833 = 0.97f;
    private static final float field_45999 = 2.5f;
    private static final float field_46000 = -5.0f;
    private static final float field_46001 = 30.0f;
    private static final float field_46002 = 50.0f;
    private final PlayerEntityModel wideModel;
    private final PlayerEntityModel slimModel;
    private final Supplier<SkinTextures> skinSupplier;
    private float xRotation = -5.0f;
    private float yRotation = 30.0f;

    public PlayerSkinWidget(int width, int height, LoadedEntityModels entityModels, Supplier<SkinTextures> skinSupplier) {
        super(0, 0, width, height, ScreenTexts.EMPTY);
        this.wideModel = new PlayerEntityModel(entityModels.getModelPart(EntityModelLayers.PLAYER), false);
        this.slimModel = new PlayerEntityModel(entityModels.getModelPart(EntityModelLayers.PLAYER_SLIM), true);
        this.skinSupplier = skinSupplier;
    }

    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        float f = 0.97f * (float)this.getHeight() / 2.125f;
        float g = -1.0625f;
        SkinTextures skinTextures = (SkinTextures)this.skinSupplier.get();
        PlayerEntityModel playerEntityModel = skinTextures.model() == PlayerSkinType.SLIM ? this.slimModel : this.wideModel;
        context.addPlayerSkin(playerEntityModel, skinTextures.body().texturePath(), f, this.xRotation, this.yRotation, -1.0625f, this.getX(), this.getY(), this.getRight(), this.getBottom());
    }

    protected void onDrag(Click click, double offsetX, double offsetY) {
        this.xRotation = MathHelper.clamp((float)(this.xRotation - (float)offsetY * 2.5f), (float)-50.0f, (float)50.0f);
        this.yRotation += (float)offsetX * 2.5f;
    }

    public void playDownSound(SoundManager soundManager) {
    }

    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return null;
    }
}

