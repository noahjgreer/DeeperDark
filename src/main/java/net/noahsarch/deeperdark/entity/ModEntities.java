package net.noahsarch.deeperdark.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.creature.CreatureEntity;

public class ModEntities {

    public static final ResourceKey<EntityType<?>> CREATURE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "creature")
    );

    public static final EntityType<CreatureEntity> CREATURE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            CREATURE_KEY,
            EntityType.Builder.<CreatureEntity>of(CreatureEntity::new, MobCategory.MISC)
                    .sized(1.5f, 3.0f)
                    .noSummon()
                    .noSave()
                    .clientTrackingRange(64)
                    .updateInterval(1)
                    .build(CREATURE_KEY)
    );

    public static final ResourceKey<EntityType<?>> PRIMED_DYNAMITE_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "primed_dynamite")
    );

    public static final EntityType<PrimedDynamite> PRIMED_DYNAMITE = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            PRIMED_DYNAMITE_KEY,
            EntityType.Builder.<PrimedDynamite>of(PrimedDynamite::new, MobCategory.MISC)
                    .fireImmune()
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(10)
                    .build(PRIMED_DYNAMITE_KEY)
    );

    public static final ResourceKey<EntityType<?>> THROWN_SPLASH_MILK_KEY = ResourceKey.create(
            Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "thrown_splash_milk")
    );

    public static final EntityType<ThrownSplashMilk> THROWN_SPLASH_MILK = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            THROWN_SPLASH_MILK_KEY,
            EntityType.Builder.<ThrownSplashMilk>of(ThrownSplashMilk::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(THROWN_SPLASH_MILK_KEY)
    );

    public static void initialize() {
        // Reference this class to trigger the static field initializers above,
        // which register the entity type. Called from Deeperdark.onInitialize().
    }
}
