package it.mltk.eebp.services;

import it.mltk.eebp.entity.*;
import it.mltk.eebp.retrofit.GitHubRetrofit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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

    public List<Post> extractPosts(GitHubTree ght, String repoUser, String repoName, String clientId, String clientSecret) throws IOException {
        for(GitHubTreeNode ghtn : ght.getTree()) {
            if(ghtn.getType().equals("blob")) {
                if(false) {
                    //TODO check if path and sha are already in database.
                } else {
                    System.out.println(ghtn);
                    List<GitHubCommit> list = this.getCommits(repoUser, repoName, ghtn.getPath(), clientId, clientSecret);
                    GitHubCommitter author = null;
                    for(GitHubCommit ghco : list) {
                        author = ghco.getAuthor();
                        System.out.println("Author: " + author);
                    }
                    String blobPath = repoMainDir + "/" + ghtn.getPath();
                    System.out.println("blobPath: " + blobPath);
                    GitHubContent ghc = this.getSingleContent(repoUser, repoName, blobPath, clientId, clientSecret);

                    System.out.println("Content: " + ghc);

                    URLConnection connection = new URL(ghc.getDownloadUrl()).openConnection();
                    String text = new Scanner(connection.getInputStream()).useDelimiter("\\Z").next();

                    String title = text.substring(0, text.indexOf(System.getProperty("line.separator"))).replaceAll("[^\\w\\d\\s]", "").trim();

                    Matcher m = Pattern.compile("(?m)^Tags:.*$").matcher(text);
                    ArrayList<Tag> tt = new ArrayList<>();
                    while (m.find()) {
                        String tagsLine = m.group().replaceAll("Tags:", "").trim();
                        System.out.println("line = " + tagsLine);
                        String[] tags = tagsLine.split(",");
                        for(String s : tags) {
                            System.out.println("tag = :" + s.trim() + ":");
                            tt.add(new Tag(s.trim()));
                        }
                    }
                }

            }
        }
        return null;
    }
}
