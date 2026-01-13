/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelCondition;
import net.minecraft.state.State;
import net.minecraft.state.StateManager;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public record MultipartModelCombinedCondition(LogicalOperator operation, List<MultipartModelCondition> terms) implements MultipartModelCondition
{
    @Override
    public <O, S extends State<O, S>> Predicate<S> instantiate(StateManager<O, S> stateManager) {
        return this.operation.apply(Lists.transform(this.terms, condition -> condition.instantiate(stateManager)));
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract sealed class LogicalOperator
    extends Enum<LogicalOperator>
    implements StringIdentifiable {
        public static final /* enum */ LogicalOperator AND = new LogicalOperator("AND"){

            @Override
            public <V> Predicate<V> apply(List<Predicate<V>> conditions) {
                return Util.allOf(conditions);
            }
        };
        public static final /* enum */ LogicalOperator OR = new LogicalOperator("OR"){

            @Override
            public <V> Predicate<V> apply(List<Predicate<V>> conditions) {
                return Util.anyOf(conditions);
            }
        };
        public static final Codec<LogicalOperator> CODEC;
        private final String name;
        private static final /* synthetic */ LogicalOperator[] field_22853;

        public static LogicalOperator[] values() {
            return (LogicalOperator[])field_22853.clone();
        }

        public static LogicalOperator valueOf(String string) {
            return Enum.valueOf(LogicalOperator.class, string);
        }

        LogicalOperator(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public abstract <V> Predicate<V> apply(List<Predicate<V>> var1);

        private static /* synthetic */ LogicalOperator[] method_36940() {
            return new LogicalOperator[]{AND, OR};
        }

        static {
            field_22853 = LogicalOperator.method_36940();
            CODEC = StringIdentifiable.createCodec(LogicalOperator::values);
        }
    }
}
