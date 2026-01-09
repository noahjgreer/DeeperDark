package net.minecraft.block;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public record BlockSetType(String name, boolean canOpenByHand, boolean canOpenByWindCharge, boolean canButtonBeActivatedByArrows, ActivationRule pressurePlateSensitivity, BlockSoundGroup soundType, SoundEvent doorClose, SoundEvent doorOpen, SoundEvent trapdoorClose, SoundEvent trapdoorOpen, SoundEvent pressurePlateClickOff, SoundEvent pressurePlateClickOn, SoundEvent buttonClickOff, SoundEvent buttonClickOn) {
   private static final Map VALUES = new Object2ObjectArrayMap();
   public static final Codec CODEC;
   public static final BlockSetType IRON;
   public static final BlockSetType COPPER;
   public static final BlockSetType GOLD;
   public static final BlockSetType STONE;
   public static final BlockSetType POLISHED_BLACKSTONE;
   public static final BlockSetType OAK;
   public static final BlockSetType SPRUCE;
   public static final BlockSetType BIRCH;
   public static final BlockSetType ACACIA;
   public static final BlockSetType CHERRY;
   public static final BlockSetType JUNGLE;
   public static final BlockSetType DARK_OAK;
   public static final BlockSetType PALE_OAK;
   public static final BlockSetType CRIMSON;
   public static final BlockSetType WARPED;
   public static final BlockSetType MANGROVE;
   public static final BlockSetType BAMBOO;

   public BlockSetType(String name) {
      this(name, true, true, true, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.WOOD, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_WOODEN_TRAPDOOR_CLOSE, SoundEvents.BLOCK_WOODEN_TRAPDOOR_OPEN, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF, SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON);
   }

   public BlockSetType(String string, boolean bl, boolean bl2, boolean bl3, ActivationRule activationRule, BlockSoundGroup blockSoundGroup, SoundEvent soundEvent, SoundEvent soundEvent2, SoundEvent soundEvent3, SoundEvent soundEvent4, SoundEvent soundEvent5, SoundEvent soundEvent6, SoundEvent soundEvent7, SoundEvent soundEvent8) {
      this.name = string;
      this.canOpenByHand = bl;
      this.canOpenByWindCharge = bl2;
      this.canButtonBeActivatedByArrows = bl3;
      this.pressurePlateSensitivity = activationRule;
      this.soundType = blockSoundGroup;
      this.doorClose = soundEvent;
      this.doorOpen = soundEvent2;
      this.trapdoorClose = soundEvent3;
      this.trapdoorOpen = soundEvent4;
      this.pressurePlateClickOff = soundEvent5;
      this.pressurePlateClickOn = soundEvent6;
      this.buttonClickOff = soundEvent7;
      this.buttonClickOn = soundEvent8;
   }

   private static BlockSetType register(BlockSetType blockSetType) {
      VALUES.put(blockSetType.name, blockSetType);
      return blockSetType;
   }

   public static Stream stream() {
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

   static {
      Function var10000 = BlockSetType::name;
      Map var10001 = VALUES;
      Objects.requireNonNull(var10001);
      CODEC = Codec.stringResolver(var10000, var10001::get);
      IRON = register(new BlockSetType("iron", false, false, false, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.IRON, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
      COPPER = register(new BlockSetType("copper", true, true, false, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.COPPER, SoundEvents.BLOCK_COPPER_DOOR_CLOSE, SoundEvents.BLOCK_COPPER_DOOR_OPEN, SoundEvents.BLOCK_COPPER_TRAPDOOR_CLOSE, SoundEvents.BLOCK_COPPER_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
      GOLD = register(new BlockSetType("gold", false, true, false, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.METAL, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
      STONE = register(new BlockSetType("stone", true, true, false, BlockSetType.ActivationRule.MOBS, BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
      POLISHED_BLACKSTONE = register(new BlockSetType("polished_blackstone", true, true, false, BlockSetType.ActivationRule.MOBS, BlockSoundGroup.STONE, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
      OAK = register(new BlockSetType("oak"));
      SPRUCE = register(new BlockSetType("spruce"));
      BIRCH = register(new BlockSetType("birch"));
      ACACIA = register(new BlockSetType("acacia"));
      CHERRY = register(new BlockSetType("cherry", true, true, true, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.CHERRY_WOOD, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_DOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_CHERRY_WOOD_BUTTON_CLICK_ON));
      JUNGLE = register(new BlockSetType("jungle"));
      DARK_OAK = register(new BlockSetType("dark_oak"));
      PALE_OAK = register(new BlockSetType("pale_oak"));
      CRIMSON = register(new BlockSetType("crimson", true, true, true, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
      WARPED = register(new BlockSetType("warped", true, true, true, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.NETHER_WOOD, SoundEvents.BLOCK_NETHER_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_DOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_NETHER_WOOD_BUTTON_CLICK_ON));
      MANGROVE = register(new BlockSetType("mangrove"));
      BAMBOO = register(new BlockSetType("bamboo", true, true, true, BlockSetType.ActivationRule.EVERYTHING, BlockSoundGroup.BAMBOO_WOOD, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_DOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_TRAPDOOR_OPEN, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, SoundEvents.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON));
   }

   public static enum ActivationRule {
      EVERYTHING,
      MOBS;

      // $FF: synthetic method
      private static ActivationRule[] method_36707() {
         return new ActivationRule[]{EVERYTHING, MOBS};
      }
   }
}
