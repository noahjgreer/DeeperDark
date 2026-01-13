/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.server.network;

import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;

static class ServerPlayNetworkHandler.2 {
    static final /* synthetic */ int[] field_28964;
    static final /* synthetic */ int[] field_28965;
    static final /* synthetic */ int[] field_28966;
    static final /* synthetic */ int[] field_28967;

    static {
        field_28967 = new int[ClientStatusC2SPacket.Mode.values().length];
        try {
            ServerPlayNetworkHandler.2.field_28967[ClientStatusC2SPacket.Mode.PERFORM_RESPAWN.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28967[ClientStatusC2SPacket.Mode.REQUEST_STATS.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_28966 = new int[ClientCommandC2SPacket.Mode.values().length];
        try {
            ServerPlayNetworkHandler.2.field_28966[ClientCommandC2SPacket.Mode.START_SPRINTING.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28966[ClientCommandC2SPacket.Mode.STOP_SPRINTING.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28966[ClientCommandC2SPacket.Mode.STOP_SLEEPING.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28966[ClientCommandC2SPacket.Mode.START_RIDING_JUMP.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28966[ClientCommandC2SPacket.Mode.STOP_RIDING_JUMP.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28966[ClientCommandC2SPacket.Mode.OPEN_INVENTORY.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28966[ClientCommandC2SPacket.Mode.START_FALL_FLYING.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_28965 = new int[PlayerActionC2SPacket.Action.values().length];
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.STAB.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.DROP_ITEM.ordinal()] = 3;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.DROP_ALL_ITEMS.ordinal()] = 4;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.RELEASE_USE_ITEM.ordinal()] = 5;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.START_DESTROY_BLOCK.ordinal()] = 6;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK.ordinal()] = 7;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28965[PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK.ordinal()] = 8;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        field_28964 = new int[CommandBlockBlockEntity.Type.values().length];
        try {
            ServerPlayNetworkHandler.2.field_28964[CommandBlockBlockEntity.Type.SEQUENCE.ordinal()] = 1;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
        try {
            ServerPlayNetworkHandler.2.field_28964[CommandBlockBlockEntity.Type.AUTO.ordinal()] = 2;
        }
        catch (NoSuchFieldError noSuchFieldError) {
            // empty catch block
        }
    }
}
