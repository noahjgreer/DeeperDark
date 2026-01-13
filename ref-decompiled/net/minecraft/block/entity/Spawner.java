/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.entity.BlockEntityType
 *  net.minecraft.block.entity.Spawner
 *  net.minecraft.entity.EntityType
 *  net.minecraft.entity.TypedEntityData
 *  net.minecraft.screen.ScreenTexts
 *  net.minecraft.text.Text
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.math.random.Random
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.block.entity;

import java.util.function.Consumer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.TypedEntityData;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;
import org.jspecify.annotations.Nullable;

/*
 * Exception performing whole class analysis ignored.
 */
public interface Spawner {
    public void setEntityType(EntityType<?> var1, Random var2);

    public static void appendSpawnDataToTooltip(@Nullable TypedEntityData<BlockEntityType<?>> nbtComponent, Consumer<Text> textConsumer, String spawnDataKey) {
        Text text = Spawner.getSpawnedEntityText(nbtComponent, (String)spawnDataKey);
        if (text != null) {
            textConsumer.accept(text);
        } else {
            textConsumer.accept(ScreenTexts.EMPTY);
            textConsumer.accept((Text)Text.translatable((String)"block.minecraft.spawner.desc1").formatted(Formatting.GRAY));
            textConsumer.accept((Text)ScreenTexts.space().append((Text)Text.translatable((String)"block.minecraft.spawner.desc2").formatted(Formatting.BLUE)));
        }
    }

    public static @Nullable Text getSpawnedEntityText(@Nullable TypedEntityData<BlockEntityType<?>> nbtComponent, String spawnDataKey) {
        if (nbtComponent == null) {
            return null;
        }
        return nbtComponent.getNbtWithoutId().getCompound(spawnDataKey).flatMap(spawnDataNbt -> spawnDataNbt.getCompound("entity")).flatMap(entityNbt -> entityNbt.get("id", EntityType.CODEC)).map(entityType -> Text.translatable((String)entityType.getTranslationKey()).formatted(Formatting.GRAY)).orElse(null);
    }
}

