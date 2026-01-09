package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.Objects;
import java.util.function.Supplier;

/** @deprecated */
@Deprecated
public class Lazy {
   private final Supplier supplier;

   public Lazy(Supplier delegate) {
      Objects.requireNonNull(delegate);
      this.supplier = Suppliers.memoize(delegate::get);
   }

   public Object get() {
      return this.supplier.get();
   }
}
