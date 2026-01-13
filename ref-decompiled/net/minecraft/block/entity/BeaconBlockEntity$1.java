/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block.entity;

import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.sound.SoundEvents;

class BeaconBlockEntity.1
implements PropertyDelegate {
    BeaconBlockEntity.1() {
    }

    @Override
    public int get(int index) {
        return switch (index) {
            case 0 -> BeaconBlockEntity.this.level;
            case 1 -> BeaconScreenHandler.getRawIdForStatusEffect(BeaconBlockEntity.this.primary);
            case 2 -> BeaconScreenHandler.getRawIdForStatusEffect(BeaconBlockEntity.this.secondary);
            default -> 0;
        };
    }

    @Override
    public void set(int index, int value) {
        switch (index) {
            case 0: {
                BeaconBlockEntity.this.level = value;
                break;
            }
            case 1: {
                if (!BeaconBlockEntity.this.world.isClient() && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
                    BeaconBlockEntity.playSound(BeaconBlockEntity.this.world, BeaconBlockEntity.this.pos, SoundEvents.BLOCK_BEACON_POWER_SELECT);
                }
                BeaconBlockEntity.this.primary = BeaconBlockEntity.getEffectOrNull(BeaconScreenHandler.getStatusEffectForRawId(value));
                break;
            }
            case 2: {
                BeaconBlockEntity.this.secondary = BeaconBlockEntity.getEffectOrNull(BeaconScreenHandler.getStatusEffectForRawId(value));
            }
        }
    }

    @Override
    public int size() {
        return 3;
    }
}
