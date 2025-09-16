package net.noahsarch.deeperdark.potion;

import net.minecraft.util.Identifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.noahsarch.deeperdark.Deeperdark;

public class ScentlessPotion {
    public static Potion SCENTLESS;
    public static Potion SCENTLESS_LONG;

    public static void registerPotions() {
        if (SCENTLESS == null) {
            SCENTLESS = Registry.register(
                Registries.POTION,
                Identifier.of("deeperdark:scentless"),
                new Potion("effect.deeperdark.scentless", new StatusEffectInstance(Deeperdark.SCENTLESS_ENTRY, 3600, 0))
            );
        }

        if (SCENTLESS_LONG == null) {
            SCENTLESS_LONG = Registry.register(
                Registries.POTION,
                Identifier.of("deeperdark:scentless_long"),
                new Potion("effect.deeperdark.scentless", new StatusEffectInstance(Deeperdark.SCENTLESS_ENTRY, 9600, 0))
            );
        }
    }
}
