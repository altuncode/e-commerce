package com.altuncode.myshop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "myservice")
@Data
@NoArgsConstructor
public class MyService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "Name is required and cannot be blank.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @Column(name = "short_description")
    private String shortDescription;

    @NotNull(message = "Active status is required.")
    @Column(name = "active", nullable = false)
    private Boolean active=true;

    @OneToMany(mappedBy = "myService", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<MyServicePhoto> photos = new ArrayList<>();

    @Column(name = "created_date", nullable = false)
    @NotNull(message = "Created date is required.")
    @DateTimeFormat(pattern = "MM/dd/yyyy") // Match your input format
    private LocalDate createdDate;


    public void addPhoto(MyServicePhoto myServicePhoto) {
        photos.add(myServicePhoto);
        myServicePhoto.setMyService(this);
    }

    public void removePhoto(MyServicePhoto myServicePhoto) {
        photos.remove(myServicePhoto);
        myServicePhoto.setMyService(null);
    }
}
