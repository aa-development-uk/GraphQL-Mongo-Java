# Overview
This document provides technical info on how this example works.
This is an FYI and should really move somewhere else (Phabricator?).
Could also form the basis of a blog post if we are going to start blogging
as this was a PIA!

## Key areas
**This is a maven project not gradle!**

### MongoDB
We want to use MongoDB as the DB engine. This DB is configured via `docker-compose`
as its a test DB. The key thing here is that the contents of the DB is 
re-initialised on rebuild via an init javascript file.

### POM
A lot of examples on the web use an old version of the Java GraphQL tooling.
Recently, the tools were moved to a separate repo and are now published under a
different package name. As a minimum we need:

```
		<!-- GraphQL Tools
		 Basic Java GraphQL lib and a helper lib so we can define our
		 GraphQL schema in `.graphqls` files
		 -->
		<dependency>
			<groupId>com.graphql-java</groupId>
			<artifactId>graphql-java</artifactId>
			<version>11.0</version>
		</dependency>

		<dependency>
			<groupId>com.graphql-java-kickstart</groupId>
			<artifactId>graphql-java-tools</artifactId>
			<version>5.5.2</version>
		</dependency>

		<!-- Spring Tools 
		We want to build this using Spring boot and Mongo
		Add the mongo lib
		Add the starter wrapper projects to make working with GraphQL easier
		-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-mongodb</artifactId>
		</dependency>

		<dependency>
			<groupId>com.graphql-java-kickstart</groupId>
			<artifactId>graphql-spring-boot-starter</artifactId>
			<version>${graphQL.helper.version}</version>
		</dependency>

		<dependency>
			<groupId>com.graphql-java-kickstart</groupId>
			<artifactId>graphiql-spring-boot-starter</artifactId>
			<version>${graphQL.helper.version}</version>
			<scope>runtime</scope>
		</dependency>

		<!-- Data tools 
		Add lombok to reduce amount of boilerplate code we write
		-->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.2</version>
		</dependency>
```

### Spring-boot
We are making use of Spring-boot 2. We can configure it via a yaml definition.
This config file is located in the `resources` folder. The main thing to note
in this folder is we configure access to the mongo instance.

### Graphqls
Rather than defining our schema in code we want to define it in schema
definition files. These files are placed in the `resource` directory.

## Application
As we are using `graphql-java-tools` dep and have placed our `graphqls` on
the classpath the main app file doesn't need to contain any bean than builds
the schema def as the library will add the schema for us and hence serve it
under `<site>/graphql`.

### Entities
We define in our schema files certain types e.g. a User type. This type needs
to map to a POJO:
```
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class User {
    private String id;
    private String name;
    private Integer age;
    private Date createdAt;
    private String nationality;
    private List<ObjectId> articles;
}
```
See lombok for info on annotations.
The document annotation is a spring-boot annotation that maps the POJO to
the appropriate MongoDB collection.
Key thing to note is that the id property is of `String` type although we
store `ObjectID's` in Mongo. Reason for this is that it causes a mapping
exception when we return the id prop as part of a query 
(can't cast it as it gets a string type from the DB)

### Repositories
These are very simple. We just extend the default mongorepo:
```
public interface ArticleRepository extends MongoRepository<Article, ObjectId> {}
```
Key thing here is that the first prop in the type def is the POJO class, 
the second prop is the key type for that collection (as defined in Mongo!)

### Queries
In the schema definition you specify the queries that you would like to be
available to the UI
```
user(id: ID!): User
```
The above indicates that an endpoint exists called user, it takes a non-nullable
parameter of type ID and returns null or a User.

The corresponding implementation for this endpoint looks like:
```
public Optional<com.aa.graphql.entities.User> getUser(ObjectId id) {
    return userRepository.findById(id);
}
```
This implements the query method as it:
- Returns an Optional (nullable) User
- Takes in a param of type ID (ObjectID)
- Uses the MongoDB method findById (provided as we extend the MongoRepository) to find the relevant User in the DB

### Resolvers
If there is a need to provide more complex data structures as part of the query response (i.e.
lookup a value from another collection) then we need to implement a `GraphQLResolver`. The resolver
typically is the name of the POJO class with `Resolver` on the end. As you can seethe user type
has a complex property that returns an Array of articles
```
articles: [Article]
```
This can't be queried directly so we need to implement a resolver so we can query another collection
for the relevant data.
```
public class UserResolver implements GraphQLResolver<User> {
    private final ArticleRepository articleRepository;

    public Iterable<Article> getArticles(User user)
    {
        return articleRepository.findAllById(user.getArticles());
    }
}
```
This is invoked on a query that retrieves a user and wants to access the linked records (Articles).
When a user is found, the User object is passed into the Resolver. Using the data stored in the
relevant User property (a list of article ObjectId's) we can query the article collection to
retrieve the desired articles.