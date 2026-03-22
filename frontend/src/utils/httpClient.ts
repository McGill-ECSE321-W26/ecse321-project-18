const BACKEND_URL = "http://localhost:8080/fashionstore";

export async function getRequest(uri: string) {
  const response = await fetch(BACKEND_URL + uri);
  return await response.json();
}
