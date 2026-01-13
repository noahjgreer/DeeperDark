/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.text;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Uuids;
import org.jspecify.annotations.Nullable;

public static class HoverEvent.EntityContent {
    public static final MapCodec<HoverEvent.EntityContent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Registries.ENTITY_TYPE.getCodec().fieldOf("id").forGetter(content -> content.entityType), (App)Uuids.STRICT_CODEC.fieldOf("uuid").forGetter(content -> content.uuid), (App)TextCodecs.CODEC.optionalFieldOf("name").forGetter(content -> content.name)).apply((Applicative)instance, HoverEvent.EntityContent::new));
    public final EntityType<?> entityType;
    public final UUID uuid;
    public final Optional<Text> name;
    private @Nullable List<Text> tooltip;

    public HoverEvent.EntityContent(EntityType<?> entityType, UUID uuid, @Nullable Text name) {
        this(entityType, uuid, Optional.ofNullable(name));
    }

    public HoverEvent.EntityContent(EntityType<?> entityType, UUID uuid, Optional<Text> name) {
        this.entityType = entityType;
        this.uuid = uuid;
        this.name = name;
    }

    public List<Text> asTooltip() {
        if (this.tooltip == null) {
            this.tooltip = new ArrayList<Text>();
            this.name.ifPresent(this.tooltip::add);
            this.tooltip.add(Text.translatable("gui.entity_tooltip.type", this.entityType.getName()));
            this.tooltip.add(Text.literal(this.uuid.toString()));
        }
        return this.tooltip;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        HoverEvent.EntityContent entityContent = (HoverEvent.EntityContent)o;
        return this.entityType.equals(entityContent.entityType) && this.uuid.equals(entityContent.uuid) && this.name.equals(entityContent.name);
    }

    public int hashCode() {
        int i = this.entityType.hashCode();
        i = 31 * i + this.uuid.hashCode();
        i = 31 * i + this.name.hashCode();
        return i;
    }
}
