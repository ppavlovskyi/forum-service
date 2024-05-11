package telran.java52.post.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import telran.java52.post.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {

	Stream<Post> findPostsByAuthorIgnoreCase(String author);

	@Query("{dateCreated:{$gte:?0,$lt:?1}}")
	Stream<Post> findPostsByPeriod(LocalDate from, LocalDate to);

	@Query("{ tags: { $all: ?0 } }")
	Stream<Post> findPostsByTags(List<String> tags);

}
