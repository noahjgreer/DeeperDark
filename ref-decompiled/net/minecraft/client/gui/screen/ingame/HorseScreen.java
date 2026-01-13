/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gui.screen.ingame.HorseScreen
 *  net.minecraft.client.gui.screen.ingame.MountScreen
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.passive.AbstractHorseEntity
 *  net.minecraft.entity.passive.LlamaEntity
 *  net.minecraft.entity.player.PlayerInventory
 *  net.minecraft.registry.tag.EntityTypeTags
 *  net.minecraft.screen.HorseScreenHandler
 *  net.minecraft.screen.MountScreenHandler
 *  net.minecraft.util.Identifier
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.MountScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.screen.MountScreenHandler;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class HorseScreen
extends MountScreen<HorseScreenHandler> {
    private static final Identifier SLOT_TEXTURE = Identifier.ofVanilla((String)"container/slot");
    private static final Identifier CHEST_SLOTS_TEXTURE = Identifier.ofVanilla((String)"container/horse/chest_slots");
    private static final Identifier TEXTURE = Identifier.ofVanilla((String)"textures/gui/container/horse.png");

    public HorseScreen(HorseScreenHandler handler, PlayerInventory inventory, AbstractHorseEntity entity, int slotColumnCount) {
        super((MountScreenHandler)handler, inventory, entity.getDisplayName(), slotColumnCount, (LivingEntity)entity);
    }

    protected Identifier getTexture() {
        return TEXTURE;
    }

    protected Identifier getSlotTexture() {
        return SLOT_TEXTURE;
    }

    protected @Nullable Identifier getChestSlotsTexture() {
        return CHEST_SLOTS_TEXTURE;
    }

    protected boolean canEquipSaddle() {
        return this.mount.canUseSlot(EquipmentSlot.SADDLE) && this.mount.getType().isIn(EntityTypeTags.CAN_EQUIP_SADDLE);
    }

    protected boolean canEquipArmor() {
        return this.mount.canUseSlot(EquipmentSlot.BODY) && (this.mount.getType().isIn(EntityTypeTags.CAN_WEAR_HORSE_ARMOR) || this.mount instanceof LlamaEntity);
    }
}

