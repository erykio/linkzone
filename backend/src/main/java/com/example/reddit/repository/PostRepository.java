package com.example.reddit.repository;

import com.example.reddit.dto.IPostResponseDto;
import com.example.reddit.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.websocket.server.PathParam;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByGroupName(String name);

    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " WHERE g.name = :name" +
            " ORDER BY ?#{#pageable}",
            countQuery = "SELECT count(*) FROM posts p LEFT JOIN group_tbl g ON g.id = p.group_id WHERE g.name = :name",
            nativeQuery = true)
    Page<IPostResponseDto> findByGroupName(@Param("name") String name, @Param("pageable") Pageable pageable, @Param("accountId") Long accountId);

    Page<Post> findByAccountUsername(String username, Pageable pageable);

    // TODO countQuery
    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT pu.is_upvote FROM post_upvote pu WHERE pu.post_id = p.id AND pu.account_id = :accountId) as upvoted," +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " INNER JOIN group_membership gm ON gm.group_id = p.group_id AND gm.user_id = :accountId" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id" +
            " ORDER BY upvotedCount DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<IPostResponseDto> findTop(@Param("accountId") Long accountId, @Param("pageable") Pageable pageable);

    @Query(value = "SELECT p.id as id, p.title as title, p.content as content, p.slug as slug, p.type as type, a.username as author, g.name as groupName, p.locked as locked, " +
            " (SELECT SUM(pu.is_upvote) FROM post_upvote pu WHERE pu.post_id = p.id) as upvotedCount" +
            " FROM posts p" +
            " LEFT JOIN group_membership gm ON gm.group_id = p.group_id" +
            " JOIN accounts a ON a.id = p.account_id" +
            " JOIN group_tbl g ON g.id = p.group_id AND g.is_default" +
            " ORDER BY upvotedCount DESC, ?#{#pageable}",
            nativeQuery = true)
    Page<IPostResponseDto> findTop(@Param("pageable") Pageable pageable);

    @Query(value = "SELECT p.id as id FROM posts p" +
            " LEFT JOIN group_tbl g ON g.id = p.group_id" +
            " INNER JOIN post_upvote pu ON pu.post_id = p.id" +
            " WHERE pu.is_upvote AND p.account_id = :accountId" +
            " ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<IPostResponseDto> findUpvoted(Long accountId, Pageable pageable);
}
