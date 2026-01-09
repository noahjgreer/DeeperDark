package net.minecraft.resource.metadata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.util.dynamic.Codecs;

public class BlockEntry {
   public static final Codec CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(Codecs.REGULAR_EXPRESSION.optionalFieldOf("namespace").forGetter((entry) -> {
         return entry.namespace;
      }), Codecs.REGULAR_EXPRESSION.optionalFieldOf("path").forGetter((entry) -> {
         return entry.path;
      })).apply(instance, BlockEntry::new);
   });
   private final Optional namespace;
   private final Predicate namespacePredicate;
   private final Optional path;
   private final Predicate pathPredicate;
   private final Predicate identifierPredicate;

   private BlockEntry(Optional namespace, Optional path) {
      this.namespace = namespace;
      this.namespacePredicate = (Predicate)namespace.map(Pattern::asPredicate).orElse((namespace_) -> {
         return true;
      });
      this.path = path;
      this.pathPredicate = (Predicate)path.map(Pattern::asPredicate).orElse((path_) -> {
         return true;
      });
      this.identifierPredicate = (id) -> {
         return this.namespacePredicate.test(id.getNamespace()) && this.pathPredicate.test(id.getPath());
      };
   }

   public Predicate getNamespacePredicate() {
      return this.namespacePredicate;
   }

   public Predicate getPathPredicate() {
      return this.pathPredicate;
   }

   public Predicate getIdentifierPredicate() {
      return this.identifierPredicate;
   }
}
