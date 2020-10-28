package main.model.enums;

public enum TypeDecisionToPost {

  ACCEPT("accept"),
  DECLINE("decline");

  private final String typeDecision;

  TypeDecisionToPost(String typeDecision){
    this.typeDecision = typeDecision;
  }

  public String getDecision(){
    return typeDecision;
  }

}
