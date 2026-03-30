import { Button, Spinner } from "@heroui/react";
import { IoMdCheckmark } from "react-icons/io";

type SubmitButtonProps = {
  text: string;
  isSubmitting: boolean;
  isFullWidth?: boolean;
  handleClick?: () => void;
};

export const SubmitButton = ({
  text,
  isSubmitting,
  isFullWidth,
  handleClick,
}: SubmitButtonProps) => {
  return (
    <Button
      className={isFullWidth ? "w-full" : ""}
      type="submit"
      isDisabled={isSubmitting}
      onClick={handleClick}
    >
      {isSubmitting ? <Spinner size="sm" color="current" /> : <IoMdCheckmark />}
      {text}
    </Button>
  );
};
