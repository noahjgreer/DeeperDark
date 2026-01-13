/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.MultipartModelCombinedCondition;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
final class MultipartModelCombinedCondition.LogicalOperator.1
extends MultipartModelCombinedCondition.LogicalOperator {
    MultipartModelCombinedCondition.LogicalOperator.1(String string2) {
    }

    @Override
    public <V> Predicate<V> apply(List<Predicate<V>> conditions) {
        return Util.allOf(conditions);
    }
}
