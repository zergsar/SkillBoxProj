package model;

import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.*;

@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private byte isActive;

    @Enumerated
    private Enum moderationStatus;




}
