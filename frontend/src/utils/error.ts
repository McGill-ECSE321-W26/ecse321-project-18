import type { ErrorResponse, ResponseObject } from "#/types/api";
import { toast } from "@heroui/react";

function isErrorResponse(res: any): res is ErrorResponse {
  return res !== null && typeof res === "object" && "errors" in res;
}

export const checkError= (response: ResponseObject) => {
  if (isErrorResponse(response)) {
    if (display) {
      displayError(response.errors);
      return;
    }
    throw new AggregateError(response.errors);
  }
  return response;
}

export const updateErrors = (
  err: any,
  errors: string[],
  setErrors: React.Dispatch<React.SetStateAction<string[]>>,
) => {
  if (err instanceof AggregateError) {
    setErrors([...errors, ...err.errors]);
  } else {
    const errorMessage = err instanceof Error ? err.message : String(err);
    setErrors([...errors, errorMessage]);
  }
};

export const displayErrors = (errors: string[]) => {
  let msg = "We encountered a few issues:\n";
  let desc =  `- ${errors.join("\n- ")}`;
  toast.danger(msg, {
    actionProps: {
      children: "Dismiss",
      onPress: () => toast.clear(),
      variant: "tertiary",
      className: ""
    },
    description: desc,
    timeout: 10000
  });
};

// export const handleErrors = (response: ResponseObject, display: boolean = true) => {
//   if (isErrorResponse(response)) {
//     if (display) {
//       displayError(response.errors);
//       return;
//     }
//     throw new AggregateError(response.errors);
//   }
//   return response;
// }
