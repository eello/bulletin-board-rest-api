package xyz.fivemillion.bulletinboardapi.post.comment.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.fivemillion.bulletinboardapi.error.*;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.IllegalArgumentException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.comment.Comment;
import xyz.fivemillion.bulletinboardapi.post.comment.repository.CommentRepository;
import xyz.fivemillion.bulletinboardapi.user.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    CommentRepository commentRepository;
    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    @DisplayName("register fail: writer=null")
    void register_fail_writerIsNull() {
        //given
        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();
        String content = "comment";

        //when
        UnAuthorizedException thrown =
                assertThrows(UnAuthorizedException.class, () -> commentService.register(null, post, content));

        //then
        assertEquals(Error.UNKNOWN_USER, thrown.getError());
    }

    @Test
    @DisplayName("register fail: post=null")
    void register_fail_postIsNull() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();
        String content = "comment";

        //when
        NotFoundException thrown =
                assertThrows(NotFoundException.class, () -> commentService.register(writer, null, content));

        //then
        assertEquals(Error.POST_NOT_FOUND, thrown.getError());
    }

    @Test
    @DisplayName("register fail: content=null")
    void register_fail_contentIsNull() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        //when
        IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> commentService.register(writer, post, null));

        //then
        assertEquals(Error.CONTENT_IS_NULL_OR_BLANK, thrown.getError());
    }

    @Test
    @DisplayName("register fail: content=blank")
    void register_fail_contentIsBlank() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        String content = "";

        //when
        IllegalArgumentException thrown =
                assertThrows(IllegalArgumentException.class, () -> commentService.register(writer, post, content));

        //then
        assertEquals(Error.CONTENT_IS_NULL_OR_BLANK, thrown.getError());
    }

    @Test
    @DisplayName("register fail: EntitySaveException")
    void register_fail_entitySaveException() {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        String content = "comment";

        doThrow(new EntitySaveException(Error.UNKNOWN_USER_OR_POST)).when(commentRepository).save(any(Comment.class));

        //when
        EntitySaveException thrown =
                assertThrows(EntitySaveException.class, () -> commentService.register(writer, post, content));

        //then
        assertEquals(Error.UNKNOWN_USER_OR_POST, thrown.getError());
    }

    @Test
    @DisplayName("register success")
    void register_success() throws Exception {
        //given
        User writer = User.builder()
                .email("abc@test.com")
                .displayName("display name")
                .build();

        Post post = Post.builder()
                .title("title")
                .content("content")
                .build();

        String content = "comment";

        //when
        Comment result = commentService.register(writer, post, content);

        //then
        verify(commentRepository, times(1)).save(any(Comment.class));
        assertEquals(content, result.getContent());
        assertEquals(writer, result.getWriter());
        assertEquals(post, result.getPost());
        assertNotNull(result.getCreateAt());
    }
}