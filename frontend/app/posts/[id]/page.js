import { getPost } from "@/lib/api";
import { notFound } from "next/navigation";
import Markdown from "@/app/components/Markdown";

export default async function PostPage({ params }) {
  const { id } = await params;
  const post = await getPost(id);
  if (!post) notFound();

  return (
    <article className="post-detail">
      <a href="/" className="back">&larr; Back</a>
      <div className="date">{post.date}</div>
      <Markdown content={post.text} />
    </article>
  );
}
