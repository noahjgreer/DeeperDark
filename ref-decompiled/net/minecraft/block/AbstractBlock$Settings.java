/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeyedValue;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.featuretoggle.FeatureFlag;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jspecify.annotations.Nullable;

public static class AbstractBlock.Settings {
    public static final Codec<AbstractBlock.Settings> CODEC = MapCodec.unitCodec(() -> AbstractBlock.Settings.create());
    Function<BlockState, MapColor> mapColorProvider = state -> MapColor.CLEAR;
    boolean collidable = true;
    BlockSoundGroup soundGroup = BlockSoundGroup.STONE;
    ToIntFunction<BlockState> luminance = state -> 0;
    float resistance;
    float hardness;
    boolean toolRequired;
    boolean randomTicks;
    float slipperiness = 0.6f;
    float velocityMultiplier = 1.0f;
    float jumpVelocityMultiplier = 1.0f;
    private @Nullable RegistryKey<Block> registryKey;
    private RegistryKeyedValue<Block, Optional<RegistryKey<LootTable>>> lootTable = registryKey -> Optional.of(RegistryKey.of(RegistryKeys.LOOT_TABLE, registryKey.getValue().withPrefixedPath("blocks/")));
    private RegistryKeyedValue<Block, String> translationKey = registryKey -> Util.createTranslationKey("block", registryKey.getValue());
    boolean opaque = true;
    boolean isAir;
    boolean burnable;
    @Deprecated
    boolean liquid;
    @Deprecated
    boolean forceNotSolid;
    boolean forceSolid;
    PistonBehavior pistonBehavior = PistonBehavior.NORMAL;
    boolean blockBreakParticles = true;
    NoteBlockInstrument instrument = NoteBlockInstrument.HARP;
    boolean replaceable;
    AbstractBlock.TypedContextPredicate<EntityType<?>> allowsSpawningPredicate = (state, world, pos, type) -> state.isSideSolidFullSquare(world, pos, Direction.UP) && state.getLuminance() < 14;
    AbstractBlock.ContextPredicate solidBlockPredicate = (state, world, pos) -> state.isFullCube(world, pos);
    AbstractBlock.ContextPredicate suffocationPredicate;
    AbstractBlock.ContextPredicate blockVisionPredicate = this.suffocationPredicate = (state, world, pos) -> state.blocksMovement() && state.isFullCube(world, pos);
    AbstractBlock.ContextPredicate postProcessPredicate = (state, world, pos) -> false;
    AbstractBlock.ContextPredicate emissiveLightingPredicate = (state, world, pos) -> false;
    boolean dynamicBounds;
    FeatureSet requiredFeatures = FeatureFlags.VANILLA_FEATURES;
    @Nullable AbstractBlock.Offsetter offsetter;

    private AbstractBlock.Settings() {
    }

    public static AbstractBlock.Settings create() {
        return new AbstractBlock.Settings();
    }

    public static AbstractBlock.Settings copy(AbstractBlock block) {
        AbstractBlock.Settings settings = AbstractBlock.Settings.copyShallow(block);
        AbstractBlock.Settings settings2 = block.settings;
        settings.jumpVelocityMultiplier = settings2.jumpVelocityMultiplier;
        settings.solidBlockPredicate = settings2.solidBlockPredicate;
        settings.allowsSpawningPredicate = settings2.allowsSpawningPredicate;
        settings.postProcessPredicate = settings2.postProcessPredicate;
        settings.suffocationPredicate = settings2.suffocationPredicate;
        settings.blockVisionPredicate = settings2.blockVisionPredicate;
        settings.lootTable = settings2.lootTable;
        settings.translationKey = settings2.translationKey;
        return settings;
    }

    @Deprecated
    public static AbstractBlock.Settings copyShallow(AbstractBlock block) {
        AbstractBlock.Settings settings = new AbstractBlock.Settings();
        AbstractBlock.Settings settings2 = block.settings;
        settings.hardness = settings2.hardness;
        settings.resistance = settings2.resistance;
        settings.collidable = settings2.collidable;
        settings.randomTicks = settings2.randomTicks;
        settings.luminance = settings2.luminance;
        settings.mapColorProvider = settings2.mapColorProvider;
        settings.soundGroup = settings2.soundGroup;
        settings.slipperiness = settings2.slipperiness;
        settings.velocityMultiplier = settings2.velocityMultiplier;
        settings.dynamicBounds = settings2.dynamicBounds;
        settings.opaque = settings2.opaque;
        settings.isAir = settings2.isAir;
        settings.burnable = settings2.burnable;
        settings.liquid = settings2.liquid;
        settings.forceNotSolid = settings2.forceNotSolid;
        settings.forceSolid = settings2.forceSolid;
        settings.pistonBehavior = settings2.pistonBehavior;
        settings.toolRequired = settings2.toolRequired;
        settings.offsetter = settings2.offsetter;
        settings.blockBreakParticles = settings2.blockBreakParticles;
        settings.requiredFeatures = settings2.requiredFeatures;
        settings.emissiveLightingPredicate = settings2.emissiveLightingPredicate;
        settings.instrument = settings2.instrument;
        settings.replaceable = settings2.replaceable;
        return settings;
    }

    public AbstractBlock.Settings mapColor(DyeColor color) {
        this.mapColorProvider = state -> color.getMapColor();
        return this;
    }

    public AbstractBlock.Settings mapColor(MapColor color) {
        this.mapColorProvider = state -> color;
        return this;
    }

    public AbstractBlock.Settings mapColor(Function<BlockState, MapColor> mapColorProvider) {
        this.mapColorProvider = mapColorProvider;
        return this;
    }

    public AbstractBlock.Settings noCollision() {
        this.collidable = false;
        this.opaque = false;
        return this;
    }

    public AbstractBlock.Settings nonOpaque() {
        this.opaque = false;
        return this;
    }

    public AbstractBlock.Settings slipperiness(float slipperiness) {
        this.slipperiness = slipperiness;
        return this;
    }

    public AbstractBlock.Settings velocityMultiplier(float velocityMultiplier) {
        this.velocityMultiplier = velocityMultiplier;
        return this;
    }

    public AbstractBlock.Settings jumpVelocityMultiplier(float jumpVelocityMultiplier) {
        this.jumpVelocityMultiplier = jumpVelocityMultiplier;
        return this;
    }

    public AbstractBlock.Settings sounds(BlockSoundGroup soundGroup) {
        this.soundGroup = soundGroup;
        return this;
    }

    public AbstractBlock.Settings luminance(ToIntFunction<BlockState> luminance) {
        this.luminance = luminance;
        return this;
    }

    public AbstractBlock.Settings strength(float hardness, float resistance) {
        return this.hardness(hardness).resistance(resistance);
    }

    public AbstractBlock.Settings breakInstantly() {
        return this.strength(0.0f);
    }

    public AbstractBlock.Settings strength(float strength) {
        this.strength(strength, strength);
        return this;
    }

    public AbstractBlock.Settings ticksRandomly() {
        this.randomTicks = true;
        return this;
    }

    public AbstractBlock.Settings dynamicBounds() {
        this.dynamicBounds = true;
        return this;
    }

    public AbstractBlock.Settings dropsNothing() {
        this.lootTable = RegistryKeyedValue.fixed(Optional.empty());
        return this;
    }

    public AbstractBlock.Settings lootTable(Optional<RegistryKey<LootTable>> lootTableKey) {
        this.lootTable = RegistryKeyedValue.fixed(lootTableKey);
        return this;
    }

    protected Optional<RegistryKey<LootTable>> getLootTableKey() {
        return this.lootTable.get(Objects.requireNonNull(this.registryKey, "Block id not set"));
    }

    public AbstractBlock.Settings burnable() {
        this.burnable = true;
        return this;
    }

    public AbstractBlock.Settings liquid() {
        this.liquid = true;
        return this;
    }

    public AbstractBlock.Settings solid() {
        this.forceSolid = true;
        return this;
    }

    @Deprecated
    public AbstractBlock.Settings notSolid() {
        this.forceNotSolid = true;
        return this;
    }

    public AbstractBlock.Settings pistonBehavior(PistonBehavior pistonBehavior) {
        this.pistonBehavior = pistonBehavior;
        return this;
    }

    public AbstractBlock.Settings air() {
        this.isAir = true;
        return this;
    }

    public AbstractBlock.Settings allowsSpawning(AbstractBlock.TypedContextPredicate<EntityType<?>> predicate) {
        this.allowsSpawningPredicate = predicate;
        return this;
    }

    public AbstractBlock.Settings solidBlock(AbstractBlock.ContextPredicate predicate) {
        this.solidBlockPredicate = predicate;
        return this;
    }

    public AbstractBlock.Settings suffocates(AbstractBlock.ContextPredicate predicate) {
        this.suffocationPredicate = predicate;
        return this;
    }

    public AbstractBlock.Settings blockVision(AbstractBlock.ContextPredicate predicate) {
        this.blockVisionPredicate = predicate;
        return this;
    }

    public AbstractBlock.Settings postProcess(AbstractBlock.ContextPredicate predicate) {
        this.postProcessPredicate = predicate;
        return this;
    }

    public AbstractBlock.Settings emissiveLighting(AbstractBlock.ContextPredicate predicate) {
        this.emissiveLightingPredicate = predicate;
        return this;
    }

    public AbstractBlock.Settings requiresTool() {
        this.toolRequired = true;
        return this;
    }

    public AbstractBlock.Settings hardness(float hardness) {
        this.hardness = hardness;
        return this;
    }

    public AbstractBlock.Settings resistance(float resistance) {
        this.resistance = Math.max(0.0f, resistance);
        return this;
    }

    public AbstractBlock.Settings offset(AbstractBlock.OffsetType offsetType) {
        this.offsetter = switch (offsetType.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> null;
            case 2 -> (state, pos) -> {
                Block block = state.getBlock();
                long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
                double d = ((double)((float)(l >> 4 & 0xFL) / 15.0f) - 1.0) * (double)block.getVerticalModelOffsetMultiplier();
                float f = block.getMaxHorizontalModelOffset();
                double e = MathHelper.clamp(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                double g = MathHelper.clamp(((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                return new Vec3d(e, d, g);
            };
            case 1 -> (state, pos) -> {
                Block block = state.getBlock();
                long l = MathHelper.hashCode(pos.getX(), 0, pos.getZ());
                float f = block.getMaxHorizontalModelOffset();
                double d = MathHelper.clamp(((double)((float)(l & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                double e = MathHelper.clamp(((double)((float)(l >> 8 & 0xFL) / 15.0f) - 0.5) * 0.5, (double)(-f), (double)f);
                return new Vec3d(d, 0.0, e);
            };
        };
        return this;
    }

    public AbstractBlock.Settings noBlockBreakParticles() {
        this.blockBreakParticles = false;
        return this;
    }

    public AbstractBlock.Settings requires(FeatureFlag ... features) {
        this.requiredFeatures = FeatureFlags.FEATURE_MANAGER.featureSetOf(features);
        return this;
    }

    public AbstractBlock.Settings instrument(NoteBlockInstrument instrument) {
        this.instrument = instrument;
        return this;
    }

    public AbstractBlock.Settings replaceable() {
        this.replaceable = true;
        return this;
    }

    public AbstractBlock.Settings registryKey(RegistryKey<Block> registryKey) {
        this.registryKey = registryKey;
        return this;
    }

    public AbstractBlock.Settings overrideTranslationKey(String translationKey) {
        this.translationKey = RegistryKeyedValue.fixed(translationKey);
        return this;
    }

    protected String getTranslationKey() {
        return this.translationKey.get(Objects.requireNonNull(this.registryKey, "Block id not set"));
    }
}
