package it.mltk.eebp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GitHubTree {
    private String sha;
    private Boolean truncated;
    private List<GitHubTreeNode> tree;
}
