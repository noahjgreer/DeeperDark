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
            ItemStack stack = player.getStackInHand(hand);
            if (!world.isClient() && (stack.isOf(Items.COD) || stack.isOf(Items.SALMON) || stack.isOf(Items.TROPICAL_FISH) && !(player.isSneaking()))) {
                // Raycast to see if looking at a cat/ocelot
                double reach = 4.5D;
                Entity target = null;
                HitResult hit = player.raycast(reach, 1.0F, false);
                if (hit instanceof EntityHitResult entityHit) {
                    target = entityHit.getEntity();
                }
                if (!(target instanceof Cat) && !(target instanceof Ocelot)) {
                    // Not looking at a wolf/dog, equip bone to head
                    ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
                    // Play sound effect
                    world.playSound(player, player.getX(), player.getY(), player.getZ(),
                            stack.isOf(Items.COD) ? net.minecraft.sounds.SoundEvents.ENTITY_COD_FLOP :
                                    stack.isOf(Items.SALMON) ? net.minecraft.sounds.SoundEvents.ENTITY_SALMON_FLOP :
                                            net.minecraft.sounds.SoundEvents.ENTITY_TROPICAL_FISH_FLOP,
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.75F);
                    player.equipStack(EquipmentSlot.HEAD, stack.copyWithCount(1));
                    if (!player.isCreative()) {
                        stack.decrement(1);
                        if (!headStack.isEmpty()) {
                            player.giveItemStack(headStack);
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.PASS;
        });
    }
}

