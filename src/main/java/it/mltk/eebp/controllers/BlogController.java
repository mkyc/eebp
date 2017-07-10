package it.mltk.eebp.controllers;

import it.mltk.eebp.repo.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by mateusz on 10.07.2017.
 */
@Controller
public class BlogController {


    @Autowired
    private PostRepository postRepository;

    @RequestMapping("/")
    public String recentPosts(@RequestParam(value = "page", defaultValue = "0", required = false) String page, Model model) {
        Integer pageNum = 0;
        try{
            pageNum = Integer.valueOf(page);
        } catch (NumberFormatException e) {}
        Pageable limit = new PageRequest(pageNum,10);
        model.addAttribute("posts", postRepository.findAll(limit));
        return "main";
    }

    @RequestMapping("/{year}/{month}/{day}/{title}")
    public String exactPost(@PathVariable String year,
                            @PathVariable String month,
                            @PathVariable String day,
                            @PathVariable String title,
                            Model model) {
        int yearNum = 2017;
        int monthNum = 7;
        int dayNum = 10;
        try {
            yearNum = Integer.valueOf(year);
            monthNum = Integer.valueOf(month);
            dayNum = Integer.valueOf(day);
        } catch (NumberFormatException e) {}
        model.addAttribute("post", postRepository.findOneByYearAndMonthAndDayAndUrlTitle(yearNum, monthNum, dayNum, title));
        return "post";
    }
}
