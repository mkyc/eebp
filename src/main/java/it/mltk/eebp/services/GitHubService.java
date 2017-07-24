package it.mltk.eebp.services;

import it.mltk.eebp.entity.GitHubContent;
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

    public static final String API_URL = "https://api.github.com";
    public static final HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;

    Retrofit retrofit;
    GitHubRetrofit github;


    public GitHubService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(level);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
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
}
