package xyz.fivemillion.bulletinboardapi.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.fivemillion.bulletinboardapi.config.web.Pageable;
import xyz.fivemillion.bulletinboardapi.error.Error;
import xyz.fivemillion.bulletinboardapi.error.ForbiddenException;
import xyz.fivemillion.bulletinboardapi.error.NotFoundException;
import xyz.fivemillion.bulletinboardapi.error.UnAuthorizedException;
import xyz.fivemillion.bulletinboardapi.post.Post;
import xyz.fivemillion.bulletinboardapi.post.dto.PostRegisterRequest;
import xyz.fivemillion.bulletinboardapi.post.repository.PostRepository;
import xyz.fivemillion.bulletinboardapi.user.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public Post register(User writer, PostRegisterRequest request) {
        if (writer.getId() == null)
            throw new UnAuthorizedException(Error.UNKNOWN_USER);

        try {
            Post post = Post.builder()
                    .writer(writer)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .build();

            postRepository.save(post);

            return post;
        } catch (IllegalStateException e) {
            throw new UnAuthorizedException(Error.UNKNOWN_USER);
        }
    }

    @Override
    public List<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable.getOffset(), pageable.getSize());
    }

    @Override
    public Post findById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException(Error.POST_NOT_FOUND));
    }

    @Override
    @Transactional
    public void increaseView(Post post) {
        post.increaseView();
    }

    @Override
    public List<Post> findByQuery(String query, Pageable pageable) {
        return postRepository.findByQuery(query, pageable.getOffset(), pageable.getSize());
    }

    @Override
    @Transactional
    public void delete(User writer, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(Error.POST_NOT_FOUND));
        if (!writer.equals(post.getWriter()))
            throw new ForbiddenException(Error.NOT_POST_WRITER);

        postRepository.delete(post);
    }
}
