package it.mltk.eebp;

import it.mltk.eebp.entity.Post;
import it.mltk.eebp.entity.Tag;
import it.mltk.eebp.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;


@SpringBootApplication
public class EebpApplication implements CommandLineRunner{


	@Autowired
	private PostRepository postRepository;

	public static void main(String[] args) {
		SpringApplication.run(EebpApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		postRepository.deleteAll();


		ArrayList<Tag> tags1= new ArrayList<Tag>();
		tags1.add(new Tag("java"));
		tags1.add(new Tag("test"));
		postRepository.save(new Post("title 2", "this is content of post", tags1, "author2"));

		ArrayList<Tag> tags2= new ArrayList<Tag>();
		tags2.add(new Tag("java"));
		tags2.add(new Tag("foo"));
		postRepository.save(new Post("title 1", "this is text of post", tags2, "author"));

		Post f1 = postRepository.findByTitle("title 1");

		System.out.println(f1.toString());

		List<Post> result = postRepository.findAllByTagsName("java");
		System.out.println(result.size());

		List<Post> res2 = postRepository.findInTitleAndContent("title");
		System.out.println(res2.size());

	}
}
