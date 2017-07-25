package it.mltk.eebp;

import it.mltk.eebp.entity.GitHubCommit;
import it.mltk.eebp.entity.GitHubCommitter;
import it.mltk.eebp.entity.GitHubContent;
import it.mltk.eebp.entity.GitHubTree;
import it.mltk.eebp.services.FlexmarkService;
import it.mltk.eebp.services.GitHubService;
import it.mltk.eebp.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Scanner;


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
	@Autowired
	private FlexmarkService flexmarkService;


	public static void main(String[] args) {
		SpringApplication.run(EebpApplication.class, args);
	}


	@Override
	public void run(String... strings) throws Exception {
		postService.clean();
        List<GitHubContent> res = gitHubService.getFiles(repoUser, repoName, repoMainDir, clientId, clientSecret);
        for(GitHubContent ghc : res) {
            //System.out.println(ghc);
			URLConnection connection = new URL(ghc.getDownloadUrl()).openConnection();
			String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
			//System.out.println(text);
			List<GitHubCommit> list = gitHubService.getCommits(repoUser, repoName, ghc.getPath(), clientId, clientSecret);
			GitHubCommitter author = null;
			for(GitHubCommit ghco : list) {
				//System.out.println(ghco);
				author = ghco.getAuthor();
			}
			//TODO add date parameters to post from ghc path
			postService.createPost(text.substring(0, 20).replaceAll("\\W", ""), flexmarkService.parseMarkdown(text), author.getLogin());
		}

//		TODO make this better version
      GitHubContent article = gitHubService.getArticlesRoot(repoUser, repoName, repoMainDir, clientId, clientSecret);
		GitHubTree ght = gitHubService.getTree(repoUser, repoName, article.getSha(), clientId, clientSecret);
		System.out.println(ght);

	}
}
