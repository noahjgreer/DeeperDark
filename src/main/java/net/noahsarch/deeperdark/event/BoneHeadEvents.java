package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;

public class BoneHeadEvents {
    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!world.isClientSide() && stack.getItem() == Items.BONE) {
                // Raycast to see if looking at a wolf/dog
                double reach = 4.5D;
                Entity target = null;
                HitResult hit = player.pick(reach, 1.0F, false);
                if (hit instanceof EntityHitResult entityHit) {
                    target = entityHit.getEntity();
                }
                if (!(target instanceof Wolf)) {
                    // Not looking at a wolf/dog, equip bone to head
                    ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
                    world.playSound(player, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ARMOR_EQUIP_LEATHER,
                            net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.25F);
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

