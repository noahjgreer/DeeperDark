package net.minecraft.client.search;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class IdentifierSearchProvider implements SearchProvider {
   protected final Comparator lastIndexComparator;
   protected final IdentifierSearcher idSearcher;

   public IdentifierSearchProvider(Function identifiersGetter, List values) {
      ToIntFunction toIntFunction = Util.lastIndexGetter(values);
      this.lastIndexComparator = Comparator.comparingInt(toIntFunction);
      this.idSearcher = IdentifierSearcher.of(values, identifiersGetter);
   }

   public List findAll(String text) {
      int i = text.indexOf(58);
      return i == -1 ? this.search(text) : this.search(text.substring(0, i).trim(), text.substring(i + 1).trim());
   }

   protected List search(String text) {
      return this.idSearcher.searchPath(text);
   }

   protected List search(String namespace, String path) {
      List list = this.idSearcher.searchNamespace(namespace);
      List list2 = this.idSearcher.searchPath(path);
      return ImmutableList.copyOf(new IdentifierSearchableIterator(list.iterator(), list2.iterator(), this.lastIndexComparator));
   }
}
