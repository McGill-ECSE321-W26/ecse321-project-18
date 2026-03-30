import { toast } from "@heroui/react";

export const displayErrors = (errors: string[]) => {
  const msg = "We encountered a few issues:\n";
  const desc = `- ${errors.join("\n- ")}`;
  toast.danger(msg, {
    actionProps: {
      children: "Dismiss",
      onPress: () => toast.clear(),
      variant: "tertiary",
      className: "",
    },
    description: desc,
    timeout: 10000,
  });
};

export const handleErrors = (error: any, display: boolean) => {
  if (error.response) {
    const errors = error.response.data.errors;
    if (!errors) {
      throw new Error(error.message);
    }
    if (display) {
      displayErrors(errors);
    }
    throw new AggregateError(errors);
  } else if (error.request) {
    throw new Error(
      "Error: The request was made but no response was received.",
    );
  }
  throw new Error(error.message || "Something went wrong!");
};
