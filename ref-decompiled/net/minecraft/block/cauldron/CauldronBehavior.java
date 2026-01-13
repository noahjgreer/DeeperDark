/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockState
 *  net.minecraft.block.Blocks
 *  net.minecraft.block.LeveledCauldronBlock
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.block.cauldron.CauldronBehavior
 *  net.minecraft.block.cauldron.CauldronBehavior$CauldronBehaviorMap
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.BannerPatternsComponent
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.fluid.FluidState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemUsage
 *  net.minecraft.item.Items
 *  net.minecraft.potion.Potions
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.registry.tag.ItemTags
 *  net.minecraft.sound.SoundCategory
 *  net.minecraft.sound.SoundEvent
 *  net.minecraft.sound.SoundEvents
 *  net.minecraft.stat.Stats
 *  net.minecraft.state.property.Property
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.Hand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.event.GameEvent
 */
package net.minecraft.block.cauldron;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

/*
 * Exception performing whole class analysis ignored.
 */
public interface CauldronBehavior {
    public static final Map<String, CauldronBehaviorMap> BEHAVIOR_MAPS = new Object2ObjectArrayMap();
    public static final Codec<CauldronBehaviorMap> CODEC = Codec.stringResolver(CauldronBehaviorMap::name, BEHAVIOR_MAPS::get);
    public static final CauldronBehaviorMap EMPTY_CAULDRON_BEHAVIOR = CauldronBehavior.createMap((String)"empty");
    public static final CauldronBehaviorMap WATER_CAULDRON_BEHAVIOR = CauldronBehavior.createMap((String)"water");
    public static final CauldronBehaviorMap LAVA_CAULDRON_BEHAVIOR = CauldronBehavior.createMap((String)"lava");
    public static final CauldronBehaviorMap POWDER_SNOW_CAULDRON_BEHAVIOR = CauldronBehavior.createMap((String)"powder_snow");

    public static CauldronBehaviorMap createMap(String name) {
        Object2ObjectOpenHashMap object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
        object2ObjectOpenHashMap.defaultReturnValue((state, world, pos, player, hand, stack) -> ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION);
        CauldronBehaviorMap cauldronBehaviorMap = new CauldronBehaviorMap(name, (Map)object2ObjectOpenHashMap);
        BEHAVIOR_MAPS.put(name, cauldronBehaviorMap);
        return cauldronBehaviorMap;
    }

    public ActionResult interact(BlockState var1, World var2, BlockPos var3, PlayerEntity var4, Hand var5, ItemStack var6);

    public static void registerBehavior() {
        Map map = EMPTY_CAULDRON_BEHAVIOR.map();
        CauldronBehavior.registerBucketBehavior((Map)map);
        map.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.get(DataComponentTypes.POTION_CONTENTS);
            if (potionContentsComponent == null || !potionContentsComponent.matches(Potions.WATER)) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            if (!world.isClient()) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack((ItemStack)stack, (PlayerEntity)player, (ItemStack)new ItemStack((ItemConvertible)Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat((Object)item));
                world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState());
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, (RegistryEntry)GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.SUCCESS;
        });
        Map map2 = WATER_CAULDRON_BEHAVIOR.map();
        CauldronBehavior.registerBucketBehavior((Map)map2);
        map2.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron((BlockState)state, (World)world, (BlockPos)pos, (PlayerEntity)player, (Hand)hand, (ItemStack)stack, (ItemStack)new ItemStack((ItemConvertible)Items.WATER_BUCKET), (T statex) -> (Integer)statex.get((Property)LeveledCauldronBlock.LEVEL) == 3, (SoundEvent)SoundEvents.ITEM_BUCKET_FILL));
        map2.put(Items.GLASS_BOTTLE, (state, world, pos, player, hand, stack) -> {
            if (!world.isClient()) {
                Item item = stack.getItem();
                player.setStackInHand(hand, ItemUsage.exchangeStack((ItemStack)stack, (PlayerEntity)player, (ItemStack)PotionContentsComponent.createStack((Item)Items.POTION, (RegistryEntry)Potions.WATER)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat((Object)item));
                LeveledCauldronBlock.decrementFluidLevel((BlockState)state, (World)world, (BlockPos)pos);
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, (RegistryEntry)GameEvent.FLUID_PICKUP, pos);
            }
            return ActionResult.SUCCESS;
        });
        map2.put(Items.POTION, (state, world, pos, player, hand, stack) -> {
            if ((Integer)state.get((Property)LeveledCauldronBlock.LEVEL) == 3) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            PotionContentsComponent potionContentsComponent = (PotionContentsComponent)stack.get(DataComponentTypes.POTION_CONTENTS);
            if (potionContentsComponent == null || !potionContentsComponent.matches(Potions.WATER)) {
                return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
            }
            if (!world.isClient()) {
                player.setStackInHand(hand, ItemUsage.exchangeStack((ItemStack)stack, (PlayerEntity)player, (ItemStack)new ItemStack((ItemConvertible)Items.GLASS_BOTTLE)));
                player.incrementStat(Stats.USE_CAULDRON);
                player.incrementStat(Stats.USED.getOrCreateStat((Object)stack.getItem()));
                world.setBlockState(pos, (BlockState)state.cycle((Property)LeveledCauldronBlock.LEVEL));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(null, (RegistryEntry)GameEvent.FLUID_PLACE, pos);
            }
            return ActionResult.SUCCESS;
        });
        map2.put(Items.LEATHER_BOOTS, CauldronBehavior::cleanArmor);
        map2.put(Items.LEATHER_LEGGINGS, CauldronBehavior::cleanArmor);
        map2.put(Items.LEATHER_CHESTPLATE, CauldronBehavior::cleanArmor);
        map2.put(Items.LEATHER_HELMET, CauldronBehavior::cleanArmor);
        map2.put(Items.LEATHER_HORSE_ARMOR, CauldronBehavior::cleanArmor);
        map2.put(Items.WOLF_ARMOR, CauldronBehavior::cleanArmor);
        map2.put(Items.WHITE_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.GRAY_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.BLACK_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.BLUE_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.BROWN_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.CYAN_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.GREEN_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.LIGHT_BLUE_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.LIGHT_GRAY_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.LIME_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.MAGENTA_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.ORANGE_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.PINK_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.PURPLE_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.RED_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.YELLOW_BANNER, CauldronBehavior::cleanBanner);
        map2.put(Items.WHITE_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.GRAY_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.BLACK_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.BLUE_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.BROWN_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.CYAN_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.GREEN_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.LIGHT_BLUE_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.LIGHT_GRAY_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.LIME_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.MAGENTA_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.ORANGE_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.PINK_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.PURPLE_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.RED_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        map2.put(Items.YELLOW_SHULKER_BOX, CauldronBehavior::cleanShulkerBox);
        Map map3 = LAVA_CAULDRON_BEHAVIOR.map();
        map3.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron((BlockState)state, (World)world, (BlockPos)pos, (PlayerEntity)player, (Hand)hand, (ItemStack)stack, (ItemStack)new ItemStack((ItemConvertible)Items.LAVA_BUCKET), (T statex) -> true, (SoundEvent)SoundEvents.ITEM_BUCKET_FILL_LAVA));
        CauldronBehavior.registerBucketBehavior((Map)map3);
        Map map4 = POWDER_SNOW_CAULDRON_BEHAVIOR.map();
        map4.put(Items.BUCKET, (state, world, pos, player, hand, stack) -> CauldronBehavior.emptyCauldron((BlockState)state, (World)world, (BlockPos)pos, (PlayerEntity)player, (Hand)hand, (ItemStack)stack, (ItemStack)new ItemStack((ItemConvertible)Items.POWDER_SNOW_BUCKET), (T statex) -> (Integer)statex.get((Property)LeveledCauldronBlock.LEVEL) == 3, (SoundEvent)SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW));
        CauldronBehavior.registerBucketBehavior((Map)map4);
    }

    public static void registerBucketBehavior(Map<Item, CauldronBehavior> behavior) {
        behavior.put(Items.LAVA_BUCKET, CauldronBehavior::tryFillWithLava);
        behavior.put(Items.WATER_BUCKET, CauldronBehavior::tryFillWithWater);
        behavior.put(Items.POWDER_SNOW_BUCKET, CauldronBehavior::tryFillWithPowderSnow);
    }

    public static ActionResult emptyCauldron(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, ItemStack output, Predicate<BlockState> fullPredicate, SoundEvent soundEvent) {
        if (!fullPredicate.test(state)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient()) {
            Item item = stack.getItem();
            player.setStackInHand(hand, ItemUsage.exchangeStack((ItemStack)stack, (PlayerEntity)player, (ItemStack)output));
            player.incrementStat(Stats.USE_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat((Object)item));
            world.setBlockState(pos, Blocks.CAULDRON.getDefaultState());
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, (RegistryEntry)GameEvent.FLUID_PICKUP, pos);
        }
        return ActionResult.SUCCESS;
    }

    public static ActionResult fillCauldron(World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, BlockState state, SoundEvent soundEvent) {
        if (!world.isClient()) {
            Item item = stack.getItem();
            player.setStackInHand(hand, ItemUsage.exchangeStack((ItemStack)stack, (PlayerEntity)player, (ItemStack)new ItemStack((ItemConvertible)Items.BUCKET)));
            player.incrementStat(Stats.FILL_CAULDRON);
            player.incrementStat(Stats.USED.getOrCreateStat((Object)item));
            world.setBlockState(pos, state);
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, (RegistryEntry)GameEvent.FLUID_PLACE, pos);
        }
        return ActionResult.SUCCESS;
    }

    private static ActionResult tryFillWithWater(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return CauldronBehavior.fillCauldron((World)world, (BlockPos)pos, (PlayerEntity)player, (Hand)hand, (ItemStack)stack, (BlockState)((BlockState)Blocks.WATER_CAULDRON.getDefaultState().with((Property)LeveledCauldronBlock.LEVEL, (Comparable)Integer.valueOf(3))), (SoundEvent)SoundEvents.ITEM_BUCKET_EMPTY);
    }

    private static ActionResult tryFillWithLava(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return CauldronBehavior.isUnderwater((World)world, (BlockPos)pos) ? ActionResult.CONSUME : CauldronBehavior.fillCauldron((World)world, (BlockPos)pos, (PlayerEntity)player, (Hand)hand, (ItemStack)stack, (BlockState)Blocks.LAVA_CAULDRON.getDefaultState(), (SoundEvent)SoundEvents.ITEM_BUCKET_EMPTY_LAVA);
    }

    private static ActionResult tryFillWithPowderSnow(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        return CauldronBehavior.isUnderwater((World)world, (BlockPos)pos) ? ActionResult.CONSUME : CauldronBehavior.fillCauldron((World)world, (BlockPos)pos, (PlayerEntity)player, (Hand)hand, (ItemStack)stack, (BlockState)((BlockState)Blocks.POWDER_SNOW_CAULDRON.getDefaultState().with((Property)LeveledCauldronBlock.LEVEL, (Comparable)Integer.valueOf(3))), (SoundEvent)SoundEvents.ITEM_BUCKET_EMPTY_POWDER_SNOW);
    }

    private static ActionResult cleanShulkerBox(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        Block block = Block.getBlockFromItem((Item)stack.getItem());
        if (!(block instanceof ShulkerBoxBlock)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient()) {
            ItemStack itemStack = stack.copyComponentsToNewStack((ItemConvertible)Blocks.SHULKER_BOX, 1);
            player.setStackInHand(hand, ItemUsage.exchangeStack((ItemStack)stack, (PlayerEntity)player, (ItemStack)itemStack, (boolean)false));
            player.incrementStat(Stats.CLEAN_SHULKER_BOX);
            LeveledCauldronBlock.decrementFluidLevel((BlockState)state, (World)world, (BlockPos)pos);
        }
        return ActionResult.SUCCESS;
    }

    private static ActionResult cleanBanner(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        BannerPatternsComponent bannerPatternsComponent = (BannerPatternsComponent)stack.getOrDefault(DataComponentTypes.BANNER_PATTERNS, (Object)BannerPatternsComponent.DEFAULT);
        if (bannerPatternsComponent.layers().isEmpty()) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient()) {
            ItemStack itemStack = stack.copyWithCount(1);
            itemStack.set(DataComponentTypes.BANNER_PATTERNS, (Object)bannerPatternsComponent.withoutTopLayer());
            player.setStackInHand(hand, ItemUsage.exchangeStack((ItemStack)stack, (PlayerEntity)player, (ItemStack)itemStack, (boolean)false));
            player.incrementStat(Stats.CLEAN_BANNER);
            LeveledCauldronBlock.decrementFluidLevel((BlockState)state, (World)world, (BlockPos)pos);
        }
        return ActionResult.SUCCESS;
    }

    private static ActionResult cleanArmor(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack) {
        if (!stack.isIn(ItemTags.DYEABLE)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!stack.contains(DataComponentTypes.DYED_COLOR)) {
            return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;
        }
        if (!world.isClient()) {
            stack.remove(DataComponentTypes.DYED_COLOR);
            player.incrementStat(Stats.CLEAN_ARMOR);
            LeveledCauldronBlock.decrementFluidLevel((BlockState)state, (World)world, (BlockPos)pos);
        }
        return ActionResult.SUCCESS;
    }

    private static boolean isUnderwater(World world, BlockPos pos) {
        FluidState fluidState = world.getFluidState(pos.up());
        return fluidState.isIn(FluidTags.WATER);
    }
}

