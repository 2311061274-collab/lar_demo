package com.petcare.news;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;


    @GetMapping("/news")
    public String list(
            @RequestParam(required = false) String keyword,
            Model model
    ) {

        model.addAttribute(
                "newsList",
                newsService.searchPublishedNews(keyword)
        );

        model.addAttribute(
                "keyword",
                keyword
        );

        return "news/list";
    }


    @GetMapping("/news/{slug}")
    public String detail(
            @PathVariable String slug,
            Model model
    ) {

        model.addAttribute(
                "news",
                newsService
                        .getPublishedBySlug(slug)
        );

        return "news/detail";
    }
}
