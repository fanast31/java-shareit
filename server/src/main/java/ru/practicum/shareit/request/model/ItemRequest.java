package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @JoinColumn(name = "requester_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User requester;

    @Column(nullable = false)
    private LocalDateTime created;

}
