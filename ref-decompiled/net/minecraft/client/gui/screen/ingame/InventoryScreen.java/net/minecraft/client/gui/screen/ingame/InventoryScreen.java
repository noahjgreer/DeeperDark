/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenPos;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.gui.screen.recipebook.CraftingRecipeBookWidget;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class InventoryScreen
extends RecipeBookScreen<PlayerScreenHandler> {
    private float mouseX;
    private float mouseY;
    private boolean mouseDown;
    private final StatusEffectsDisplay statusEffectsDisplay;

    public InventoryScreen(PlayerEntity player) {
        super(player.playerScreenHandler, new CraftingRecipeBookWidget(player.playerScreenHandler), player.getInventory(), Text.translatable("container.crafting"));
        this.titleX = 97;
        this.statusEffectsDisplay = new StatusEffectsDisplay(this);
    }

    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if (this.client.player.isInCreativeMode()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()));
        }
    }

    @Override
    protected void init() {
        if (this.client.player.isInCreativeMode()) {
            this.client.setScreen(new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), this.client.options.getOperatorItemsTab().getValue()));
            return;
        }
        super.init();
    }

    @Override
    protected ScreenPos getRecipeBookButtonPos() {
        return new ScreenPos(this.x + 104, this.height / 2 - 22);
    }

    @Override
    protected void onRecipeBookToggled() {
        this.mouseDown = true;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, -12566464, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.statusEffectsDisplay.render(context, mouseX, mouseY);
        super.render(context, mouseX, mouseY, deltaTicks);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    public boolean showsStatusEffects() {
        return this.statusEffectsDisplay.shouldHideStatusEffectHud();
    }

    @Override
    protected boolean shouldAddPaddingToGhostResult() {
        return false;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        InventoryScreen.drawEntity(context, i + 26, j + 8, i + 75, j + 78, 30, 0.0625f, this.mouseX, this.mouseY, this.client.player);
    }

    public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float scale, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)(x1 + x2) / 2.0f;
        float g = (float)(y1 + y2) / 2.0f;
        float h = (float)Math.atan((f - mouseX) / 40.0f);
        float i = (float)Math.atan((g - mouseY) / 40.0f);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(i * 20.0f * ((float)Math.PI / 180));
        quaternionf.mul((Quaternionfc)quaternionf2);
        EntityRenderState entityRenderState = InventoryScreen.drawEntity(entity);
        if (entityRenderState instanceof LivingEntityRenderState) {
            LivingEntityRenderState livingEntityRenderState = (LivingEntityRenderState)entityRenderState;
            livingEntityRenderState.bodyYaw = 180.0f + h * 20.0f;
            livingEntityRenderState.relativeHeadYaw = h * 20.0f;
            livingEntityRenderState.pitch = livingEntityRenderState.pose != EntityPose.GLIDING ? -i * 20.0f : 0.0f;
            livingEntityRenderState.width /= livingEntityRenderState.baseScale;
            livingEntityRenderState.height /= livingEntityRenderState.baseScale;
            livingEntityRenderState.baseScale = 1.0f;
        }
        Vector3f vector3f = new Vector3f(0.0f, entityRenderState.height / 2.0f + scale, 0.0f);
        context.addEntity(entityRenderState, size, vector3f, quaternionf, quaternionf2, x1, y1, x2, y2);
    }

    private static EntityRenderState drawEntity(LivingEntity entity) {
        EntityRenderManager entityRenderManager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<?, LivingEntity> entityRenderer = entityRenderManager.getRenderer(entity);
        LivingEntity entityRenderState = entityRenderer.getAndUpdateRenderState(entity, 1.0f);
        ((EntityRenderState)((Object)entityRenderState)).light = 0xF000F0;
        ((EntityRenderState)((Object)entityRenderState)).shadowPieces.clear();
        ((EntityRenderState)((Object)entityRenderState)).outlineColor = 0;
        return entityRenderState;
    }

    @Override
    public boolean mouseReleased(Click click) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        }
        return super.mouseReleased(click);
    }
}
