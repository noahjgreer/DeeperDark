/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  net.minecraft.block.BlockSetType
 *  net.minecraft.block.BlockSetType$ActivationRule
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

public record BlockSetType(String name, boolean canOpenByHand, boolean canOpenByWindCharge, boolean canButtonBeActivatedByArrows, ActivationRule pressurePlateSensitivity, BlockSoundGroup soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
    private final String name;
    private final boolean canOpenByHand;
    private final boolean canOpenByWindCharge;
    private final boolean canButtonBeActivatedByArrows;
    private final ActivationRule pressurePlateSensitivity;
    private final BlockSoundGroup soundType;
    private final SoundEvent doorClose;
    private final SoundEvent doorOpen;
    private final SoundEvent trapdoorClose;
    private final SoundEvent trapdoorOpen;
    private final SoundEvent pressurePlateClickOff;
    private final SoundEvent pressurePlateClickOn;
    private final SoundEvent buttonClickOff;
    private final SoundEvent buttonClickOn;
    private static final Map<String, BlockSetType> VALUES = new Object2ObjectArrayMap();
    public static final Codec<BlockSetType> CODEC = Codec.stringResolver(BlockSetType::name, VALUES::get);
    public static final BlockSetType IRON = BlockSetType.register((BlockSetType)new BlockSetType("iron", false, false, false, ActivationRule.EVERYTHING, BlockSoundGroup.IRON, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType COPPER = BlockSetType.register((BlockSetType)new BlockSetType("copper", true, true, false, ActivationRule.EVERYTHING, BlockSoundGroup.COPPER, SoundEvents.BLOCK_COPPER_DOOR_CLOSE, SoundEvents.BLOCK_COPPER_DOOR_OPEN, SoundEvents.BLOCK_COPPER_TRAPDOOR_CLOSE, SoundEvents.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType GOLD = BlockSetType.register((BlockSetType)new BlockSetType("gold", false, true, false, ActivationRule.EVERYTHING, BlockSoundGroup.METAL, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType STONE = BlockSetType.register((BlockSetType)new BlockSetType("stone", true, true, false, ActivationRule.MOBS, BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType POLISHED_BLACKSTONE = BlockSetType.register((BlockSetType)new BlockSetType("polished_blackstone", true, true, false, ActivationRule.MOBS, BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final BlockSetType OAK = BlockSetType.register((BlockSetType)new BlockSetType("oak"));
    public static final BlockSetType SPRUCE = BlockSetType.register((BlockSetType)new BlockSetType("spruce"));
    public static final BlockSetType BIRCH = BlockSetType.register((BlockSetType)new BlockSetType("birch"));
    public static final BlockSetType ACACIA = BlockSetType.register((BlockSetType)new BlockSetType("acacia"));
    public static final BlockSetType CHERRY = BlockSetType.register((BlockSetType)new BlockSetType("cherry", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.CHERRY_WOOD, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType JUNGLE = BlockSetType.register((BlockSetType)new BlockSetType("jungle"));
    public static final BlockSetType DARK_OAK = BlockSetType.register((BlockSetType)new BlockSetType("dark_oak"));
    public static final BlockSetType PALE_OAK = BlockSetType.register((BlockSetType)new BlockSetType("pale_oak"));
    public static final BlockSetType CRIMSON = BlockSetType.register((BlockSetType)new BlockSetType("crimson", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType WARPED = BlockSetType.register((BlockSetType)new BlockSetType("warped", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
    public static final BlockSetType MANGROVE = BlockSetType.register((BlockSetType)new BlockSetType("mangrove"));
    public static final BlockSetType BAMBOO = BlockSetType.register((BlockSetType)new BlockSetType("bamboo", true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.BAMBOO_WOOD, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON));

    public BlockSetType(String name) {
        this(name, true, true, true, ActivationRule.EVERYTHING, BlockSoundGroup.WOOD, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);
    }

    public BlockSetType(String name, boolean canOpenByHand, boolean canOpenByWindCharge, boolean canButtonBeActivatedByArrows, ActivationRule pressurePlateSensitivity, BlockSoundGroup soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
        this.name = name;
        this.canOpenByHand = canOpenByHand;
        this.canOpenByWindCharge = canOpenByWindCharge;
        this.canButtonBeActivatedByArrows = canButtonBeActivatedByArrows;
        this.pressurePlateSensitivity = pressurePlateSensitivity;
        this.soundType = soundType;
        this.doorClose = doorClose;
        this.doorOpen = doorOpen;
        this.trapdoorClose = trapdoorClose;
        this.trapdoorOpen = trapdoorOpen;
        this.pressurePlateClickOff = pressurePlateClickOff;
        this.pressurePlateClickOn = pressurePlateClickOn;
        this.buttonClickOff = buttonClickOff;
        this.buttonClickOn = buttonClickOn;
    }

    private static BlockSetType register(BlockSetType blockSetType) {
        VALUES.put(blockSetType.name, blockSetType);
        return blockSetType;
    }

    public static Stream<BlockSetType> stream() {
        return VALUES.values().stream();
    }

    public String name() {
        return this.name;
    }

    public boolean canOpenByHand() {
        return this.canOpenByHand;
    }

    public boolean canOpenByWindCharge() {
        return this.canOpenByWindCharge;
    }

    public boolean canButtonBeActivatedByArrows() {
        return this.canButtonBeActivatedByArrows;
    }

    public ActivationRule pressurePlateSensitivity() {
        return this.pressurePlateSensitivity;
    }

    public BlockSoundGroup soundType() {
        return this.soundType;
    }

    public SoundEvent doorClose() {
        return this.doorClose;
    }

    public SoundEvent doorOpen() {
        return this.doorOpen;
    }

    public SoundEvent trapdoorClose() {
        return this.trapdoorClose;
    }

    public SoundEvent trapdoorOpen() {
        return this.trapdoorOpen;
    }

    public SoundEvent pressurePlateClickOff() {
        return this.pressurePlateClickOff;
    }

    public SoundEvent pressurePlateClickOn() {
        return this.pressurePlateClickOn;
    }

    public SoundEvent buttonClickOff() {
        return this.buttonClickOff;
    }

    public SoundEvent buttonClickOn() {
        return this.buttonClickOn;
    }
}

