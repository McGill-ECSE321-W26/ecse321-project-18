import axios from "axios";
import type { RequestObject } from "#/types/api";
import { handleErrors, displayErrors } from "./error";

const BACKEND_URL = "http://localhost:8080/fashionstore";

export async function getRequest<T>(uri: string): Promise<T> {
  return axios
    .get(BACKEND_URL + uri)
    .then(({ data }) => handleErrors(data)) as T;
}

export async function postRequest<T>(
  uri: string,
  requestBody: RequestObject,
  display: boolean = true,
): Promise<T> {
  return axios
    .post(BACKEND_URL + uri, requestBody)
    .then(({ data }) => data)
    .catch(function (error) {
      const errors = error.response?.data.errors;
      if (errors) {
        if (!display) {
          throw new AggregateError(errors);
        }
        displayErrors(errors);
      }
      throw new Error(error.message);
    }) as T;
}

export async function putRequest<T>(
  uri: string,
  requestBody: RequestObject,
  display: boolean = true,
): Promise<T> {
  return axios.put(BACKEND_URL + uri, requestBody).then(({ data }) => {
    if (display) {
      displayErrors(data);
    }
    return handleErrors(data, display);
  }) as T;
}

export async function deleteRequest<T>(uri: string): Promise<T> {
  return axios
    .delete(BACKEND_URL + uri)
    .then(({ data }) => handleErrors(data)) as T;
}
