package backend_service.shop.entity;

import backend_service.shop.util.CategoryStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Category")
@Table(name = "tbl_category")
public class Category extends AbstractEntity<Long> {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryStatus status;

    // Quan hệ phân cấp cha - con (self join)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> children;

    @Column(name = "is_hot", columnDefinition = "BIT", nullable = false)
    @Builder.Default
    private Boolean isHot = false;

    @Column(name = "is_new", columnDefinition = "BIT", nullable = false)
    @Builder.Default
    private Boolean isNew = false;


}
