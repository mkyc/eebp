package it.mltk.eebp;

import it.mltk.eebp.entity.Post;
import it.mltk.eebp.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

		postRepository.save(new Post("title 1", "this is text of post", "author"));

	}
}
