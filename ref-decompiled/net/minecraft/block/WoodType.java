/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.WoodType
 *  net.minecraft.sound.BlockSoundGroup
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 */
package net.minecraft.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.block.BlockSetType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record WoodType(String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
    private final String name;
    private final BlockSetType setType;
    private final BlockSoundGroup soundType;
    private final BlockSoundGroup hangingSignSoundType;
    private final SoundEvent fenceGateClose;
    private final SoundEvent fenceGateOpen;
    private static final Map<String, WoodType> VALUES = new Object2ObjectArrayMap();
    public static final Codec<WoodType> CODEC = Codec.stringResolver(WoodType::name, VALUES::get);
    public static final WoodType OAK = WoodType.register((WoodType)new WoodType("oak", BlockSetType.OAK));
    public static final WoodType SPRUCE = WoodType.register((WoodType)new WoodType("spruce", BlockSetType.SPRUCE));
    public static final WoodType BIRCH = WoodType.register((WoodType)new WoodType("birch", BlockSetType.BIRCH));
    public static final WoodType ACACIA = WoodType.register((WoodType)new WoodType("acacia", BlockSetType.ACACIA));
    public static final WoodType CHERRY = WoodType.register((WoodType)new WoodType("cherry", BlockSetType.CHERRY, BlockSoundGroup.CHERRY_WOOD, BlockSoundGroup.CHERRY_WOOD_HANGING_SIGN, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN));
    public static final WoodType JUNGLE = WoodType.register((WoodType)new WoodType("jungle", BlockSetType.JUNGLE));
    public static final WoodType DARK_OAK = WoodType.register((WoodType)new WoodType("dark_oak", BlockSetType.DARK_OAK));
    public static final WoodType PALE_OAK = WoodType.register((WoodType)new WoodType("pale_oak", BlockSetType.PALE_OAK));
    public static final WoodType CRIMSON = WoodType.register((WoodType)new WoodType("crimson", BlockSetType.CRIMSON, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
    public static final WoodType WARPED = WoodType.register((WoodType)new WoodType("warped", BlockSetType.WARPED, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
    public static final WoodType MANGROVE = WoodType.register((WoodType)new WoodType("mangrove", BlockSetType.MANGROVE));
    public static final WoodType BAMBOO = WoodType.register((WoodType)new WoodType("bamboo", BlockSetType.BAMBOO, BlockSoundGroup.BAMBOO_WOOD, BlockSoundGroup.BAMBOO_WOOD_HANGING_SIGN, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN));

    public WoodType(String name, BlockSetType setType) {
        this(name, setType, BlockSoundGroup.WOOD, BlockSoundGroup.HANGING_SIGN, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN);
    }

    public WoodType(String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
        this.name = name;
        this.setType = setType;
        this.soundType = soundType;
        this.hangingSignSoundType = hangingSignSoundType;
        this.fenceGateClose = fenceGateClose;
        this.fenceGateOpen = fenceGateOpen;
    }

    private static WoodType register(WoodType type) {
        VALUES.put(type.name(), type);
        return type;
    }

    public static Stream<WoodType> stream() {
        return VALUES.values().stream();
    }

    public String name() {
        return this.name;
    }

    public BlockSetType setType() {
        return this.setType;
    }

    public BlockSoundGroup soundType() {
        return this.soundType;
    }

    public BlockSoundGroup hangingSignSoundType() {
        return this.hangingSignSoundType;
    }

    public SoundEvent fenceGateClose() {
        return this.fenceGateClose;
    }

    public SoundEvent fenceGateOpen() {
        return this.fenceGateOpen;
    }
}

