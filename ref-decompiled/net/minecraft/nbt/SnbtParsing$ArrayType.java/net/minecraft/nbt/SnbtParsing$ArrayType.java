/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JavaOps
 *  it.unimi.dsi.fastutil.bytes.ByteArrayList
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JavaOps;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.util.packrat.ParsingState;
import org.jspecify.annotations.Nullable;

static abstract sealed class SnbtParsing.ArrayType
extends Enum<SnbtParsing.ArrayType> {
    public static final /* enum */ SnbtParsing.ArrayType BYTE = new SnbtParsing.ArrayType(SnbtParsing.NumericType.BYTE, new SnbtParsing.NumericType[0]){
        private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);

        @Override
        public <T> T createEmpty(DynamicOps<T> ops) {
            return (T)ops.createByteList(EMPTY_BUFFER);
        }

        @Override
        public <T> @Nullable T decode(DynamicOps<T> ops, List<SnbtParsing.IntValue> values, ParsingState<?> state) {
            ByteArrayList byteList = new ByteArrayList();
            for (SnbtParsing.IntValue intValue : values) {
                Number number = this.decode(intValue, state);
                if (number == null) {
                    return null;
                }
                byteList.add(number.byteValue());
            }
            return (T)ops.createByteList(ByteBuffer.wrap(byteList.toByteArray()));
        }
    };
    public static final /* enum */ SnbtParsing.ArrayType INT = new SnbtParsing.ArrayType(SnbtParsing.NumericType.INT, new SnbtParsing.NumericType[]{SnbtParsing.NumericType.BYTE, SnbtParsing.NumericType.SHORT}){

        @Override
        public <T> T createEmpty(DynamicOps<T> ops) {
            return (T)ops.createIntList(IntStream.empty());
        }

        @Override
        public <T> @Nullable T decode(DynamicOps<T> ops, List<SnbtParsing.IntValue> values, ParsingState<?> state) {
            IntStream.Builder builder = IntStream.builder();
            for (SnbtParsing.IntValue intValue : values) {
                Number number = this.decode(intValue, state);
                if (number == null) {
                    return null;
                }
                builder.add(number.intValue());
            }
            return (T)ops.createIntList(builder.build());
        }
    };
    public static final /* enum */ SnbtParsing.ArrayType LONG = new SnbtParsing.ArrayType(SnbtParsing.NumericType.LONG, new SnbtParsing.NumericType[]{SnbtParsing.NumericType.BYTE, SnbtParsing.NumericType.SHORT, SnbtParsing.NumericType.INT}){

        @Override
        public <T> T createEmpty(DynamicOps<T> ops) {
            return (T)ops.createLongList(LongStream.empty());
        }

        @Override
        public <T> @Nullable T decode(DynamicOps<T> ops, List<SnbtParsing.IntValue> values, ParsingState<?> state) {
            LongStream.Builder builder = LongStream.builder();
            for (SnbtParsing.IntValue intValue : values) {
                Number number = this.decode(intValue, state);
                if (number == null) {
                    return null;
                }
                builder.add(number.longValue());
            }
            return (T)ops.createLongList(builder.build());
        }
    };
    private final SnbtParsing.NumericType elementType;
    private final Set<SnbtParsing.NumericType> castableTypes;
    private static final /* synthetic */ SnbtParsing.ArrayType[] field_58007;

    public static SnbtParsing.ArrayType[] values() {
        return (SnbtParsing.ArrayType[])field_58007.clone();
    }

    public static SnbtParsing.ArrayType valueOf(String string) {
        return Enum.valueOf(SnbtParsing.ArrayType.class, string);
    }

    SnbtParsing.ArrayType(SnbtParsing.NumericType elementType, SnbtParsing.NumericType ... castableTypes) {
        this.castableTypes = Set.of(castableTypes);
        this.elementType = elementType;
    }

    public boolean isTypeAllowed(SnbtParsing.NumericType type) {
        return type == this.elementType || this.castableTypes.contains((Object)type);
    }

    public abstract <T> T createEmpty(DynamicOps<T> var1);

    public abstract <T> @Nullable T decode(DynamicOps<T> var1, List<SnbtParsing.IntValue> var2, ParsingState<?> var3);

    protected @Nullable Number decode(SnbtParsing.IntValue value, ParsingState<?> state) {
        SnbtParsing.NumericType numericType = this.getType(value.suffix);
        if (numericType == null) {
            state.getErrors().add(state.getCursor(), INVALID_ARRAY_ELEMENT_TYPE_EXCEPTION);
            return null;
        }
        return (Number)value.decode(JavaOps.INSTANCE, numericType, state);
    }

    private  @Nullable SnbtParsing.NumericType getType(SnbtParsing.NumberSuffix suffix) {
        SnbtParsing.NumericType numericType = suffix.type();
        if (numericType == null) {
            return this.elementType;
        }
        if (!this.isTypeAllowed(numericType)) {
            return null;
        }
        return numericType;
    }

    private static /* synthetic */ SnbtParsing.ArrayType[] method_68642() {
        return new SnbtParsing.ArrayType[]{BYTE, INT, LONG};
    }

    static {
        field_58007 = SnbtParsing.ArrayType.method_68642();
    }
}
