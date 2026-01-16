package net.noahsarch.deeperdark.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

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
                if (!(target instanceof CatEntity) && !(target instanceof OcelotEntity)) {
                    // Not looking at a wolf/dog, equip bone to head
                    ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
                    // Play sound effect
                    world.playSound(player, player.getX(), player.getY(), player.getZ(),
                            stack.isOf(Items.COD) ? net.minecraft.sound.SoundEvents.ENTITY_COD_FLOP :
                                    stack.isOf(Items.SALMON) ? net.minecraft.sound.SoundEvents.ENTITY_SALMON_FLOP :
                                            net.minecraft.sound.SoundEvents.ENTITY_TROPICAL_FISH_FLOP,
                            net.minecraft.sound.SoundCategory.PLAYERS, 1.0F, 1.75F);
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

