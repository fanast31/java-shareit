package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean available;

    @JoinColumn(name = "owner_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User owner;

    @JoinColumn(name = "request_id")
    @ManyToOne
    @ToString.Exclude
    private ItemRequest request;

}
