import { Button } from "@heroui/react";
import { useNavigate } from "@tanstack/react-router";
import { IoArrowBack } from "react-icons/io5";

export default function NotFoundPage() {
  // Goes back to homepage.
  const navigate = useNavigate();

  return (
    <div className="p-10 flex justify-center">
      <div>
        <h1 className="flex justify-center text-9xl text-red-500 font-bold">
          404
        </h1>
        <p className="flex justify-center text-red-500 font-bold text-2xl">
          Page Not Found
        </p>

        <div className="text-center">
          <p className="mt-15 text-gray-700">
            We can't seem to find the page you were looking for.<br></br>
            If this is not expected, please contact the admin.
          </p>
        </div>

        <div className="mt-10 flex justify-center">
          <Button
            variant="tertiary"
            onPress={() => navigate({ to: "/" })}
            className="mt-5 flex justify-center"
          >
            <IoArrowBack />
            <span className="font-bold">Back to Home</span>
          </Button>
        </div>
      </div>
    </div>
  );
}
