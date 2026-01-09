package net.minecraft.client.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.BasicItemModel;
import net.minecraft.client.render.item.model.CompositeItemModel;
import net.minecraft.client.render.item.model.ConditionItemModel;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.RangeDispatchItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.model.SpecialItemModel;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.render.item.property.bool.HasComponentProperty;
import net.minecraft.client.render.item.property.bool.UsingItemProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.render.item.property.select.ContextDimensionProperty;
import net.minecraft.client.render.item.property.select.ItemBlockStateProperty;
import net.minecraft.client.render.item.property.select.LocalTimeProperty;
import net.minecraft.client.render.item.property.select.SelectProperty;
import net.minecraft.client.render.item.tint.ConstantTintSource;
import net.minecraft.client.render.item.tint.TintSource;
import net.minecraft.component.ComponentType;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemModels {
   public static ItemModel.Unbaked basic(Identifier model) {
      return new BasicItemModel.Unbaked(model, List.of());
   }

   public static ItemModel.Unbaked tinted(Identifier model, TintSource... tints) {
      return new BasicItemModel.Unbaked(model, List.of(tints));
   }

   public static TintSource constantTintSource(int value) {
      return new ConstantTintSource(value);
   }

   public static ItemModel.Unbaked composite(ItemModel.Unbaked... models) {
      return new CompositeItemModel.Unbaked(List.of(models));
   }

   public static ItemModel.Unbaked special(Identifier base, SpecialModelRenderer.Unbaked specialModel) {
      return new SpecialItemModel.Unbaked(base, specialModel);
   }

   public static RangeDispatchItemModel.Entry rangeDispatchEntry(ItemModel.Unbaked model, float threshold) {
      return new RangeDispatchItemModel.Entry(threshold, model);
   }

   public static ItemModel.Unbaked rangeDispatch(NumericProperty property, ItemModel.Unbaked fallback, RangeDispatchItemModel.Entry... entries) {
      return new RangeDispatchItemModel.Unbaked(property, 1.0F, List.of(entries), Optional.of(fallback));
   }

   public static ItemModel.Unbaked rangeDispatch(NumericProperty property, float scale, ItemModel.Unbaked fallback, RangeDispatchItemModel.Entry... entries) {
      return new RangeDispatchItemModel.Unbaked(property, scale, List.of(entries), Optional.of(fallback));
   }

   public static ItemModel.Unbaked rangeDispatch(NumericProperty property, ItemModel.Unbaked fallback, List entries) {
      return new RangeDispatchItemModel.Unbaked(property, 1.0F, entries, Optional.of(fallback));
   }

   public static ItemModel.Unbaked rangeDispatch(NumericProperty property, List entries) {
      return new RangeDispatchItemModel.Unbaked(property, 1.0F, entries, Optional.empty());
   }

   public static ItemModel.Unbaked rangeDispatch(NumericProperty property, float scale, List entries) {
      return new RangeDispatchItemModel.Unbaked(property, scale, entries, Optional.empty());
   }

   public static ItemModel.Unbaked condition(BooleanProperty property, ItemModel.Unbaked onTrue, ItemModel.Unbaked onFalse) {
      return new ConditionItemModel.Unbaked(property, onTrue, onFalse);
   }

   public static SelectItemModel.SwitchCase switchCase(Object value, ItemModel.Unbaked model) {
      return new SelectItemModel.SwitchCase(List.of(value), model);
   }

   public static SelectItemModel.SwitchCase switchCase(List values, ItemModel.Unbaked model) {
      return new SelectItemModel.SwitchCase(values, model);
   }

   @SafeVarargs
   public static ItemModel.Unbaked select(SelectProperty property, ItemModel.Unbaked fallback, SelectItemModel.SwitchCase... cases) {
      return select(property, fallback, List.of(cases));
   }

   public static ItemModel.Unbaked select(SelectProperty property, ItemModel.Unbaked fallback, List cases) {
      return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch(property, cases), Optional.of(fallback));
   }

   @SafeVarargs
   public static ItemModel.Unbaked select(SelectProperty property, SelectItemModel.SwitchCase... cases) {
      return select(property, List.of(cases));
   }

   public static ItemModel.Unbaked select(SelectProperty property, List cases) {
      return new SelectItemModel.Unbaked(new SelectItemModel.UnbakedSwitch(property, cases), Optional.empty());
   }

   public static BooleanProperty usingItemProperty() {
      return new UsingItemProperty();
   }

   public static BooleanProperty hasComponentProperty(ComponentType component) {
      return new HasComponentProperty(component, false);
   }

   public static ItemModel.Unbaked overworldSelect(ItemModel.Unbaked overworldModel, ItemModel.Unbaked fallback) {
      return select((SelectProperty)(new ContextDimensionProperty()), fallback, (SelectItemModel.SwitchCase[])(switchCase((Object)World.OVERWORLD, overworldModel)));
   }

   public static ItemModel.Unbaked select(Property property, ItemModel.Unbaked fallback, Map valuesToModels) {
      List list = valuesToModels.entrySet().stream().sorted(Entry.comparingByKey()).map((entry) -> {
         String string = property.name((Comparable)entry.getKey());
         return new SelectItemModel.SwitchCase(List.of(string), (ItemModel.Unbaked)entry.getValue());
      }).toList();
      return select((SelectProperty)(new ItemBlockStateProperty(property.getName())), fallback, (List)list);
   }

   public static ItemModel.Unbaked christmasSelect(ItemModel.Unbaked regularModel, ItemModel.Unbaked christmasModel) {
      return select((SelectProperty)LocalTimeProperty.create("MM-dd", "", Optional.empty()), christmasModel, (List)List.of(switchCase(List.of("12-24", "12-25", "12-26"), regularModel)));
   }
}
