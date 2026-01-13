/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.network.Address
 *  net.minecraft.client.network.AddressResolver
 *  net.minecraft.client.network.AllowedAddressResolver
 *  net.minecraft.client.network.BlockListChecker
 *  net.minecraft.client.network.RedirectResolver
 *  net.minecraft.client.network.ServerAddress
 */
package net.minecraft.client.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AddressResolver;
import net.minecraft.client.network.BlockListChecker;
import net.minecraft.client.network.RedirectResolver;
import net.minecraft.client.network.ServerAddress;

@Environment(value=EnvType.CLIENT)
public class AllowedAddressResolver {
    public static final AllowedAddressResolver DEFAULT = new AllowedAddressResolver(AddressResolver.DEFAULT, RedirectResolver.createSrv(), BlockListChecker.create());
    private final AddressResolver addressResolver;
    private final RedirectResolver redirectResolver;
    private final BlockListChecker blockListChecker;

    @VisibleForTesting
    AllowedAddressResolver(AddressResolver addressResolver, RedirectResolver redirectResolver, BlockListChecker blockListChecker) {
        this.addressResolver = addressResolver;
        this.redirectResolver = redirectResolver;
        this.blockListChecker = blockListChecker;
    }

    public Optional<Address> resolve(ServerAddress address) {
        Optional<Address> optional = this.addressResolver.resolve(address);
        if (optional.isPresent() && !this.blockListChecker.isAllowed((Address)optional.get()) || !this.blockListChecker.isAllowed(address)) {
            return Optional.empty();
        }
        Optional optional2 = this.redirectResolver.lookupRedirect(address);
        if (optional2.isPresent()) {
            optional = this.addressResolver.resolve((ServerAddress)optional2.get()).filter(arg_0 -> ((BlockListChecker)this.blockListChecker).isAllowed(arg_0));
        }
        return optional;
    }
}

