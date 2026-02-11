# Minimal Blog

A minimal personal blog with a Next.js frontend and Spring Boot backend.

## Prerequisites

- **Java 17+** (for the backend)
- **Maven** (or use the Maven wrapper if provided)
- **Node.js 18+** and **npm** (for the frontend)

## Running the Backend

```bash
cd backend
./mvnw spring-boot:run
# or if you have Maven installed:
mvn spring-boot:run
```

The API will be available at **http://localhost:8080**.

The backend uses an in-memory H2 database and seeds 4 example posts on startup.

### API Endpoints

| Method | Endpoint       | Description              |
|--------|----------------|--------------------------|
| GET    | /posts         | List published posts     |
| GET    | /posts?all=true| List all posts (admin)   |
| GET    | /posts/{id}    | Get a single post        |
| POST   | /posts         | Create a new post        |
| PUT    | /posts/{id}    | Update a post            |
| DELETE | /posts/{id}    | Delete a post            |

## Running the Frontend

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at **http://localhost:3000**.

## Pages

- **/** — Blog post list (published posts only)
- **/posts/{id}** — Single post view with rendered Markdown
- **/admin** — Admin panel to create, edit, delete, and publish/unpublish posts
