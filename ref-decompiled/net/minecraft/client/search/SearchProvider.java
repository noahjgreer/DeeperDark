package net.minecraft.client.search;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@FunctionalInterface
@Environment(EnvType.CLIENT)
public interface SearchProvider {
   static SearchProvider empty() {
      return (string) -> {
         return List.of();
      };
   }

   static SearchProvider plainText(List list, Function function) {
      if (list.isEmpty()) {
         return empty();
      } else {
         SuffixArray suffixArray = new SuffixArray();
         Iterator var3 = list.iterator();

         while(var3.hasNext()) {
            Object object = var3.next();
            ((Stream)function.apply(object)).forEach((string) -> {
               suffixArray.add(object, string.toLowerCase(Locale.ROOT));
            });
         }

         suffixArray.build();
         Objects.requireNonNull(suffixArray);
         return suffixArray::findAll;
      }
   }

   List findAll(String text);
}
