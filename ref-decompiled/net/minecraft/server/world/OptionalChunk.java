package net.minecraft.server.world;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

public interface OptionalChunk {
   static OptionalChunk of(Object chunk) {
      return new ActualChunk(chunk);
   }

   static OptionalChunk of(String error) {
      return of(() -> {
         return error;
      });
   }

   static OptionalChunk of(Supplier error) {
      return new LoadFailure(error);
   }

   boolean isPresent();

   @Nullable
   Object orElse(@Nullable Object other);

   @Nullable
   static Object orElse(OptionalChunk optionalChunk, @Nullable Object other) {
      Object object = optionalChunk.orElse((Object)null);
      return object != null ? object : other;
   }

   @Nullable
   String getError();

   OptionalChunk ifPresent(Consumer callback);

   OptionalChunk map(Function mapper);

   Object orElseThrow(Supplier exceptionSupplier) throws Throwable;

   public static record ActualChunk(Object value) implements OptionalChunk {
      public ActualChunk(Object object) {
         this.value = object;
      }

      public boolean isPresent() {
         return true;
      }

      public Object orElse(@Nullable Object other) {
         return this.value;
      }

      @Nullable
      public String getError() {
         return null;
      }

      public OptionalChunk ifPresent(Consumer callback) {
         callback.accept(this.value);
         return this;
      }

      public OptionalChunk map(Function mapper) {
         return new ActualChunk(mapper.apply(this.value));
      }

      public Object orElseThrow(Supplier exceptionSupplier) throws Throwable {
         return this.value;
      }

      public Object value() {
         return this.value;
      }
   }

   public static record LoadFailure(Supplier error) implements OptionalChunk {
      public LoadFailure(Supplier supplier) {
         this.error = supplier;
      }

      public boolean isPresent() {
         return false;
      }

      @Nullable
      public Object orElse(@Nullable Object other) {
         return other;
      }

      public String getError() {
         return (String)this.error.get();
      }

      public OptionalChunk ifPresent(Consumer callback) {
         return this;
      }

      public OptionalChunk map(Function mapper) {
         return new LoadFailure(this.error);
      }

      public Object orElseThrow(Supplier exceptionSupplier) throws Throwable {
         throw (Throwable)exceptionSupplier.get();
      }

      public Supplier error() {
         return this.error;
      }
   }
}
