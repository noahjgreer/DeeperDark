package net.minecraft.resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.util.Identifier;

public interface ResourceManager extends ResourceFactory {
   Set getAllNamespaces();

   List getAllResources(Identifier id);

   Map findResources(String startingPath, Predicate allowedPathPredicate);

   Map findAllResources(String startingPath, Predicate allowedPathPredicate);

   Stream streamResourcePacks();

   public static enum Empty implements ResourceManager {
      INSTANCE;

      public Set getAllNamespaces() {
         return Set.of();
      }

      public Optional getResource(Identifier identifier) {
         return Optional.empty();
      }

      public List getAllResources(Identifier id) {
         return List.of();
      }

      public Map findResources(String startingPath, Predicate allowedPathPredicate) {
         return Map.of();
      }

      public Map findAllResources(String startingPath, Predicate allowedPathPredicate) {
         return Map.of();
      }

      public Stream streamResourcePacks() {
         return Stream.of();
      }

      // $FF: synthetic method
      private static Empty[] method_36585() {
         return new Empty[]{INSTANCE};
      }
   }
}
