/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.SelectProperties;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DataCache;
import net.minecraft.registry.ContextSwapper;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record SelectItemModel.UnbakedSwitch<P extends SelectProperty<T>, T>(P property, List<SelectItemModel.SwitchCase<T>> cases) {
    public static final MapCodec<SelectItemModel.UnbakedSwitch<?, ?>> CODEC = SelectProperties.CODEC.dispatchMap("property", unbakedSwitch -> unbakedSwitch.property().getType(), SelectProperty.Type::switchCodec);

    public ItemModel bake(ItemModel.BakeContext context, ItemModel fallback) {
        Object2ObjectOpenHashMap object2ObjectMap = new Object2ObjectOpenHashMap();
        for (SelectItemModel.SwitchCase<T> switchCase : this.cases) {
            ItemModel.Unbaked unbaked = switchCase.model;
            ItemModel itemModel = unbaked.bake(context);
            for (Object object : switchCase.values) {
                object2ObjectMap.put(object, (Object)itemModel);
            }
        }
        object2ObjectMap.defaultReturnValue((Object)fallback);
        return new SelectItemModel<T>(this.property, this.buildModelSelector((Object2ObjectMap<T, ItemModel>)object2ObjectMap, context.contextSwapper()));
    }

    private SelectItemModel.ModelSelector<T> buildModelSelector(Object2ObjectMap<T, ItemModel> models, @Nullable ContextSwapper contextSwapper) {
        if (contextSwapper == null) {
            return (value, world) -> (ItemModel)models.get(value);
        }
        ItemModel itemModel = (ItemModel)models.defaultReturnValue();
        DataCache<ClientWorld, Object2ObjectMap> dataCache = new DataCache<ClientWorld, Object2ObjectMap>(world -> {
            Object2ObjectOpenHashMap object2ObjectMap2 = new Object2ObjectOpenHashMap(models.size());
            object2ObjectMap2.defaultReturnValue((Object)itemModel);
            models.forEach((arg_0, arg_1) -> this.method_67280(contextSwapper, world, (Object2ObjectMap)object2ObjectMap2, arg_0, arg_1));
            return object2ObjectMap2;
        });
        return (value, world) -> {
            if (world == null) {
                return (ItemModel)models.get(value);
            }
            if (value == null) {
                return itemModel;
            }
            return (ItemModel)((Object2ObjectMap)dataCache.compute(world)).get(value);
        };
    }

    public void resolveCases(ResolvableModel.Resolver resolver) {
        for (SelectItemModel.SwitchCase<T> switchCase : this.cases) {
            switchCase.model.resolve(resolver);
        }
    }

    private /* synthetic */ void method_67280(ContextSwapper contextSwapper, ClientWorld clientWorld, Object2ObjectMap object2ObjectMap, Object value, ItemModel world) {
        contextSwapper.swapContext(this.property.valueCodec(), value, clientWorld.getRegistryManager()).ifSuccess(swappedValue -> object2ObjectMap.put(swappedValue, (Object)world));
    }
}
