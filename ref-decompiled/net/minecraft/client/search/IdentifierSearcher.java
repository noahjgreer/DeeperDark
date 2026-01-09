package net.minecraft.client.search;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface IdentifierSearcher {
   static IdentifierSearcher of() {
      return new IdentifierSearcher() {
         public List searchNamespace(String namespace) {
            return List.of();
         }

         public List searchPath(String path) {
            return List.of();
         }
      };
   }

   static IdentifierSearcher of(List values, Function identifiersGetter) {
      if (values.isEmpty()) {
         return of();
      } else {
         final SuffixArray suffixArray = new SuffixArray();
         final SuffixArray suffixArray2 = new SuffixArray();
         Iterator var4 = values.iterator();

         while(var4.hasNext()) {
            Object object = var4.next();
            ((Stream)identifiersGetter.apply(object)).forEach((id) -> {
               suffixArray.add(object, id.getNamespace().toLowerCase(Locale.ROOT));
               suffixArray2.add(object, id.getPath().toLowerCase(Locale.ROOT));
            });
         }

         suffixArray.build();
         suffixArray2.build();
         return new IdentifierSearcher() {
            public List searchNamespace(String namespace) {
               return suffixArray.findAll(namespace);
            }

            public List searchPath(String path) {
               return suffixArray2.findAll(path);
            }
         };
      }
   }

   List searchNamespace(String namespace);

   List searchPath(String path);
}
