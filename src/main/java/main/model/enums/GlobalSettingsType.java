package main.model.enums;

public enum GlobalSettingsType {

  MULTIUSER_MODE("MULTIUSER_MODE"),
  POST_PREMODERATION("POST_PREMODERATION"),
  STATISTICS_IS_PUBLIC("STATISTICS_IS_PUBLIC");

  private final String globalSettings;

  GlobalSettingsType(String gs){
    this.globalSettings = gs;
  }

  public String getValue(){
    return globalSettings;
  }

}
