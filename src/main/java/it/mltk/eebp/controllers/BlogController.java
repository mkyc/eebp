package it.mltk.eebp.controllers;

import it.mltk.eebp.entity.Tag;
import it.mltk.eebp.repo.PostRepository;
import it.mltk.eebp.repo.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.NoSuchAlgorithmException;

/**
 * Created by mateusz on 10.07.2017.
 */
@Controller
public class BlogController {


    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TagRepository tagRepository;

    @RequestMapping("/")
    public String recentPosts(
            @RequestParam(value = "page", defaultValue = "0", required = false) String page,
            @RequestParam(value = "tag", required = false) String tag,
            Model model) throws NoSuchAlgorithmException {
        Integer pageNum = 0;
        try{
            pageNum = Integer.valueOf(page);
        } catch (NumberFormatException e) {}
        Pageable window = new PageRequest(pageNum,10);
        if(tag == null) {
            model.addAttribute("posts", postRepository.findAllByOrderByCreatedDesc(window));
        } else {
            Tag t = tagRepository.findByName(tag);
            model.addAttribute("posts", postRepository.findAllByTagsOrderByCreatedDesc(t, window));
        }

        boolean first = pageNum.equals(0) ? true : false;
        long size = postRepository.count();
        boolean last = (size - (pageNum * 10)) <= 10 ? true : false;
        model.addAttribute("first", first);
        model.addAttribute("last", last);
        model.addAttribute("page", pageNum);
        model.addAttribute("tags", tagRepository.findAll());
        return "main";
    }

    @RequestMapping("/post/{year}/{month}/{day}/{title}")
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
