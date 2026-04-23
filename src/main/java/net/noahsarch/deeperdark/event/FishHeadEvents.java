package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class FishHeadEvents {
    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!world.isClientSide() && (stack.getItem() == Items.COD || stack.getItem() == Items.SALMON || stack.getItem() == Items.TROPICAL_FISH && !(player.isShiftKeyDown()))) {
                // Raycast to see if looking at a cat/ocelot
                double reach = 4.5D;
                Entity target = null;
                HitResult hit = player.pick(reach, 1.0F, false);
                if (hit instanceof EntityHitResult entityHit) {
                    target = entityHit.getEntity();
                }
                if (!(target instanceof Cat) && !(target instanceof Ocelot)) {
                    // Not looking at a wolf/dog, equip bone to head
                    ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
                    // Play sound effect
                    world.playSound(player, player.getX(), player.getY(), player.getZ(),
                            stack.getItem() == Items.COD ? net.minecraft.sounds.SoundEvents.COD_FLOP :
                                    stack.getItem() == Items.SALMON ? net.minecraft.sounds.SoundEvents.SALMON_FLOP :
                                            net.minecraft.sounds.SoundEvents.TROPICAL_FISH_FLOP,
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.75F);
                    player.setItemSlot(EquipmentSlot.HEAD, stack.copyWithCount(1));
                    if (!player.isCreative()) {
                        stack.shrink(1);
                        if (!headStack.isEmpty()) {
                            player.addItem(headStack);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });
    }
}

