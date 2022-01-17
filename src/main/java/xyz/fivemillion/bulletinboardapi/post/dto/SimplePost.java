package xyz.fivemillion.bulletinboardapi.post.dto;

import lombok.Getter;
import lombok.Setter;
import xyz.fivemillion.bulletinboardapi.post.Post;

import java.time.LocalDateTime;

@Getter @Setter
public class SimplePost {
    private String title;
    private String writer;
    private Long views;
    private Long commentCount;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public SimplePost() {/*Empty*/}

    public SimplePost(String title, String writer, Long views, Long commentCount, LocalDateTime createAt, LocalDateTime updateAt) {
        this.title = title;
        this.writer = writer;
        this.views = views;
        this.commentCount = commentCount;
        this.createAt = createAt;
        this.updateAt = updateAt;
    }

    public SimplePost(Post post) {
        this.title = post.getTitle();
        this.writer = post.getWriter().getDisplayName();
        this.views = post.getViews();
        this.commentCount = 0L;
        this.createAt = post.getCreateAt();
        this.updateAt = post.getUpdateAt();
    }
}