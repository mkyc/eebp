package it.mltk.eebp.services;

import it.mltk.eebp.entity.GitHubCommit;
import it.mltk.eebp.entity.GitHubContent;
import it.mltk.eebp.entity.GitHubTree;
import it.mltk.eebp.retrofit.GitHubRetrofit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GitHubService {

    private static final String API_URL = "https://api.github.com";
    private static final HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.NONE;
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
}
