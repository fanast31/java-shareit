package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {

    public static CommentDtoResponse toCommentDtoResponse(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(CommentDtoRequest commentDtoRequest) {
        if (commentDtoRequest == null) {
            return null;
        }
        return Comment.builder()
                .text(commentDtoRequest.getText())
                .build();
    }

}
