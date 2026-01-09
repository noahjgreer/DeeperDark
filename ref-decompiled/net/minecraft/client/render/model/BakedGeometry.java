package net.minecraft.client.render.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class BakedGeometry {
   public static final BakedGeometry EMPTY = new BakedGeometry(List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
   private final List allQuads;
   private final List sidelessQuads;
   private final List northQuads;
   private final List southQuads;
   private final List eastQuads;
   private final List westQuads;
   private final List upQuads;
   private final List downQuads;

   BakedGeometry(List allQuads, List sidelessQuads, List northQuads, List southQuads, List eastQuads, List westQuads, List upQuads, List downQuads) {
      this.allQuads = allQuads;
      this.sidelessQuads = sidelessQuads;
      this.northQuads = northQuads;
      this.southQuads = southQuads;
      this.eastQuads = eastQuads;
      this.westQuads = westQuads;
      this.upQuads = upQuads;
      this.downQuads = downQuads;
   }

   public List getQuads(@Nullable Direction side) {
      byte var3 = 0;
      List var10000;
      switch (side.enumSwitch<invokedynamic>(side, var3)) {
         case -1:
            var10000 = this.sidelessQuads;
            break;
         case 0:
            var10000 = this.northQuads;
            break;
         case 1:
            var10000 = this.southQuads;
            break;
         case 2:
            var10000 = this.eastQuads;
            break;
         case 3:
            var10000 = this.westQuads;
            break;
         case 4:
            var10000 = this.upQuads;
            break;
         case 5:
            var10000 = this.downQuads;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public List getAllQuads() {
      return this.allQuads;
   }

   @Environment(EnvType.CLIENT)
   public static class Builder {
      private final ImmutableList.Builder sidelessQuads = ImmutableList.builder();
      private final Multimap sidedQuads = ArrayListMultimap.create();

      public Builder add(Direction side, BakedQuad quad) {
         this.sidedQuads.put(side, quad);
         return this;
      }

      public Builder add(BakedQuad quad) {
         this.sidelessQuads.add(quad);
         return this;
      }

      private static BakedGeometry buildFromList(List quads, int sidelessCount, int northCount, int southCount, int eastCount, int westCount, int upCount, int downCount) {
         int i = 0;
         List list = quads.subList(i, i += sidelessCount);
         List list2 = quads.subList(i, i += northCount);
         List list3 = quads.subList(i, i += southCount);
         List list4 = quads.subList(i, i += eastCount);
         List list5 = quads.subList(i, i += westCount);
         List list6 = quads.subList(i, i += upCount);
         List list7 = quads.subList(i, i + downCount);
         return new BakedGeometry(quads, list, list2, list3, list4, list5, list6, list7);
      }

      public BakedGeometry build() {
         ImmutableList immutableList = this.sidelessQuads.build();
         if (this.sidedQuads.isEmpty()) {
            return immutableList.isEmpty() ? BakedGeometry.EMPTY : new BakedGeometry(immutableList, immutableList, List.of(), List.of(), List.of(), List.of(), List.of(), List.of());
         } else {
            ImmutableList.Builder builder = ImmutableList.builder();
            builder.addAll(immutableList);
            Collection collection = this.sidedQuads.get(Direction.NORTH);
            builder.addAll(collection);
            Collection collection2 = this.sidedQuads.get(Direction.SOUTH);
            builder.addAll(collection2);
            Collection collection3 = this.sidedQuads.get(Direction.EAST);
            builder.addAll(collection3);
            Collection collection4 = this.sidedQuads.get(Direction.WEST);
            builder.addAll(collection4);
            Collection collection5 = this.sidedQuads.get(Direction.UP);
            builder.addAll(collection5);
            Collection collection6 = this.sidedQuads.get(Direction.DOWN);
            builder.addAll(collection6);
            return buildFromList(builder.build(), immutableList.size(), collection.size(), collection2.size(), collection3.size(), collection4.size(), collection5.size(), collection6.size());
         }
      }
   }
}
