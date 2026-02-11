const API = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

export async function login(username, password) {
  const res = await fetch(`${API}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ username, password }),
  });
  if (!res.ok) return null;
  const data = await res.json();
  localStorage.setItem("token", data.token);
  return data.token;
}

export function logout() {
  const token = localStorage.getItem("token");
  if (token) {
    fetch(`${API}/auth/logout`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
    });
  }
  localStorage.removeItem("token");
}

export function getToken() {
  return localStorage.getItem("token");
}

function authHeaders() {
  const token = getToken();
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = `Bearer ${token}`;
  return headers;
}

export async function getPosts() {
  const res = await fetch(`${API}/posts`, { cache: "no-store" });
  return res.json();
}

export async function getAllPosts() {
  const res = await fetch(`${API}/posts?all=true`, { cache: "no-store" });
  return res.json();
}

export async function getPost(id) {
  const res = await fetch(`${API}/posts/${id}`, { cache: "no-store" });
  if (!res.ok) return null;
  return res.json();
}

export async function createPost(post) {
  const res = await fetch(`${API}/posts`, {
    method: "POST",
    headers: authHeaders(),
    body: JSON.stringify(post),
  });
  if (res.status === 401) throw new Error("Unauthorized");
  return res.json();
}

export async function updatePost(id, post) {
  const res = await fetch(`${API}/posts/${id}`, {
    method: "PUT",
    headers: authHeaders(),
    body: JSON.stringify(post),
  });
  if (res.status === 401) throw new Error("Unauthorized");
  return res.json();
}

export async function deletePost(id) {
  const res = await fetch(`${API}/posts/${id}`, {
    method: "DELETE",
    headers: authHeaders(),
  });
  if (res.status === 401) throw new Error("Unauthorized");
}
