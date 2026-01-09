package net.minecraft.structure.processor;

import java.util.List;

public class StructureProcessorList {
   private final List list;

   public StructureProcessorList(List list) {
      this.list = list;
   }

   public List getList() {
      return this.list;
   }

   public String toString() {
      return "ProcessorList[" + String.valueOf(this.list) + "]";
   }
}
