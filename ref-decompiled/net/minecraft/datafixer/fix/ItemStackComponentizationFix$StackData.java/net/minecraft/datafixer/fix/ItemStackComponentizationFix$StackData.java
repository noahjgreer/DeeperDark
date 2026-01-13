/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

static class ItemStackComponentizationFix.StackData {
    private final String itemId;
    private final int count;
    private Dynamic<?> components;
    private final Dynamic<?> leftoverNbt;
    Dynamic<?> nbt;

    private ItemStackComponentizationFix.StackData(String itemId, int count, Dynamic<?> dynamic) {
        this.itemId = IdentifierNormalizingSchema.normalize(itemId);
        this.count = count;
        this.components = dynamic.emptyMap();
        this.nbt = dynamic.get("tag").orElseEmptyMap();
        this.leftoverNbt = dynamic.remove("tag");
    }

    public static Optional<ItemStackComponentizationFix.StackData> fromDynamic(Dynamic<?> dynamic) {
        return dynamic.get("id").asString().apply2stable((itemId, count) -> new ItemStackComponentizationFix.StackData((String)itemId, count.intValue(), (Dynamic<?>)dynamic.remove("id").remove("Count")), dynamic.get("Count").asNumber()).result();
    }

    public OptionalDynamic<?> getAndRemove(String key) {
        OptionalDynamic optionalDynamic = this.nbt.get(key);
        this.nbt = this.nbt.remove(key);
        return optionalDynamic;
    }

    public void setComponent(String key, Dynamic<?> value) {
        this.components = this.components.set(key, value);
    }

    public void setComponent(String key, OptionalDynamic<?> optionalValue) {
        optionalValue.result().ifPresent(value -> {
            this.components = this.components.set(key, value);
        });
    }

    public Dynamic<?> moveToComponent(String nbtKey, Dynamic<?> components, String componentId) {
        Optional optional = this.getAndRemove(nbtKey).result();
        if (optional.isPresent()) {
            return components.set(componentId, (Dynamic)optional.get());
        }
        return components;
    }

    public void moveToComponent(String nbtKey, String componentId, Dynamic<?> defaultValue) {
        Optional optional = this.getAndRemove(nbtKey).result();
        if (optional.isPresent() && !((Dynamic)optional.get()).equals(defaultValue)) {
            this.setComponent(componentId, (Dynamic)optional.get());
        }
    }

    public void moveToComponent(String nbtKey, String componentId) {
        this.getAndRemove(nbtKey).result().ifPresent(nbt -> this.setComponent(componentId, (Dynamic<?>)nbt));
    }

    public void applyFixer(String nbtKey, boolean removeIfEmpty, UnaryOperator<Dynamic<?>> fixer) {
        OptionalDynamic optionalDynamic = this.nbt.get(nbtKey);
        if (removeIfEmpty && optionalDynamic.result().isEmpty()) {
            return;
        }
        Dynamic dynamic = optionalDynamic.orElseEmptyMap();
        this.nbt = (dynamic = (Dynamic)fixer.apply(dynamic)).equals((Object)dynamic.emptyMap()) ? this.nbt.remove(nbtKey) : this.nbt.set(nbtKey, dynamic);
    }

    public Dynamic<?> finalize() {
        Dynamic dynamic = this.nbt.emptyMap().set("id", this.nbt.createString(this.itemId)).set("count", this.nbt.createInt(this.count));
        if (!this.nbt.equals((Object)this.nbt.emptyMap())) {
            this.components = this.components.set("minecraft:custom_data", this.nbt);
        }
        if (!this.components.equals((Object)this.nbt.emptyMap())) {
            dynamic = dynamic.set("components", this.components);
        }
        return ItemStackComponentizationFix.StackData.mergeLeftoverNbt(dynamic, this.leftoverNbt);
    }

    private static <T> Dynamic<T> mergeLeftoverNbt(Dynamic<T> data, Dynamic<?> leftoverNbt) {
        DynamicOps dynamicOps = data.getOps();
        return dynamicOps.getMap(data.getValue()).flatMap(mapLike -> dynamicOps.mergeToMap(leftoverNbt.convert(dynamicOps).getValue(), mapLike)).map(object -> new Dynamic(dynamicOps, object)).result().orElse(data);
    }

    public boolean itemEquals(String itemId) {
        return this.itemId.equals(itemId);
    }

    public boolean itemMatches(Set<String> itemIds) {
        return itemIds.contains(this.itemId);
    }

    public boolean itemContains(String componentId) {
        return this.components.get(componentId).result().isPresent();
    }
}
