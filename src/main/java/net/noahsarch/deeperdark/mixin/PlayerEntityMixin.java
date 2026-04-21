package net.noahsarch.deeperdark.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.duck.EntityAccessor;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity implements Leashable {

    @Unique
    private Leashable.LeashData leashData;

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeLeashData(ValueOutput nbt, CallbackInfo ci) {
        this.writeLeashData(nbt, this.leashData);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readLeashData(ValueInput nbt, CallbackInfo ci) {
        this.readLeashData(nbt);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickLeash(CallbackInfo ci) {
        if (!((EntityAccessor)this).deeperdark$getWorld().isClient()) {
            Leashable.tickLeash((ServerLevel) ((EntityAccessor)this).deeperdark$getWorld(), (LivingEntity & Leashable) this);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        if (itemStack.isOf(Items.LEAD) && this.canBeLeashedTo(player)) {
            if (!this.isLeashed()) {
                this.attachLeash(player, true);
                itemStack.decrement(1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.interact(player, hand);
    }
}
