package main.model.enums;

public enum SettingsValue {

  YES("YES"),
  NO("NO");

  private final String settingsValue;

  SettingsValue(String settingsValue){
    this.settingsValue = settingsValue;
  }

  public String getValue(){
    return settingsValue;
  }
}
