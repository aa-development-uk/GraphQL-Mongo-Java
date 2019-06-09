# Java Mongo GraphQL

## Set-up
As we use dot env you will need to create `mongo.env` in the project dir and add:
```
MONGO_INITDB_ROOT_USERNAME=root
MONGO_INITDB_ROOT_PASSWORD=example
MONGO_INITDB_DATABASE=admin
MONGO_HOST=mongo
```

## Running MongoDB
- In a terminal enter `docker-compose up -d mongo`

## Running Server (docker)
- In a terminal enter `docker-compose up -d server`
- Visit [GraphiQL](http://localhost:9000/graphiql) in your browser

## Running server (command line)
- In a terminal run: `mvn spring-boot:run`
- Visit [GraphiQL](http://localhost:9000/graphiql) in your browser

## Example Queries
Here are some sample queries that you can execute via GraphiQL:
```
query CertainUserQuery {
  user(id: "5cee7e5ce032770277afc497") {
    name,
    articles {
      title
    }
  }
}

query AllUsersQuery {
  users {
    name
    id
  }
}

query CertainArticleQuery {
  article(id: "5cee7eef78479805c956a0fc") {
    title
  }
}

query AllArticlesQuery {
  articles {
    title
    id
  }
}
```