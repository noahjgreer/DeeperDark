/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Suppliers
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector3fc
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.render.item.model;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.client.render.item.tint.TintSourceTypes;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BakedSimpleModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.ModelSettings;
import net.minecraft.client.render.model.ModelTextures;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.Identifier;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class BasicItemModel
implements ItemModel {
    private static final Function<ItemStack, RenderLayer> ITEMS_ATLAS_RENDER_LAYER_GETTER = stack -> TexturedRenderLayers.getItemTranslucentCull();
    private static final Function<ItemStack, RenderLayer> BLOCKS_ATLAS_RENDER_LAYER_GETTER = stack -> {
        BlockItem blockItem;
        BlockRenderLayer blockRenderLayer;
        Item item = stack.getItem();
        if (item instanceof BlockItem && (blockRenderLayer = BlockRenderLayers.getBlockLayer((blockItem = (BlockItem)item).getBlock().getDefaultState())) != BlockRenderLayer.TRANSLUCENT) {
            return TexturedRenderLayers.getEntityCutout();
        }
        return TexturedRenderLayers.getBlockTranslucentCull();
    };
    private final List<TintSource> tints;
    private final List<BakedQuad> quads;
    private final Supplier<Vector3fc[]> vector;
    private final ModelSettings settings;
    private final boolean animated;
    private final Function<ItemStack, RenderLayer> renderLayerGetter;

    BasicItemModel(List<TintSource> tints, List<BakedQuad> quads, ModelSettings settings, Function<ItemStack, RenderLayer> renderLayerGetter) {
        this.tints = tints;
        this.quads = quads;
        this.settings = settings;
        this.renderLayerGetter = renderLayerGetter;
        this.vector = Suppliers.memoize(() -> BasicItemModel.bakeQuads(this.quads));
        boolean bl = false;
        for (BakedQuad bakedQuad : quads) {
            if (!bakedQuad.sprite().getContents().isAnimated()) continue;
            bl = true;
            break;
        }
        this.animated = bl;
    }

    public static Vector3fc[] bakeQuads(List<BakedQuad> quads) {
        HashSet<Vector3fc> set = new HashSet<Vector3fc>();
        for (BakedQuad bakedQuad : quads) {
            for (int i = 0; i < 4; ++i) {
                set.add(bakedQuad.getPosition(i));
            }
        }
        return (Vector3fc[])set.toArray(Vector3fc[]::new);
    }

    @Override
    public void update(ItemRenderState state, ItemStack stack, ItemModelManager resolver, ItemDisplayContext displayContext, @Nullable ClientWorld world, @Nullable HeldItemContext heldItemContext, int seed) {
        state.addModelKey(this);
        ItemRenderState.LayerRenderState layerRenderState = state.newLayer();
        if (stack.hasGlint()) {
            ItemRenderState.Glint glint = BasicItemModel.shouldUseSpecialGlint(stack) ? ItemRenderState.Glint.SPECIAL : ItemRenderState.Glint.STANDARD;
            layerRenderState.setGlint(glint);
            state.markAnimated();
            state.addModelKey((Object)glint);
        }
        int i = this.tints.size();
        int[] is = layerRenderState.initTints(i);
        for (int j = 0; j < i; ++j) {
            int k;
            is[j] = k = this.tints.get(j).getTint(stack, world, heldItemContext == null ? null : heldItemContext.getEntity());
            state.addModelKey(k);
        }
        layerRenderState.setVertices(this.vector);
        layerRenderState.setRenderLayer(this.renderLayerGetter.apply(stack));
        this.settings.addSettings(layerRenderState, displayContext);
        layerRenderState.getQuads().addAll(this.quads);
        if (this.animated) {
            state.markAnimated();
        }
    }

    static Function<ItemStack, RenderLayer> findRenderLayerGetter(List<BakedQuad> quads) {
        Iterator<BakedQuad> iterator = quads.iterator();
        if (!iterator.hasNext()) {
            return ITEMS_ATLAS_RENDER_LAYER_GETTER;
        }
        Identifier identifier = iterator.next().sprite().getAtlasId();
        while (iterator.hasNext()) {
            BakedQuad bakedQuad = iterator.next();
            Identifier identifier2 = bakedQuad.sprite().getAtlasId();
            if (identifier2.equals(identifier)) continue;
            throw new IllegalStateException("Multiple atlases used in model, expected " + String.valueOf(identifier) + ", but also got " + String.valueOf(identifier2));
        }
        if (identifier.equals(SpriteAtlasTexture.ITEMS_ATLAS_TEXTURE)) {
            return ITEMS_ATLAS_RENDER_LAYER_GETTER;
        }
        if (identifier.equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)) {
            return BLOCKS_ATLAS_RENDER_LAYER_GETTER;
        }
        throw new IllegalArgumentException("Atlas " + String.valueOf(identifier) + " can't be usef for item models");
    }

    private static boolean shouldUseSpecialGlint(ItemStack stack) {
        return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
    }

    @Environment(value=EnvType.CLIENT)
    public record Unbaked(Identifier model, List<TintSource> tints) implements ItemModel.Unbaked
    {
        public static final MapCodec<Unbaked> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model), (App)TintSourceTypes.CODEC.listOf().optionalFieldOf("tints", List.of()).forGetter(Unbaked::tints)).apply((Applicative)instance, Unbaked::new));

        @Override
        public void resolve(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakeContext context) {
            Baker baker = context.blockModelBaker();
            BakedSimpleModel bakedSimpleModel = baker.getModel(this.model);
            ModelTextures modelTextures = bakedSimpleModel.getTextures();
            List<BakedQuad> list = bakedSimpleModel.bakeGeometry(modelTextures, baker, ModelRotation.IDENTITY).getAllQuads();
            ModelSettings modelSettings = ModelSettings.resolveSettings(baker, bakedSimpleModel, modelTextures);
            Function<ItemStack, RenderLayer> function = BasicItemModel.findRenderLayerGetter(list);
            return new BasicItemModel(this.tints, list, modelSettings, function);
        }

        public MapCodec<Unbaked> getCodec() {
            return CODEC;
        }
    }
}
