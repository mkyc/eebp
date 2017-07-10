package it.mltk.eebp.controllers;

import it.mltk.eebp.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by mateusz on 10.07.2017.
 */
@Controller
public class BlogController {


    @Autowired
    private PostRepository postRepository;

    @RequestMapping("/")
    public String recentPosts(Model model) {
        //TODO change to page request from parameters
        Pageable limit = new PageRequest(0,10);
        model.addAttribute("posts", postRepository.findAll(limit));
        return "main";
    }
}
