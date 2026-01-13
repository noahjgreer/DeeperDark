/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 */
package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

public static final class SetNameLootFunction.Target
extends Enum<SetNameLootFunction.Target>
implements StringIdentifiable {
    public static final /* enum */ SetNameLootFunction.Target CUSTOM_NAME = new SetNameLootFunction.Target("custom_name");
    public static final /* enum */ SetNameLootFunction.Target ITEM_NAME = new SetNameLootFunction.Target("item_name");
    public static final Codec<SetNameLootFunction.Target> CODEC;
    private final String id;
    private static final /* synthetic */ SetNameLootFunction.Target[] field_50214;

    public static SetNameLootFunction.Target[] values() {
        return (SetNameLootFunction.Target[])field_50214.clone();
    }

    public static SetNameLootFunction.Target valueOf(String string) {
        return Enum.valueOf(SetNameLootFunction.Target.class, string);
    }

    private SetNameLootFunction.Target(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return this.id;
    }

    public ComponentType<Text> getComponentType() {
        return switch (this.ordinal()) {
            default -> throw new MatchException(null, null);
            case 1 -> DataComponentTypes.ITEM_NAME;
            case 0 -> DataComponentTypes.CUSTOM_NAME;
        };
    }

    private static /* synthetic */ SetNameLootFunction.Target[] method_58735() {
        return new SetNameLootFunction.Target[]{CUSTOM_NAME, ITEM_NAME};
    }

    static {
        field_50214 = SetNameLootFunction.Target.method_58735();
        CODEC = StringIdentifiable.createCodec(SetNameLootFunction.Target::values);
    }
}
