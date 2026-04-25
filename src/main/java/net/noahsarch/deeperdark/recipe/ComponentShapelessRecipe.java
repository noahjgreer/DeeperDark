package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.NormalCraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ComponentShapelessRecipe extends NormalCraftingRecipe {

    public static final Identifier SERIALIZER_ID = Identifier.fromNamespaceAndPath("deeperdark", "crafting_component_shapeless");

    public static final MapCodec<ComponentShapelessRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(r -> r.commonInfo),
            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(r -> r.bookInfo),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(r -> r.result),
            ItemStackTemplate.CODEC.listOf(1, 9).fieldOf("ingredients").forGetter(r -> r.ingredients)
        ).apply(i, ComponentShapelessRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ComponentShapelessRecipe> STREAM_CODEC = StreamCodec.composite(
        Recipe.CommonInfo.STREAM_CODEC, r -> r.commonInfo,
        CraftingRecipe.CraftingBookInfo.STREAM_CODEC, r -> r.bookInfo,
        ItemStackTemplate.STREAM_CODEC, r -> r.result,
        ItemStackTemplate.STREAM_CODEC.apply(ByteBufCodecs.list()), r -> r.ingredients,
        ComponentShapelessRecipe::new
    );

    public static final RecipeSerializer<ComponentShapelessRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final ItemStackTemplate result;
    private final List<ItemStackTemplate> ingredients;

    public ComponentShapelessRecipe(
        Recipe.CommonInfo commonInfo,
        CraftingRecipe.CraftingBookInfo bookInfo,
        ItemStackTemplate result,
        List<ItemStackTemplate> ingredients
    ) {
        super(commonInfo, bookInfo);
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != ingredients.size()) return false;
        boolean[] used = new boolean[input.size()];
        for (ItemStackTemplate prototype : ingredients) {
            boolean found = false;
            for (int i = 0; i < input.size(); i++) {
                if (!used[i] && matchesTemplate(prototype, input.getItem(i))) {
                    used[i] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }

    private static boolean matchesTemplate(ItemStackTemplate prototype, ItemStack candidate) {
        if (candidate.getItem() != prototype.item().value()) return false;
        for (var entry : prototype.components().entrySet()) {
            var type = entry.getKey();
            var required = entry.getValue();
            if (required.isPresent()) {
                if (!Objects.equals(candidate.get(type), required.get())) return false;
            } else {
                if (candidate.has(type)) return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        return result.create();
    }

    @Override
    protected PlacementInfo createPlacementInfo() {
        return PlacementInfo.create(
            ingredients.stream()
                .map(t -> Ingredient.of(t.item().value()))
                .collect(Collectors.toList())
        );
    }

    @Override
    public RecipeSerializer<ComponentShapelessRecipe> getSerializer() {
        return SERIALIZER;
    }
}
