package net.minecraft.nbt;

import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.text.Text;
import net.minecraft.util.Uuids;
import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.ParsingState;
import net.minecraft.util.packrat.Suggestable;
import org.jetbrains.annotations.Nullable;

public class SnbtOperation {
   static final CursorExceptionType EXPECTED_STRING_UUID_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_string_uuid")));
   static final CursorExceptionType EXPECTED_NUMBER_OR_BOOLEAN_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_number_or_boolean")));
   public static final String TRUE = "true";
   public static final String FALSE = "false";
   public static final Map OPERATIONS = Map.of(new Type("bool", 1), new Operator() {
      public Object apply(DynamicOps ops, List args, ParsingState state) {
         Boolean boolean_ = asBoolean(ops, args.getFirst());
         if (boolean_ == null) {
            state.getErrors().add(state.getCursor(), SnbtOperation.EXPECTED_NUMBER_OR_BOOLEAN_EXCEPTION);
            return null;
         } else {
            return ops.createBoolean(boolean_);
         }
      }

      @Nullable
      private static Boolean asBoolean(DynamicOps ops, Object value) {
         Optional optional = ops.getBooleanValue(value).result();
         if (optional.isPresent()) {
            return (Boolean)optional.get();
         } else {
            Optional optional2 = ops.getNumberValue(value).result();
            return optional2.isPresent() ? ((Number)optional2.get()).doubleValue() != 0.0 : null;
         }
      }
   }, new Type("uuid", 1), new Operator() {
      public Object apply(DynamicOps ops, List args, ParsingState state) {
         Optional optional = ops.getStringValue(args.getFirst()).result();
         if (optional.isEmpty()) {
            state.getErrors().add(state.getCursor(), SnbtOperation.EXPECTED_STRING_UUID_EXCEPTION);
            return null;
         } else {
            UUID uUID;
            try {
               uUID = UUID.fromString((String)optional.get());
            } catch (IllegalArgumentException var7) {
               state.getErrors().add(state.getCursor(), SnbtOperation.EXPECTED_STRING_UUID_EXCEPTION);
               return null;
            }

            return ops.createIntList(IntStream.of(Uuids.toIntArray(uUID)));
         }
      }
   });
   public static final Suggestable SUGGESTIONS = new Suggestable() {
      private final Set values;

      {
         this.values = (Set)Stream.concat(Stream.of("false", "true"), SnbtOperation.OPERATIONS.keySet().stream().map(Type::id)).collect(Collectors.toSet());
      }

      public Stream possibleValues(ParsingState parsingState) {
         return this.values.stream();
      }
   };

   public static record Type(String id, int argCount) {
      public Type(String string, int i) {
         this.id = string;
         this.argCount = i;
      }

      public String toString() {
         return this.id + "/" + this.argCount;
      }

      public String id() {
         return this.id;
      }

      public int argCount() {
         return this.argCount;
      }
   }

   public interface Operator {
      @Nullable
      Object apply(DynamicOps ops, List args, ParsingState state);
   }
}
