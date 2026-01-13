/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.attribute;

import net.minecraft.util.Formatting;

public static final class EntityAttribute.Category
extends Enum<EntityAttribute.Category> {
    public static final /* enum */ EntityAttribute.Category POSITIVE = new EntityAttribute.Category();
    public static final /* enum */ EntityAttribute.Category NEUTRAL = new EntityAttribute.Category();
    public static final /* enum */ EntityAttribute.Category NEGATIVE = new EntityAttribute.Category();
    private static final /* synthetic */ EntityAttribute.Category[] field_51888;

    public static EntityAttribute.Category[] values() {
        return (EntityAttribute.Category[])field_51888.clone();
    }

    public static EntityAttribute.Category valueOf(String string) {
        return Enum.valueOf(EntityAttribute.Category.class, string);
    }

    public Formatting getFormatting(boolean addition) {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> {
                if (addition) {
                    yield Formatting.BLUE;
                }
                yield Formatting.RED;
            }
            case 1 -> Formatting.GRAY;
            case 2 -> addition ? Formatting.RED : Formatting.BLUE;
        };
    }

    private static /* synthetic */ EntityAttribute.Category[] method_60495() {
        return new EntityAttribute.Category[]{POSITIVE, NEUTRAL, NEGATIVE};
    }

    static {
        field_51888 = EntityAttribute.Category.method_60495();
    }
}
