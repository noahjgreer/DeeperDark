/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
static class BlockListChecker.1
implements BlockListChecker {
    final /* synthetic */ ImmutableList field_33846;

    BlockListChecker.1(ImmutableList immutableList) {
        this.field_33846 = immutableList;
    }

    @Override
    public boolean isAllowed(Address address) {
        String string = address.getHostName();
        String string2 = address.getHostAddress();
        return this.field_33846.stream().noneMatch(predicate -> predicate.test(string) || predicate.test(string2));
    }

    @Override
    public boolean isAllowed(ServerAddress address) {
        String string = address.getAddress();
        return this.field_33846.stream().noneMatch(predicate -> predicate.test(string));
    }
}
