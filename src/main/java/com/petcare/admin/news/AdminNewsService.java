package com.petcare.admin.news;

import com.petcare.news.News;
import com.petcare.news.NewsRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminNewsService {

    private final NewsRepository newsRepository;


    @Transactional(readOnly = true)
    public List<News> findAll() {

        return newsRepository
                .findAllByOrderByCreatedAtDesc();
    }


    @Transactional(readOnly = true)
    public News findById(Long id) {

        return newsRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Không tìm thấy bài viết"
                        )
                );
    }


    @Transactional
    public News create(
            AdminNewsRequest request
    ) {

        String slug =
                normalizeSlug(
                        request.getSlug()
                );

        if (newsRepository.existsBySlug(slug)) {

            throw new IllegalArgumentException(
                    "Slug đã tồn tại."
            );
        }

        News news = new News();

        applyRequest(
                news,
                request,
                slug
        );

        updatePublishedState(
                news,
                Boolean.TRUE.equals(
                        request.getPublished()
                )
        );

        return newsRepository.save(news);
    }


    @Transactional
    public News update(
            Long id,
            AdminNewsRequest request
    ) {

        News news =
                findById(id);

        String slug =
                normalizeSlug(
                        request.getSlug()
                );

        if (newsRepository
                .existsBySlugAndIdNot(
                        slug,
                        id
                )) {

            throw new IllegalArgumentException(
                    "Slug đã được sử dụng bởi bài viết khác."
            );
        }

        applyRequest(
                news,
                request,
                slug
        );

        updatePublishedState(
                news,
                Boolean.TRUE.equals(
                        request.getPublished()
                )
        );

        return newsRepository.save(news);
    }


    @Transactional
    public void togglePublished(Long id) {

        News news =
                findById(id);

        boolean newState =
                !Boolean.TRUE.equals(
                        news.getPublished()
                );

        updatePublishedState(
                news,
                newState
        );

        newsRepository.save(news);
    }


    @Transactional
    public void delete(Long id) {

        News news = findById(id);

        newsRepository.delete(news);
    }


    public AdminNewsRequest toRequest(
            News news
    ) {

        AdminNewsRequest request =
                new AdminNewsRequest();

        request.setTitle(
                news.getTitle()
        );

        request.setSlug(
                news.getSlug()
        );

        request.setSummary(
                news.getSummary()
        );

        request.setContent(
                news.getContent()
        );

        request.setImageUrl(
                news.getImageUrl()
        );

        request.setPublished(
                news.getPublished()
        );

        return request;
    }


    private void applyRequest(
            News news,
            AdminNewsRequest request,
            String slug
    ) {

        news.setTitle(
                request.getTitle().trim()
        );

        news.setSlug(slug);

        news.setSummary(
                normalizeNullable(
                        request.getSummary()
                )
        );

        news.setContent(
                request.getContent().trim()
        );

        news.setImageUrl(
                normalizeNullable(
                        request.getImageUrl()
                )
        );
    }


    private void updatePublishedState(
            News news,
            boolean published
    ) {

        if (published) {

            if (!Boolean.TRUE.equals(
                    news.getPublished()
            ) || news.getPublishedAt() == null) {

                news.setPublishedAt(
                        LocalDateTime.now()
                );
            }

            news.setPublished(true);

        } else {

            news.setPublished(false);

            news.setPublishedAt(null);
        }
    }


    private String normalizeSlug(
            String slug
    ) {

        return slug
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-");
    }


    private String normalizeNullable(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }
}
