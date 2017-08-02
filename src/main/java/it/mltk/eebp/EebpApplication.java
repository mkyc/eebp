package it.mltk.eebp;

import it.mltk.eebp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class EebpApplication implements CommandLineRunner{




	@Autowired
	private PostService postService;


	public static void main(String[] args) {
		SpringApplication.run(EebpApplication.class, args);
	}


	@Override
	public void run(String... strings) throws Exception {
		postService.clean();
	}
}
