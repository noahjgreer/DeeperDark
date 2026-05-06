package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Shaped upgrade recipe: 8 paper surrounding a box in a 3x3 grid.
 * Copies all data components (inventory contents) from the source box to the result.
 */
public class BoxUpgradeRecipe extends CustomRecipe {

    public static final Identifier SERIALIZER_ID = Identifier.fromNamespaceAndPath("deeperdark", "box_upgrade");

    public static final MapCodec<BoxUpgradeRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("from").forGetter(r -> r.fromItem),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("to").forGetter(r -> r.toItem)
        ).apply(i, BoxUpgradeRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BoxUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.ITEM), r -> r.fromItem,
        ByteBufCodecs.registry(Registries.ITEM), r -> r.toItem,
        BoxUpgradeRecipe::new
    );

    public static final RecipeSerializer<BoxUpgradeRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private final Item fromItem;
    private final Item toItem;

    public BoxUpgradeRecipe(Item fromItem, Item toItem) {
        this.fromItem = fromItem;
        this.toItem = toItem;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) return false;
        // Center slot (index 4) must be the source box; all others must be paper.
        ItemStack center = input.getItem(4);
        if (!center.is(fromItem)) return false;
        for (int i = 0; i < input.size(); i++) {
            if (i == 4) continue;
            if (!input.getItem(i).is(Items.PAPER)) return false;
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack source = input.getItem(4);
        ItemStack result = new ItemStack(toItem);
        // Copy all data components (including stored inventory contents) to the result.
        result.applyComponents(source.getComponentsPatch());
        return result;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    @Override
    public RecipeSerializer<BoxUpgradeRecipe> getSerializer() {
        return SERIALIZER;
    }
}
