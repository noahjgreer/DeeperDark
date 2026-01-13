/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2ObjectFunction
 *  it.unimi.dsi.fastutil.objects.Object2ObjectMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.client.render.model.BakedSimpleModel
 *  net.minecraft.client.render.model.MissingModel
 *  net.minecraft.client.render.model.ReferencedModelsCollector
 *  net.minecraft.client.render.model.ReferencedModelsCollector$Holder
 *  net.minecraft.client.render.model.ResolvableModel
 *  net.minecraft.client.render.model.ResolvableModel$Resolver
 *  net.minecraft.client.render.model.UnbakedModel
 *  net.minecraft.util.Identifier
 *  org.slf4j.Logger
 */
package net.minecraft.client.render.model;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.MissingModel;
import net.minecraft.client.render.model.ReferencedModelsCollector;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/*
 * Exception performing whole class analysis ignored.
 */
@Environment(value=EnvType.CLIENT)
public class ReferencedModelsCollector {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Object2ObjectMap<Identifier, Holder> modelCache = new Object2ObjectOpenHashMap();
    private final Holder missingModel;
    private final Object2ObjectFunction<Identifier, Holder> holder;
    private final ResolvableModel.Resolver resolver;
    private final Queue<Holder> queue = new ArrayDeque();

    public ReferencedModelsCollector(Map<Identifier, UnbakedModel> unbakedModels, UnbakedModel missingModel) {
        this.missingModel = new Holder(MissingModel.ID, missingModel, true);
        this.modelCache.put((Object)MissingModel.ID, (Object)this.missingModel);
        this.holder = id -> {
            Identifier identifier = (Identifier)id;
            UnbakedModel unbakedModel = (UnbakedModel)unbakedModels.get(identifier);
            if (unbakedModel == null) {
                LOGGER.warn("Missing block model: {}", (Object)identifier);
                return this.missingModel;
            }
            return this.schedule(identifier, unbakedModel);
        };
        this.resolver = arg_0 -> this.resolve(arg_0);
    }

    private static boolean isRootModel(UnbakedModel model) {
        return model.parent() == null;
    }

    private Holder resolve(Identifier id) {
        return (Holder)this.modelCache.computeIfAbsent((Object)id, this.holder);
    }

    private Holder schedule(Identifier id, UnbakedModel model) {
        boolean bl = ReferencedModelsCollector.isRootModel((UnbakedModel)model);
        Holder holder = new Holder(id, model, bl);
        if (!bl) {
            this.queue.add(holder);
        }
        return holder;
    }

    public void resolve(ResolvableModel model) {
        model.resolve(this.resolver);
    }

    public void addSpecialModel(Identifier id, UnbakedModel model) {
        if (!ReferencedModelsCollector.isRootModel((UnbakedModel)model)) {
            LOGGER.warn("Trying to add non-root special model {}, ignoring", (Object)id);
            return;
        }
        Holder holder = (Holder)this.modelCache.put((Object)id, (Object)this.schedule(id, model));
        if (holder != null) {
            LOGGER.warn("Duplicate special model {}", (Object)id);
        }
    }

    public BakedSimpleModel getMissingModel() {
        return this.missingModel;
    }

    public Map<Identifier, BakedSimpleModel> collectModels() {
        ArrayList list = new ArrayList();
        this.resolveAll(list);
        ReferencedModelsCollector.checkIfValid(list);
        ImmutableMap.Builder builder = ImmutableMap.builder();
        this.modelCache.forEach((id, model) -> {
            if (model.valid) {
                builder.put(id, model);
            } else {
                LOGGER.warn("Model {} ignored due to cyclic dependency", id);
            }
        });
        return builder.build();
    }

    private void resolveAll(List<Holder> models) {
        Holder holder;
        while ((holder = (Holder)this.queue.poll()) != null) {
            Holder holder2;
            Identifier identifier = Objects.requireNonNull(holder.model.parent());
            holder.parent = holder2 = this.resolve(identifier);
            if (holder2.valid) {
                holder.valid = true;
                continue;
            }
            models.add(holder);
        }
    }

    private static void checkIfValid(List<Holder> models) {
        boolean bl = true;
        while (bl) {
            bl = false;
            Iterator<Holder> iterator = models.iterator();
            while (iterator.hasNext()) {
                Holder holder = iterator.next();
                if (!Objects.requireNonNull(holder.parent).valid) continue;
                holder.valid = true;
                iterator.remove();
                bl = true;
            }
        }
    }
}

