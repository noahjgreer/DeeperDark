/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.RenderPipelines
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 *  net.minecraft.client.gui.screen.ingame.MountScreen
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.MountScreenHandler
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MountScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class MountScreen<T extends MountScreenHandler>
extends HandledScreen<T> {
    protected final int slotColumnCount;
    protected float mouseX;
    protected float mouseY;
    protected LivingEntity mount;

    public MountScreen(T handler, PlayerInventory inventory, Text title, int slotColumnCount, LivingEntity mount) {
        super(handler, inventory, title);
        this.slotColumnCount = slotColumnCount;
        this.mount = mount;
    }

    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.getTexture(), i, j, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        if (this.slotColumnCount > 0 && this.getChestSlotsTexture() != null) {
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getChestSlotsTexture(), 90, 54, 0, 0, i + 79, j + 17, this.slotColumnCount * 18, 54);
        }
        if (this.canEquipSaddle()) {
            this.drawSlot(context, i + 7, j + 35 - 18);
        }
        if (this.canEquipArmor()) {
            this.drawSlot(context, i + 7, j + 35);
        }
        InventoryScreen.drawEntity((DrawContext)context, (int)(i + 26), (int)(j + 18), (int)(i + 78), (int)(j + 70), (int)17, (float)0.25f, (float)this.mouseX, (float)this.mouseY, (LivingEntity)this.mount);
    }

    protected void drawSlot(DrawContext context, int x, int y) {
        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, this.getSlotTexture(), x, y, 18, 18);
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    protected abstract Identifier getTexture();

    protected abstract Identifier getSlotTexture();

    protected abstract @Nullable Identifier getChestSlotsTexture();

    protected abstract boolean canEquipSaddle();

    protected abstract boolean canEquipArmor();
}

