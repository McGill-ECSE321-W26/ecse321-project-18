import {
  Button,
  FieldError,
  InputGroup,
  Label,
  TextField,
} from "@heroui/react";
import { useState } from "react";
import { IoMdEye, IoMdEyeOff } from "react-icons/io";

type PasswordToggleInputProps = {
  label?: string;
  placeholder?: string;
  password: string;
  handleChange: (value: string) => void;
  validateFn?: (value: string) => string | null;
  type?: string | null;
};

/* input component that allows the user to toggle password visibility */
export const PasswordToggleInput = ({
  label,
  placeholder,
  password,
  handleChange,
  validateFn,
  type,
}: PasswordToggleInputProps) => {
  const [isPasswordVisible, setIsPasswordVisible] = useState<boolean>(false);

  return (
    <TextField
      isRequired
      name="password"
      type={isPasswordVisible ? "text" : "password"}
      minLength={8}
      maxLength={32}
      value={password}
      onChange={handleChange}
      validate={validateFn}
    >
      <Label>{label || "Password"}</Label>
      <InputGroup className={type || "text-1"}>
        <InputGroup.Input
          className="w-full"
          placeholder={placeholder || "Enter your password"}
        />
        <InputGroup.Suffix className="pr-0">
          <Button
            isIconOnly
            aria-label={isPasswordVisible ? "Hide password" : "Show password"}
            size="sm"
            className={type || "ghost"}
            onPress={() => setIsPasswordVisible(!isPasswordVisible)}
          >
            {isPasswordVisible ? <IoMdEye /> : <IoMdEyeOff />}
          </Button>
        </InputGroup.Suffix>
      </InputGroup>
      <FieldError />
    </TextField>
  );
};
