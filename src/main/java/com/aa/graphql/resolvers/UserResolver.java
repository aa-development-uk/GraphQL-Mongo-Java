package com.aa.graphql.resolvers;

import com.aa.graphql.entities.Article;
import com.aa.graphql.entities.User;
import com.aa.graphql.repositories.ArticleRepository;
import com.coxautodev.graphql.tools.GraphQLResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserResolver implements GraphQLResolver<User> {
    private final ArticleRepository articleRepository;

    public Iterable<Article> getArticles(User user)
    {
        return articleRepository.findAllById(user.getArticles());
    }
}
