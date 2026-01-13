/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

class ServerPlayNetworkHandler.1
implements PlayerInteractEntityC2SPacket.Handler {
    final /* synthetic */ ServerWorld field_39991;
    final /* synthetic */ Entity field_28962;

    ServerPlayNetworkHandler.1() {
        this.field_39991 = serverWorld;
        this.field_28962 = entity;
    }

    private void processInteract(Hand hand, ServerPlayNetworkHandler.Interaction action) {
        ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(hand);
        if (!itemStack.isItemEnabled(this.field_39991.getEnabledFeatures())) {
            return;
        }
        ItemStack itemStack2 = itemStack.copy();
        ActionResult actionResult = action.run(ServerPlayNetworkHandler.this.player, this.field_28962, hand);
        if (actionResult instanceof ActionResult.Success) {
            ActionResult.Success success = (ActionResult.Success)actionResult;
            ItemStack itemStack3 = success.shouldIncrementStat() ? itemStack2 : ItemStack.EMPTY;
            Criteria.PLAYER_INTERACTED_WITH_ENTITY.trigger(ServerPlayNetworkHandler.this.player, itemStack3, this.field_28962);
            if (success.swingSource() == ActionResult.SwingSource.SERVER) {
                ServerPlayNetworkHandler.this.player.swingHand(hand, true);
            }
        }
    }

    @Override
    public void interact(Hand hand) {
        this.processInteract(hand, PlayerEntity::interact);
    }

    @Override
    public void interactAt(Hand hand, Vec3d pos) {
        this.processInteract(hand, (player, entity, handx) -> entity.interactAt(player, pos, handx));
    }

    @Override
    public void attack() {
        PersistentProjectileEntity persistentProjectileEntity;
        if (this.field_28962 instanceof ItemEntity || this.field_28962 instanceof ExperienceOrbEntity || this.field_28962 == ServerPlayNetworkHandler.this.player || this.field_28962 instanceof PersistentProjectileEntity && !(persistentProjectileEntity = (PersistentProjectileEntity)this.field_28962).isAttackable()) {
            ServerPlayNetworkHandler.this.disconnect(Text.translatable("multiplayer.disconnect.invalid_entity_attacked"));
            LOGGER.warn("Player {} tried to attack an invalid entity", (Object)ServerPlayNetworkHandler.this.player.getStringifiedName());
            return;
        }
        ItemStack itemStack = ServerPlayNetworkHandler.this.player.getStackInHand(Hand.MAIN_HAND);
        if (!itemStack.isItemEnabled(this.field_39991.getEnabledFeatures())) {
            return;
        }
        if (ServerPlayNetworkHandler.this.player.isBelowMinimumAttackCharge(itemStack, 5)) {
            return;
        }
        ServerPlayNetworkHandler.this.player.attack(this.field_28962);
    }
}
