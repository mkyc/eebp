package it.mltk.eebp;

import it.mltk.eebp.entity.GitHubContent;
import it.mltk.eebp.entity.GitHubTree;
import it.mltk.eebp.services.GitHubService;
import it.mltk.eebp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class EebpApplication implements CommandLineRunner{


    @Value("${eebp.clientId}")
    private String clientId;
    @Value("${eebp.clientSecret}")
    private String clientSecret;
    @Value("${eebp.repoName}")
    private String repoName;
    @Value("${eebp.repoUser}")
    private String repoUser;
    @Value("${eebp.repoMainDir}")
    private String repoMainDir;

	@Autowired
	private PostService postService;
    @Autowired
    private GitHubService gitHubService;


	public static void main(String[] args) {
		SpringApplication.run(EebpApplication.class, args);
	}


	@Override
	public void run(String... strings) throws Exception {
		postService.clean();

        GitHubContent article = gitHubService.getArticlesRoot(repoUser, repoName, repoMainDir, clientId, clientSecret);
		GitHubTree ght = gitHubService.getTree(repoUser, repoName, article.getSha(), clientId, clientSecret);

		gitHubService.extractPosts(ght, repoUser, repoName, clientId, clientSecret);
	}
}
