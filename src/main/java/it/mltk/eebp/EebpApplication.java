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

		for(int i = 0; i<50; i++) {
			postService.createPost("Do something in bash" + i,
					"here goes a code <br /><code>code</code><br /> and code ended",
					"author" + i,
					"java", "spring", "spring boot", "azure", "kubernetes", "bash", "awk", "sed");
		}

	}
}
