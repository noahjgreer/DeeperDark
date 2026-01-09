package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class EditGameRulesScreen extends Screen {
   private static final Text TITLE = Text.translatable("editGamerule.title");
   private static final int field_49559 = 8;
   final ThreePartsLayoutWidget layout = new ThreePartsLayoutWidget(this);
   private final Consumer ruleSaver;
   private final Set invalidRuleWidgets = Sets.newHashSet();
   private final GameRules gameRules;
   @Nullable
   private RuleListWidget ruleListWidget;
   @Nullable
   private ButtonWidget doneButton;

   public EditGameRulesScreen(GameRules gameRules, Consumer ruleSaveConsumer) {
      super(TITLE);
      this.gameRules = gameRules;
      this.ruleSaver = ruleSaveConsumer;
   }

   protected void init() {
      this.layout.addHeader(TITLE, this.textRenderer);
      this.ruleListWidget = (RuleListWidget)this.layout.addBody(new RuleListWidget(this.gameRules));
      DirectionalLayoutWidget directionalLayoutWidget = (DirectionalLayoutWidget)this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
      this.doneButton = (ButtonWidget)directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
         this.ruleSaver.accept(Optional.of(this.gameRules));
      }).build());
      directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> {
         this.close();
      }).build());
      this.layout.forEachChild((child) -> {
         ClickableWidget var10000 = (ClickableWidget)this.addDrawableChild(child);
      });
      this.refreshWidgetPositions();
   }

   protected void refreshWidgetPositions() {
      this.layout.refreshPositions();
      if (this.ruleListWidget != null) {
         this.ruleListWidget.position(this.width, this.layout);
      }

   }

   public void close() {
      this.ruleSaver.accept(Optional.empty());
   }

   private void updateDoneButton() {
      if (this.doneButton != null) {
         this.doneButton.active = this.invalidRuleWidgets.isEmpty();
      }

   }

   void markInvalid(AbstractRuleWidget ruleWidget) {
      this.invalidRuleWidgets.add(ruleWidget);
      this.updateDoneButton();
   }

   void markValid(AbstractRuleWidget ruleWidget) {
      this.invalidRuleWidgets.remove(ruleWidget);
      this.updateDoneButton();
   }

   @Environment(EnvType.CLIENT)
   public class RuleListWidget extends ElementListWidget {
      private static final int field_49561 = 24;

      public RuleListWidget(final GameRules gameRules) {
         super(MinecraftClient.getInstance(), EditGameRulesScreen.this.width, EditGameRulesScreen.this.layout.getContentHeight(), EditGameRulesScreen.this.layout.getHeaderHeight(), 24);
         final Map map = Maps.newHashMap();
         gameRules.accept(new GameRules.Visitor() {
            public void visitBoolean(GameRules.Key key, GameRules.Type type) {
               this.createRuleWidget(key, (name, description, ruleName, rule) -> {
                  return EditGameRulesScreen.thisx.new BooleanRuleWidget(EditGameRulesScreen.thisx, name, description, ruleName, rule);
               });
            }

            public void visitInt(GameRules.Key key, GameRules.Type type) {
               this.createRuleWidget(key, (name, description, ruleName, rule) -> {
                  return EditGameRulesScreen.thisx.new IntRuleWidget(name, description, ruleName, rule);
               });
            }

            private void createRuleWidget(GameRules.Key key, RuleWidgetFactory widgetFactory) {
               Text text = Text.translatable(key.getTranslationKey());
               Text text2 = Text.literal(key.getName()).formatted(Formatting.YELLOW);
               GameRules.Rule rule = gameRules.get(key);
               String string = rule.serialize();
               Text text3 = Text.translatable("editGamerule.default", Text.literal(string)).formatted(Formatting.GRAY);
               String string2 = key.getTranslationKey() + ".description";
               ImmutableList list;
               String string3;
               if (I18n.hasTranslation(string2)) {
                  ImmutableList.Builder builder = ImmutableList.builder().add(text2.asOrderedText());
                  Text text4 = Text.translatable(string2);
                  List var10000 = EditGameRulesScreen.this.textRenderer.wrapLines(text4, 150);
                  Objects.requireNonNull(builder);
                  var10000.forEach(builder::add);
                  list = builder.add(text3.asOrderedText()).build();
                  String var13 = text4.getString();
                  string3 = var13 + "\n" + text3.getString();
               } else {
                  list = ImmutableList.of(text2.asOrderedText(), text3.asOrderedText());
                  string3 = text3.getString();
               }

               ((Map)map.computeIfAbsent(key.getCategory(), (category) -> {
                  return Maps.newHashMap();
               })).put(key, widgetFactory.create(text, list, string3, rule));
            }
         });
         map.entrySet().stream().sorted(Entry.comparingByKey()).forEach((entry) -> {
            this.addEntry(EditGameRulesScreen.this.new RuleCategoryWidget(Text.translatable(((GameRules.Category)entry.getKey()).getCategory()).formatted(Formatting.BOLD, Formatting.YELLOW)));
            ((Map)entry.getValue()).entrySet().stream().sorted(Entry.comparingByKey(Comparator.comparing(GameRules.Key::getName))).forEach((e) -> {
               this.addEntry((AbstractRuleWidget)e.getValue());
            });
         });
      }

      public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
         super.renderWidget(context, mouseX, mouseY, deltaTicks);
         AbstractRuleWidget abstractRuleWidget = (AbstractRuleWidget)this.getHoveredEntry();
         if (abstractRuleWidget != null && abstractRuleWidget.description != null) {
            context.drawTooltip(abstractRuleWidget.description, mouseX, mouseY);
         }

      }
   }

   @Environment(EnvType.CLIENT)
   public class IntRuleWidget extends NamedRuleWidget {
      private final TextFieldWidget valueWidget;

      public IntRuleWidget(final Text name, final List description, final String ruleName, final GameRules.IntRule rule) {
         super(description, name);
         this.valueWidget = new TextFieldWidget(EditGameRulesScreen.this.client.textRenderer, 10, 5, 44, 20, name.copy().append("\n").append(ruleName).append("\n"));
         this.valueWidget.setText(Integer.toString(rule.get()));
         this.valueWidget.setChangedListener((value) -> {
            if (rule.validateAndSet(value)) {
               this.valueWidget.setEditableColor(-2039584);
               EditGameRulesScreen.this.markValid(this);
            } else {
               this.valueWidget.setEditableColor(-65536);
               EditGameRulesScreen.this.markInvalid(this);
            }

         });
         this.children.add(this.valueWidget);
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         this.drawName(context, y, x);
         this.valueWidget.setX(x + entryWidth - 45);
         this.valueWidget.setY(y);
         this.valueWidget.render(context, mouseX, mouseY, tickProgress);
      }
   }

   @Environment(EnvType.CLIENT)
   public class BooleanRuleWidget extends NamedRuleWidget {
      private final CyclingButtonWidget toggleButton;

      public BooleanRuleWidget(final EditGameRulesScreen editGameRulesScreen, final Text name, final List description, final String ruleName, final GameRules.BooleanRule rule) {
         super(description, name);
         this.toggleButton = CyclingButtonWidget.onOffBuilder(rule.get()).omitKeyText().narration((button) -> {
            return button.getGenericNarrationMessage().append("\n").append(ruleName);
         }).build(10, 5, 44, 20, name, (button, value) -> {
            rule.set(value, (MinecraftServer)null);
         });
         this.children.add(this.toggleButton);
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         this.drawName(context, y, x);
         this.toggleButton.setX(x + entryWidth - 45);
         this.toggleButton.setY(y);
         this.toggleButton.render(context, mouseX, mouseY, tickProgress);
      }
   }

   @Environment(EnvType.CLIENT)
   public abstract class NamedRuleWidget extends AbstractRuleWidget {
      private final List name;
      protected final List children = Lists.newArrayList();

      public NamedRuleWidget(@Nullable final List description, final Text name) {
         super(description);
         this.name = EditGameRulesScreen.this.client.textRenderer.wrapLines(name, 175);
      }

      public List children() {
         return this.children;
      }

      public List selectableChildren() {
         return this.children;
      }

      protected void drawName(DrawContext context, int x, int y) {
         if (this.name.size() == 1) {
            context.drawTextWithShadow(EditGameRulesScreen.this.client.textRenderer, (OrderedText)((OrderedText)this.name.get(0)), y, x + 5, -1);
         } else if (this.name.size() >= 2) {
            context.drawTextWithShadow(EditGameRulesScreen.this.client.textRenderer, (OrderedText)((OrderedText)this.name.get(0)), y, x, -1);
            context.drawTextWithShadow(EditGameRulesScreen.this.client.textRenderer, (OrderedText)((OrderedText)this.name.get(1)), y, x + 10, -1);
         }

      }
   }

   @FunctionalInterface
   @Environment(EnvType.CLIENT)
   private interface RuleWidgetFactory {
      AbstractRuleWidget create(Text name, List description, String ruleName, GameRules.Rule rule);
   }

   @Environment(EnvType.CLIENT)
   public class RuleCategoryWidget extends AbstractRuleWidget {
      final Text name;

      public RuleCategoryWidget(final Text text) {
         super((List)null);
         this.name = text;
      }

      public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickProgress) {
         context.drawCenteredTextWithShadow(EditGameRulesScreen.this.client.textRenderer, (Text)this.name, x + entryWidth / 2, y + 5, -1);
      }

      public List children() {
         return ImmutableList.of();
      }

      public List selectableChildren() {
         return ImmutableList.of(new Selectable() {
            public Selectable.SelectionType getType() {
               return Selectable.SelectionType.HOVERED;
            }

            public void appendNarrations(NarrationMessageBuilder builder) {
               builder.put(NarrationPart.TITLE, RuleCategoryWidget.this.name);
            }
         });
      }
   }

   @Environment(EnvType.CLIENT)
   public abstract static class AbstractRuleWidget extends ElementListWidget.Entry {
      @Nullable
      final List description;

      public AbstractRuleWidget(@Nullable List description) {
         this.description = description;
      }
   }
}
