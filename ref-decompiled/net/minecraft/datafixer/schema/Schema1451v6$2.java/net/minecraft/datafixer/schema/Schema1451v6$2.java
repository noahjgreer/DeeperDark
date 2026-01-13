/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.templates.Hook;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.datafixer.schema.Schema1451v6;

class Schema1451v6.2
implements Hook.HookFunction {
    Schema1451v6.2() {
    }

    public <T> T apply(DynamicOps<T> ops, T value) {
        Dynamic dynamic = new Dynamic(ops, value);
        Optional<Dynamic> optional = dynamic.get("CriteriaType").get().result().flatMap(dynamic2 -> {
            Optional optional = dynamic2.get("type").asString().result();
            Optional optional2 = dynamic2.get("id").asString().result();
            if (optional.isPresent() && optional2.isPresent()) {
                String string = (String)optional.get();
                if (string.equals(Schema1451v6.SPECIAL_TYPE)) {
                    return Optional.of(dynamic.createString((String)optional2.get()));
                }
                return Optional.of(dynamic2.createString(Schema1451v6.toDotSeparated(string) + ":" + Schema1451v6.toDotSeparated((String)optional2.get())));
            }
            return Optional.empty();
        });
        return (T)((Dynamic)DataFixUtils.orElse(optional.map(criteriaName -> dynamic.set("CriteriaName", criteriaName).remove("CriteriaType")), (Object)dynamic)).getValue();
    }
}
