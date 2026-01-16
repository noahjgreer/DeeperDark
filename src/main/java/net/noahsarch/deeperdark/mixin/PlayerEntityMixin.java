package net.noahsarch.deeperdark.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
