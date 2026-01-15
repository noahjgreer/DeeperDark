package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Leashable {

    @Unique
    private Leashable.LeashData leashData;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    @Nullable
    public Leashable.LeashData getLeashData() {
        return this.leashData;
    }

    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.leashData = leashData;
    }

    @Inject(method = "writeCustomData", at = @At("TAIL"))
    private void writeLeashData(WriteView nbt, CallbackInfo ci) {
        this.writeLeashData(nbt, this.leashData);
    }

    @Inject(method = "readCustomData", at = @At("TAIL"))
    private void readLeashData(ReadView nbt, CallbackInfo ci) {
        this.readLeashData(nbt);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickLeash(CallbackInfo ci) {
        if (!((EntityAccessor)this).deeperdark$getWorld().isClient()) {
            Leashable.tickLeash((ServerWorld) ((EntityAccessor)this).deeperdark$getWorld(), (LivingEntity & Leashable) this);
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.LEAD) && this.canBeLeashedTo(player)) {
            if (!this.isLeashed()) {
                this.attachLeash(player, true);
                itemStack.decrement(1);
                return ActionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void deeperdark$boneToHead(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.BONE)) {
            // Only run on server
            if (!player.getWorld().isClient()) {
                // Raycast to see if looking at a wolf/dog
                Entity target = null;
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    double reach = serverPlayer.interactionManager.getReachDistance();
                    HitResult hit = serverPlayer.raycast(reach, 1.0F, false);
                    if (hit instanceof EntityHitResult entityHit) {
                        target = entityHit.getEntity();
                      }
                }
                if (!(target instanceof WolfEntity)) {
                    // Not looking at a wolf/dog, equip bone to head
                    ItemStack headStack = player.getEquippedStack(EquipmentSlot.HEAD);
                    player.equipStack(EquipmentSlot.HEAD, itemStack.copyWithCount(1));
                    if (!player.isCreative()) {
                        itemStack.decrement(1);
                        // If there was something in the head slot, give it back to the player
                        if (!headStack.isEmpty()) {
                            player.giveItemStack(headStack);
                        }
                    }
                    cir.setReturnValue(ActionResult.SUCCESS);
                }
            }
        }
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void deeperdark$boneToHead(World world, ItemStack stack, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        if (!world.isClient && stack.isOf(Items.BONE)) {
            // Raycast to see if looking at a wolf/dog
            double reach = 4.5D;
            Entity target = null;
            HitResult hit = this.raycast(reach, 1.0F, false);
            if (hit instanceof EntityHitResult entityHit) {
                target = entityHit.getEntity();
            }
            if (!(target instanceof WolfEntity)) {
                // Not looking at a wolf/dog, equip bone to head
                ItemStack headStack = this.getEquippedStack(EquipmentSlot.HEAD);
                this.equipStack(EquipmentSlot.HEAD, stack.copyWithCount(1));
                if (!this.isCreative()) {
                    stack.decrement(1);
                    if (!headStack.isEmpty()) {
                        this.giveItemStack(headStack);
                    }
                }
                cir.setReturnValue(TypedActionResult.success(stack, world.isClient));
            }
        }
    }
}
