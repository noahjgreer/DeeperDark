/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.component.type;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.item.consume.PlaySoundConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public static class ConsumableComponent.Builder {
    private float consumeSeconds = 1.6f;
    private UseAction useAction = UseAction.EAT;
    private RegistryEntry<SoundEvent> sound = SoundEvents.ENTITY_GENERIC_EAT;
    private boolean consumeParticles = true;
    private final List<ConsumeEffect> consumeEffects = new ArrayList<ConsumeEffect>();

    ConsumableComponent.Builder() {
    }

    public ConsumableComponent.Builder consumeSeconds(float consumeSeconds) {
        this.consumeSeconds = consumeSeconds;
        return this;
    }

    public ConsumableComponent.Builder useAction(UseAction useAction) {
        this.useAction = useAction;
        return this;
    }

    public ConsumableComponent.Builder sound(RegistryEntry<SoundEvent> sound) {
        this.sound = sound;
        return this;
    }

    public ConsumableComponent.Builder finishSound(RegistryEntry<SoundEvent> finishSound) {
        return this.consumeEffect(new PlaySoundConsumeEffect(finishSound));
    }

    public ConsumableComponent.Builder consumeParticles(boolean consumeParticles) {
        this.consumeParticles = consumeParticles;
        return this;
    }

    public ConsumableComponent.Builder consumeEffect(ConsumeEffect consumeEffect) {
        this.consumeEffects.add(consumeEffect);
        return this;
    }

    public ConsumableComponent build() {
        return new ConsumableComponent(this.consumeSeconds, this.useAction, this.sound, this.consumeParticles, this.consumeEffects);
    }
}
