package it.mltk.eebp.retrofit;

import it.mltk.eebp.entity.GitHubCommit;
import it.mltk.eebp.entity.GitHubContent;
import it.mltk.eebp.entity.GitHubTree;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface GitHubRetrofit {
    @GET("repos/{user}/{repo}/contents")
    Call<List<GitHubContent>> repoContent(
            @Path("user") String user,
            @Path("repo") String repo,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret);


    @GET("/repos/{user}/{repo}/git/trees/{sha}?recursive=1")
    Call<GitHubTree> repoTree(
            @Path("user") String user,
            @Path("repo") String repo,
            @Path("sha") String sha,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret);

    @GET("/repos/{user}/{repo}/commits")
    Call<List<GitHubCommit>> repoCommit(
            @Path("user") String user,
            @Path("repo") String repo,
            @Query("path") String path,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecre);

    @Headers("Accept: application/vnd.github.3.raw")
    @GET("/repos/{user}/{repo}/git/blobs/{sha}")
    Call<String> repoBlob(
            @Path("user") String user,
            @Path("repo") String repo,
            @Path("sha") String sha,
            @Query("client_id") String clientId,
            @Query("client_secret") String clientSecret);

}
