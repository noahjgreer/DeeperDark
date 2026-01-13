/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.registry.entry;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jspecify.annotations.Nullable;

public static class RegistryEntry.Reference<T>
implements RegistryEntry<T> {
    private final RegistryEntryOwner<T> owner;
    private @Nullable Set<TagKey<T>> tags;
    private final Type referenceType;
    private @Nullable RegistryKey<T> registryKey;
    private @Nullable T value;

    protected RegistryEntry.Reference(Type referenceType, RegistryEntryOwner<T> owner, @Nullable RegistryKey<T> registryKey, @Nullable T value) {
        this.owner = owner;
        this.referenceType = referenceType;
        this.registryKey = registryKey;
        this.value = value;
    }

    public static <T> RegistryEntry.Reference<T> standAlone(RegistryEntryOwner<T> owner, RegistryKey<T> registryKey) {
        return new RegistryEntry.Reference<Object>(Type.STAND_ALONE, owner, registryKey, null);
    }

    @Deprecated
    public static <T> RegistryEntry.Reference<T> intrusive(RegistryEntryOwner<T> owner, @Nullable T value) {
        return new RegistryEntry.Reference<T>(Type.INTRUSIVE, owner, null, value);
    }

    public RegistryKey<T> registryKey() {
        if (this.registryKey == null) {
            throw new IllegalStateException("Trying to access unbound value '" + String.valueOf(this.value) + "' from registry " + String.valueOf(this.owner));
        }
        return this.registryKey;
    }

    @Override
    public T value() {
        if (this.value == null) {
            throw new IllegalStateException("Trying to access unbound value '" + String.valueOf(this.registryKey) + "' from registry " + String.valueOf(this.owner));
        }
        return this.value;
    }

    @Override
    public boolean matchesId(Identifier id) {
        return this.registryKey().getValue().equals(id);
    }

    @Override
    public boolean matchesKey(RegistryKey<T> key) {
        return this.registryKey() == key;
    }

    private Set<TagKey<T>> getTags() {
        if (this.tags == null) {
            throw new IllegalStateException("Tags not bound");
        }
        return this.tags;
    }

    @Override
    public boolean isIn(TagKey<T> tag) {
        return this.getTags().contains(tag);
    }

    @Override
    public boolean matches(RegistryEntry<T> entry) {
        return entry.matchesKey(this.registryKey());
    }

    @Override
    public boolean matches(Predicate<RegistryKey<T>> predicate) {
        return predicate.test(this.registryKey());
    }

    @Override
    public boolean ownerEquals(RegistryEntryOwner<T> owner) {
        return this.owner.ownerEquals(owner);
    }

    @Override
    public Either<RegistryKey<T>, T> getKeyOrValue() {
        return Either.left(this.registryKey());
    }

    @Override
    public Optional<RegistryKey<T>> getKey() {
        return Optional.of(this.registryKey());
    }

    @Override
    public RegistryEntry.Type getType() {
        return RegistryEntry.Type.REFERENCE;
    }

    @Override
    public boolean hasKeyAndValue() {
        return this.registryKey != null && this.value != null;
    }

    void setRegistryKey(RegistryKey<T> registryKey) {
        if (this.registryKey != null && registryKey != this.registryKey) {
            throw new IllegalStateException("Can't change holder key: existing=" + String.valueOf(this.registryKey) + ", new=" + String.valueOf(registryKey));
        }
        this.registryKey = registryKey;
    }

    protected void setValue(T value) {
        if (this.referenceType == Type.INTRUSIVE && this.value != value) {
            throw new IllegalStateException("Can't change holder " + String.valueOf(this.registryKey) + " value: existing=" + String.valueOf(this.value) + ", new=" + String.valueOf(value));
        }
        this.value = value;
    }

    void setTags(Collection<TagKey<T>> tags) {
        this.tags = Set.copyOf(tags);
    }

    @Override
    public Stream<TagKey<T>> streamTags() {
        return this.getTags().stream();
    }

    public String toString() {
        return "Reference{" + String.valueOf(this.registryKey) + "=" + String.valueOf(this.value) + "}";
    }

    protected static final class Type
    extends Enum<Type> {
        public static final /* enum */ Type STAND_ALONE = new Type();
        public static final /* enum */ Type INTRUSIVE = new Type();
        private static final /* synthetic */ Type[] field_36456;

        public static Type[] values() {
            return (Type[])field_36456.clone();
        }

        public static Type valueOf(String string) {
            return Enum.valueOf(Type.class, string);
        }

        private static /* synthetic */ Type[] method_40238() {
            return new Type[]{STAND_ALONE, INTRUSIVE};
        }

        static {
            field_36456 = Type.method_40238();
        }
    }
}
