/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.gl.Defines
 *  net.minecraft.client.gl.Defines$Builder
 */
package net.minecraft.client.gl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Defines;

@Environment(value=EnvType.CLIENT)
public record Defines(Map<String, String> values, Set<String> flags) {
    private final Map<String, String> values;
    private final Set<String> flags;
    public static final Defines EMPTY = new Defines(Map.of(), Set.of());
    public static final Codec<Defines> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.STRING).optionalFieldOf("values", Map.of()).forGetter(Defines::values), (App)Codec.STRING.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("flags", Set.of()).forGetter(Defines::flags)).apply((Applicative)instance, Defines::new));

    public Defines(Map<String, String> values, Set<String> flags) {
        this.values = values;
        this.flags = flags;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Defines withMerged(Defines other) {
        if (this.isEmpty()) {
            return other;
        }
        if (other.isEmpty()) {
            return this;
        }
        ImmutableMap.Builder builder = ImmutableMap.builderWithExpectedSize((int)(this.values.size() + other.values.size()));
        builder.putAll(this.values);
        builder.putAll(other.values);
        ImmutableSet.Builder builder2 = ImmutableSet.builderWithExpectedSize((int)(this.flags.size() + other.flags.size()));
        builder2.addAll((Iterable)this.flags);
        builder2.addAll((Iterable)other.flags);
        return new Defines((Map)builder.buildKeepingLast(), (Set)builder2.build());
    }

    public String toSource() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry entry : this.values.entrySet()) {
            String string = (String)entry.getKey();
            String string2 = (String)entry.getValue();
            stringBuilder.append("#define ").append(string).append(" ").append(string2).append('\n');
        }
        for (String string3 : this.flags) {
            stringBuilder.append("#define ").append(string3).append('\n');
        }
        return stringBuilder.toString();
    }

    public boolean isEmpty() {
        return this.values.isEmpty() && this.flags.isEmpty();
    }

    public Map<String, String> values() {
        return this.values;
    }

    public Set<String> flags() {
        return this.flags;
    }
}

