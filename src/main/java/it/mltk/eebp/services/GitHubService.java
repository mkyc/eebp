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
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@Component
public class GitHubService {

    private static final String API_URL = "https://api.github.com";
    private static final long RATE = 300000;
    private static final HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BASIC;

    private Retrofit retrofit;
    private GitHubRetrofit gitHubRetrofit;

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
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        gitHubRetrofit = retrofit.create(GitHubRetrofit.class);

    }

    @Scheduled(fixedRate = RATE)
    void scheduledCheck() throws IOException, InterruptedException {
        GitHubContent article = getArticlesRoot();
        if(article != null && article.getSha() != null) {
            GitHubTree ght = getTree(article.getSha());

            for (GitHubTreeNode gitHubTreeNode : ght.getTree()) {
                if (gitHubTreeNode.getType().equals("blob") && gitHubTreeNode.getPath().endsWith(".md")) {
                    Post testWithPathAndSha = postRepository.findOneByPathAndSha(gitHubTreeNode.getPath(), gitHubTreeNode.getSha());
                    Post testWithPath = postRepository.findOneByPath(gitHubTreeNode.getPath());
                    if (testWithPathAndSha != null) {
                        //nothing to do
                    } else if (testWithPath != null) {
                        //update post
                        Post p = null;
                        do {
                            try {
                                p = this.updatePost(testWithPath, gitHubTreeNode);
                            } catch (NotYetUpdatedException e) {
                                e.printStackTrace();
                                sleep(1000);
                            }
                        } while (p == null);
                        postRepository.save(p);
                    } else {
                        //create new post
                        Post post = createNewPost(gitHubTreeNode);
                        if (post != null) {
                            postRepository.insert(post);
                        }
                    }

                }
            }
        }
    }

    private Post createNewPost(GitHubTreeNode gitHubTreeNode) throws IOException {
        Post result = new Post();

        //get first commiter BEGIN
        List<GitHubCommit> list = this.getCommits(repoUser, repoName, repoMainDir + "/" + gitHubTreeNode.getPath(), clientId, clientSecret);
        GitHubCommitter gitHubCommitter = list.get(0).getAuthor();
        //get first commiter END

        //get content
        String text = getBlobContent(gitHubTreeNode.getSha());

        //get tags BEGIN
        Matcher m = Pattern.compile("(?m)^Tags:.*$").matcher(text);
        String[] tags = null;
        if (m.find()) {
            String tagsLine = m.group().replaceAll("Tags:", "").trim();
            tags = tagsLine.split(",");
        }
        if(tags != null) {
            ArrayList<Tag> tt = new ArrayList<>();
            for (String s : tags) {
                Tag t = tagRepository.findByName(s.trim());
                if (t == null) {
                    t = new Tag(s.trim());
                    t = tagRepository.save(t);
                }
                tt.add(t);
            }
            result.setTags(tt);
        }
        //get tags END

        //get title
        String title = text.substring(0, text.indexOf(System.getProperty("line.separator"))).replaceAll("[^\\w\\d\\s]", "").trim();

        //remove tags from content and parse it BEGIN
        text = text.replaceFirst("(?m)^Tags.*", "");
        String content = flexmarkService.parseMarkdown(text.substring(text.indexOf(System.getProperty("line.separator")), text.length()));
        //remove tags from content and parse it END

        //create dates and path elements BEGIN
        String[] pathElements = this.extractPathElements(gitHubTreeNode.getPath());
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
        //create dates and path elements END

        //set another things
        result.setAuthor(gitHubCommitter.getLogin());
        result.setTitle(title);
        result.setContent(content);

        result.setUrlTitle(title.replaceAll("\\W", ""));
        result.setAuthorUrl(gitHubCommitter.getHtmlUrl());
        result.setAvatarUrl(gitHubCommitter.getAvatarUrl());
        result.setSha(gitHubTreeNode.getSha());
        result.setPath(gitHubTreeNode.getPath());
        return result;
    }

    private String getBlobContent(String sha) throws IOException {
        Call<String> call = gitHubRetrofit.repoBlob(repoUser, repoName, sha, clientId, clientSecret);
        return call.execute().body();
    }

    private GitHubContent getArticlesRoot() throws IOException {
        Call<List<GitHubContent>> call = gitHubRetrofit.repoContent(repoUser, repoName, clientId, clientSecret);
        List<GitHubContent> contents = call.execute().body();
        if(contents != null) {
            for (GitHubContent ghc : contents) {
                if (ghc.getName().equals(repoMainDir) && ghc.getType().equals("dir")) {
                    return ghc;
                }
            }
        }
        return null;
    }

    private GitHubTree getTree(String sha) throws IOException {
        Call<GitHubTree> call = gitHubRetrofit.repoTree(repoUser, repoName, sha, clientId, clientSecret);
        return call.execute().body();
    }

    private List<GitHubCommit> getCommits(String user, String repo, String path, String clientId, String clientSecret) throws IOException {
        Call<List<GitHubCommit>> call = gitHubRetrofit.repoCommit(user, repo, path, clientId, clientSecret);
        return call.execute().body();
    }

    private String[] extractPathElements(String path) {
        String[] splittedPath = path.split("/");
        if(splittedPath.length == 4) {
            return splittedPath;
        }
        return null;
    }

    private Post updatePost(Post post, GitHubTreeNode gitHubTreeNode) throws IOException, NotYetUpdatedException {
        Post newPost = this.createNewPost(gitHubTreeNode);
        if(newPost.getSha().equals(post.getSha())) {
            throw new NotYetUpdatedException("new Post sha: " + newPost.getSha() + ", old Post sha: " + post.getSha());
        }
        newPost.setId(post.getId());
        return newPost;
    }
}
