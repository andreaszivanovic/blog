import { getPosts } from "@/lib/api";

export default async function Home() {
  const posts = await getPosts();

  return (
    <div>
      <h1>Blog</h1>
      {posts.length === 0 ? (
        <p>No posts yet.</p>
      ) : (
        <ul className="post-list">
          {posts.map((post) => (
            <li key={post.id}>
              <a href={`/posts/${post.id}`}>{post.title}</a>
              <div className="date">{post.date}</div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
