package main.model.enums;

public enum VoteType {

    LIKE(1), DISLIKE(-1);

    private int value;

    VoteType(int value){
      this.value = value;
    }

    public int getValue(){
      return value;
    }

}
