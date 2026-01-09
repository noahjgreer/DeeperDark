package net.minecraft.datafixer;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.RewriteResult;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.View;
import com.mojang.datafixers.functions.PointFreeRule;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Util;

public class FixUtil {
   public static Dynamic fixBlockPos(Dynamic dynamic) {
      Optional optional = dynamic.get("X").asNumber().result();
      Optional optional2 = dynamic.get("Y").asNumber().result();
      Optional optional3 = dynamic.get("Z").asNumber().result();
      return !optional.isEmpty() && !optional2.isEmpty() && !optional3.isEmpty() ? createBlockPos(dynamic, ((Number)optional.get()).intValue(), ((Number)optional2.get()).intValue(), ((Number)optional3.get()).intValue()) : dynamic;
   }

   public static Dynamic consolidateBlockPos(Dynamic dynamic, String xKey, String yKey, String zKey, String newPosKey) {
      Optional optional = dynamic.get(xKey).asNumber().result();
      Optional optional2 = dynamic.get(yKey).asNumber().result();
      Optional optional3 = dynamic.get(zKey).asNumber().result();
      return !optional.isEmpty() && !optional2.isEmpty() && !optional3.isEmpty() ? dynamic.remove(xKey).remove(yKey).remove(zKey).set(newPosKey, createBlockPos(dynamic, ((Number)optional.get()).intValue(), ((Number)optional2.get()).intValue(), ((Number)optional3.get()).intValue())) : dynamic;
   }

   public static Dynamic createBlockPos(Dynamic dynamic, int x, int y, int z) {
      return dynamic.createIntList(IntStream.of(new int[]{x, y, z}));
   }

   public static Typed withType(Type type, Typed typed) {
      return new Typed(type, typed.getOps(), typed.getValue());
   }

   public static Typed withType(Type type, Object value, DynamicOps ops) {
      return new Typed(type, ops, value);
   }

   public static Type withTypeChanged(Type type, Type oldType, Type newType) {
      return type.all(typeChangingRule(oldType, newType), true, false).view().newType();
   }

   private static TypeRewriteRule typeChangingRule(Type oldType, Type newType) {
      RewriteResult rewriteResult = RewriteResult.create(View.create("Patcher", oldType, newType, (ops) -> {
         return (object) -> {
            throw new UnsupportedOperationException();
         };
      }), new BitSet());
      return TypeRewriteRule.everywhere(TypeRewriteRule.ifSame(oldType, rewriteResult), PointFreeRule.nop(), true, true);
   }

   @SafeVarargs
   public static Function compose(Function... fixes) {
      return (typed) -> {
         Function[] var2 = fixes;
         int var3 = fixes.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Function function = var2[var4];
            typed = (Typed)function.apply(typed);
         }

         return typed;
      };
   }

   public static Dynamic createBlockState(String id, Map properties) {
      Dynamic dynamic = new Dynamic(NbtOps.INSTANCE, new NbtCompound());
      Dynamic dynamic2 = dynamic.set("Name", dynamic.createString(id));
      if (!properties.isEmpty()) {
         dynamic2 = dynamic2.set("Properties", dynamic.createMap((Map)properties.entrySet().stream().collect(Collectors.toMap((entry) -> {
            return dynamic.createString((String)entry.getKey());
         }, (entry) -> {
            return dynamic.createString((String)entry.getValue());
         }))));
      }

      return dynamic2;
   }

   public static Dynamic createBlockState(String id) {
      return createBlockState(id, Map.of());
   }

   public static Dynamic apply(Dynamic dynamic, String fieldName, UnaryOperator applier) {
      return dynamic.update(fieldName, (value) -> {
         DataResult var10000 = value.asString().map(applier);
         Objects.requireNonNull(dynamic);
         return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createString).result(), value);
      });
   }

   public static String getColorName(int index) {
      String var10000;
      switch (index) {
         case 1:
            var10000 = "orange";
            break;
         case 2:
            var10000 = "magenta";
            break;
         case 3:
            var10000 = "light_blue";
            break;
         case 4:
            var10000 = "yellow";
            break;
         case 5:
            var10000 = "lime";
            break;
         case 6:
            var10000 = "pink";
            break;
         case 7:
            var10000 = "gray";
            break;
         case 8:
            var10000 = "light_gray";
            break;
         case 9:
            var10000 = "cyan";
            break;
         case 10:
            var10000 = "purple";
            break;
         case 11:
            var10000 = "blue";
            break;
         case 12:
            var10000 = "brown";
            break;
         case 13:
            var10000 = "green";
            break;
         case 14:
            var10000 = "red";
            break;
         case 15:
            var10000 = "black";
            break;
         default:
            var10000 = "white";
      }

      return var10000;
   }

   public static Typed method_67590(Typed typed, OpticFinder opticFinder, Dynamic dynamic) {
      return typed.set(opticFinder, Util.readTyped(opticFinder.type(), dynamic, true));
   }
}
