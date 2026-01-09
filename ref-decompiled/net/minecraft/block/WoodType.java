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

public record WoodType(String name, BlockSetType setType, BlockSoundGroup soundType, BlockSoundGroup hangingSignSoundType, SoundEvent fenceGateClose, SoundEvent fenceGateOpen) {
   private static final Map VALUES = new Object2ObjectArrayMap();
   public static final Codec CODEC;
   public static final WoodType OAK;
   public static final WoodType SPRUCE;
   public static final WoodType BIRCH;
   public static final WoodType ACACIA;
   public static final WoodType CHERRY;
   public static final WoodType JUNGLE;
   public static final WoodType DARK_OAK;
   public static final WoodType PALE_OAK;
   public static final WoodType CRIMSON;
   public static final WoodType WARPED;
   public static final WoodType MANGROVE;
   public static final WoodType BAMBOO;

   public WoodType(String name, BlockSetType setType) {
      this(name, setType, BlockSoundGroup.WOOD, BlockSoundGroup.HANGING_SIGN, SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundEvents.BLOCK_FENCE_GATE_OPEN);
   }

   public WoodType(String string, BlockSetType blockSetType, BlockSoundGroup blockSoundGroup, BlockSoundGroup blockSoundGroup2, SoundEvent soundEvent, SoundEvent soundEvent2) {
      this.name = string;
      this.setType = blockSetType;
      this.soundType = blockSoundGroup;
      this.hangingSignSoundType = blockSoundGroup2;
      this.fenceGateClose = soundEvent;
      this.fenceGateOpen = soundEvent2;
   }

   private static WoodType register(WoodType type) {
      VALUES.put(type.name(), type);
      return type;
   }

   public static Stream stream() {
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

   static {
      Function var10000 = WoodType::name;
      Map var10001 = VALUES;
      Objects.requireNonNull(var10001);
      CODEC = Codec.stringResolver(var10000, var10001::get);
      OAK = register(new WoodType("oak", BlockSetType.OAK));
      SPRUCE = register(new WoodType("spruce", BlockSetType.SPRUCE));
      BIRCH = register(new WoodType("birch", BlockSetType.BIRCH));
      ACACIA = register(new WoodType("acacia", BlockSetType.ACACIA));
      CHERRY = register(new WoodType("cherry", BlockSetType.CHERRY, BlockSoundGroup.CHERRY_WOOD, BlockSoundGroup.CHERRY_WOOD_HANGING_SIGN, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_CHERRY_WOOD_FENCE_GATE_OPEN));
      JUNGLE = register(new WoodType("jungle", BlockSetType.JUNGLE));
      DARK_OAK = register(new WoodType("dark_oak", BlockSetType.DARK_OAK));
      PALE_OAK = register(new WoodType("pale_oak", BlockSetType.PALE_OAK));
      CRIMSON = register(new WoodType("crimson", BlockSetType.CRIMSON, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
      WARPED = register(new WoodType("warped", BlockSetType.WARPED, BlockSoundGroup.NETHER_WOOD, BlockSoundGroup.NETHER_WOOD_HANGING_SIGN, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_NETHER_WOOD_FENCE_GATE_OPEN));
      MANGROVE = register(new WoodType("mangrove", BlockSetType.MANGROVE));
      BAMBOO = register(new WoodType("bamboo", BlockSetType.BAMBOO, BlockSoundGroup.BAMBOO_WOOD, BlockSoundGroup.BAMBOO_WOOD_HANGING_SIGN, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_CLOSE, SoundEvents.BLOCK_BAMBOO_WOOD_FENCE_GATE_OPEN));
   }
}
