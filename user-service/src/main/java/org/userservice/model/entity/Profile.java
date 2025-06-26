package org.userservice.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "profile_customizations")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Double sellerRating;
    private Double buyerRating;

    private String country;

    private String info;

    @Column(length = 2000)
    private String bio;

    private String imageUrl;

    private String backgroundUrl;

    @Column(length = 100)
    private String themeColorHex; // Например, "#6A4BFF"

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> socialLinks;

    @ElementCollection
    @CollectionTable(name = "profile_badges", joinColumns = @JoinColumn(name = "customization_id"))
    private Set<String> unlockedBadges; // "premium", "early_adopter"

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> customSettings; // Доп. настройки

}