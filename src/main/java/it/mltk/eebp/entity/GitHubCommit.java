package it.mltk.eebp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubCommit {
    private String url;
    private GitHubCommitter committer;
    private GitHubCommitter author;
}
