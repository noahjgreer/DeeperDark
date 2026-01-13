/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record WorldCreator.WorldType(@Nullable RegistryEntry<WorldPreset> preset) {
    private static final Text CUSTOM_GENERATOR_TEXT = Text.translatable("generator.custom");

    public Text getName() {
        return Optional.ofNullable(this.preset).flatMap(RegistryEntry::getKey).map(key -> Text.translatable(key.getValue().toTranslationKey("generator"))).orElse(CUSTOM_GENERATOR_TEXT);
    }

    public boolean isAmplified() {
        return Optional.ofNullable(this.preset).flatMap(RegistryEntry::getKey).filter(key -> key.equals(WorldPresets.AMPLIFIED)).isPresent();
    }
}
