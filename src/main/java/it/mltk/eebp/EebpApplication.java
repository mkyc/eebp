package it.mltk.eebp;

import it.mltk.eebp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class EebpApplication implements CommandLineRunner{


	@Autowired
	private PostService postService;


	public static void main(String[] args) {
		SpringApplication.run(EebpApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		postService.clean();

		postService.createPost("How to create something",
				"here is a content prepared with text",
				"author1",
				"java", "test");
		postService.createPost("Do something in bash",
				"here goes a code <br /><code>code</code><br /> and code ended",
				"author2",
				"java", "foo");

	}
}
