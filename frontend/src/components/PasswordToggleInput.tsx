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
  password: string;
  handleChange: (value: string) => void;
};

/* input component that allows the user to toggle password visibility */
export const PasswordToggleInput = ({
  password,
  handleChange,
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
    >
      <Label>Password</Label>
      <InputGroup>
        <InputGroup.Input
          className="w-full"
          placeholder="Enter your password"
        />
        <InputGroup.Suffix className="pr-0">
          <Button
            isIconOnly
            aria-label={isPasswordVisible ? "Hide password" : "Show password"}
            size="sm"
            variant="ghost"
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
