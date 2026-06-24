package net.noahsarch.deeperdark.mixin;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.noahsarch.deeperdark.fluid.ModFluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes the milk bucket place milk fluid when right-clicked aimed at a block surface,
 * matching the behaviour of water/lava buckets.
 */
@Mixin(Item.class)
public class MilkBucketPlacementMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void deeperdark$placeMilk(Level level, Player player, InteractionHand hand,
                                      CallbackInfoReturnable<InteractionResult> cir) {
        if ((Object) this != Items.MILK_BUCKET) return;

        ItemStack stack = player.getItemInHand(hand);
        BlockHitResult hit = Item.getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

        if (hit.getType() != HitResult.Type.BLOCK) return;

        BlockPos clickedPos = hit.getBlockPos();
        Direction face = hit.getDirection();
        BlockPos placePos = clickedPos.relative(face);

        if (!level.mayInteract(player, clickedPos)) return;
        if (!player.mayUseItemAt(placePos, face, stack)) return;

        BlockState target = level.getBlockState(placePos);
        if (!target.canBeReplaced(ModFluids.MILK_STILL)) return;

        if (!level.isClientSide()) {
            level.setBlock(placePos, ModFluids.MILK_STILL.defaultFluidState().createLegacyBlock(), 11);
            level.playSound(null, placePos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.FLUID_PLACE, placePos);
            if (player instanceof ServerPlayer sp) {
                CriteriaTriggers.PLACED_BLOCK.trigger(sp, placePos, stack);
            }
            player.awardStat(Stats.ITEM_USED.get(Items.MILK_BUCKET));
            ItemStack result = ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET));
            player.setItemInHand(hand, result);
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
