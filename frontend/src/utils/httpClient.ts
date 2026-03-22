import axios from "axios";
import type { RequestObject } from "#/types/api";

const BACKEND_URL = "http://localhost:8080/fashionstore";

export async function getRequest(uri: string) {
  return axios.get(BACKEND_URL + uri).then(({ data }) => data);
}

export async function postRequest(uri: string, requestBody: RequestObject) {
  return axios
    .post(BACKEND_URL + uri, {
      data: JSON.stringify(requestBody),
    })
    .then(({ data }) => data);
}
