/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component.type;

import java.util.Optional;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public static class EquippableComponent.Builder {
    private final EquipmentSlot slot;
    private RegistryEntry<SoundEvent> equipSound = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
    private Optional<RegistryKey<EquipmentAsset>> model = Optional.empty();
    private Optional<Identifier> cameraOverlay = Optional.empty();
    private Optional<RegistryEntryList<EntityType<?>>> allowedEntities = Optional.empty();
    private boolean dispensable = true;
    private boolean swappable = true;
    private boolean damageOnHurt = true;
    private boolean equipOnInteract;
    private boolean canBeSheared;
    private RegistryEntry<SoundEvent> shearingSound = Registries.SOUND_EVENT.getEntry(SoundEvents.ITEM_SHEARS_SNIP);

    EquippableComponent.Builder(EquipmentSlot slot) {
        this.slot = slot;
    }

    public EquippableComponent.Builder equipSound(RegistryEntry<SoundEvent> equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public EquippableComponent.Builder model(RegistryKey<EquipmentAsset> model) {
        this.model = Optional.of(model);
        return this;
    }

    public EquippableComponent.Builder cameraOverlay(Identifier cameraOverlay) {
        this.cameraOverlay = Optional.of(cameraOverlay);
        return this;
    }

    public EquippableComponent.Builder allowedEntities(EntityType<?> ... allowedEntities) {
        return this.allowedEntities(RegistryEntryList.of(EntityType::getRegistryEntry, allowedEntities));
    }

    public EquippableComponent.Builder allowedEntities(RegistryEntryList<EntityType<?>> allowedEntities) {
        this.allowedEntities = Optional.of(allowedEntities);
        return this;
    }

    public EquippableComponent.Builder dispensable(boolean dispensable) {
        this.dispensable = dispensable;
        return this;
    }

    public EquippableComponent.Builder swappable(boolean swappable) {
        this.swappable = swappable;
        return this;
    }

    public EquippableComponent.Builder damageOnHurt(boolean damageOnHurt) {
        this.damageOnHurt = damageOnHurt;
        return this;
    }

    public EquippableComponent.Builder equipOnInteract(boolean equipOnInteract) {
        this.equipOnInteract = equipOnInteract;
        return this;
    }

    public EquippableComponent.Builder canBeSheared(boolean canBeSheared) {
        this.canBeSheared = canBeSheared;
        return this;
    }

    public EquippableComponent.Builder shearingSound(RegistryEntry<SoundEvent> shearingSound) {
        this.shearingSound = shearingSound;
        return this;
    }

    public EquippableComponent build() {
        return new EquippableComponent(this.slot, this.equipSound, this.model, this.cameraOverlay, this.allowedEntities, this.dispensable, this.swappable, this.damageOnHurt, this.equipOnInteract, this.canBeSheared, this.shearingSound);
    }
}
