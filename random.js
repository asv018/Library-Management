// # 1. Create an author
// curl -X POST http://localhost:8080/authors \
//   -H "Content-Type: application/json" \
//   -d '{"name":"Stephen King","birthdate":"1947-09-21","nationality":"American"}'

// # 2. Create a book (use the author ID from step 1)
// curl -X POST http://localhost:8080/books \
//   -H "Content-Type: application/json" \
//   -d '{"title":"The Shining","authorId":1,"publishedDate":"1977-01-28","isbn":"978-0-385-12167-5"}'

// # 3. Get all books with pagination
// curl http://localhost:8080/books?page=1&size=10

// # 4. Search for books
// curl http://localhost:8080/books?search=Shining