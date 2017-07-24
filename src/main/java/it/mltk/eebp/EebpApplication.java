package it.mltk.eebp;

import it.mltk.eebp.entity.GitHubContent;
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


    public static void main(String[] args) {
		SpringApplication.run(EebpApplication.class, args);
	}


	@Override
	public void run(String... strings) throws Exception {
		postService.clean();

		for(int i = 0; i<25; i++) {
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

        List<GitHubContent> res = gitHubService.getFiles(repoUser, repoName, repoMainDir, clientId, clientSecret);
        for(GitHubContent ghc : res) {
            System.out.println(ghc.getName() + " " + ghc.getType() + " " + ghc.getPath() + " " + ghc.getDownloadUrl());
			URLConnection connection = new URL(ghc.getDownloadUrl()).openConnection();
			String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
			//System.out.println(text);
		}
	}
}
