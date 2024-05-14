package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Item item;

    @JoinColumn(name = "author_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User author;

    @Column(nullable = false)
    private LocalDateTime created;

}
