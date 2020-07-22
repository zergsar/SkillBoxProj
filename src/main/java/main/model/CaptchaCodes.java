package main.model;


import javax.persistence.*;
import java.util.Calendar;

@Entity
@Table(name = "captcha_codes")
public class CaptchaCodes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "time", nullable = false)
    private Calendar time;

    @Column(name = "code", nullable = false)
    private byte code;

    @Column(name = "secret_code", nullable = false)
    private byte secretCode;


    public int getId() {
        return id;
    }

    public Calendar getTime() {
        return time;
    }

    public void setTime(Calendar time) {
        this.time = time;
    }

    public byte getCode() {
        return code;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public byte getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(byte secretCode) {
        this.secretCode = secretCode;
    }
}
