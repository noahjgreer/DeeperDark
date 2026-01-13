/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ingame.MountScreen
 *  net.minecraft.client.gui.screen.ingame.NautilusScreen
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.AbstractNautilusEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.screen.MountScreenHandler
 *  net.minecraft.screen.NautilusScreenHandler
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.MountScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractNautilusEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MountScreenHandler;
import net.minecraft.screen.NautilusScreenHandler;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class NautilusScreen
extends MountScreen<NautilusScreenHandler> {
    private static final Identifier SLOT_TEXTURE = Identifier.ofVanilla((String)"container/slot");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/nautilus.png");

    public NautilusScreen(NautilusScreenHandler handler, PlayerInventory inventory, AbstractNautilusEntity nautilus, int slotColumnCount) {
        super((MountScreenHandler)handler, inventory, nautilus.getDisplayName(), slotColumnCount, (LivingEntity)nautilus);
    }

    protected Identifier getTexture() {
        return TEXTURE;
    }

    protected Identifier getSlotTexture() {
        return SLOT_TEXTURE;
    }

    protected @Nullable Identifier getChestSlotsTexture() {
        return null;
    }

    protected boolean canEquipSaddle() {
        return this.mount.canUseSlot(EquipmentSlot.SADDLE);
    }

    protected boolean canEquipArmor() {
        return this.mount.canUseSlot(EquipmentSlot.BODY);
    }
}

