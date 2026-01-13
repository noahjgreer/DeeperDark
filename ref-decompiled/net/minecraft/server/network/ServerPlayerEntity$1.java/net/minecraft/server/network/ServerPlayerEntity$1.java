/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.hash.HashCode
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.server.network;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.hash.HashCode;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import net.minecraft.component.Component;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCursorItemS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.sync.TrackedSlot;
import net.minecraft.util.dynamic.HashCodeOps;

class ServerPlayerEntity.1
implements ScreenHandlerSyncHandler {
    private final LoadingCache<Component<?>, Integer> componentHashCache = CacheBuilder.newBuilder().maximumSize(256L).build(new CacheLoader<Component<?>, Integer>(){
        private final DynamicOps<HashCode> hashOps;
        {
            this.hashOps = ServerPlayerEntity.this.getRegistryManager().getOps(HashCodeOps.INSTANCE);
        }

        public Integer load(Component<?> component) {
            return ((HashCode)component.encode(this.hashOps).getOrThrow(error -> new IllegalArgumentException("Failed to hash " + String.valueOf(component) + ": " + error))).asInt();
        }

        public /* synthetic */ Object load(Object component) throws Exception {
            return this.load((Component)component);
        }
    });

    ServerPlayerEntity.1() {
    }

    @Override
    public void updateState(ScreenHandler handler, List<ItemStack> stacks, ItemStack cursorStack, int[] properties) {
        ServerPlayerEntity.this.networkHandler.sendPacket(new InventoryS2CPacket(handler.syncId, handler.nextRevision(), stacks, cursorStack));
        for (int i = 0; i < properties.length; ++i) {
            this.sendPropertyUpdate(handler, i, properties[i]);
        }
    }

    @Override
    public void updateSlot(ScreenHandler handler, int slot, ItemStack stack) {
        ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(handler.syncId, handler.nextRevision(), slot, stack));
    }

    @Override
    public void updateCursorStack(ScreenHandler handler, ItemStack stack) {
        ServerPlayerEntity.this.networkHandler.sendPacket(new SetCursorItemS2CPacket(stack));
    }

    @Override
    public void updateProperty(ScreenHandler handler, int property, int value) {
        this.sendPropertyUpdate(handler, property, value);
    }

    private void sendPropertyUpdate(ScreenHandler handler, int property, int value) {
        ServerPlayerEntity.this.networkHandler.sendPacket(new ScreenHandlerPropertyUpdateS2CPacket(handler.syncId, property, value));
    }

    @Override
    public TrackedSlot createTrackedSlot() {
        return new TrackedSlot.Impl(arg_0 -> this.componentHashCache.getUnchecked(arg_0));
    }
}
