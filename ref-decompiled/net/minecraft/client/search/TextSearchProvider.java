package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextSearchProvider extends IdentifierSearchProvider {
   private final SearchProvider textSearcher;

   public TextSearchProvider(Function textsGetter, Function identifiersGetter, List values) {
      super(identifiersGetter, values);
      this.textSearcher = SearchProvider.plainText(values, textsGetter);
   }

   protected List search(String text) {
      return this.textSearcher.findAll(text);
   }

   protected List search(String namespace, String path) {
      List list = this.idSearcher.searchNamespace(namespace);
      List list2 = this.idSearcher.searchPath(path);
      List list3 = this.textSearcher.findAll(path);
      Iterator iterator = new TextSearchableIterator(list2.iterator(), list3.iterator(), this.lastIndexComparator);
      return ImmutableList.copyOf(new IdentifierSearchableIterator(list.iterator(), iterator, this.lastIndexComparator));
   }
}
