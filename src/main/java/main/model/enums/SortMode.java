package main.model.enums;

public enum SortMode {

  RECENT("recent"),
  POPULAR("popular"),
  BEST("best"),
  EARLY("early");

  private final String sortMode;

  SortMode(String sortMode) {
    this.sortMode = sortMode;
  }

  public String getMode() {
    return sortMode;
  }
}
