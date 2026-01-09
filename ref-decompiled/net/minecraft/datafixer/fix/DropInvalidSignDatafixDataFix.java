package net.minecraft.datafixer.fix;

import com.google.common.collect.Streams;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Util;

public class DropInvalidSignDatafixDataFix extends DataFix {
   private final String field_55637;

   public DropInvalidSignDatafixDataFix(Schema outputSchema, String name) {
      super(outputSchema, false);
      this.field_55637 = name;
   }

   private Dynamic dropInvalidDatafixData(Dynamic dynamic) {
      dynamic = dynamic.update("front_text", DropInvalidSignDatafixDataFix::dropInvalidDatafixDataOnSide);
      dynamic = dynamic.update("back_text", DropInvalidSignDatafixDataFix::dropInvalidDatafixDataOnSide);

      String string;
      for(Iterator var2 = UpdateSignTextFormatFix.field_55629.iterator(); var2.hasNext(); dynamic = dynamic.remove(string)) {
         string = (String)var2.next();
      }

      return dynamic;
   }

   private static Dynamic dropInvalidDatafixDataOnSide(Dynamic textData) {
      Optional optional = textData.get("filtered_messages").asStreamOpt().result();
      if (optional.isEmpty()) {
         return textData;
      } else {
         Dynamic dynamic = TextFixes.empty(textData.getOps());
         List list = ((Stream)textData.get("messages").asStreamOpt().result().orElse(Stream.of())).toList();
         List list2 = Streams.mapWithIndex((Stream)optional.get(), (message, index) -> {
            Dynamic dynamic2 = index < (long)list.size() ? (Dynamic)list.get((int)index) : dynamic;
            return message.equals(dynamic) ? dynamic2 : message;
         }).toList();
         return list2.equals(list) ? textData.remove("filtered_messages") : textData.set("filtered_messages", textData.createList(list2.stream()));
      }
   }

   public TypeRewriteRule makeRule() {
      Type type = this.getInputSchema().getType(TypeReferences.BLOCK_ENTITY);
      Type type2 = this.getInputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, this.field_55637);
      OpticFinder opticFinder = DSL.namedChoice(this.field_55637, type2);
      return this.fixTypeEverywhereTyped("DropInvalidSignDataFix for " + this.field_55637, type, (typed) -> {
         return typed.updateTyped(opticFinder, type2, (typedx) -> {
            boolean bl = ((Dynamic)typedx.get(DSL.remainderFinder())).get("_filtered_correct").asBoolean(false);
            return bl ? typedx.update(DSL.remainderFinder(), (dynamic) -> {
               return dynamic.remove("_filtered_correct");
            }) : Util.apply(typedx, type2, this::dropInvalidDatafixData);
         });
      });
   }
}
