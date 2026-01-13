/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.types.templates.Hook$HookFunction
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.types.templates.Hook;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.datafixer.schema.Schema99;

class Schema99.1
implements Hook.HookFunction {
    Schema99.1() {
    }

    public <T> T apply(DynamicOps<T> ops, T value) {
        return Schema99.updateBlockEntityTags(new Dynamic(ops, value), BLOCKS_TO_BLOCK_ENTITIES, field_49718);
    }
}
