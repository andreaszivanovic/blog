"use client";

import { useState, useEffect } from "react";
import { getAllPosts, getPost, createPost, updatePost, deletePost, login, logout, getToken } from "@/lib/api";

const emptyForm = { title: "", text: "", date: "", published: false };

export default function AdminPage() {
  const [authenticated, setAuthenticated] = useState(false);
  const [checking, setChecking] = useState(true);
  const [loginError, setLoginError] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [posts, setPosts] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);

  useEffect(() => {
    if (getToken()) setAuthenticated(true);
    setChecking(false);
  }, []);

  useEffect(() => {
    if (authenticated) load();
  }, [authenticated]);

  const load = () => getAllPosts().then(setPosts);

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoginError("");
    const token = await login(username, password);
    if (token) {
      setAuthenticated(true);
      setUsername("");
      setPassword("");
    } else {
      setLoginError("Invalid username or password");
    }
  };

  const handleLogout = () => {
    logout();
    setAuthenticated(false);
    setPosts([]);
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleAuthError = () => {
    logout();
    setAuthenticated(false);
    setLoginError("Session expired. Please log in again.");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await updatePost(editingId, form);
      } else {
        await createPost(form);
      }
      setForm(emptyForm);
      setEditingId(null);
      load();
    } catch (err) {
      if (err.message === "Unauthorized") handleAuthError();
    }
  };

  const handleEdit = async (id) => {
    const post = await getPost(id);
    if (!post) return;
    setForm({ title: post.title, text: post.text, date: post.date, published: post.published });
    setEditingId(id);
  };

  const handleDelete = async (id) => {
    if (!confirm("Delete this post?")) return;
    try {
      await deletePost(id);
      if (editingId === id) { setForm(emptyForm); setEditingId(null); }
      load();
    } catch (err) {
      if (err.message === "Unauthorized") handleAuthError();
    }
  };

  const handleToggle = async (post) => {
    try {
      const full = await getPost(post.id);
      if (!full) return;
      await updatePost(post.id, { ...full, published: !post.published });
      load();
    } catch (err) {
      if (err.message === "Unauthorized") handleAuthError();
    }
  };

  if (checking) return null;

  if (!authenticated) {
    return (
      <div className="admin login-container">
        <h1>Admin Login</h1>
        <form className="form login-form" onSubmit={handleLogin}>
          {loginError && <p className="login-error">{loginError}</p>}
          <label>Username</label>
          <input value={username} onChange={(e) => setUsername(e.target.value)} required />
          <label>Password</label>
          <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          <button type="submit" className="primary">Log In</button>
        </form>
      </div>
    );
  }

  return (
    <div className="admin">
      <div className="admin-header">
        <h1>Admin</h1>
        <button onClick={handleLogout}>Log out</button>
      </div>

      <table>
        <thead>
          <tr>
            <th>Title</th>
            <th>Date</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((p) => (
            <tr key={p.id}>
              <td>{p.title}</td>
              <td>{p.date}</td>
              <td>
                <span className={`status ${p.published ? "published" : "draft"}`}>
                  {p.published ? "Published" : "Draft"}
                </span>
              </td>
              <td>
                <div className="actions">
                  <button onClick={() => handleEdit(p.id)}>Edit</button>
                  <button onClick={() => handleToggle(p)}>
                    {p.published ? "Unpublish" : "Publish"}
                  </button>
                  <button className="danger" onClick={() => handleDelete(p.id)}>Delete</button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <form className="form" onSubmit={handleSubmit}>
        <h2>{editingId ? "Edit Post" : "New Post"}</h2>

        <label>Title</label>
        <input
          value={form.title}
          onChange={(e) => setForm({ ...form, title: e.target.value })}
          required
        />

        <label>Date</label>
        <input
          type="date"
          value={form.date}
          onChange={(e) => setForm({ ...form, date: e.target.value })}
          required
        />

        <label>Content (Markdown)</label>
        <textarea
          value={form.text}
          onChange={(e) => setForm({ ...form, text: e.target.value })}
          required
        />

        <div className="checkbox">
          <input
            type="checkbox"
            id="published"
            checked={form.published}
            onChange={(e) => setForm({ ...form, published: e.target.checked })}
          />
          <label htmlFor="published">Published</label>
        </div>

        <div className="form-actions">
          <button type="submit" className="primary">
            {editingId ? "Save" : "Create"}
          </button>
          {editingId && (
            <button type="button" onClick={() => { setForm(emptyForm); setEditingId(null); }}>
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
}
