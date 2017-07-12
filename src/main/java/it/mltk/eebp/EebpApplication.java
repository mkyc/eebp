package it.mltk.eebp;

import it.mltk.eebp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@SpringBootApplication
@EnableOAuth2Sso
public class EebpApplication extends WebSecurityConfigurerAdapter implements CommandLineRunner{


	@Autowired
	private PostService postService;


	public static void main(String[] args) {
		SpringApplication.run(EebpApplication.class, args);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.antMatcher("/**")
				.authorizeRequests()
				.antMatchers("/", "/post/**")
				.permitAll()
				.anyRequest()
				.authenticated();
	}

	@Override
	public void run(String... strings) throws Exception {
		postService.clean();

		for(int i = 0; i<55; i++) {
			postService.createPost("Do something in bash " + i,
					"there is a long story there is a long story there is a long story " +
							"there is a long story there is a long story there is a long story " +
							"there is a long story there is a long story there is a long story " +
							"there is a long story there is a long story there is a long story " +
							"here goes a code <br /><code>code</code><br /> and code ended" +
							"there is a long story there is a long story there is a long story " +
							"there is a long story there is a long story there is a long story " +
							"there is a long story and break <br /> there is a long story " +
							"there is a long story there is a long story there is a long story " +
							"there is a long story there is a long story there is a long story " +
							"there is a long story there is a long story and break <br />" +
							"there is a long story there is a long story there is a long story " +
							"there is a long story there is a long story there is a long story " +
							"there is a long story <p> there is a long story there is a long story " +
							"there is a long story there is a long story </p> " +
							"there is a long story there is a long story there is a long story ",
					"author" + i,
					"java", "spring", "spring boot", "azure", "kubernetes", "bash", "awk", "sed");
		}

	}
}
