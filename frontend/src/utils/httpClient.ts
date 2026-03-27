import axios from "axios";
import type { ErrorResponse, RequestObject, ResponseObject } from "#/types/api";

const BACKEND_URL = "http://localhost:8080/fashionstore";

function isErrorResponse(res: ResponseObject): res is ErrorResponse {
  return true;
}

function checkError(response: ResponseObject) {
  if (isErrorResponse(response)) {
    throw new AggregateError(response.errors);
  }
  return response;
}

export async function getRequest<T>(uri: string): Promise<T> {
  return axios.get(BACKEND_URL + uri).then(({ data }) => checkError(data)) as T;
}

export async function postRequest<T>(
  uri: string,
  requestBody: RequestObject,
): Promise<T> {
  return axios
    .post(BACKEND_URL + uri, requestBody)
    .then(({ data }) => checkError(data)) as T;
}

export async function putRequest<T>(
  uri: string,
  requestBody: RequestObject,
): Promise<T> {
  return axios
    .put(BACKEND_URL + uri, requestBody)
    .then(({ data }) => checkError(data)) as T;
}

export async function deleteRequest<T>(uri: string): Promise<T> {
  return axios
    .delete(BACKEND_URL + uri)
    .then(({ data }) => checkError(data)) as T;
}
