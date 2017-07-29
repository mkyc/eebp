package it.mltk.eebp.services;

import it.mltk.eebp.entity.*;
import it.mltk.eebp.repo.PostRepository;
import it.mltk.eebp.repo.TagRepository;
import it.mltk.eebp.retrofit.GitHubRetrofit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

@Component
public class GitHubService {

    private static final String API_URL = "https://api.github.com";
    private static final HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;

    @Value("${eebp.repoMainDir}")
    private String repoMainDir;

    Retrofit retrofit;
    GitHubRetrofit github;

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
        github = retrofit.create(GitHubRetrofit.class);

    }

    public List<GitHubContent> getContent(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        Call<List<GitHubContent>> call = github.repoContent(user, repo, path, clientId, clientSecret);
        return call.execute().body();
    }

    public GitHubContent getSingleContent(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        Call<GitHubContent> call = github.repoSingleContent(user, repo, path, clientId, clientSecret);
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

    public List<GitHubContent> getFiles(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        List<GitHubContent> result = new ArrayList<>();
        for(GitHubContent ghc : this.getContent(user, repo, path, clientId, clientSecret)) {
            if(ghc.getType().equals("dir")) {
                result.addAll(this.getFiles(user, repo, path + "/" + ghc.getName(), clientId, clientSecret));
            } else if(ghc.getType().equals("file")) {
                result.add(ghc);
            }
        }
        return result;
    }

    public GitHubTree getTree(String user, String repo, String sha, String clientId, String clientSecret) throws IOException {
        Call<GitHubTree> call = github.repoTree(user, repo, sha, clientId, clientSecret);
        return call.execute().body();
    }

    public List<GitHubCommit> getCommits(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        Call<List<GitHubCommit>> call = github.repoCommit(user, repo, path, clientId, clientSecret);
        return call.execute().body();
    }

    public void extractPosts(GitHubTree ght, String repoUser, String repoName, String clientId, String clientSecret) throws IOException {

        for(GitHubTreeNode ghtn : ght.getTree()) {
            if(ghtn.getType().equals("blob")) {
                if(false) {
                    //TODO check if path and sha are already in database.
                } else {
                    List<GitHubCommit> list = this.getCommits(repoUser, repoName, repoMainDir + "/" + ghtn.getPath(), clientId, clientSecret);
                    GitHubCommitter author = null;
                    for(GitHubCommit ghco : list) {
                        author = ghco.getAuthor();
                        System.out.println("author: " + author);
                    }
                    String blobPath = repoMainDir + "/" + ghtn.getPath();
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
                    Post p = preparePost(text, author, tags, ghtn);
                    System.out.println(p);
                    if(p!= null) {
                        postRepository.save(p);
                    }
                }

            }
        }
    }

    private Post preparePost(String text, GitHubCommitter committer, String[] tags, GitHubTreeNode ghtn) {
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
        String author = committer.getLogin();
        String authorUrl = committer.getHtmlUrl();
        String avatarUrl = committer.getAvatarUrl();
        String urlTitle = title.replaceAll("\\W", "");
        String[] pathElements = this.extractPathElements(ghtn.getPath());
        result.setTitle(title);
        result.setContent(content);
        result.setTags(tt);
        result.setAuthor(author);
        result.setUrlTitle(urlTitle);
        result.setAuthorUrl(authorUrl);
        result.setAvatarUrl(avatarUrl);
        LocalDateTime ldt = LocalDateTime.now();
        LocalTime lt = ldt.toLocalTime();
        LocalDate ld = ldt.toLocalDate();
        result.setTimestamp(lt);
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
//        System.out.println(path);
        String[] splittedPath = path.split("/");
//        for(String s : splittedPath) {
//            s = s.trim();
//        }
        if(splittedPath.length == 4) {
            return splittedPath;
        }
        return null;
    }
}
