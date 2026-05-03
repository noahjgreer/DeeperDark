package net.noahsarch.deeperdark.recipe;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.level.Level;
import net.noahsarch.deeperdark.block.ModBlocks;
import org.jspecify.annotations.Nullable;

/**
 * Dye any glass door (clear or stained) with a dye item to get the matching stained variant.
 * Mirrors the bed-dyeing mechanic: one door + one dye = one recoloured door.
 */
public class DyedGlassDoorRecipe extends CustomRecipe {

    public static final MapCodec<DyedGlassDoorRecipe> MAP_CODEC =
        MapCodec.unit(new DyedGlassDoorRecipe());
    public static final StreamCodec<RegistryFriendlyByteBuf, DyedGlassDoorRecipe> STREAM_CODEC =
        StreamCodec.unit(new DyedGlassDoorRecipe());
    public static final RecipeSerializer<DyedGlassDoorRecipe> SERIALIZER =
        new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    // Checked lazily in methods so there is no static-init ordering dependency on ModBlocks.
    private static boolean isGlassDoor(ItemStack stack) {
        Item item = stack.getItem();
        return item == ModBlocks.GLASS_DOOR.asItem()
            || item == ModBlocks.WHITE_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.ORANGE_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.MAGENTA_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.LIGHT_BLUE_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.YELLOW_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.LIME_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.PINK_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.GRAY_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.LIGHT_GRAY_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.CYAN_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.PURPLE_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.BLUE_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.BROWN_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.GREEN_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.RED_STAINED_GLASS_DOOR.asItem()
            || item == ModBlocks.BLACK_STAINED_GLASS_DOOR.asItem();
    }

    @Nullable
    private static Item doorForColor(DyeColor color) {
        return switch (color) {
            case WHITE      -> ModBlocks.WHITE_STAINED_GLASS_DOOR.asItem();
            case ORANGE     -> ModBlocks.ORANGE_STAINED_GLASS_DOOR.asItem();
            case MAGENTA    -> ModBlocks.MAGENTA_STAINED_GLASS_DOOR.asItem();
            case LIGHT_BLUE -> ModBlocks.LIGHT_BLUE_STAINED_GLASS_DOOR.asItem();
            case YELLOW     -> ModBlocks.YELLOW_STAINED_GLASS_DOOR.asItem();
            case LIME       -> ModBlocks.LIME_STAINED_GLASS_DOOR.asItem();
            case PINK       -> ModBlocks.PINK_STAINED_GLASS_DOOR.asItem();
            case GRAY       -> ModBlocks.GRAY_STAINED_GLASS_DOOR.asItem();
            case LIGHT_GRAY -> ModBlocks.LIGHT_GRAY_STAINED_GLASS_DOOR.asItem();
            case CYAN       -> ModBlocks.CYAN_STAINED_GLASS_DOOR.asItem();
            case PURPLE     -> ModBlocks.PURPLE_STAINED_GLASS_DOOR.asItem();
            case BLUE       -> ModBlocks.BLUE_STAINED_GLASS_DOOR.asItem();
            case BROWN      -> ModBlocks.BROWN_STAINED_GLASS_DOOR.asItem();
            case GREEN      -> ModBlocks.GREEN_STAINED_GLASS_DOOR.asItem();
            case RED        -> ModBlocks.RED_STAINED_GLASS_DOOR.asItem();
            case BLACK      -> ModBlocks.BLACK_STAINED_GLASS_DOOR.asItem();
        };
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() != 2) return false;
        boolean hasDoor = false;
        boolean hasDye  = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (isGlassDoor(stack)) {
                if (hasDoor) return false;
                hasDoor = true;
            } else if (stack.has(DataComponents.DYE)) {
                if (hasDye) return false;
                hasDye = true;
            } else {
                return false;
            }
        }
        return hasDoor && hasDye;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.has(DataComponents.DYE)) {
                DyeColor color = stack.get(DataComponents.DYE);
                if (color != null) {
                    Item door = doorForColor(color);
                    if (door != null) return new ItemStack(door);
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public List<RecipeDisplay> display() {
        return List.of();
    }

    @Override
    public RecipeSerializer<DyedGlassDoorRecipe> getSerializer() {
        return SERIALIZER;
    }
}
