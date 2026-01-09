package net.minecraft.server.function;

import java.util.List;
import net.minecraft.util.Identifier;

public interface Procedure {
   Identifier id();

   List entries();
}
