/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMaps
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.lang.runtime.SwitchBootstraps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.SimpleModel;
import net.minecraft.client.util.SpriteIdentifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public static class ModelTextures.Builder {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<ModelTextures.Textures> textures = new ArrayList<ModelTextures.Textures>();

    public ModelTextures.Builder addLast(ModelTextures.Textures textures) {
        this.textures.addLast(textures);
        return this;
    }

    public ModelTextures.Builder addFirst(ModelTextures.Textures textures) {
        this.textures.addFirst(textures);
        return this;
    }

    public ModelTextures build(SimpleModel modelNameSupplier) {
        if (this.textures.isEmpty()) {
            return EMPTY;
        }
        Object2ObjectArrayMap object2ObjectMap = new Object2ObjectArrayMap();
        Object2ObjectArrayMap object2ObjectMap2 = new Object2ObjectArrayMap();
        for (ModelTextures.Textures textures : Lists.reverse(this.textures)) {
            textures.values.forEach((arg_0, arg_1) -> ModelTextures.Builder.method_65552((Object2ObjectMap)object2ObjectMap2, (Object2ObjectMap)object2ObjectMap, arg_0, arg_1));
        }
        if (object2ObjectMap2.isEmpty()) {
            return new ModelTextures((Map<String, SpriteIdentifier>)object2ObjectMap);
        }
        boolean bl = true;
        while (bl) {
            bl = false;
            ObjectIterator objectIterator = Object2ObjectMaps.fastIterator((Object2ObjectMap)object2ObjectMap2);
            while (objectIterator.hasNext()) {
                Object2ObjectMap.Entry entry2 = (Object2ObjectMap.Entry)objectIterator.next();
                SpriteIdentifier spriteIdentifier = (SpriteIdentifier)object2ObjectMap.get((Object)((ModelTextures.TextureReferenceEntry)entry2.getValue()).target);
                if (spriteIdentifier == null) continue;
                object2ObjectMap.put((Object)((String)entry2.getKey()), (Object)spriteIdentifier);
                objectIterator.remove();
                bl = true;
            }
        }
        if (!object2ObjectMap2.isEmpty()) {
            LOGGER.warn("Unresolved texture references in {}:\n{}", (Object)modelNameSupplier.name(), (Object)object2ObjectMap2.entrySet().stream().map(entry -> "\t#" + (String)entry.getKey() + "-> #" + ((ModelTextures.TextureReferenceEntry)entry.getValue()).target + "\n").collect(Collectors.joining()));
        }
        return new ModelTextures((Map<String, SpriteIdentifier>)object2ObjectMap);
    }

    private static /* synthetic */ void method_65552(Object2ObjectMap object2ObjectMap, Object2ObjectMap object2ObjectMap2, String textureId, ModelTextures.Entry entry) {
        ModelTextures.Entry entry2 = entry;
        Objects.requireNonNull(entry2);
        ModelTextures.Entry entry22 = entry2;
        int i = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{ModelTextures.SpriteEntry.class, ModelTextures.TextureReferenceEntry.class}, (Object)entry22, i)) {
            default: {
                throw new MatchException(null, null);
            }
            case 0: {
                ModelTextures.SpriteEntry spriteEntry = (ModelTextures.SpriteEntry)entry22;
                object2ObjectMap.remove((Object)textureId);
                object2ObjectMap2.put((Object)textureId, (Object)spriteEntry.material());
                break;
            }
            case 1: {
                ModelTextures.TextureReferenceEntry textureReferenceEntry = (ModelTextures.TextureReferenceEntry)entry22;
                object2ObjectMap2.remove((Object)textureId);
                object2ObjectMap.put((Object)textureId, (Object)textureReferenceEntry);
            }
        }
    }
}
