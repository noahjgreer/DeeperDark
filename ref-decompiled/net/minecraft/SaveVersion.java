package net.minecraft;

public record SaveVersion(int id, String series) {
   public static final String MAIN_SERIES = "main";

   public SaveVersion(int id, String series) {
      this.id = id;
      this.series = series;
   }

   public boolean isNotMainSeries() {
      return !this.series.equals("main");
   }

   public boolean isAvailableTo(SaveVersion other) {
      return this.series().equals(other.series());
   }

   public int id() {
      return this.id;
   }

   public String series() {
      return this.series;
   }
}
