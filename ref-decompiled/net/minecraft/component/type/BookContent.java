package net.minecraft.component.type;

import java.util.List;

public interface BookContent {
   List pages();

   Object withPages(List pages);
}
