package net.noahsarch.deeperdark.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.noahsarch.deeperdark.event.ItemMagnetHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "tick", at = @At("TAIL"))
    private void deeperdark$checkSaddleAndDismount(CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (!self.level().isClientSide() && self.isVehicle() && !deeperdark$hasSaddleEquipped()) {
            self.ejectPassengers();
        }
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void deeperdark$preventAttackingOwnRider(Entity target, CallbackInfo ci) {
        if (target.getVehicle() == (Entity)(Object)this) {
            ci.cancel();
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
        Entity self = (Entity)(Object)this;
        if (!self.level().isClientSide()) {
            if (self instanceof ServerPlayer sp) {
                ServerPlayNetworking.send(sp, new net.noahsarch.deeperdark.payload.PlayerLeashPacket(self.getId(), -1));
            }
            // Drop the lead item at the leashed player's feet, matching vanilla mob behaviour.
            if (self.level() instanceof ServerLevel serverLevel) {
                self.spawnAtLocation(serverLevel, Items.LEAD);
            }
        }
        this.removeLeash();
    }

    @Override
    public Vec3 getLeashOffset() {
        Player self = (Player)(Object)this;
        return new Vec3(0.0, self.getEyeHeight() - 0.25, self.getBbWidth() * 0.4F);
    }

    @Unique
    private void deeperdark$snapLeashForRiding(Player rider) {
        Leashable self = (Leashable)(Object)this;
        // Vehicle is leashed to the rider
        if (self.isLeashed() && self.getLeashHolder() == rider) {
            self.dropLeash();
            return;
        }
        // Rider is leashed to the vehicle
        if (rider instanceof Leashable riderLeashable
                && riderLeashable.isLeashed()
                && riderLeashable.getLeashHolder() == (Entity)(Object)this) {
            riderLeashable.dropLeash();
        }
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

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;Z)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    private void deeperdark$trackDroppedItem(ItemStack stack, boolean throwRandomly, CallbackInfoReturnable<ItemEntity> cir) {
        ItemEntity dropped = cir.getReturnValue();
        if (dropped != null && !((Entity)(Object)this).level().isClientSide()) {
            ItemMagnetHandler.markDroppedByPlayer(dropped.getUUID(), ((Entity)(Object)this).getUUID());
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 pos) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.is(Items.LEAD) && this.canBeLeashed() && !this.isLeashed()) {
            if (!(this instanceof CollarHolder ch) || ch.deeperdark$getCollarItem().isEmpty()) {
                return InteractionResult.PASS;
            }
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
                && !this.isVehicle()
                && deeperdark$hasSaddleEquipped()
                && player.startRiding(this, true, true)) {
            // Snap any leash between the rider and the vehicle — riding and leashing the same
            // player creates an irrecoverable desync, so drop the lead on the ground.
            deeperdark$snapLeashForRiding(player);
            // The tracker sends SetPassengers to observers but NOT to the vehicle player's own client
            if ((Object)this instanceof ServerPlayer vehiclePlayer) {
                vehiclePlayer.connection.send(new ClientboundSetPassengersPacket((Entity)(Object)this));
            }
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand, pos);
    }
}
