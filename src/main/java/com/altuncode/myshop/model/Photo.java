package com.altuncode.myshop.model;

import com.altuncode.myshop.model.interfaces.IProfuctFiles;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;


@Entity
@Table(name = "photo")
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public class Photo implements IProfuctFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_photo")
    private Integer orderImg = 1;

    @Column(name = "url", nullable = false,unique = true)
    private String url;

    @Column(name = "type", insertable = false, updatable = false)
    private String type; // To identify if the image is for a product or a service.

    @Override
    public List<String> getAllowedExtensions() {
        return Arrays.asList("jpg", "jpeg", "png", "webp");
    }

    @Override
    public List<String> getAllowedContentTypes() {
        return Arrays.asList("image/jpeg", "image/png", "image/webp");
    }
}
