package com.altuncode.myshop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("MYSERVICE")
@Data
@NoArgsConstructor
public class MyServicePhoto extends Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "myservice_id", referencedColumnName = "id")
    private MyService myService;

}
