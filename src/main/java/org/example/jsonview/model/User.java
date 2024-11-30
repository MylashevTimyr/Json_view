package org.example.jsonview.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.jsonview.view.UserDetails;
import org.example.jsonview.view.UserSummary;

import java.util.List;

@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UserSummary.class)
    private Long id;

    @NotBlank(message = "Введите имя")
    @JsonView(UserSummary.class)
    private String name;

    @NotBlank(message = "Введите email")
    @JsonView(UserSummary.class)
    private String email;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonView(UserDetails.class)
    @JsonManagedReference
    private List<Order> orders;
}
