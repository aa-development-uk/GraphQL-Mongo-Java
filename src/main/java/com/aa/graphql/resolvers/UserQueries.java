package com.aa.graphql.resolvers;

import com.aa.graphql.repositories.UserRepository;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserQueries implements GraphQLQueryResolver {
    private final UserRepository userRepository;

    public List<com.aa.graphql.entities.User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<com.aa.graphql.entities.User> getUser(ObjectId id) {
        return userRepository.findById(id);
    }
}
