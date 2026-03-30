import axios from "axios";
import { handleErrors } from "./error";
import type { RequestObject } from "#/types/api";

const BACKEND_URL = "http://localhost:8080/fashionstore";

export async function getRequest<T>(
  uri: string,
  display: boolean = true,
): Promise<T> {
  return axios
    .get(BACKEND_URL + uri)
    .then(({ data }) => data)
    .catch((error) => handleErrors(error, display)) as T;
}

export async function postRequest<T>(
  uri: string,
  requestBody: RequestObject,
  display: boolean = true,
): Promise<T> {
  return axios
    .post(BACKEND_URL + uri, requestBody)
    .then(({ data }) => data)
    .catch((error) => handleErrors(error, display)) as T;
}

export async function putRequest<T>(
  uri: string,
  requestBody: RequestObject,
  display: boolean = true,
): Promise<T> {
  return axios
    .put(BACKEND_URL + uri, requestBody)
    .then(({ data }) => data)
    .catch((error) => handleErrors(error, display)) as T;
}

export async function deleteRequest<T>(
  uri: string,
  display: boolean = true,
): Promise<T> {
  return axios
    .delete(BACKEND_URL + uri)
    .then(({ data }) => data)
    .catch((error) => handleErrors(error, display)) as T;
}
