package net.noahsarch.deeperdark.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.PlayerFaceExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.noahsarch.deeperdark.config.ConfigMenu;
import net.noahsarch.deeperdark.config.ConfigOption;
import net.noahsarch.deeperdark.config.ConfigOptions;
import net.noahsarch.deeperdark.sound.PlayerSoundManager;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/** The right-hand list of {@link ConfigScreen}: section headers followed by one row per option. */
@Environment(EnvType.CLIENT)
class ConfigOptionList extends ContainerObjectSelectionList<ConfigOptionList.Row> {
    private static final int RESET_WIDTH = 44;
    private static final int GAP = 4;
    private static final Component RESET = Component.translatable("controls.reset");
    private static final Component PREVIEW = Component.literal("▶");

    private final ConfigScreen screen;

    ConfigOptionList(ConfigScreen screen, Minecraft minecraft, int height, int y) {
        super(minecraft, screen.width, height, y, 24);
        this.screen = screen;
        populate();
    }

    @Override
    public int getRowWidth() {
        return Math.min(getWidth() - 24, 360);
    }

    /** Shows the selected category, or every match across categories while searching. */
    void populate() {
        clearEntries();
        String filter = screen.searchText();
        if (!filter.isEmpty() || ConfigScreen.PLAYER_SOUNDS.equals(screen.selectedCategory)) addPlayerSounds(filter);
        for (ConfigOptions.Category category : screen.operator ? ConfigOptions.CATEGORIES : List.<ConfigOptions.Category>of()) {
            if (!filter.isEmpty() || category.id().equals(screen.selectedCategory)) {
                Component categoryLabel = ConfigScreen.categoryLabel(category.id());
                addSection(new Row.Header(categoryLabel, category.icon()), category.options().stream()
                    .filter(option -> matches(filter, option, categoryLabel))
                    .map(option -> new OptionRow(option.key(), option)));
            }
        }
        setScrollAmount(0);
    }

    private void addPlayerSounds(String filter) {
        Component categoryLabel = ConfigScreen.categoryLabel(ConfigScreen.PLAYER_SOUNDS);
        addSection(new Row.Header(Component.translatable("deeperdark.config.section.listening"), ConfigScreen.PLAYER_SOUNDS_ICON),
            ConfigOptions.LISTENER.stream()
                .filter(option -> matches(filter, option, categoryLabel))
                .map(option -> new OptionRow(ConfigMenu.SELF + option.key(), option)));
        for (String name : screen.profiles) addProfile(name, filter, categoryLabel);
        if (screen.operator && filter.isEmpty()) addEntry(new AddPlayerRow());
    }

    private void addProfile(String name, String filter, Component categoryLabel) {
        boolean nameMatches = name.toLowerCase(Locale.ROOT).contains(filter);
        addSection(new PlayerHeader(name), ConfigOptions.PROFILE.stream()
            .filter(option -> nameMatches || matches(filter, option, categoryLabel))
            .map(option -> new OptionRow(ConfigMenu.PLAYER + name + "." + option.key(), option)));
    }

    private void addSection(Row header, Stream<OptionRow> rows) {
        List<OptionRow> list = rows.toList();
        if (list.isEmpty()) return;
        addEntry(header);
        list.forEach(this::addEntry);
    }

    private static boolean matches(String filter, ConfigOption<?> option, Component categoryLabel) {
        return filter.isEmpty()
            || option.key().contains(filter)
            || label(option).getString().toLowerCase(Locale.ROOT).contains(filter)
            || description(option).getString().toLowerCase(Locale.ROOT).contains(filter)
            || categoryLabel.getString().toLowerCase(Locale.ROOT).contains(filter);
    }

    private static Component label(ConfigOption<?> option) {
        return Component.translatable("deeperdark.config.option." + option.key());
    }

    private static Component description(ConfigOption<?> option) {
        Component description = Component.translatable("deeperdark.config.option." + option.key() + ".desc");
        return ComponentUtils.isTranslationResolvable(description) ? description : Component.empty();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractWidgetRenderState(graphics, mouseX, mouseY, a);
        Row hovered = getHovered();
        if (hovered instanceof OptionRow row) graphics.setTooltipForNextFrame(row.tooltip, mouseX, mouseY);
    }

    @Environment(EnvType.CLIENT)
    abstract static class Row extends ContainerObjectSelectionList.Entry<Row> {
        /** A bold section title with an item icon. */
        @Environment(EnvType.CLIENT)
        static class Header extends Row {
            private final Component label;
            private final @Nullable ItemStack icon;

            Header(Component label, @Nullable Item icon) {
                this.label = label.copy().withStyle(ChatFormatting.BOLD, ChatFormatting.YELLOW);
                this.icon = icon == null ? null : new ItemStack(icon);
            }

            @Override
            public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
                if (icon != null) graphics.item(icon, getContentX(), getContentYMiddle() - 8);
                graphics.text(Minecraft.getInstance().font, label, getContentX() + 20, getContentYMiddle() - 4, -1);
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return List.of();
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return List.of(new NarratableEntry() {
                    @Override
                    public NarrationPriority narrationPriority() {
                        return NarrationPriority.HOVERED;
                    }

                    @Override
                    public void updateNarration(NarrationElementOutput output) {
                        output.add(NarratedElementType.TITLE, label);
                    }
                });
            }
        }
    }

    /** A profile header showing the player's face: their live skin if online, else the default for their name. */
    @Environment(EnvType.CLIENT)
    class PlayerHeader extends Row.Header {
        private final PlayerSkin skin;

        PlayerHeader(String name) {
            super(name.equalsIgnoreCase(screen.self) ? Component.translatable("deeperdark.config.section.you", name) : Component.literal(name), null);
            PlayerInfo info = minecraft.getConnection() == null ? null : minecraft.getConnection().getPlayerInfoIgnoreCase(name);
            this.skin = info != null ? info.getSkin() : DefaultPlayerSkin.get(UUIDUtil.createOfflinePlayerUUID(name));
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            PlayerFaceExtractor.extractRenderState(graphics, skin, getContentX(), getContentYMiddle() - 8, 16);
            super.extractContent(graphics, mouseX, mouseY, hovered, a);
        }
    }

    /** Label on the left; the value control and a Reset button on the right. */
    @Environment(EnvType.CLIENT)
    class OptionRow extends Row {
        private final String key;
        private final ConfigOption<?> option;
        private final Component label;
        final List<FormattedCharSequence> tooltip = new ArrayList<>();
        private final List<AbstractWidget> children = new ArrayList<>();
        private final AbstractWidget control;
        private final @Nullable Button preview;
        private final Button reset;
        private List<FormattedCharSequence> labelLines = List.of();
        private int labelWidth = -1;

        OptionRow(String key, ConfigOption<?> option) {
            this.key = key;
            this.option = option;
            this.label = label(option);
            String value = screen.values.get(key);

            this.control = switch (option.kind()) {
                case BOOL -> CycleButton.onOffBuilder(Boolean.parseBoolean(value)).displayOnlyValue()
                    .create(0, 0, 60, 20, label, (button, on) -> changed(on.toString()));
                case INT, DOUBLE, SOUND -> {
                    EditBox box = new EditBox(minecraft.font, 0, 0, option.kind() == ConfigOption.Kind.SOUND ? 140 : 60, 20, label);
                    box.setMaxLength(256);
                    box.setValue(value);
                    box.setResponder(this::changed);
                    yield box;
                }
            };
            this.preview = option.kind() == ConfigOption.Kind.SOUND
                ? Button.builder(PREVIEW, button -> playPreview()).width(20).build() : null;
            this.reset = Button.builder(RESET, button -> resetToDefault()).width(RESET_WIDTH).build();
            children.add(control);
            if (preview != null) children.add(preview);
            children.add(reset);

            tooltip.add(Component.literal(key).withStyle(ChatFormatting.YELLOW).getVisualOrderText());
            tooltip.addAll(minecraft.font.split(description(option), 200));
            tooltip.add(Component.translatable("deeperdark.config.default",
                option.defaultValue().isEmpty() ? Component.translatable("deeperdark.config.none") : Component.literal(option.defaultValue()))
                .withStyle(ChatFormatting.GRAY).getVisualOrderText());
            if (option.kind() == ConfigOption.Kind.INT || option.kind() == ConfigOption.Kind.DOUBLE) {
                tooltip.add(range(option).withStyle(ChatFormatting.GRAY).getVisualOrderText());
            }
            refresh(value);
        }

        private void changed(String value) {
            screen.setValue(key, value);
            refresh(value);
        }

        private void refresh(String value) {
            boolean valid = option.parse(value) != null;
            if (control instanceof EditBox box) box.setTextColor(valid ? 0xFFE0E0E0 : 0xFFFF5555);
            if (preview != null) preview.active = valid && !value.isBlank();
            reset.active = !value.equals(option.defaultValue());
        }

        @SuppressWarnings("unchecked")
        private void resetToDefault() {
            String value = option.defaultValue();
            if (control instanceof CycleButton<?> button) ((CycleButton<Boolean>) button).setValue(Boolean.parseBoolean(value));
            if (control instanceof EditBox box) box.setValue(value);
            changed(value);
        }

        private void playPreview() {
            Identifier sound = PlayerSoundManager.parseSoundId(screen.values.get(key).trim());
            if (sound == null) return;
            String pitchKey = key.substring(0, key.lastIndexOf('.') + 1) + "pitch";
            Object pitch = ConfigOptions.find(ConfigOptions.PROFILE, "pitch").map(o -> o.parse(screen.values.getOrDefault(pitchKey, ""))).orElse(null);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvent.createVariableRangeEvent(sound), pitch instanceof Double d ? d.floatValue() : 1.0F));
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            int right = getContentRight();
            reset.setPosition(right - RESET_WIDTH, getContentY());
            control.setPosition(reset.getX() - GAP - control.getWidth(), getContentY());
            int controlsLeft = control.getX();
            if (preview != null) {
                preview.setPosition(control.getX() - 2 - preview.getWidth(), getContentY());
                controlsLeft = preview.getX();
            }

            int width = controlsLeft - GAP - getContentX();
            if (width != labelWidth) {
                labelWidth = width;
                labelLines = minecraft.font.split(label, Math.max(width, 20));
            }
            int textY = labelLines.size() == 1 ? getContentY() + 6 : getContentY() + 1;
            for (int i = 0; i < Math.min(labelLines.size(), 2); i++) {
                graphics.text(minecraft.font, labelLines.get(i), getContentX(), textY + i * 10, -1);
            }
            for (AbstractWidget child : children) child.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return children;
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return children;
        }
    }

    private static MutableComponent range(ConfigOption<?> option) {
        boolean integer = option.kind() == ConfigOption.Kind.INT;
        String min = integer ? String.valueOf((long) option.min()) : String.valueOf(option.min());
        if (option.max() >= Integer.MAX_VALUE) return Component.translatable("deeperdark.config.minimum", min);
        String max = integer ? String.valueOf((long) option.max()) : String.valueOf(option.max());
        return Component.translatable("deeperdark.config.range", min, max);
    }

    /** Operators type a player name to start editing that player's sounds, online or not. */
    @Environment(EnvType.CLIENT)
    class AddPlayerRow extends Row {
        private final EditBox name;
        private final Button add;

        AddPlayerRow() {
            this.name = new EditBox(minecraft.font, 0, 0, 140, 20, Component.translatable("deeperdark.config.add_player.name"));
            name.setMaxLength(16);
            name.setHint(Component.translatable("deeperdark.config.add_player.name").withStyle(EditBox.SEARCH_HINT_STYLE));
            // Appends in place rather than repopulating: this row is mid-click and must stay in the list
            this.add = Button.builder(Component.translatable("deeperdark.config.add_player"), button -> {
                String player = name.getValue();
                if (!screen.addProfile(player)) return;
                removeEntry(this);
                addProfile(player, "", ConfigScreen.categoryLabel(ConfigScreen.PLAYER_SOUNDS));
                addEntry(this);
                name.setValue("");
                setScrollAmount(maxScrollAmount());
            }).width(90).build();
            add.active = false;
            name.setResponder(text -> add.active = ConfigScreen.PLAYER_NAME.matcher(text).matches()
                && screen.profiles.stream().noneMatch(text::equalsIgnoreCase));
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            add.setPosition(getContentRight() - add.getWidth(), getContentY());
            name.setPosition(add.getX() - GAP - name.getWidth(), getContentY());
            name.extractRenderState(graphics, mouseX, mouseY, a);
            add.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(name, add);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(name, add);
        }
    }
}
