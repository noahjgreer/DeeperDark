/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.Click
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.ScreenPos
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.gui.screen.ingame.RecipeBookScreen
 *  net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay
 *  net.minecraft.client.gui.screen.recipebook.CraftingRecipeBookWidget
 *  net.minecraft.client.gui.screen.recipebook.RecipeBookWidget
 *  net.minecraft.client.render.entity.EntityRenderManager
 *  net.minecraft.client.render.entity.EntityRenderer
 *  net.minecraft.client.render.entity.state.EntityRenderState
 *  net.minecraft.client.render.entity.state.LivingEntityRenderState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityPose
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.AbstractCraftingScreenHandler
 *  net.minecraft.screen.AbstractRecipeScreenHandler
 *  net.minecraft.screen.PlayerScreenHandler
 *  net.minecraft.text.Text
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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.ingame.StatusEffectsDisplay;
import net.minecraft.client.gui.screen.recipebook.CraftingRecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.AbstractCraftingScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class InventoryScreen
extends RecipeBookScreen<PlayerScreenHandler> {
    private float mouseX;
    private float mouseY;
    private boolean mouseDown;
    private final StatusEffectsDisplay statusEffectsDisplay;

    public InventoryScreen(PlayerEntity player) {
        super((AbstractRecipeScreenHandler)player.playerScreenHandler, (RecipeBookWidget)new CraftingRecipeBookWidget((AbstractCraftingScreenHandler)player.playerScreenHandler), player.getInventory(), (Text)Text.translatable((String)"container.crafting"));
        this.titleX = 97;
        this.statusEffectsDisplay = new StatusEffectsDisplay((HandledScreen)this);
    }

    public void handledScreenTick() {
        super.handledScreenTick();
        if (this.client.player.isInCreativeMode()) {
            this.client.setScreen((Screen)new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), ((Boolean)this.client.options.getOperatorItemsTab().getValue()).booleanValue()));
        }
    }

    protected void init() {
        if (this.client.player.isInCreativeMode()) {
            this.client.setScreen((Screen)new CreativeInventoryScreen(this.client.player, this.client.player.networkHandler.getEnabledFeatures(), ((Boolean)this.client.options.getOperatorItemsTab().getValue()).booleanValue()));
            return;
        }
        super.init();
    }

    protected ScreenPos getRecipeBookButtonPos() {
        return new ScreenPos(this.x + 104, this.height / 2 - 22);
    }

    protected void onRecipeBookToggled() {
        this.mouseDown = true;
    }

    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, -12566464, false);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.statusEffectsDisplay.render(context, mouseX, mouseY);
        super.render(context, mouseX, mouseY, deltaTicks);
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public boolean showsStatusEffects() {
        return this.statusEffectsDisplay.shouldHideStatusEffectHud();
    }

    protected boolean shouldAddPaddingToGhostResult() {
        return false;
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = this.x;
        int j = this.y;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, BACKGROUND_TEXTURE, i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        InventoryScreen.drawEntity((DrawContext)context, (int)(i + 26), (int)(j + 8), (int)(i + 75), (int)(j + 78), (int)30, (float)0.0625f, (float)this.mouseX, (float)this.mouseY, (LivingEntity)this.client.player);
    }

    public static void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float scale, float mouseX, float mouseY, LivingEntity entity) {
        float f = (float)(x1 + x2) / 2.0f;
        float g = (float)(y1 + y2) / 2.0f;
        float h = (float)Math.atan((f - mouseX) / 40.0f);
        float i = (float)Math.atan((g - mouseY) / 40.0f);
        Quaternionf quaternionf = new Quaternionf().rotateZ((float)Math.PI);
        Quaternionf quaternionf2 = new Quaternionf().rotateX(i * 20.0f * ((float)Math.PI / 180));
        quaternionf.mul((Quaternionfc)quaternionf2);
        EntityRenderState entityRenderState = InventoryScreen.drawEntity((LivingEntity)entity);
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
        context.addEntity(entityRenderState, (float)size, vector3f, quaternionf, quaternionf2, x1, y1, x2, y2);
    }

    private static EntityRenderState drawEntity(LivingEntity entity) {
        EntityRenderManager entityRenderManager = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer entityRenderer = entityRenderManager.getRenderer((Entity)entity);
        EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState((Entity)entity, 1.0f);
        entityRenderState.light = 0xF000F0;
        entityRenderState.shadowPieces.clear();
        entityRenderState.outlineColor = 0;
        return entityRenderState;
    }

    public boolean mouseReleased(Click click) {
        if (this.mouseDown) {
            this.mouseDown = false;
            return true;
        }
        return super.mouseReleased(click);
    }
}

