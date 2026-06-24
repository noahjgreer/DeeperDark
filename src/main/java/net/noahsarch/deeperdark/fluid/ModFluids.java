package net.noahsarch.deeperdark.fluid;

import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.gameevent.GameEvent;
import net.noahsarch.deeperdark.Deeperdark;
import net.noahsarch.deeperdark.block.ModBlocks;

public class ModFluids {

    public static FlowingFluid MILK_STILL;
    public static FlowingFluid MILK_FLOWING;

    public static void initialize() {
        MILK_STILL = Registry.register(
                BuiltInRegistries.FLUID,
                Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "milk"),
                new MilkFluid.Still());
        MILK_FLOWING = Registry.register(
                BuiltInRegistries.FLUID,
                Identifier.fromNamespaceAndPath(Deeperdark.MOD_ID, "flowing_milk"),
                new MilkFluid.Flowing());

        registerCauldronInteractions();
    }

    private static void registerCauldronInteractions() {
        // Filling any cauldron with milk → switches to milk cauldron
        CauldronInteraction milkFill = (state, level, pos, player, hand, stack) -> {
            if (!level.isClientSide()) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
                player.awardStat(Stats.FILL_CAULDRON);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                level.setBlockAndUpdate(pos, ModBlocks.MILK_CAULDRON.defaultBlockState());
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            }
            return InteractionResult.SUCCESS;
        };

        CauldronInteractions.EMPTY.put(Items.MILK_BUCKET, milkFill);
        CauldronInteractions.WATER.put(Items.MILK_BUCKET, milkFill);
        CauldronInteractions.LAVA.put(Items.MILK_BUCKET, milkFill);
        CauldronInteractions.POWDER_SNOW.put(Items.MILK_BUCKET, milkFill);
    }
}
