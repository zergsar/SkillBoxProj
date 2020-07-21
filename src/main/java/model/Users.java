package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Calendar;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private byte isModerator;
    private Calendar regTime;
    private String name;
    private String email;
    private String password;
    private String code;
    private String photo;

}
