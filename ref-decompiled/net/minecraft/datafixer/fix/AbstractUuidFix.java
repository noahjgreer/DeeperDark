package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class AbstractUuidFix extends DataFix {
   protected DSL.TypeReference typeReference;

   public AbstractUuidFix(Schema outputSchema, DSL.TypeReference typeReference) {
      super(outputSchema, false);
      this.typeReference = typeReference;
   }

   protected Typed updateTyped(Typed typed, String name, Function updater) {
      Type type = this.getInputSchema().getChoiceType(this.typeReference, name);
      Type type2 = this.getOutputSchema().getChoiceType(this.typeReference, name);
      return typed.updateTyped(DSL.namedChoice(name, type), type2, (typedx) -> {
         return typedx.update(DSL.remainderFinder(), updater);
      });
   }

   protected static Optional updateStringUuid(Dynamic dynamic, String oldKey, String newKey) {
      return createArrayFromStringUuid(dynamic, oldKey).map((dynamic2) -> {
         return dynamic.remove(oldKey).set(newKey, dynamic2);
      });
   }

   protected static Optional updateCompoundUuid(Dynamic dynamic, String oldKey, String newKey) {
      return dynamic.get(oldKey).result().flatMap(AbstractUuidFix::createArrayFromCompoundUuid).map((dynamic2) -> {
         return dynamic.remove(oldKey).set(newKey, dynamic2);
      });
   }

   protected static Optional updateRegularMostLeast(Dynamic dynamic, String oldKey, String newKey) {
      String string = oldKey + "Most";
      String string2 = oldKey + "Least";
      return createArrayFromMostLeastTags(dynamic, string, string2).map((dynamic2) -> {
         return dynamic.remove(string).remove(string2).set(newKey, dynamic2);
      });
   }

   protected static Optional createArrayFromStringUuid(Dynamic dynamic, String key) {
      return dynamic.get(key).result().flatMap((dynamic2) -> {
         String string = dynamic2.asString((String)null);
         if (string != null) {
            try {
               UUID uUID = UUID.fromString(string);
               return createArray(dynamic, uUID.getMostSignificantBits(), uUID.getLeastSignificantBits());
            } catch (IllegalArgumentException var4) {
            }
         }

         return Optional.empty();
      });
   }

   protected static Optional createArrayFromCompoundUuid(Dynamic dynamic) {
      return createArrayFromMostLeastTags(dynamic, "M", "L");
   }

   protected static Optional createArrayFromMostLeastTags(Dynamic dynamic, String mostBitsKey, String leastBitsKey) {
      long l = dynamic.get(mostBitsKey).asLong(0L);
      long m = dynamic.get(leastBitsKey).asLong(0L);
      return l != 0L && m != 0L ? createArray(dynamic, l, m) : Optional.empty();
   }

   protected static Optional createArray(Dynamic dynamic, long mostBits, long leastBits) {
      return Optional.of(dynamic.createIntList(Arrays.stream(new int[]{(int)(mostBits >> 32), (int)mostBits, (int)(leastBits >> 32), (int)leastBits})));
   }
}
