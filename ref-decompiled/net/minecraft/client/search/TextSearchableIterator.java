package net.minecraft.client.search;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import java.util.Comparator;
import java.util.Iterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TextSearchableIterator extends AbstractIterator {
   private final PeekingIterator idPathsIterator;
   private final PeekingIterator textsIterator;
   private final Comparator lastIndexComparator;

   public TextSearchableIterator(Iterator idPathsIterator, Iterator textsIterator, Comparator lastIndexComparator) {
      this.idPathsIterator = Iterators.peekingIterator(idPathsIterator);
      this.textsIterator = Iterators.peekingIterator(textsIterator);
      this.lastIndexComparator = lastIndexComparator;
   }

   protected Object computeNext() {
      boolean bl = !this.idPathsIterator.hasNext();
      boolean bl2 = !this.textsIterator.hasNext();
      if (bl && bl2) {
         return this.endOfData();
      } else if (bl) {
         return this.textsIterator.next();
      } else if (bl2) {
         return this.idPathsIterator.next();
      } else {
         int i = this.lastIndexComparator.compare(this.idPathsIterator.peek(), this.textsIterator.peek());
         if (i == 0) {
            this.textsIterator.next();
         }

         return i <= 0 ? this.idPathsIterator.next() : this.textsIterator.next();
      }
   }
}
