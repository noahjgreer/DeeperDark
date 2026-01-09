package net.minecraft.predicate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;

public interface NumberRange {
   SimpleCommandExceptionType EXCEPTION_EMPTY = new SimpleCommandExceptionType(Text.translatable("argument.range.empty"));
   SimpleCommandExceptionType EXCEPTION_SWAPPED = new SimpleCommandExceptionType(Text.translatable("argument.range.swapped"));

   Optional min();

   Optional max();

   default boolean isDummy() {
      return this.min().isEmpty() && this.max().isEmpty();
   }

   default Optional getConstantValue() {
      Optional optional = this.min();
      Optional optional2 = this.max();
      return optional.equals(optional2) ? optional : Optional.empty();
   }

   static Codec createCodec(Codec valueCodec, Factory rangeFactory) {
      Codec codec = RecordCodecBuilder.create((instance) -> {
         Products.P2 var10000 = instance.group(valueCodec.optionalFieldOf("min").forGetter(NumberRange::min), valueCodec.optionalFieldOf("max").forGetter(NumberRange::max));
         Objects.requireNonNull(rangeFactory);
         return var10000.apply(instance, rangeFactory::create);
      });
      return Codec.either(codec, valueCodec).xmap((either) -> {
         return (NumberRange)either.map((range) -> {
            return range;
         }, (value) -> {
            return rangeFactory.create(Optional.of(value), Optional.of(value));
         });
      }, (range) -> {
         Optional optional = range.getConstantValue();
         return optional.isPresent() ? Either.right((Number)optional.get()) : Either.left(range);
      });
   }

   static PacketCodec createPacketCodec(final PacketCodec valuePacketCodec, final Factory rangeFactory) {
      return new PacketCodec() {
         private static final int field_56292 = 1;
         public static final int field_56289 = 2;

         public NumberRange decode(ByteBuf byteBuf) {
            byte b = byteBuf.readByte();
            Optional optional = (b & 1) != 0 ? Optional.of((Number)valuePacketCodec.decode(byteBuf)) : Optional.empty();
            Optional optional2 = (b & 2) != 0 ? Optional.of((Number)valuePacketCodec.decode(byteBuf)) : Optional.empty();
            return rangeFactory.create(optional, optional2);
         }

         public void encode(ByteBuf byteBuf, NumberRange numberRange) {
            Optional optional = numberRange.min();
            Optional optional2 = numberRange.max();
            byteBuf.writeByte((optional.isPresent() ? 1 : 0) | (optional2.isPresent() ? 2 : 0));
            optional.ifPresent((number) -> {
               valuePacketCodec.encode(byteBuf, number);
            });
            optional2.ifPresent((number) -> {
               valuePacketCodec.encode(byteBuf, number);
            });
         }

         // $FF: synthetic method
         public void encode(final Object object, final Object object2) {
            this.encode((ByteBuf)object, (NumberRange)object2);
         }

         // $FF: synthetic method
         public Object decode(final Object object) {
            return this.decode((ByteBuf)object);
         }
      };
   }

   static NumberRange parse(StringReader commandReader, CommandFactory commandFactory, Function converter, Supplier exceptionTypeSupplier, Function mapper) throws CommandSyntaxException {
      if (!commandReader.canRead()) {
         throw EXCEPTION_EMPTY.createWithContext(commandReader);
      } else {
         int i = commandReader.getCursor();

         try {
            Optional optional = fromStringReader(commandReader, converter, exceptionTypeSupplier).map(mapper);
            Optional optional2;
            if (commandReader.canRead(2) && commandReader.peek() == '.' && commandReader.peek(1) == '.') {
               commandReader.skip();
               commandReader.skip();
               optional2 = fromStringReader(commandReader, converter, exceptionTypeSupplier).map(mapper);
               if (optional.isEmpty() && optional2.isEmpty()) {
                  throw EXCEPTION_EMPTY.createWithContext(commandReader);
               }
            } else {
               optional2 = optional;
            }

            if (optional.isEmpty() && optional2.isEmpty()) {
               throw EXCEPTION_EMPTY.createWithContext(commandReader);
            } else {
               return commandFactory.create(commandReader, optional, optional2);
            }
         } catch (CommandSyntaxException var8) {
            commandReader.setCursor(i);
            throw new CommandSyntaxException(var8.getType(), var8.getRawMessage(), var8.getInput(), i);
         }
      }
   }

   private static Optional fromStringReader(StringReader reader, Function converter, Supplier exceptionTypeSupplier) throws CommandSyntaxException {
      int i = reader.getCursor();

      while(reader.canRead() && isNextCharValid(reader)) {
         reader.skip();
      }

      String string = reader.getString().substring(i, reader.getCursor());
      if (string.isEmpty()) {
         return Optional.empty();
      } else {
         try {
            return Optional.of((Number)converter.apply(string));
         } catch (NumberFormatException var6) {
            throw ((DynamicCommandExceptionType)exceptionTypeSupplier.get()).createWithContext(reader, string);
         }
      }
   }

   private static boolean isNextCharValid(StringReader reader) {
      char c = reader.peek();
      if ((c < '0' || c > '9') && c != '-') {
         if (c != '.') {
            return false;
         } else {
            return !reader.canRead(2) || reader.peek(1) != '.';
         }
      } else {
         return true;
      }
   }

   @FunctionalInterface
   public interface Factory {
      NumberRange create(Optional min, Optional max);
   }

   @FunctionalInterface
   public interface CommandFactory {
      NumberRange create(StringReader reader, Optional min, Optional max) throws CommandSyntaxException;
   }

   public static record DoubleRange(Optional min, Optional max, Optional squaredMin, Optional squaredMax) implements NumberRange {
      public static final DoubleRange ANY = new DoubleRange(Optional.empty(), Optional.empty());
      public static final Codec CODEC;
      public static final PacketCodec PACKET_CODEC;

      private DoubleRange(Optional min, Optional max) {
         this(min, max, square(min), square(max));
      }

      public DoubleRange(Optional optional, Optional optional2, Optional optional3, Optional optional4) {
         this.min = optional;
         this.max = optional2;
         this.squaredMin = optional3;
         this.squaredMax = optional4;
      }

      private static DoubleRange create(StringReader reader, Optional min, Optional max) throws CommandSyntaxException {
         if (min.isPresent() && max.isPresent() && (Double)min.get() > (Double)max.get()) {
            throw EXCEPTION_SWAPPED.createWithContext(reader);
         } else {
            return new DoubleRange(min, max);
         }
      }

      private static Optional square(Optional value) {
         return value.map((d) -> {
            return d * d;
         });
      }

      public static DoubleRange exactly(double value) {
         return new DoubleRange(Optional.of(value), Optional.of(value));
      }

      public static DoubleRange between(double min, double max) {
         return new DoubleRange(Optional.of(min), Optional.of(max));
      }

      public static DoubleRange atLeast(double value) {
         return new DoubleRange(Optional.of(value), Optional.empty());
      }

      public static DoubleRange atMost(double value) {
         return new DoubleRange(Optional.empty(), Optional.of(value));
      }

      public boolean test(double value) {
         if (this.min.isPresent() && (Double)this.min.get() > value) {
            return false;
         } else {
            return this.max.isEmpty() || !((Double)this.max.get() < value);
         }
      }

      public boolean testSqrt(double value) {
         if (this.squaredMin.isPresent() && (Double)this.squaredMin.get() > value) {
            return false;
         } else {
            return this.squaredMax.isEmpty() || !((Double)this.squaredMax.get() < value);
         }
      }

      public static DoubleRange parse(StringReader reader) throws CommandSyntaxException {
         return parse(reader, (value) -> {
            return value;
         });
      }

      public static DoubleRange parse(StringReader reader, Function mapper) throws CommandSyntaxException {
         CommandFactory var10001 = DoubleRange::create;
         Function var10002 = Double::parseDouble;
         BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10003);
         return (DoubleRange)NumberRange.parse(reader, var10001, var10002, var10003::readerInvalidDouble, mapper);
      }

      public Optional min() {
         return this.min;
      }

      public Optional max() {
         return this.max;
      }

      public Optional squaredMin() {
         return this.squaredMin;
      }

      public Optional squaredMax() {
         return this.squaredMax;
      }

      static {
         CODEC = NumberRange.createCodec(Codec.DOUBLE, DoubleRange::new);
         PACKET_CODEC = NumberRange.createPacketCodec(PacketCodecs.DOUBLE, DoubleRange::new);
      }
   }

   public static record IntRange(Optional min, Optional max, Optional minSquared, Optional maxSquared) implements NumberRange {
      public static final IntRange ANY = new IntRange(Optional.empty(), Optional.empty());
      public static final Codec CODEC;
      public static final PacketCodec PACKET_CODEC;

      private IntRange(Optional min, Optional max) {
         this(min, max, min.map((i) -> {
            return i.longValue() * i.longValue();
         }), square(max));
      }

      public IntRange(Optional optional, Optional optional2, Optional optional3, Optional optional4) {
         this.min = optional;
         this.max = optional2;
         this.minSquared = optional3;
         this.maxSquared = optional4;
      }

      private static IntRange parse(StringReader reader, Optional min, Optional max) throws CommandSyntaxException {
         if (min.isPresent() && max.isPresent() && (Integer)min.get() > (Integer)max.get()) {
            throw EXCEPTION_SWAPPED.createWithContext(reader);
         } else {
            return new IntRange(min, max);
         }
      }

      private static Optional square(Optional value) {
         return value.map((i) -> {
            return i.longValue() * i.longValue();
         });
      }

      public static IntRange exactly(int value) {
         return new IntRange(Optional.of(value), Optional.of(value));
      }

      public static IntRange between(int min, int max) {
         return new IntRange(Optional.of(min), Optional.of(max));
      }

      public static IntRange atLeast(int value) {
         return new IntRange(Optional.of(value), Optional.empty());
      }

      public static IntRange atMost(int value) {
         return new IntRange(Optional.empty(), Optional.of(value));
      }

      public boolean test(int value) {
         if (this.min.isPresent() && (Integer)this.min.get() > value) {
            return false;
         } else {
            return this.max.isEmpty() || (Integer)this.max.get() >= value;
         }
      }

      public boolean testSqrt(long value) {
         if (this.minSquared.isPresent() && (Long)this.minSquared.get() > value) {
            return false;
         } else {
            return this.maxSquared.isEmpty() || (Long)this.maxSquared.get() >= value;
         }
      }

      public static IntRange parse(StringReader reader) throws CommandSyntaxException {
         return fromStringReader(reader, (value) -> {
            return value;
         });
      }

      public static IntRange fromStringReader(StringReader reader, Function converter) throws CommandSyntaxException {
         CommandFactory var10001 = IntRange::parse;
         Function var10002 = Integer::parseInt;
         BuiltInExceptionProvider var10003 = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
         Objects.requireNonNull(var10003);
         return (IntRange)NumberRange.parse(reader, var10001, var10002, var10003::readerInvalidInt, converter);
      }

      public Optional min() {
         return this.min;
      }

      public Optional max() {
         return this.max;
      }

      public Optional minSquared() {
         return this.minSquared;
      }

      public Optional maxSquared() {
         return this.maxSquared;
      }

      static {
         CODEC = NumberRange.createCodec(Codec.INT, IntRange::new);
         PACKET_CODEC = NumberRange.createPacketCodec(PacketCodecs.INTEGER, IntRange::new);
      }
   }
}
