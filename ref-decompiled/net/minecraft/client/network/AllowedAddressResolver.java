package net.minecraft.client.network;

import com.google.common.annotations.VisibleForTesting;
import java.util.Objects;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AllowedAddressResolver {
   public static final AllowedAddressResolver DEFAULT;
   private final AddressResolver addressResolver;
   private final RedirectResolver redirectResolver;
   private final BlockListChecker blockListChecker;

   @VisibleForTesting
   AllowedAddressResolver(AddressResolver addressResolver, RedirectResolver redirectResolver, BlockListChecker blockListChecker) {
      this.addressResolver = addressResolver;
      this.redirectResolver = redirectResolver;
      this.blockListChecker = blockListChecker;
   }

   public Optional resolve(ServerAddress address) {
      Optional optional = this.addressResolver.resolve(address);
      if ((!optional.isPresent() || this.blockListChecker.isAllowed((Address)optional.get())) && this.blockListChecker.isAllowed(address)) {
         Optional optional2 = this.redirectResolver.lookupRedirect(address);
         if (optional2.isPresent()) {
            Optional var10000 = this.addressResolver.resolve((ServerAddress)optional2.get());
            BlockListChecker var10001 = this.blockListChecker;
            Objects.requireNonNull(var10001);
            optional = var10000.filter(var10001::isAllowed);
         }

         return optional;
      } else {
         return Optional.empty();
      }
   }

   static {
      DEFAULT = new AllowedAddressResolver(AddressResolver.DEFAULT, RedirectResolver.createSrv(), BlockListChecker.create());
   }
}
