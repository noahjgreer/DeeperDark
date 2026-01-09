package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class StructurePiecesCollector implements StructurePiecesHolder {
   private final List pieces = Lists.newArrayList();

   public void addPiece(StructurePiece piece) {
      this.pieces.add(piece);
   }

   @Nullable
   public StructurePiece getIntersecting(BlockBox box) {
      return StructurePiece.firstIntersecting(this.pieces, box);
   }

   /** @deprecated */
   @Deprecated
   public void shift(int y) {
      Iterator var2 = this.pieces.iterator();

      while(var2.hasNext()) {
         StructurePiece structurePiece = (StructurePiece)var2.next();
         structurePiece.translate(0, y, 0);
      }

   }

   /** @deprecated */
   @Deprecated
   public int shiftInto(int topY, int bottomY, Random random, int topPenalty) {
      int i = topY - topPenalty;
      BlockBox blockBox = this.getBoundingBox();
      int j = blockBox.getBlockCountY() + bottomY + 1;
      if (j < i) {
         j += random.nextInt(i - j);
      }

      int k = j - blockBox.getMaxY();
      this.shift(k);
      return k;
   }

   /** @deprecated */
   public void shiftInto(Random random, int baseY, int topY) {
      BlockBox blockBox = this.getBoundingBox();
      int i = topY - baseY + 1 - blockBox.getBlockCountY();
      int j;
      if (i > 1) {
         j = baseY + random.nextInt(i);
      } else {
         j = baseY;
      }

      int k = j - blockBox.getMinY();
      this.shift(k);
   }

   public StructurePiecesList toList() {
      return new StructurePiecesList(this.pieces);
   }

   public void clear() {
      this.pieces.clear();
   }

   public boolean isEmpty() {
      return this.pieces.isEmpty();
   }

   public BlockBox getBoundingBox() {
      return StructurePiece.boundingBox(this.pieces.stream());
   }
}
