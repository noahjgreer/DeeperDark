/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.schema;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import net.minecraft.datafixer.schema.Schema1451v6;
import net.minecraft.util.Identifier;

class Schema1451v6.1
implements Hook.HookFunction {
    Schema1451v6.1() {
    }

    public <T> T apply(DynamicOps<T> ops, T value) {
        Dynamic dynamic = new Dynamic(ops, value);
        return (T)((Dynamic)DataFixUtils.orElse(dynamic.get("CriteriaName").asString().result().map(criteriaName -> {
            int i = criteriaName.indexOf(58);
            if (i < 0) {
                return Pair.of((Object)Schema1451v6.SPECIAL_TYPE, (Object)criteriaName);
            }
            try {
                Identifier identifier = Identifier.splitOn(criteriaName.substring(0, i), '.');
                Identifier identifier2 = Identifier.splitOn(criteriaName.substring(i + 1), '.');
                return Pair.of((Object)identifier.toString(), (Object)identifier2.toString());
            }
            catch (Exception exception) {
                return Pair.of((Object)Schema1451v6.SPECIAL_TYPE, (Object)criteriaName);
            }
        }).map(pair -> dynamic.set("CriteriaType", dynamic.createMap((Map)ImmutableMap.of((Object)dynamic.createString("type"), (Object)dynamic.createString((String)pair.getFirst()), (Object)dynamic.createString("id"), (Object)dynamic.createString((String)pair.getSecond()))))), (Object)dynamic)).getValue();
    }
}
