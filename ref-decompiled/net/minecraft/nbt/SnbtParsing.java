package net.minecraft.nbt;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.UnsignedBytes;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.chars.CharList;
import java.nio.ByteBuffer;
import java.util.HexFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import net.minecraft.text.Text;
import net.minecraft.util.packrat.CursorExceptionType;
import net.minecraft.util.packrat.Literals;
import net.minecraft.util.packrat.NumeralParsingRule;
import net.minecraft.util.packrat.PackratParser;
import net.minecraft.util.packrat.ParseResults;
import net.minecraft.util.packrat.ParsingRuleEntry;
import net.minecraft.util.packrat.ParsingRules;
import net.minecraft.util.packrat.ParsingState;
import net.minecraft.util.packrat.PatternParsingRule;
import net.minecraft.util.packrat.Symbol;
import net.minecraft.util.packrat.Term;
import net.minecraft.util.packrat.TokenParsingRule;
import net.minecraft.util.packrat.UnquotedStringParsingRule;
import org.jetbrains.annotations.Nullable;

public class SnbtParsing {
   private static final DynamicCommandExceptionType NUMBER_PARSE_FAILURE_EXCEPTION = new DynamicCommandExceptionType((value) -> {
      return Text.stringifiedTranslatable("snbt.parser.number_parse_failure", value);
   });
   static final DynamicCommandExceptionType EXPECTED_HEX_ESCAPE_EXCEPTION = new DynamicCommandExceptionType((length) -> {
      return Text.stringifiedTranslatable("snbt.parser.expected_hex_escape", length);
   });
   private static final DynamicCommandExceptionType INVALID_CODEPOINT_EXCEPTION = new DynamicCommandExceptionType((value) -> {
      return Text.stringifiedTranslatable("snbt.parser.invalid_codepoint", value);
   });
   private static final DynamicCommandExceptionType NO_SUCH_OPERATION_EXCEPTION = new DynamicCommandExceptionType((operation) -> {
      return Text.stringifiedTranslatable("snbt.parser.no_such_operation", operation);
   });
   static final CursorExceptionType EXPECTED_INTEGER_TYPE_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_integer_type")));
   private static final CursorExceptionType EXPECTED_FLOAT_TYPE_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_float_type")));
   static final CursorExceptionType EXPECTED_NON_NEGATIVE_NUMBER_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_non_negative_number")));
   private static final CursorExceptionType INVALID_CHARACTER_NAME_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.invalid_character_name")));
   static final CursorExceptionType INVALID_ARRAY_ELEMENT_TYPE_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.invalid_array_element_type")));
   private static final CursorExceptionType INVALID_UNQUOTED_START_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.invalid_unquoted_start")));
   private static final CursorExceptionType EXPECTED_UNQUOTED_STRING_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_unquoted_string")));
   private static final CursorExceptionType INVALID_STRING_CONTENTS_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.invalid_string_contents")));
   private static final CursorExceptionType EXPECTED_BINARY_NUMERAL_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_binary_numeral")));
   private static final CursorExceptionType UNDERSCORE_NOT_ALLOWED_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.underscore_not_allowed")));
   private static final CursorExceptionType EXPECTED_DECIMAL_NUMERAL_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_decimal_numeral")));
   private static final CursorExceptionType EXPECTED_HEX_NUMERAL_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.expected_hex_numeral")));
   private static final CursorExceptionType EMPTY_KEY_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.empty_key")));
   private static final CursorExceptionType LEADING_ZERO_NOT_ALLOWED_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.leading_zero_not_allowed")));
   private static final CursorExceptionType INFINITY_NOT_ALLOWED_EXCEPTION = CursorExceptionType.create(new SimpleCommandExceptionType(Text.translatable("snbt.parser.infinity_not_allowed")));
   private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();
   private static final NumeralParsingRule BINARY_RULE;
   private static final NumeralParsingRule DECIMAL_RULE;
   private static final NumeralParsingRule HEX_RULE;
   private static final TokenParsingRule UNQUOTED_STRING_RULE;
   private static final Literals.CharacterLiteral DECIMAL_CHAR;
   private static final Pattern UNICODE_NAME_PATTERN;

   static CursorExceptionType toNumberParseFailure(NumberFormatException exception) {
      return CursorExceptionType.create(NUMBER_PARSE_FAILURE_EXCEPTION, exception.getMessage());
   }

   @Nullable
   public static String escapeSpecialChar(char c) {
      String var10000;
      switch (c) {
         case '\b':
            var10000 = "b";
            break;
         case '\t':
            var10000 = "t";
            break;
         case '\n':
            var10000 = "n";
            break;
         case '\u000b':
         default:
            var10000 = c < ' ' ? "x" + HEX_FORMAT.toHexDigits((byte)c) : null;
            break;
         case '\f':
            var10000 = "f";
            break;
         case '\r':
            var10000 = "r";
      }

      return var10000;
   }

   private static boolean canUnquotedStringStartWith(char c) {
      return !isPartOfDecimal(c);
   }

   static boolean isPartOfDecimal(char c) {
      boolean var10000;
      switch (c) {
         case '+':
         case '-':
         case '.':
         case '0':
         case '1':
         case '2':
         case '3':
         case '4':
         case '5':
         case '6':
         case '7':
         case '8':
         case '9':
            var10000 = true;
            break;
         case ',':
         case '/':
         default:
            var10000 = false;
      }

      return var10000;
   }

   static boolean containsUnderscore(String string) {
      return string.indexOf(95) != -1;
   }

   private static void skipUnderscoreAndAppend(StringBuilder builder, String value) {
      append(builder, value, containsUnderscore(value));
   }

   static void append(StringBuilder builder, String value, boolean skipUnderscore) {
      if (skipUnderscore) {
         char[] var3 = value.toCharArray();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            char c = var3[var5];
            if (c != '_') {
               builder.append(c);
            }
         }
      } else {
         builder.append(value);
      }

   }

   static short parseUnsignedShort(String value, int radix) {
      int i = Integer.parseInt(value, radix);
      if (i >> 16 == 0) {
         return (short)i;
      } else {
         throw new NumberFormatException("out of range: " + i);
      }
   }

   @Nullable
   private static Object decodeFloat(DynamicOps ops, Sign sign, @Nullable String intPart, @Nullable String fractionalPart, @Nullable SignedValue exponent, @Nullable NumericType type, ParsingState state) {
      StringBuilder stringBuilder = new StringBuilder();
      sign.append(stringBuilder);
      if (intPart != null) {
         skipUnderscoreAndAppend(stringBuilder, intPart);
      }

      if (fractionalPart != null) {
         stringBuilder.append('.');
         skipUnderscoreAndAppend(stringBuilder, fractionalPart);
      }

      if (exponent != null) {
         stringBuilder.append('e');
         exponent.sign().append(stringBuilder);
         skipUnderscoreAndAppend(stringBuilder, (String)exponent.value);
      }

      try {
         String string = stringBuilder.toString();
         byte var10 = 0;
         Object var10000;
         switch (type.enumSwitch<invokedynamic>(type, var10)) {
            case -1:
               var10000 = (Object)parseFiniteDouble(ops, state, string);
               break;
            case 0:
               var10000 = (Object)parseFiniteFloat(ops, state, string);
               break;
            case 1:
               var10000 = (Object)parseFiniteDouble(ops, state, string);
               break;
            default:
               state.getErrors().add(state.getCursor(), EXPECTED_FLOAT_TYPE_EXCEPTION);
               var10000 = null;
         }

         return var10000;
      } catch (NumberFormatException var11) {
         state.getErrors().add(state.getCursor(), toNumberParseFailure(var11));
         return null;
      }
   }

   @Nullable
   private static Object parseFiniteFloat(DynamicOps ops, ParsingState state, String value) {
      float f = Float.parseFloat(value);
      if (!Float.isFinite(f)) {
         state.getErrors().add(state.getCursor(), INFINITY_NOT_ALLOWED_EXCEPTION);
         return null;
      } else {
         return ops.createFloat(f);
      }
   }

   @Nullable
   private static Object parseFiniteDouble(DynamicOps ops, ParsingState state, String value) {
      double d = Double.parseDouble(value);
      if (!Double.isFinite(d)) {
         state.getErrors().add(state.getCursor(), INFINITY_NOT_ALLOWED_EXCEPTION);
         return null;
      } else {
         return ops.createDouble(d);
      }
   }

   private static String join(List values) {
      String var10000;
      switch (values.size()) {
         case 0:
            var10000 = "";
            break;
         case 1:
            var10000 = (String)values.getFirst();
            break;
         default:
            var10000 = String.join("", values);
      }

      return var10000;
   }

   public static PackratParser createParser(DynamicOps ops) {
      Object object = ops.createBoolean(true);
      Object object2 = ops.createBoolean(false);
      Object object3 = ops.emptyMap();
      Object object4 = ops.emptyList();
      ParsingRules parsingRules = new ParsingRules();
      Symbol symbol = Symbol.of("sign");
      parsingRules.set(symbol, Term.anyOf(Term.sequence(Literals.character('+'), Term.always(symbol, SnbtParsing.Sign.PLUS)), Term.sequence(Literals.character('-'), Term.always(symbol, SnbtParsing.Sign.MINUS))), (results) -> {
         return (Sign)results.getOrThrow(symbol);
      });
      Symbol symbol2 = Symbol.of("integer_suffix");
      parsingRules.set(symbol2, Term.anyOf(Term.sequence(Literals.character('u', 'U'), Term.anyOf(Term.sequence(Literals.character('b', 'B'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.UNSIGNED, SnbtParsing.NumericType.BYTE))), Term.sequence(Literals.character('s', 'S'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.UNSIGNED, SnbtParsing.NumericType.SHORT))), Term.sequence(Literals.character('i', 'I'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.UNSIGNED, SnbtParsing.NumericType.INT))), Term.sequence(Literals.character('l', 'L'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.UNSIGNED, SnbtParsing.NumericType.LONG))))), Term.sequence(Literals.character('s', 'S'), Term.anyOf(Term.sequence(Literals.character('b', 'B'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.SIGNED, SnbtParsing.NumericType.BYTE))), Term.sequence(Literals.character('s', 'S'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.SIGNED, SnbtParsing.NumericType.SHORT))), Term.sequence(Literals.character('i', 'I'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.SIGNED, SnbtParsing.NumericType.INT))), Term.sequence(Literals.character('l', 'L'), Term.always(symbol2, new NumberSuffix(SnbtParsing.Signedness.SIGNED, SnbtParsing.NumericType.LONG))))), Term.sequence(Literals.character('b', 'B'), Term.always(symbol2, new NumberSuffix((Signedness)null, SnbtParsing.NumericType.BYTE))), Term.sequence(Literals.character('s', 'S'), Term.always(symbol2, new NumberSuffix((Signedness)null, SnbtParsing.NumericType.SHORT))), Term.sequence(Literals.character('i', 'I'), Term.always(symbol2, new NumberSuffix((Signedness)null, SnbtParsing.NumericType.INT))), Term.sequence(Literals.character('l', 'L'), Term.always(symbol2, new NumberSuffix((Signedness)null, SnbtParsing.NumericType.LONG)))), (results) -> {
         return (NumberSuffix)results.getOrThrow(symbol2);
      });
      Symbol symbol3 = Symbol.of("binary_numeral");
      parsingRules.set(symbol3, BINARY_RULE);
      Symbol symbol4 = Symbol.of("decimal_numeral");
      parsingRules.set(symbol4, DECIMAL_RULE);
      Symbol symbol5 = Symbol.of("hex_numeral");
      parsingRules.set(symbol5, HEX_RULE);
      Symbol symbol6 = Symbol.of("integer_literal");
      ParsingRuleEntry parsingRuleEntry = parsingRules.set(symbol6, Term.sequence(Term.optional(parsingRules.term(symbol)), Term.anyOf(Term.sequence(Literals.character('0'), Term.cutting(), Term.anyOf(Term.sequence(Literals.character('x', 'X'), Term.cutting(), parsingRules.term(symbol5)), Term.sequence(Literals.character('b', 'B'), parsingRules.term(symbol3)), Term.sequence(parsingRules.term(symbol4), Term.cutting(), Term.fail(LEADING_ZERO_NOT_ALLOWED_EXCEPTION)), Term.always(symbol4, "0"))), parsingRules.term(symbol4)), Term.optional(parsingRules.term(symbol2))), (results) -> {
         NumberSuffix numberSuffix = (NumberSuffix)results.getOrDefault(symbol2, SnbtParsing.NumberSuffix.DEFAULT);
         Sign sign = (Sign)results.getOrDefault(symbol, SnbtParsing.Sign.PLUS);
         String string = (String)results.get(symbol4);
         if (string != null) {
            return new IntValue(sign, SnbtParsing.Radix.DECIMAL, string, numberSuffix);
         } else {
            String string2 = (String)results.get(symbol5);
            if (string2 != null) {
               return new IntValue(sign, SnbtParsing.Radix.HEX, string2, numberSuffix);
            } else {
               String string3 = (String)results.getOrThrow(symbol3);
               return new IntValue(sign, SnbtParsing.Radix.BINARY, string3, numberSuffix);
            }
         }
      });
      Symbol symbol7 = Symbol.of("float_type_suffix");
      parsingRules.set(symbol7, Term.anyOf(Term.sequence(Literals.character('f', 'F'), Term.always(symbol7, SnbtParsing.NumericType.FLOAT)), Term.sequence(Literals.character('d', 'D'), Term.always(symbol7, SnbtParsing.NumericType.DOUBLE))), (results) -> {
         return (NumericType)results.getOrThrow(symbol7);
      });
      Symbol symbol8 = Symbol.of("float_exponent_part");
      parsingRules.set(symbol8, Term.sequence(Literals.character('e', 'E'), Term.optional(parsingRules.term(symbol)), parsingRules.term(symbol4)), (results) -> {
         return new SignedValue((Sign)results.getOrDefault(symbol, SnbtParsing.Sign.PLUS), (String)results.getOrThrow(symbol4));
      });
      Symbol symbol9 = Symbol.of("float_whole_part");
      Symbol symbol10 = Symbol.of("float_fraction_part");
      Symbol symbol11 = Symbol.of("float_literal");
      parsingRules.set(symbol11, Term.sequence(Term.optional(parsingRules.term(symbol)), Term.anyOf(Term.sequence(parsingRules.term(symbol4, symbol9), Literals.character('.'), Term.cutting(), Term.optional(parsingRules.term(symbol4, symbol10)), Term.optional(parsingRules.term(symbol8)), Term.optional(parsingRules.term(symbol7))), Term.sequence(Literals.character('.'), Term.cutting(), parsingRules.term(symbol4, symbol10), Term.optional(parsingRules.term(symbol8)), Term.optional(parsingRules.term(symbol7))), Term.sequence(parsingRules.term(symbol4, symbol9), parsingRules.term(symbol8), Term.cutting(), Term.optional(parsingRules.term(symbol7))), Term.sequence(parsingRules.term(symbol4, symbol9), Term.optional(parsingRules.term(symbol8)), parsingRules.term(symbol7)))), (state) -> {
         ParseResults parseResults = state.getResults();
         Sign sign = (Sign)parseResults.getOrDefault(symbol, SnbtParsing.Sign.PLUS);
         String string = (String)parseResults.get(symbol9);
         String string2 = (String)parseResults.get(symbol10);
         SignedValue signedValue = (SignedValue)parseResults.get(symbol8);
         NumericType numericType = (NumericType)parseResults.get(symbol7);
         return decodeFloat(ops, sign, string, string2, signedValue, numericType, state);
      });
      Symbol symbol12 = Symbol.of("string_hex_2");
      parsingRules.set(symbol12, new HexParsingRule(2));
      Symbol symbol13 = Symbol.of("string_hex_4");
      parsingRules.set(symbol13, new HexParsingRule(4));
      Symbol symbol14 = Symbol.of("string_hex_8");
      parsingRules.set(symbol14, new HexParsingRule(8));
      Symbol symbol15 = Symbol.of("string_unicode_name");
      parsingRules.set(symbol15, new PatternParsingRule(UNICODE_NAME_PATTERN, INVALID_CHARACTER_NAME_EXCEPTION));
      Symbol symbol16 = Symbol.of("string_escape_sequence");
      parsingRules.set(symbol16, Term.anyOf(Term.sequence(Literals.character('b'), Term.always(symbol16, "\b")), Term.sequence(Literals.character('s'), Term.always(symbol16, " ")), Term.sequence(Literals.character('t'), Term.always(symbol16, "\t")), Term.sequence(Literals.character('n'), Term.always(symbol16, "\n")), Term.sequence(Literals.character('f'), Term.always(symbol16, "\f")), Term.sequence(Literals.character('r'), Term.always(symbol16, "\r")), Term.sequence(Literals.character('\\'), Term.always(symbol16, "\\")), Term.sequence(Literals.character('\''), Term.always(symbol16, "'")), Term.sequence(Literals.character('"'), Term.always(symbol16, "\"")), Term.sequence(Literals.character('x'), parsingRules.term(symbol12)), Term.sequence(Literals.character('u'), parsingRules.term(symbol13)), Term.sequence(Literals.character('U'), parsingRules.term(symbol14)), Term.sequence(Literals.character('N'), Literals.character('{'), parsingRules.term(symbol15), Literals.character('}'))), (state) -> {
         ParseResults parseResults = state.getResults();
         String string = (String)parseResults.getAny(symbol16);
         if (string != null) {
            return string;
         } else {
            String string2 = (String)parseResults.getAny(symbol12, symbol13, symbol14);
            if (string2 != null) {
               int i = HexFormat.fromHexDigits(string2);
               if (!Character.isValidCodePoint(i)) {
                  state.getErrors().add(state.getCursor(), CursorExceptionType.create(INVALID_CODEPOINT_EXCEPTION, String.format(Locale.ROOT, "U+%08X", i)));
                  return null;
               } else {
                  return Character.toString(i);
               }
            } else {
               String string3 = (String)parseResults.getOrThrow(symbol15);

               int j;
               try {
                  j = Character.codePointOf(string3);
               } catch (IllegalArgumentException var12) {
                  state.getErrors().add(state.getCursor(), INVALID_CHARACTER_NAME_EXCEPTION);
                  return null;
               }

               return Character.toString(j);
            }
         }
      });
      Symbol symbol17 = Symbol.of("string_plain_contents");
      parsingRules.set(symbol17, UNQUOTED_STRING_RULE);
      Symbol symbol18 = Symbol.of("string_chunks");
      Symbol symbol19 = Symbol.of("string_contents");
      Symbol symbol20 = Symbol.of("single_quoted_string_chunk");
      ParsingRuleEntry parsingRuleEntry2 = parsingRules.set(symbol20, Term.anyOf(parsingRules.term(symbol17, symbol19), Term.sequence(Literals.character('\\'), parsingRules.term(symbol16, symbol19)), Term.sequence(Literals.character('"'), Term.always(symbol19, "\""))), (results) -> {
         return (String)results.getOrThrow(symbol19);
      });
      Symbol symbol21 = Symbol.of("single_quoted_string_contents");
      parsingRules.set(symbol21, Term.repeated(parsingRuleEntry2, symbol18), (results) -> {
         return join((List)results.getOrThrow(symbol18));
      });
      Symbol symbol22 = Symbol.of("double_quoted_string_chunk");
      ParsingRuleEntry parsingRuleEntry3 = parsingRules.set(symbol22, Term.anyOf(parsingRules.term(symbol17, symbol19), Term.sequence(Literals.character('\\'), parsingRules.term(symbol16, symbol19)), Term.sequence(Literals.character('\''), Term.always(symbol19, "'"))), (results) -> {
         return (String)results.getOrThrow(symbol19);
      });
      Symbol symbol23 = Symbol.of("double_quoted_string_contents");
      parsingRules.set(symbol23, Term.repeated(parsingRuleEntry3, symbol18), (results) -> {
         return join((List)results.getOrThrow(symbol18));
      });
      Symbol symbol24 = Symbol.of("quoted_string_literal");
      parsingRules.set(symbol24, Term.anyOf(Term.sequence(Literals.character('"'), Term.cutting(), Term.optional(parsingRules.term(symbol23, symbol19)), Literals.character('"')), Term.sequence(Literals.character('\''), Term.optional(parsingRules.term(symbol21, symbol19)), Literals.character('\''))), (results) -> {
         return (String)results.getOrThrow(symbol19);
      });
      Symbol symbol25 = Symbol.of("unquoted_string");
      parsingRules.set(symbol25, new UnquotedStringParsingRule(1, EXPECTED_UNQUOTED_STRING_EXCEPTION));
      Symbol symbol26 = Symbol.of("literal");
      Symbol symbol27 = Symbol.of("arguments");
      parsingRules.set(symbol27, Term.repeatWithPossiblyTrailingSeparator(parsingRules.getOrCreate(symbol26), symbol27, Literals.character(',')), (parseResults) -> {
         return (List)parseResults.getOrThrow(symbol27);
      });
      Symbol symbol28 = Symbol.of("unquoted_string_or_builtin");
      parsingRules.set(symbol28, Term.sequence(parsingRules.term(symbol25), Term.optional(Term.sequence(Literals.character('('), parsingRules.term(symbol27), Literals.character(')')))), (state) -> {
         ParseResults parseResults = state.getResults();
         String string = (String)parseResults.getOrThrow(symbol25);
         if (!string.isEmpty() && canUnquotedStringStartWith(string.charAt(0))) {
            List list = (List)parseResults.get(symbol27);
            if (list != null) {
               SnbtOperation.Type type = new SnbtOperation.Type(string, list.size());
               SnbtOperation.Operator operator = (SnbtOperation.Operator)SnbtOperation.OPERATIONS.get(type);
               if (operator != null) {
                  return operator.apply(ops, list, state);
               } else {
                  state.getErrors().add(state.getCursor(), CursorExceptionType.create(NO_SUCH_OPERATION_EXCEPTION, type.toString()));
                  return null;
               }
            } else if (string.equalsIgnoreCase("true")) {
               return object;
            } else {
               return string.equalsIgnoreCase("false") ? object2 : ops.createString(string);
            }
         } else {
            state.getErrors().add(state.getCursor(), SnbtOperation.SUGGESTIONS, INVALID_UNQUOTED_START_EXCEPTION);
            return null;
         }
      });
      Symbol symbol29 = Symbol.of("map_key");
      parsingRules.set(symbol29, Term.anyOf(parsingRules.term(symbol24), parsingRules.term(symbol25)), (results) -> {
         return (String)results.getAnyOrThrow(symbol24, symbol25);
      });
      Symbol symbol30 = Symbol.of("map_entry");
      ParsingRuleEntry parsingRuleEntry4 = parsingRules.set(symbol30, Term.sequence(parsingRules.term(symbol29), Literals.character(':'), parsingRules.term(symbol26)), (state) -> {
         ParseResults parseResults = state.getResults();
         String string = (String)parseResults.getOrThrow(symbol29);
         if (string.isEmpty()) {
            state.getErrors().add(state.getCursor(), EMPTY_KEY_EXCEPTION);
            return null;
         } else {
            Object object = parseResults.getOrThrow(symbol26);
            return Map.entry(string, object);
         }
      });
      Symbol symbol31 = Symbol.of("map_entries");
      parsingRules.set(symbol31, Term.repeatWithPossiblyTrailingSeparator(parsingRuleEntry4, symbol31, Literals.character(',')), (results) -> {
         return (List)results.getOrThrow(symbol31);
      });
      Symbol symbol32 = Symbol.of("map_literal");
      parsingRules.set(symbol32, Term.sequence(Literals.character('{'), parsingRules.term(symbol31), Literals.character('}')), (results) -> {
         List list = (List)results.getOrThrow(symbol31);
         if (list.isEmpty()) {
            return object3;
         } else {
            ImmutableMap.Builder builder = ImmutableMap.builderWithExpectedSize(list.size());
            Iterator var6 = list.iterator();

            while(var6.hasNext()) {
               Map.Entry entry = (Map.Entry)var6.next();
               builder.put(ops.createString((String)entry.getKey()), entry.getValue());
            }

            return ops.createMap(builder.buildKeepingLast());
         }
      });
      Symbol symbol33 = Symbol.of("list_entries");
      parsingRules.set(symbol33, Term.repeatWithPossiblyTrailingSeparator(parsingRules.getOrCreate(symbol26), symbol33, Literals.character(',')), (results) -> {
         return (List)results.getOrThrow(symbol33);
      });
      Symbol symbol34 = Symbol.of("array_prefix");
      parsingRules.set(symbol34, Term.anyOf(Term.sequence(Literals.character('B'), Term.always(symbol34, SnbtParsing.ArrayType.BYTE)), Term.sequence(Literals.character('L'), Term.always(symbol34, SnbtParsing.ArrayType.LONG)), Term.sequence(Literals.character('I'), Term.always(symbol34, SnbtParsing.ArrayType.INT))), (results) -> {
         return (ArrayType)results.getOrThrow(symbol34);
      });
      Symbol symbol35 = Symbol.of("int_array_entries");
      parsingRules.set(symbol35, Term.repeatWithPossiblyTrailingSeparator(parsingRuleEntry, symbol35, Literals.character(',')), (results) -> {
         return (List)results.getOrThrow(symbol35);
      });
      Symbol symbol36 = Symbol.of("list_literal");
      parsingRules.set(symbol36, Term.sequence(Literals.character('['), Term.anyOf(Term.sequence(parsingRules.term(symbol34), Literals.character(';'), parsingRules.term(symbol35)), parsingRules.term(symbol33)), Literals.character(']')), (state) -> {
         ParseResults parseResults = state.getResults();
         ArrayType arrayType = (ArrayType)parseResults.get(symbol34);
         List list;
         if (arrayType != null) {
            list = (List)parseResults.getOrThrow(symbol35);
            return list.isEmpty() ? arrayType.createEmpty(ops) : arrayType.decode(ops, list, state);
         } else {
            list = (List)parseResults.getOrThrow(symbol33);
            return list.isEmpty() ? object4 : ops.createList(list.stream());
         }
      });
      ParsingRuleEntry parsingRuleEntry5 = parsingRules.set(symbol26, Term.anyOf(Term.sequence(Term.positiveLookahead(DECIMAL_CHAR), Term.anyOf(parsingRules.term(symbol11, symbol26), parsingRules.term(symbol6))), Term.sequence(Term.positiveLookahead(Literals.character('"', '\'')), Term.cutting(), parsingRules.term(symbol24)), Term.sequence(Term.positiveLookahead(Literals.character('{')), Term.cutting(), parsingRules.term(symbol32, symbol26)), Term.sequence(Term.positiveLookahead(Literals.character('[')), Term.cutting(), parsingRules.term(symbol36, symbol26)), parsingRules.term(symbol28, symbol26)), (state) -> {
         ParseResults parseResults = state.getResults();
         String string = (String)parseResults.get(symbol24);
         if (string != null) {
            return ops.createString(string);
         } else {
            IntValue intValue = (IntValue)parseResults.get(symbol6);
            return intValue != null ? intValue.decode(ops, state) : parseResults.getOrThrow(symbol26);
         }
      });
      return new PackratParser(parsingRules, parsingRuleEntry5);
   }

   static {
      BINARY_RULE = new NumeralParsingRule(EXPECTED_BINARY_NUMERAL_EXCEPTION, UNDERSCORE_NOT_ALLOWED_EXCEPTION) {
         protected boolean accepts(char c) {
            boolean var10000;
            switch (c) {
               case '0':
               case '1':
               case '_':
                  var10000 = true;
                  break;
               default:
                  var10000 = false;
            }

            return var10000;
         }
      };
      DECIMAL_RULE = new NumeralParsingRule(EXPECTED_DECIMAL_NUMERAL_EXCEPTION, UNDERSCORE_NOT_ALLOWED_EXCEPTION) {
         protected boolean accepts(char c) {
            boolean var10000;
            switch (c) {
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               case '_':
                  var10000 = true;
                  break;
               default:
                  var10000 = false;
            }

            return var10000;
         }
      };
      HEX_RULE = new NumeralParsingRule(EXPECTED_HEX_NUMERAL_EXCEPTION, UNDERSCORE_NOT_ALLOWED_EXCEPTION) {
         protected boolean accepts(char c) {
            boolean var10000;
            switch (c) {
               case '0':
               case '1':
               case '2':
               case '3':
               case '4':
               case '5':
               case '6':
               case '7':
               case '8':
               case '9':
               case 'A':
               case 'B':
               case 'C':
               case 'D':
               case 'E':
               case 'F':
               case '_':
               case 'a':
               case 'b':
               case 'c':
               case 'd':
               case 'e':
               case 'f':
                  var10000 = true;
                  break;
               case ':':
               case ';':
               case '<':
               case '=':
               case '>':
               case '?':
               case '@':
               case 'G':
               case 'H':
               case 'I':
               case 'J':
               case 'K':
               case 'L':
               case 'M':
               case 'N':
               case 'O':
               case 'P':
               case 'Q':
               case 'R':
               case 'S':
               case 'T':
               case 'U':
               case 'V':
               case 'W':
               case 'X':
               case 'Y':
               case 'Z':
               case '[':
               case '\\':
               case ']':
               case '^':
               case '`':
               default:
                  var10000 = false;
            }

            return var10000;
         }
      };
      UNQUOTED_STRING_RULE = new TokenParsingRule(1, INVALID_STRING_CONTENTS_EXCEPTION) {
         protected boolean isValidChar(char c) {
            boolean var10000;
            switch (c) {
               case '"':
               case '\'':
               case '\\':
                  var10000 = false;
                  break;
               default:
                  var10000 = true;
            }

            return var10000;
         }
      };
      DECIMAL_CHAR = new Literals.CharacterLiteral(CharList.of()) {
         protected boolean accepts(char c) {
            return SnbtParsing.isPartOfDecimal(c);
         }
      };
      UNICODE_NAME_PATTERN = Pattern.compile("[-a-zA-Z0-9 ]+");
   }

   private static enum Sign {
      PLUS,
      MINUS;

      public void append(StringBuilder builder) {
         if (this == MINUS) {
            builder.append("-");
         }

      }

      // $FF: synthetic method
      private static Sign[] method_68653() {
         return new Sign[]{PLUS, MINUS};
      }
   }

   static record SignedValue(Sign sign, Object value) {
      final Object value;

      SignedValue(Sign sign, Object object) {
         this.sign = sign;
         this.value = object;
      }

      public Sign sign() {
         return this.sign;
      }

      public Object value() {
         return this.value;
      }
   }

   private static enum NumericType {
      FLOAT,
      DOUBLE,
      BYTE,
      SHORT,
      INT,
      LONG;

      // $FF: synthetic method
      private static NumericType[] method_68656() {
         return new NumericType[]{FLOAT, DOUBLE, BYTE, SHORT, INT, LONG};
      }
   }

   static record NumberSuffix(@Nullable Signedness signed, @Nullable NumericType type) {
      @Nullable
      final Signedness signed;
      @Nullable
      final NumericType type;
      public static final NumberSuffix DEFAULT = new NumberSuffix((Signedness)null, (NumericType)null);

      NumberSuffix(@Nullable Signedness signedness, @Nullable NumericType numericType) {
         this.signed = signedness;
         this.type = numericType;
      }

      @Nullable
      public Signedness signed() {
         return this.signed;
      }

      @Nullable
      public NumericType type() {
         return this.type;
      }
   }

   private static enum Signedness {
      SIGNED,
      UNSIGNED;

      // $FF: synthetic method
      private static Signedness[] method_68655() {
         return new Signedness[]{SIGNED, UNSIGNED};
      }
   }

   static class HexParsingRule extends TokenParsingRule {
      public HexParsingRule(int length) {
         super(length, length, CursorExceptionType.create(SnbtParsing.EXPECTED_HEX_ESCAPE_EXCEPTION, String.valueOf(length)));
      }

      protected boolean isValidChar(char c) {
         boolean var10000;
         switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'a':
            case 'b':
            case 'c':
            case 'd':
            case 'e':
            case 'f':
               var10000 = true;
               break;
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            default:
               var10000 = false;
         }

         return var10000;
      }
   }

   static enum ArrayType {
      BYTE(SnbtParsing.NumericType.BYTE, new NumericType[0]) {
         private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

         public Object createEmpty(DynamicOps ops) {
            return ops.createByteList(EMPTY_BUFFER);
         }

         @Nullable
         public Object decode(DynamicOps ops, List values, ParsingState state) {
            ByteList byteList = new ByteArrayList();
            Iterator var5 = values.iterator();

            while(var5.hasNext()) {
               IntValue intValue = (IntValue)var5.next();
               Number number = this.decode(intValue, state);
               if (number == null) {
                  return null;
               }

               byteList.add(number.byteValue());
            }

            return ops.createByteList(ByteBuffer.wrap(byteList.toByteArray()));
         }
      },
      INT(SnbtParsing.NumericType.INT, new NumericType[]{SnbtParsing.NumericType.BYTE, SnbtParsing.NumericType.SHORT}) {
         public Object createEmpty(DynamicOps ops) {
            return ops.createIntList(IntStream.empty());
         }

         @Nullable
         public Object decode(DynamicOps ops, List values, ParsingState state) {
            IntStream.Builder builder = IntStream.builder();
            Iterator var5 = values.iterator();

            while(var5.hasNext()) {
               IntValue intValue = (IntValue)var5.next();
               Number number = this.decode(intValue, state);
               if (number == null) {
                  return null;
               }

               builder.add(number.intValue());
            }

            return ops.createIntList(builder.build());
         }
      },
      LONG(SnbtParsing.NumericType.LONG, new NumericType[]{SnbtParsing.NumericType.BYTE, SnbtParsing.NumericType.SHORT, SnbtParsing.NumericType.INT}) {
         public Object createEmpty(DynamicOps ops) {
            return ops.createLongList(LongStream.empty());
         }

         @Nullable
         public Object decode(DynamicOps ops, List values, ParsingState state) {
            LongStream.Builder builder = LongStream.builder();
            Iterator var5 = values.iterator();

            while(var5.hasNext()) {
               IntValue intValue = (IntValue)var5.next();
               Number number = this.decode(intValue, state);
               if (number == null) {
                  return null;
               }

               builder.add(number.longValue());
            }

            return ops.createLongList(builder.build());
         }
      };

      private final NumericType elementType;
      private final Set castableTypes;

      ArrayType(final NumericType elementType, final NumericType... castableTypes) {
         this.castableTypes = Set.of(castableTypes);
         this.elementType = elementType;
      }

      public boolean isTypeAllowed(NumericType type) {
         return type == this.elementType || this.castableTypes.contains(type);
      }

      public abstract Object createEmpty(DynamicOps ops);

      @Nullable
      public abstract Object decode(DynamicOps ops, List values, ParsingState state);

      @Nullable
      protected Number decode(IntValue value, ParsingState state) {
         NumericType numericType = this.getType(value.suffix);
         if (numericType == null) {
            state.getErrors().add(state.getCursor(), SnbtParsing.INVALID_ARRAY_ELEMENT_TYPE_EXCEPTION);
            return null;
         } else {
            return (Number)value.decode(JavaOps.INSTANCE, numericType, state);
         }
      }

      @Nullable
      private NumericType getType(NumberSuffix suffix) {
         NumericType numericType = suffix.type();
         if (numericType == null) {
            return this.elementType;
         } else {
            return !this.isTypeAllowed(numericType) ? null : numericType;
         }
      }

      // $FF: synthetic method
      private static ArrayType[] method_68642() {
         return new ArrayType[]{BYTE, INT, LONG};
      }
   }

   private static record IntValue(Sign sign, Radix base, String digits, NumberSuffix suffix) {
      final NumberSuffix suffix;

      IntValue(Sign sign, Radix radix, String string, NumberSuffix numberSuffix) {
         this.sign = sign;
         this.base = radix;
         this.digits = string;
         this.suffix = numberSuffix;
      }

      private Signedness getSignedness() {
         if (this.suffix.signed != null) {
            return this.suffix.signed;
         } else {
            Signedness var10000;
            switch (this.base.ordinal()) {
               case 0:
               case 2:
                  var10000 = SnbtParsing.Signedness.UNSIGNED;
                  break;
               case 1:
                  var10000 = SnbtParsing.Signedness.SIGNED;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            return var10000;
         }
      }

      private String toString(Sign sign) {
         boolean bl = SnbtParsing.containsUnderscore(this.digits);
         if (sign != SnbtParsing.Sign.MINUS && !bl) {
            return this.digits;
         } else {
            StringBuilder stringBuilder = new StringBuilder();
            sign.append(stringBuilder);
            SnbtParsing.append(stringBuilder, this.digits, bl);
            return stringBuilder.toString();
         }
      }

      @Nullable
      public Object decode(DynamicOps ops, ParsingState state) {
         return this.decode(ops, (NumericType)Objects.requireNonNullElse(this.suffix.type, SnbtParsing.NumericType.INT), state);
      }

      @Nullable
      public Object decode(DynamicOps ops, NumericType type, ParsingState state) {
         boolean bl = this.getSignedness() == SnbtParsing.Signedness.SIGNED;
         if (!bl && this.sign == SnbtParsing.Sign.MINUS) {
            state.getErrors().add(state.getCursor(), SnbtParsing.EXPECTED_NON_NEGATIVE_NUMBER_EXCEPTION);
            return null;
         } else {
            String string = this.toString(this.sign);
            byte var10000;
            switch (this.base.ordinal()) {
               case 0:
                  var10000 = 2;
                  break;
               case 1:
                  var10000 = 10;
                  break;
               case 2:
                  var10000 = 16;
                  break;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }

            int i = var10000;

            try {
               Object var9;
               if (bl) {
                  switch (type.ordinal()) {
                     case 2:
                        var9 = (Object)ops.createByte(Byte.parseByte(string, i));
                        break;
                     case 3:
                        var9 = (Object)ops.createShort(Short.parseShort(string, i));
                        break;
                     case 4:
                        var9 = (Object)ops.createInt(Integer.parseInt(string, i));
                        break;
                     case 5:
                        var9 = (Object)ops.createLong(Long.parseLong(string, i));
                        break;
                     default:
                        state.getErrors().add(state.getCursor(), SnbtParsing.EXPECTED_INTEGER_TYPE_EXCEPTION);
                        var9 = null;
                  }

                  return var9;
               } else {
                  switch (type.ordinal()) {
                     case 2:
                        var9 = (Object)ops.createByte(UnsignedBytes.parseUnsignedByte(string, i));
                        break;
                     case 3:
                        var9 = (Object)ops.createShort(SnbtParsing.parseUnsignedShort(string, i));
                        break;
                     case 4:
                        var9 = (Object)ops.createInt(Integer.parseUnsignedInt(string, i));
                        break;
                     case 5:
                        var9 = (Object)ops.createLong(Long.parseUnsignedLong(string, i));
                        break;
                     default:
                        state.getErrors().add(state.getCursor(), SnbtParsing.EXPECTED_INTEGER_TYPE_EXCEPTION);
                        var9 = null;
                  }

                  return var9;
               }
            } catch (NumberFormatException var8) {
               state.getErrors().add(state.getCursor(), SnbtParsing.toNumberParseFailure(var8));
               return null;
            }
         }
      }

      public Sign sign() {
         return this.sign;
      }

      public Radix base() {
         return this.base;
      }

      public String digits() {
         return this.digits;
      }

      public NumberSuffix suffix() {
         return this.suffix;
      }
   }

   private static enum Radix {
      BINARY,
      DECIMAL,
      HEX;

      // $FF: synthetic method
      private static Radix[] method_68648() {
         return new Radix[]{BINARY, DECIMAL, HEX};
      }
   }
}
