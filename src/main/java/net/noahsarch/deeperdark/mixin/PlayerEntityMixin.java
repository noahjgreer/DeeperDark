package net.noahsarch.deeperdark.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.noahsarch.deeperdark.duck.CollarHolder;
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
        if (!((EntityAccessor)this).deeperdark$getWorld().isClientSide()) {
            Leashable.tickLeash((ServerLevel) ((EntityAccessor)this).deeperdark$getWorld(), (LivingEntity & Leashable) this);
        }
    }

    @Override
    public double leashSnapDistance() {
        return 24.0;
    }

    @Override
    public double leashElasticDistance() {
        return 6.0;
    }

    @Override
    public void dropLeash() {
        // removeLeash() broadcasts an unlink packet to trackers (for third-party observers).
        // The custom PlayerLeashPacket tells the leashed player's own client (snap/auto-break case).
        Entity self = (Entity)(Object)this;
        if (!self.level().isClientSide() && self instanceof ServerPlayer sp) {
            ServerPlayNetworking.send(sp, new net.noahsarch.deeperdark.payload.PlayerLeashPacket(self.getId(), -1));
        }
        this.removeLeash();
    }

    @Unique
    private boolean deeperdark$hasSaddleEquipped() {
        Player self = (Player)(Object)this;
        if (self.getItemBySlot(EquipmentSlot.HEAD).is(Items.SADDLE)) return true;
        if (!(self instanceof CollarHolder holder)) return false;
        ItemStack collar = holder.deeperdark$getCollarItem();
        if (collar.isEmpty()) return false;
        ItemContainerContents contents = collar.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        return contents.nonEmptyItemCopyStream().anyMatch(s -> s.is(Items.SADDLE));
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 pos) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.LEAD) && this.canBeLeashed() && !this.isLeashed()) {
            this.setLeashedTo(player, true);
            if (!player.hasInfiniteMaterials()) {
                itemStack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        // Saddle riding: right-click with an empty hand to ride a player who has a saddle equipped
        if (hand == InteractionHand.MAIN_HAND
                && player.getItemInHand(hand).isEmpty()
                && !player.isPassenger()
                && !this.hasPassengers()
                && deeperdark$hasSaddleEquipped()) {
            player.startRiding(this, true);
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand, pos);
    }
}
