package net.minecraft.network.message;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

public interface SignedCommandArguments {
   SignedCommandArguments EMPTY = new SignedCommandArguments() {
      @Nullable
      public SignedMessage getMessage(String argumentName) {
         return null;
      }
   };

   @Nullable
   SignedMessage getMessage(String argumentName);

   public static record Impl(Map arguments) implements SignedCommandArguments {
      public Impl(Map map) {
         this.arguments = map;
      }

      @Nullable
      public SignedMessage getMessage(String argumentName) {
         return (SignedMessage)this.arguments.get(argumentName);
      }

      public Map arguments() {
         return this.arguments;
      }
   }
}
