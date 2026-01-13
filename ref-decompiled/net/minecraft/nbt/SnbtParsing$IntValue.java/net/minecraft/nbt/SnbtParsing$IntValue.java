/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.UnsignedBytes
 *  com.mojang.serialization.DynamicOps
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.nbt;

import com.google.common.primitives.UnsignedBytes;
import com.mojang.serialization.DynamicOps;
import java.lang.invoke.MethodHandle;
import java.lang.runtime.ObjectMethods;
import java.util.Objects;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.util.packrat.ParsingState;
import org.jspecify.annotations.Nullable;

static final class SnbtParsing.IntValue
extends Record {
    private final SnbtParsing.Sign sign;
    private final SnbtParsing.Radix base;
    private final String digits;
    final SnbtParsing.NumberSuffix suffix;

    SnbtParsing.IntValue(SnbtParsing.Sign sign, SnbtParsing.Radix base, String digits, SnbtParsing.NumberSuffix suffix) {
        this.sign = sign;
        this.base = base;
        this.digits = digits;
        this.suffix = suffix;
    }

    private SnbtParsing.Signedness getSignedness() {
        if (this.suffix.signed != null) {
            return this.suffix.signed;
        }
        return switch (this.base.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0, 2 -> SnbtParsing.Signedness.UNSIGNED;
            case 1 -> SnbtParsing.Signedness.SIGNED;
        };
    }

    private String toString(SnbtParsing.Sign sign) {
        boolean bl = SnbtParsing.containsUnderscore(this.digits);
        if (sign == SnbtParsing.Sign.MINUS || bl) {
            StringBuilder stringBuilder = new StringBuilder();
            sign.append(stringBuilder);
            SnbtParsing.append(stringBuilder, this.digits, bl);
            return stringBuilder.toString();
        }
        return this.digits;
    }

    public <T> @Nullable T decode(DynamicOps<T> ops, ParsingState<?> state) {
        return this.decode(ops, Objects.requireNonNullElse(this.suffix.type, SnbtParsing.NumericType.INT), state);
    }

    public <T> @Nullable T decode(DynamicOps<T> ops, SnbtParsing.NumericType type, ParsingState<?> state) {
        boolean bl;
        boolean bl2 = bl = this.getSignedness() == SnbtParsing.Signedness.SIGNED;
        if (!bl && this.sign == SnbtParsing.Sign.MINUS) {
            state.getErrors().add(state.getCursor(), EXPECTED_NON_NEGATIVE_NUMBER_EXCEPTION);
            return null;
        }
        String string = this.toString(this.sign);
        int i = switch (this.base.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> 2;
            case 1 -> 10;
            case 2 -> 16;
        };
        try {
            if (bl) {
                return (T)(switch (type.ordinal()) {
                    case 2 -> ops.createByte(Byte.parseByte(string, i));
                    case 3 -> ops.createShort(Short.parseShort(string, i));
                    case 4 -> ops.createInt(Integer.parseInt(string, i));
                    case 5 -> ops.createLong(Long.parseLong(string, i));
                    default -> {
                        state.getErrors().add(state.getCursor(), EXPECTED_INTEGER_TYPE_EXCEPTION);
                        yield null;
                    }
                });
            }
            return (T)(switch (type.ordinal()) {
                case 2 -> ops.createByte(UnsignedBytes.parseUnsignedByte((String)string, (int)i));
                case 3 -> ops.createShort(SnbtParsing.parseUnsignedShort(string, i));
                case 4 -> ops.createInt(Integer.parseUnsignedInt(string, i));
                case 5 -> ops.createLong(Long.parseUnsignedLong(string, i));
                default -> {
                    state.getErrors().add(state.getCursor(), EXPECTED_INTEGER_TYPE_EXCEPTION);
                    yield null;
                }
            });
        }
        catch (NumberFormatException numberFormatException) {
            state.getErrors().add(state.getCursor(), SnbtParsing.toNumberParseFailure(numberFormatException));
            return null;
        }
    }

    @Override
    public final String toString() {
        return ObjectMethods.bootstrap("toString", new MethodHandle[]{SnbtParsing.IntValue.class, "sign;base;digits;suffix", "sign", "base", "digits", "suffix"}, this);
    }

    @Override
    public final int hashCode() {
        return (int)ObjectMethods.bootstrap("hashCode", new MethodHandle[]{SnbtParsing.IntValue.class, "sign;base;digits;suffix", "sign", "base", "digits", "suffix"}, this);
    }

    @Override
    public final boolean equals(Object object) {
        return (boolean)ObjectMethods.bootstrap("equals", new MethodHandle[]{SnbtParsing.IntValue.class, "sign;base;digits;suffix", "sign", "base", "digits", "suffix"}, this, object);
    }

    public SnbtParsing.Sign sign() {
        return this.sign;
    }

    public SnbtParsing.Radix base() {
        return this.base;
    }

    public String digits() {
        return this.digits;
    }

    public SnbtParsing.NumberSuffix suffix() {
        return this.suffix;
    }
}
