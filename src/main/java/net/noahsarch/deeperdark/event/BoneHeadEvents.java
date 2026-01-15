package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BoneHeadEvents {
    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            if (!world.isClient() && stack.isOf(Items.BONE)) {
                // Raycast to see if looking at a wolf/dog
                double reach = 4.5D;
                Entity target = null;
                HitResult hit = player.raycast(reach, 1.0F, false);
                if (hit instanceof EntityHitResult entityHit) {
                    target = entityHit.getEntity();
                }
                if (!(target instanceof WolfEntity)) {
                    // Not looking at a wolf/dog, equip bone to head
                    ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
                    player.equipStack(EquipmentSlot.HEAD, stack.copyWithCount(1));
                    if (!player.isCreative()) {
                        stack.decrement(1);
                        if (!headStack.isEmpty()) {
                            player.giveItemStack(headStack);
                        }
                    }
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }
}

