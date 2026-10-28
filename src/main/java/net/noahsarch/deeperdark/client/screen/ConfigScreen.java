package net.noahsarch.deeperdark.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.noahsarch.deeperdark.config.ConfigMenu;
import net.noahsarch.deeperdark.config.ConfigOption;
import net.noahsarch.deeperdark.config.ConfigOptions;
import net.noahsarch.deeperdark.payload.ConfigChangesPayload;
import net.noahsarch.deeperdark.payload.ConfigMenuPayload;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The "/dd" config menu: a category sidebar beside a searchable option list. Edits stay local
 * until Done, which sends only the changed values; see {@link ConfigMenu} for the key format.
 */
@Environment(EnvType.CLIENT)
public class ConfigScreen extends Screen {
    public static final String PLAYER_SOUNDS = "player_sounds";
    public static final Item PLAYER_SOUNDS_ICON = Items.NOTE_BLOCK;
    private static final Component TITLE = Component.translatable("deeperdark.config.title");
    private static final Component SEARCH_HINT = Component.translatable("deeperdark.config.search").withStyle(EditBox.SEARCH_HINT_STYLE);
    private static final int SIDEBAR_WIDTH = 130;
    static final Pattern PLAYER_NAME = Pattern.compile("[A-Za-z0-9_]{1,16}");

    final boolean operator;
    final String self;
    final Map<String, String> initial;
    final Map<String, String> values;
    /** Players with a sound profile in this menu, yourself first. */
    final List<String> profiles = new ArrayList<>();
    String selectedCategory = PLAYER_SOUNDS;

    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 12 + 9 + 15, 33);
    private @Nullable EditBox searchBox;
    private @Nullable CategoryList sidebar;
    private @Nullable ConfigOptionList optionList;
    private @Nullable Button doneButton;

    public ConfigScreen(ConfigMenuPayload payload) {
        super(TITLE);
        this.operator = payload.operator();
        this.self = Minecraft.getInstance().player.getName().getString();
        this.initial = new HashMap<>(payload.values());
        this.values = new HashMap<>(payload.values());

        profiles.add(self);
        initial.keySet().stream()
            .filter(key -> key.startsWith(ConfigMenu.PLAYER))
            .map(key -> key.substring(ConfigMenu.PLAYER.length(), key.lastIndexOf('.')))
            .distinct()
            .filter(name -> !name.equalsIgnoreCase(self))
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .forEach(profiles::add);
    }

    @Override
    protected void init() {
        LinearLayout header = layout.addToHeader(LinearLayout.vertical().spacing(4));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(TITLE, font));
        searchBox = header.addChild(new EditBox(font, 200, 15, Component.empty()));
        searchBox.setHint(SEARCH_HINT);
        searchBox.setResponder(text -> {
            if (optionList != null) optionList.populate();
        });

        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
        doneButton = footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onDone()).build());
        footer.addChild(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose()).build());
        layout.visitWidgets(this::addRenderableWidget);

        sidebar = addRenderableWidget(new CategoryList());
        optionList = addRenderableWidget(new ConfigOptionList(this, minecraft, layout.getContentHeight(), layout.getHeaderHeight()));
        repositionElements();
        updateDoneButton();
    }

    @Override
    protected void repositionElements() {
        layout.arrangeElements();
        if (sidebar != null) sidebar.updateSizeAndPosition(SIDEBAR_WIDTH, layout.getContentHeight(), 0, layout.getHeaderHeight());
        if (optionList != null) optionList.updateSizeAndPosition(width - SIDEBAR_WIDTH, layout.getContentHeight(), SIDEBAR_WIDTH, layout.getHeaderHeight());
    }

    @Override
    protected void setInitialFocus() {
        if (searchBox != null) setInitialFocus(searchBox);
    }

    String searchText() {
        return searchBox == null ? "" : searchBox.getValue().trim().toLowerCase(Locale.ROOT);
    }

    void setValue(String key, String value) {
        values.put(key, value);
        updateDoneButton();
    }

    /** Adds an empty profile; nothing reaches the server until one of its fields changes. */
    boolean addProfile(String name) {
        if (!PLAYER_NAME.matcher(name).matches() || profiles.stream().anyMatch(name::equalsIgnoreCase)) return false;
        profiles.add(name);
        for (ConfigOption<?> option : ConfigOptions.PROFILE) {
            String key = ConfigMenu.PLAYER + name + "." + option.key();
            initial.put(key, option.defaultValue());
            values.put(key, option.defaultValue());
        }
        return true;
    }

    /** The option behind a menu key, so the client validates exactly like the server will. */
    static Optional<? extends ConfigOption<?>> optionFor(String key) {
        if (key.startsWith(ConfigMenu.SELF)) return ConfigOptions.find(ConfigOptions.LISTENER, key.substring(ConfigMenu.SELF.length()));
        if (key.startsWith(ConfigMenu.PLAYER)) return ConfigOptions.find(ConfigOptions.PROFILE, key.substring(key.lastIndexOf('.') + 1));
        return Optional.ofNullable(ConfigOptions.SERVER.get(key));
    }

    private void updateDoneButton() {
        if (doneButton != null) {
            doneButton.active = values.entrySet().stream()
                .allMatch(entry -> optionFor(entry.getKey()).map(option -> option.parse(entry.getValue()) != null).orElse(true));
        }
    }

    private Map<String, String> changes() {
        Map<String, String> changes = new HashMap<>();
        values.forEach((key, value) -> {
            if (!value.equals(initial.get(key))) changes.put(key, value);
        });
        return changes;
    }

    private void onDone() {
        Map<String, String> chunk = new HashMap<>();
        for (Map.Entry<String, String> change : changes().entrySet()) {
            chunk.put(change.getKey(), change.getValue());
            if (chunk.size() == ConfigChangesPayload.MAX_CHANGES) {
                ClientPlayNetworking.send(new ConfigChangesPayload(chunk));
                chunk = new HashMap<>();
            }
        }
        if (!chunk.isEmpty()) ClientPlayNetworking.send(new ConfigChangesPayload(chunk));
        minecraft.setScreen(null);
    }

    @Override
    public void onClose() {
        if (changes().isEmpty()) {
            minecraft.setScreen(null);
            return;
        }
        minecraft.setScreen(new ConfirmScreen(discard -> minecraft.setScreen(discard ? null : this),
            Component.translatable("deeperdark.config.discard.title"), Component.translatable("deeperdark.config.discard.message")));
    }

    @Environment(EnvType.CLIENT)
    class CategoryList extends ObjectSelectionList<CategoryList.Entry> {
        CategoryList() {
            super(ConfigScreen.this.minecraft, SIDEBAR_WIDTH, layout.getContentHeight(), layout.getHeaderHeight(), 22);
            addEntry(new Entry(PLAYER_SOUNDS, PLAYER_SOUNDS_ICON));
            if (operator) ConfigOptions.CATEGORIES.forEach(category -> addEntry(new Entry(category.id(), category.icon())));
            children().stream().filter(entry -> entry.id.equals(selectedCategory)).findFirst().ifPresent(super::setSelected);
        }

        @Override
        public int getRowWidth() {
            return getWidth() - 16;
        }

        @Override
        public void setSelected(@Nullable Entry entry) {
            super.setSelected(entry);
            if (entry == null || entry.id.equals(selectedCategory) && searchText().isEmpty()) return;
            selectedCategory = entry.id;
            // Clearing a search repopulates through the search box's responder
            if (searchBox != null && !searchBox.getValue().isEmpty()) searchBox.setValue("");
            else if (optionList != null) optionList.populate();
        }

        @Environment(EnvType.CLIENT)
        class Entry extends ObjectSelectionList.Entry<Entry> {
            final String id;
            private final ItemStack icon;
            private final Component label;

            Entry(String id, Item icon) {
                this.id = id;
                this.icon = new ItemStack(icon);
                this.label = categoryLabel(id);
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                graphics.item(icon, getContentX() + 2, getContentYMiddle() - 8);
                graphics.text(font, label, getContentX() + 22, getContentYMiddle() - 4, -1);
            }

            @Override
            public Component getNarration() {
                return label;
            }
        }
    }

    static Component categoryLabel(String id) {
        return Component.translatable("deeperdark.config.category." + id);
    }
}
