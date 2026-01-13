/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public static abstract sealed class MultipartModelCombinedCondition.LogicalOperator
extends Enum<MultipartModelCombinedCondition.LogicalOperator>
implements StringIdentifiable {
    public static final /* enum */ MultipartModelCombinedCondition.LogicalOperator AND = new MultipartModelCombinedCondition.LogicalOperator("AND"){

        @Override
        public <V> Predicate<V> apply(List<Predicate<V>> conditions) {
            return Util.allOf(conditions);
        }
    };
    public static final /* enum */ MultipartModelCombinedCondition.LogicalOperator OR = new MultipartModelCombinedCondition.LogicalOperator("OR"){

        @Override
        public <V> Predicate<V> apply(List<Predicate<V>> conditions) {
            return Util.anyOf(conditions);
        }
    };
    public static final Codec<MultipartModelCombinedCondition.LogicalOperator> CODEC;
    private final String name;
    private static final /* synthetic */ MultipartModelCombinedCondition.LogicalOperator[] field_22853;

    public static MultipartModelCombinedCondition.LogicalOperator[] values() {
        return (MultipartModelCombinedCondition.LogicalOperator[])field_22853.clone();
    }

    public static MultipartModelCombinedCondition.LogicalOperator valueOf(String string) {
        return Enum.valueOf(MultipartModelCombinedCondition.LogicalOperator.class, string);
    }

    MultipartModelCombinedCondition.LogicalOperator(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public abstract <V> Predicate<V> apply(List<Predicate<V>> var1);

    private static /* synthetic */ MultipartModelCombinedCondition.LogicalOperator[] method_36940() {
        return new MultipartModelCombinedCondition.LogicalOperator[]{AND, OR};
    }

    static {
        field_22853 = MultipartModelCombinedCondition.LogicalOperator.method_36940();
        CODEC = StringIdentifiable.createCodec(MultipartModelCombinedCondition.LogicalOperator::values);
    }
}
