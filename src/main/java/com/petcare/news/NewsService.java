package com.petcare.news;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;


    @Transactional(readOnly = true)
    public List<News> getPublishedNews() {

        return newsRepository
                .findByPublishedTrueOrderByPublishedAtDesc();
    }


    @Transactional(readOnly = true)
    public List<News> searchPublishedNews(String keyword) {

        String normalized = keyword == null ? "" : keyword.trim();

        return newsRepository
                .searchPublishedNews(normalized);
    }


    @Transactional(readOnly = true)
    public News getPublishedBySlug(
            String slug
    ) {

        return newsRepository
                .findBySlugAndPublishedTrue(slug)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy bài viết"
                        )
                );
    }
}
