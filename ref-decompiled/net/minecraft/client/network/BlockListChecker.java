/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Streams
 *  com.mojang.blocklist.BlockListSupplier
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.Address
 *  net.minecraft.client.network.BlockListChecker
 *  net.minecraft.client.network.ServerAddress
 */
package net.minecraft.client.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.mojang.blocklist.BlockListSupplier;
import java.util.Objects;
import java.util.ServiceLoader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
public interface BlockListChecker {
    public boolean isAllowed(Address var1);

    public boolean isAllowed(ServerAddress var1);

    public static BlockListChecker create() {
        ImmutableList immutableList = (ImmutableList)Streams.stream(ServiceLoader.load(BlockListSupplier.class)).map(BlockListSupplier::createBlockList).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
        return new /* Unavailable Anonymous Inner Class!! */;
    }
}

