/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType$Builder
 */
package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.block.Block;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public static class EntityType.Builder<T extends Entity>
implements FabricEntityType.Builder<T> {
    private final EntityType.EntityFactory<T> factory;
    private final SpawnGroup spawnGroup;
    private ImmutableSet<Block> canSpawnInside = ImmutableSet.of();
    private boolean saveable = true;
    private boolean summonable = true;
    private boolean fireImmune;
    private boolean spawnableFarFromPlayer;
    private int maxTrackingRange = 5;
    private int trackingTickInterval = 3;
    private EntityDimensions dimensions = EntityDimensions.changing(0.6f, 1.8f);
    private float spawnBoxScale = 1.0f;
    private EntityAttachments.Builder attachments = EntityAttachments.builder();
    private FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;
    private RegistryKeyedValue<EntityType<?>, Optional<RegistryKey<LootTable>>> lootTable = registryKey -> Optional.of(RegistryKey.of(RegistryKeys.LOOT_TABLE, registryKey.getValue().withPrefixedPath("entities/")));
    private final RegistryKeyedValue<EntityType<?>, String> translationKey = registryKey -> Util.createTranslationKey("entity", registryKey.getValue());
    private boolean allowedInPeaceful = true;

    private EntityType.Builder(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
        this.factory = factory;
        this.spawnGroup = spawnGroup;
        this.spawnableFarFromPlayer = spawnGroup == SpawnGroup.CREATURE || spawnGroup == SpawnGroup.MISC;
    }

    public static <T extends Entity> EntityType.Builder<T> create(EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup) {
        return new EntityType.Builder<T>(factory, spawnGroup);
    }

    public static <T extends Entity> EntityType.Builder<T> create(SpawnGroup spawnGroup) {
        return new EntityType.Builder<Entity>((type, world) -> null, spawnGroup);
    }

    public EntityType.Builder<T> dimensions(float width, float height) {
        this.dimensions = EntityDimensions.changing(width, height);
        return this;
    }

    public EntityType.Builder<T> spawnBoxScale(float spawnBoxScale) {
        this.spawnBoxScale = spawnBoxScale;
        return this;
    }

    public EntityType.Builder<T> eyeHeight(float eyeHeight) {
        this.dimensions = this.dimensions.withEyeHeight(eyeHeight);
        return this;
    }

    public EntityType.Builder<T> passengerAttachments(float ... offsetYs) {
        for (float f : offsetYs) {
            this.attachments = this.attachments.add(EntityAttachmentType.PASSENGER, 0.0f, f, 0.0f);
        }
        return this;
    }

    public EntityType.Builder<T> passengerAttachments(Vec3d ... passengerAttachments) {
        for (Vec3d vec3d : passengerAttachments) {
            this.attachments = this.attachments.add(EntityAttachmentType.PASSENGER, vec3d);
        }
        return this;
    }

    public EntityType.Builder<T> vehicleAttachment(Vec3d vehicleAttachment) {
        return this.attachment(EntityAttachmentType.VEHICLE, vehicleAttachment);
    }

    public EntityType.Builder<T> vehicleAttachment(float offsetY) {
        return this.attachment(EntityAttachmentType.VEHICLE, 0.0f, -offsetY, 0.0f);
    }

    public EntityType.Builder<T> nameTagAttachment(float offsetY) {
        return this.attachment(EntityAttachmentType.NAME_TAG, 0.0f, offsetY, 0.0f);
    }

    public EntityType.Builder<T> attachment(EntityAttachmentType type, float offsetX, float offsetY, float offsetZ) {
        this.attachments = this.attachments.add(type, offsetX, offsetY, offsetZ);
        return this;
    }

    public EntityType.Builder<T> attachment(EntityAttachmentType type, Vec3d offset) {
        this.attachments = this.attachments.add(type, offset);
        return this;
    }

    public EntityType.Builder<T> disableSummon() {
        this.summonable = false;
        return this;
    }

    public EntityType.Builder<T> disableSaving() {
        this.saveable = false;
        return this;
    }

    public EntityType.Builder<T> makeFireImmune() {
        this.fireImmune = true;
        return this;
    }

    public EntityType.Builder<T> allowSpawningInside(Block ... blocks) {
        this.canSpawnInside = ImmutableSet.copyOf((Object[])blocks);
        return this;
    }

    public EntityType.Builder<T> spawnableFarFromPlayer() {
        this.spawnableFarFromPlayer = true;
        return this;
    }

    public EntityType.Builder<T> maxTrackingRange(int maxTrackingRange) {
        this.maxTrackingRange = maxTrackingRange;
        return this;
    }

    public EntityType.Builder<T> trackingTickInterval(int trackingTickInterval) {
        this.trackingTickInterval = trackingTickInterval;
        return this;
    }

    public EntityType.Builder<T> requires(FeatureFlag ... features) {
        this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
        return this;
    }

    public EntityType.Builder<T> dropsNothing() {
        this.lootTable = RegistryKeyedValue.fixed(Optional.empty());
        return this;
    }

    public EntityType.Builder<T> notAllowedInPeaceful() {
        this.allowedInPeaceful = false;
        return this;
    }

    public EntityType<T> build(RegistryKey<EntityType<?>> registryKey) {
        if (this.saveable) {
            Util.getChoiceType(TypeReferences.ENTITY_TREE, registryKey.getValue().toString());
        }
        return new EntityType<T>(this.factory, this.spawnGroup, this.saveable, this.summonable, this.fireImmune, this.spawnableFarFromPlayer, this.canSpawnInside, this.dimensions.withAttachments(this.attachments), this.spawnBoxScale, this.maxTrackingRange, this.trackingTickInterval, this.translationKey.get(registryKey), this.lootTable.get(registryKey), this.requiredFeatures, this.allowedInPeaceful);
    }
}
