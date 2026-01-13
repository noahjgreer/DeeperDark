/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ibm.icu.text.Collator
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.jspecify.annotations.Nullable
 */
package net.minecraft.client.gui.screen.world;

import com.ibm.icu.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import org.jspecify.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class CustomizeBuffetLevelScreen
extends Screen {
    private static final Text SEARCH_TEXT = Text.translatable("createWorld.customize.buffet.search").fillStyle(TextFieldWidget.SEARCH_STYLE);
    private static final int field_49494 = 3;
    private static final int field_64199 = 15;
    final ThreePartsLayoutWidget layout;
    private final Screen parent;
    private final Consumer<RegistryEntry<Biome>> onDone;
    final Registry<Biome> biomeRegistry;
    private BuffetBiomesListWidget biomeSelectionList;
    RegistryEntry<Biome> biome;
    private ButtonWidget confirmButton;

    public CustomizeBuffetLevelScreen(Screen parent, GeneratorOptionsHolder generatorOptionsHolder, Consumer<RegistryEntry<Biome>> onDone) {
        super(Text.translatable("createWorld.customize.buffet.title"));
        this.parent = parent;
        this.onDone = onDone;
        this.layout = new ThreePartsLayoutWidget(this, 13 + this.textRenderer.fontHeight + 3 + 15, 33);
        this.biomeRegistry = generatorOptionsHolder.getCombinedRegistryManager().getOrThrow(RegistryKeys.BIOME);
        RegistryEntry registryEntry = (RegistryEntry)this.biomeRegistry.getOptional(BiomeKeys.PLAINS).or(() -> this.biomeRegistry.streamEntries().findAny()).orElseThrow();
        this.biome = generatorOptionsHolder.selectedDimensions().getChunkGenerator().getBiomeSource().getBiomes().stream().findFirst().orElse(registryEntry);
    }

    @Override
    public void close() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addHeader(DirectionalLayoutWidget.vertical().spacing(3));
        directionalLayoutWidget.getMainPositioner().alignHorizontalCenter();
        directionalLayoutWidget.add(new TextWidget(this.getTitle(), this.textRenderer));
        TextFieldWidget textFieldWidget = directionalLayoutWidget.add(new TextFieldWidget(this.textRenderer, 200, 15, Text.empty()));
        BuffetBiomesListWidget buffetBiomesListWidget = new BuffetBiomesListWidget();
        textFieldWidget.setPlaceholder(SEARCH_TEXT);
        textFieldWidget.setChangedListener(buffetBiomesListWidget::onSearch);
        this.biomeSelectionList = this.layout.addBody(buffetBiomesListWidget);
        DirectionalLayoutWidget directionalLayoutWidget2 = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
        this.confirmButton = directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.DONE, button -> {
            this.onDone.accept(this.biome);
            this.close();
        }).build());
        directionalLayoutWidget2.add(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close()).build());
        this.biomeSelectionList.setSelected((BuffetBiomesListWidget.BuffetBiomeItem)this.biomeSelectionList.children().stream().filter(entry -> Objects.equals(entry.biome, this.biome)).findFirst().orElse(null));
        this.layout.forEachChild(this::addDrawableChild);
        this.refreshWidgetPositions();
    }

    @Override
    protected void refreshWidgetPositions() {
        this.layout.refreshPositions();
        this.biomeSelectionList.position(this.width, this.layout);
    }

    void refreshConfirmButton() {
        this.confirmButton.active = this.biomeSelectionList.getSelectedOrNull() != null;
    }

    @Environment(value=EnvType.CLIENT)
    class BuffetBiomesListWidget
    extends AlwaysSelectedEntryListWidget<BuffetBiomeItem> {
        BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.client, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.layout.getContentHeight(), CustomizeBuffetLevelScreen.this.layout.getHeaderHeight(), 15);
            this.onSearch("");
        }

        private void onSearch(String search) {
            Collator collator = Collator.getInstance((Locale)Locale.getDefault());
            String string = search.toLowerCase(Locale.ROOT);
            List<BuffetBiomeItem> list = CustomizeBuffetLevelScreen.this.biomeRegistry.streamEntries().map(entry -> new BuffetBiomeItem((RegistryEntry.Reference<Biome>)entry)).sorted(Comparator.comparing(biome -> biome.text.getString(), collator)).filter(biome -> search.isEmpty() || biome.text.getString().toLowerCase(Locale.ROOT).contains(string)).toList();
            this.replaceEntries(list);
            this.refreshScroll();
        }

        @Override
        public void setSelected(@Nullable BuffetBiomeItem buffetBiomeItem) {
            super.setSelected(buffetBiomeItem);
            if (buffetBiomeItem != null) {
                CustomizeBuffetLevelScreen.this.biome = buffetBiomeItem.biome;
            }
            CustomizeBuffetLevelScreen.this.refreshConfirmButton();
        }

        @Environment(value=EnvType.CLIENT)
        class BuffetBiomeItem
        extends AlwaysSelectedEntryListWidget.Entry<BuffetBiomeItem> {
            final RegistryEntry.Reference<Biome> biome;
            final Text text;

            public BuffetBiomeItem(RegistryEntry.Reference<Biome> biome) {
                this.biome = biome;
                Identifier identifier = biome.registryKey().getValue();
                String string = identifier.toTranslationKey("biome");
                this.text = Language.getInstance().hasTranslation(string) ? Text.translatable(string) : Text.literal(identifier.toString());
            }

            @Override
            public Text getNarration() {
                return Text.translatable("narrator.select", this.text);
            }

            @Override
            public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
                context.drawTextWithShadow(CustomizeBuffetLevelScreen.this.textRenderer, this.text, this.getContentX() + 5, this.getContentY() + 2, -1);
            }

            @Override
            public boolean mouseClicked(Click click, boolean doubled) {
                BuffetBiomesListWidget.this.setSelected(this);
                return super.mouseClicked(click, doubled);
            }
        }
    }
}
