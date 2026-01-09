package net.minecraft.datafixer.fix;

import com.google.common.collect.Streams;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.datafixer.TypeReferences;

public class UpdateSignTextFormatFix extends ChoiceWriteReadFix {
   public static final List field_55629 = List.of("Text1", "Text2", "Text3", "Text4", "FilteredText1", "FilteredText2", "FilteredText3", "FilteredText4", "Color", "GlowingText");
   public static final String FILTERED_CORRECT = "_filtered_correct";
   private static final String DEFAULT_COLOR = "black";

   public UpdateSignTextFormatFix(Schema outputSchema, String name, String blockEntityId) {
      super(outputSchema, true, name, TypeReferences.BLOCK_ENTITY, blockEntityId);
   }

   protected Dynamic transform(Dynamic data) {
      data = data.set("front_text", updateFront(data)).set("back_text", updateBack(data)).set("is_waxed", data.createBoolean(false)).set("_filtered_correct", data.createBoolean(true));

      String string;
      for(Iterator var2 = field_55629.iterator(); var2.hasNext(); data = data.remove(string)) {
         string = (String)var2.next();
      }

      return data;
   }

   private static Dynamic updateFront(Dynamic signData) {
      Dynamic dynamic = TextFixes.empty(signData.getOps());
      List list = streamKeys(signData, "Text").map((text) -> {
         return (Dynamic)text.orElse(dynamic);
      }).toList();
      Dynamic dynamic2 = signData.emptyMap().set("messages", signData.createList(list.stream())).set("color", (Dynamic)signData.get("Color").result().orElse(signData.createString("black"))).set("has_glowing_text", (Dynamic)signData.get("GlowingText").result().orElse(signData.createBoolean(false)));
      List list2 = streamKeys(signData, "FilteredText").toList();
      if (list2.stream().anyMatch(Optional::isPresent)) {
         dynamic2 = dynamic2.set("filtered_messages", signData.createList(Streams.mapWithIndex(list2.stream(), (message, index) -> {
            Dynamic dynamic = (Dynamic)list.get((int)index);
            return (Dynamic)message.orElse(dynamic);
         })));
      }

      return dynamic2;
   }

   private static Stream streamKeys(Dynamic signData, String prefix) {
      return Stream.of(signData.get(prefix + "1").result(), signData.get(prefix + "2").result(), signData.get(prefix + "3").result(), signData.get(prefix + "4").result());
   }

   private static Dynamic updateBack(Dynamic signData) {
      return signData.emptyMap().set("messages", emptySignData(signData)).set("color", signData.createString("black")).set("has_glowing_text", signData.createBoolean(false));
   }

   private static Dynamic emptySignData(Dynamic signData) {
      Dynamic dynamic = TextFixes.empty(signData.getOps());
      return signData.createList(Stream.of(dynamic, dynamic, dynamic, dynamic));
   }
}
