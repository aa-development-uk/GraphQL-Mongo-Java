// Auth against DB
db.auth('root', 'example')

// Change to desired DB
db = db.getSiblingDB('graphql-app')

// Clear old test data
db.users.drop();
db.articles.drop();

// Insert test data
db.articles.insertMany([
  {
    _id: ObjectId("5cee7eef78479805c956a0fc"),
    title: "Super awesome article"
  },
  {
    _id: ObjectId("5cefef30ffc5f2d4f98d70f7"),
    title: "You need to read this article"
  },
  {
    _id: ObjectId("5cee7ef66baa57f44bbb9776"),
    title: "The best ever article"
  }
]);

db.users.insertMany([
  {
    _id: ObjectId("5cee7e717cef75bd589417bd"),
    name: "Joe Smith",
    age: 40,
    createdAt: new Date(),
    nationality: "British",
    articles: [
      ObjectId("5cee7eef78479805c956a0fc")
    ]
  },
  {
    _id: ObjectId("5cee7e5ce032770277afc497"),
    name: "Jen Ford",
    age: 45,
    createdAt: new Date(),
    nationality: "American",
    articles: [
      ObjectId("5cee7ef66baa57f44bbb9776"),
      ObjectId("5cefef30ffc5f2d4f98d70f7")
    ]
  }
]);