package it.mltk.eebp.services;

import it.mltk.eebp.entity.*;
import it.mltk.eebp.exceptions.NotYetUpdatedException;
import it.mltk.eebp.repo.PostRepository;
import it.mltk.eebp.repo.TagRepository;
import it.mltk.eebp.retrofit.GitHubRetrofit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Component
public class GitHubService {

    private static final String API_URL = "https://api.github.com";
    private static final HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BASIC;

    Retrofit retrofit;
    GitHubRetrofit gitHubRetrofit;

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
    private FlexmarkService flexmarkService;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostRepository postRepository;


    public GitHubService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(level);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(logging);
        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        gitHubRetrofit = retrofit.create(GitHubRetrofit.class);

    }

    public List<GitHubContent> getContent(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        Call<List<GitHubContent>> call = gitHubRetrofit.repoContent(user, repo, path, clientId, clientSecret);
        return call.execute().body();
    }

    public GitHubContent getSingleContent(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        Call<GitHubContent> call = gitHubRetrofit.repoSingleContent(user, repo, path, clientId, clientSecret);
        return call.execute().body();
    }

    public GitHubContent getArticlesRoot(String user, String repo, String mainDir, String clientId, String clientSecret) throws IOException {
        List<GitHubContent> contents = this.getContent(user, repo, "", clientId, clientSecret);
        for(GitHubContent ghc : contents) {
            if(ghc.getName().equals(mainDir) && ghc.getType().equals("dir")) {
                return ghc;
            }
        }
        return null;
    }

    public GitHubTree getTree(String user, String repo, String sha, String clientId, String clientSecret) throws IOException {
        Call<GitHubTree> call = gitHubRetrofit.repoTree(user, repo, sha, clientId, clientSecret);
        return call.execute().body();
    }

    public List<GitHubCommit> getCommits(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        Call<List<GitHubCommit>> call = gitHubRetrofit.repoCommit(user, repo, path, clientId, clientSecret);
        return call.execute().body();
    }

    private Post createNewPost(GitHubTreeNode gitHubTreeNode) throws IOException {

        List<GitHubCommit> list = this.getCommits(repoUser, repoName, repoMainDir + "/" + gitHubTreeNode.getPath(), clientId, clientSecret);
        GitHubCommitter gitHubCommitter = null;
        for(GitHubCommit gitHubCommit : list) {
            gitHubCommitter = gitHubCommit.getAuthor();
            System.out.println("author: " + gitHubCommitter);
        }
        String blobPath = repoMainDir + "/" + gitHubTreeNode.getPath();
        GitHubContent ghc = this.getSingleContent(repoUser, repoName, blobPath, clientId, clientSecret);
        URLConnection connection = new URL(ghc.getDownloadUrl()).openConnection();
        String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();
        Matcher m = Pattern.compile("(?m)^Tags:.*$").matcher(text);
        String[] tags = null;
        while (m.find()) {
            String tagsLine = m.group().replaceAll("Tags:", "").trim();
            tags = tagsLine.split(",");
            break;
        }
        text = text.replaceFirst("(?m)^Tags.*", "");

        Post result = new Post();
        String title = text.substring(0, text.indexOf(System.getProperty("line.separator"))).replaceAll("[^\\w\\d\\s]", "").trim();
        System.out.println(title);
        String content = flexmarkService.parseMarkdown(text.substring(text.indexOf(System.getProperty("line.separator")), text.length()));
        ArrayList<Tag> tt = new ArrayList<>();
        for(String s : tags) {
            Tag t = tagRepository.findByName(s.trim());
            if(t == null) {
                t = new Tag(s.trim());
                t = tagRepository.save(t);
            }
            tt.add(t);
        }
        String author = gitHubCommitter.getLogin();
        String authorUrl = gitHubCommitter.getHtmlUrl();
        String avatarUrl = gitHubCommitter.getAvatarUrl();
        String urlTitle = title.replaceAll("\\W", "");
        String[] pathElements = this.extractPathElements(gitHubTreeNode.getPath());
        result.setTitle(title);
        result.setContent(content);
        result.setTags(tt);
        result.setAuthor(author);
        result.setUrlTitle(urlTitle);
        result.setAuthorUrl(authorUrl);
        result.setAvatarUrl(avatarUrl);
        result.setSha(gitHubTreeNode.getSha());
        result.setPath(gitHubTreeNode.getPath());
        LocalDateTime ldt = LocalDateTime.now();
        LocalTime lt = ldt.toLocalTime();
        LocalDate ld = ldt.toLocalDate();
        result.setCreated(lt);
        if(pathElements != null) {
            result.setYear(Integer.valueOf(pathElements[0]));
            result.setMonth(Integer.valueOf(pathElements[1]));
            result.setDay(Integer.valueOf(pathElements[2]));
        } else {
            result.setYear(ld.getYear());
            result.setMonth(ld.getMonthValue());
            result.setDay(ld.getDayOfMonth());
        }
        return result;
    }

    private String[] extractPathElements(String path) {
        String[] splittedPath = path.split("/");
        if(splittedPath.length == 4) {
            return splittedPath;
        }
        return null;
    }

    @Scheduled(fixedRate = 5000)
    void scheduledCheck() throws IOException, InterruptedException {
        GitHubContent article = getArticlesRoot(repoUser, repoName, repoMainDir, clientId, clientSecret);
        GitHubTree ght = getTree(repoUser, repoName, article.getSha(), clientId, clientSecret);

        for(GitHubTreeNode gitHubTreeNode : ght.getTree()) {
            if(gitHubTreeNode.getType().equals("blob")) {
                List<Post> testListWithPathAndSha = postRepository.findAllByPathAndSha(gitHubTreeNode.getPath(), gitHubTreeNode.getSha());
                List<Post> testListWithPath = postRepository.findAllByPath(gitHubTreeNode.getPath());
                if(testListWithPathAndSha != null && testListWithPathAndSha.size() == 1) {
                    //nothing to do
                    System.out.println("is good: " + gitHubTreeNode.getPath());
                } else if (testListWithPath != null && testListWithPath.size() == 1) {
                    //update
                    System.out.println("update " + gitHubTreeNode.getPath());
                    Post p = null;
                    do {
                        try {
                            p = this.updatePost(testListWithPath.get(0), gitHubTreeNode);
                        } catch (NotYetUpdatedException e) {
                            e.printStackTrace();
                            sleep(1000);
                        }
                    } while (p != null);
                    System.out.println(p);
                    postRepository.save(p);
                } else {
                    //create new
                    Post post = createNewPost(gitHubTreeNode);
                    if(post!= null) {
                        System.out.println(post);
                        postRepository.save(post);
                    }
                }

            }
        }
    }

    private Post updatePost(Post post, GitHubTreeNode gitHubTreeNode) throws IOException, NotYetUpdatedException {
        Post newPost = this.createNewPost(gitHubTreeNode);
        if(newPost.getSha() == post.getSha()) {
            throw new NotYetUpdatedException();
        }
        post.update(newPost);
        return post;
    }
}
