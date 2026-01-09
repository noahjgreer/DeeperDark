package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.types.templates.TaggedChoice;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class AddTrappedChestFix extends DataFix {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int field_29910 = 4096;
   private static final short field_29911 = 12;

   public AddTrappedChestFix(Schema schema, boolean bl) {
      super(schema, bl);
   }

   public TypeRewriteRule makeRule() {
      Type type = this.getOutputSchema().getType(TypeReferences.CHUNK);
      Type type2 = type.findFieldType("Level");
      Type type3 = type2.findFieldType("TileEntities");
      if (!(type3 instanceof List.ListType listType)) {
         throw new IllegalStateException("Tile entity type is not a list type.");
      } else {
         OpticFinder opticFinder = DSL.fieldFinder("TileEntities", listType);
         Type type4 = this.getInputSchema().getType(TypeReferences.CHUNK);
         OpticFinder opticFinder2 = type4.findField("Level");
         OpticFinder opticFinder3 = opticFinder2.type().findField("Sections");
         Type type5 = opticFinder3.type();
         if (!(type5 instanceof List.ListType)) {
            throw new IllegalStateException("Expecting sections to be a list.");
         } else {
            Type type6 = ((List.ListType)type5).getElement();
            OpticFinder opticFinder4 = DSL.typeFinder(type6);
            return TypeRewriteRule.seq((new ChoiceTypesFix(this.getOutputSchema(), "AddTrappedChestFix", TypeReferences.BLOCK_ENTITY)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", type4, (typed) -> {
               return typed.updateTyped(opticFinder2, (typedx) -> {
                  Optional optional = typedx.getOptionalTyped(opticFinder3);
                  if (optional.isEmpty()) {
                     return typedx;
                  } else {
                     java.util.List list = ((Typed)optional.get()).getAllTyped(opticFinder4);
                     IntSet intSet = new IntOpenHashSet();
                     Iterator var8 = list.iterator();

                     while(true) {
                        ListFixer listFixer;
                        do {
                           if (!var8.hasNext()) {
                              Dynamic dynamic = (Dynamic)typedx.get(DSL.remainderFinder());
                              int k = dynamic.get("xPos").asInt(0);
                              int l = dynamic.get("zPos").asInt(0);
                              TaggedChoice.TaggedChoiceType taggedChoiceType = this.getInputSchema().findChoiceType(TypeReferences.BLOCK_ENTITY);
                              return typedx.updateTyped(opticFinder, (typed) -> {
                                 return typed.updateTyped(taggedChoiceType.finder(), (typedx) -> {
                                    Dynamic dynamic = (Dynamic)typedx.getOrCreate(DSL.remainderFinder());
                                    int kx = dynamic.get("x").asInt(0) - (k << 4);
                                    int lx = dynamic.get("y").asInt(0);
                                    int m = dynamic.get("z").asInt(0) - (l << 4);
                                    return intSet.contains(LeavesFix.packLocalPos(kx, lx, m)) ? typedx.update(taggedChoiceType.finder(), (pair) -> {
                                       return pair.mapFirst((string) -> {
                                          if (!Objects.equals(string, "minecraft:chest")) {
                                             LOGGER.warn("Block Entity was expected to be a chest");
                                          }

                                          return "minecraft:trapped_chest";
                                       });
                                    }) : typedx;
                                 });
                              });
                           }

                           Typed typed2 = (Typed)var8.next();
                           listFixer = new ListFixer(typed2, this.getInputSchema());
                        } while(listFixer.isFixed());

                        for(int i = 0; i < 4096; ++i) {
                           int j = listFixer.blockStateAt(i);
                           if (listFixer.isTarget(j)) {
                              intSet.add(listFixer.getY() << 12 | i);
                           }
                        }
                     }
                  }
               });
            }));
         }
      }
   }

   public static final class ListFixer extends LeavesFix.ListFixer {
      @Nullable
      private IntSet targets;

      public ListFixer(Typed typed, Schema schema) {
         super(typed, schema);
      }

      protected boolean computeIsFixed() {
         this.targets = new IntOpenHashSet();

         for(int i = 0; i < this.properties.size(); ++i) {
            Dynamic dynamic = (Dynamic)this.properties.get(i);
            String string = dynamic.get("Name").asString("");
            if (Objects.equals(string, "minecraft:trapped_chest")) {
               this.targets.add(i);
            }
         }

         return this.targets.isEmpty();
      }

      public boolean isTarget(int index) {
         return this.targets.contains(index);
      }
   }
}
