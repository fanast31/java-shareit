package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    @Test
    public void shouldReturnNullIfCommentIsNull() {
        assertNull(CommentMapper.toCommentDtoResponse(null));
    }

    @Test
    public void toCommentDtoResponse() {

        User author = new User();
        author.setName("John Doe");
        Comment comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .author(author)
                .created(LocalDateTime.now())
                .build();

        CommentDtoResponse dto = CommentMapper.toCommentDtoResponse(comment);

        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertNotNull(dto.getCreated());

    }

    @Test
    public void shouldReturnNullIfDtoIsNull() {
        assertNull(CommentMapper.toComment(null));
    }

    @Test
    void toComment() {

        CommentDtoRequest dtoRequest = CommentDtoRequest.builder()
                .text("Needs improvement")
                .build();

        Comment comment = CommentMapper.toComment(dtoRequest);

        assertNull(comment.getId());
        assertEquals("Needs improvement", comment.getText());

    }

}