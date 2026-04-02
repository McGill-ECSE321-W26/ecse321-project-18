import { Spinner } from "@heroui/react";

export default function Skeleton() {
  return (
    <div className="w-full h-full flex items-center justify-center">
      <Spinner size="xl" />
    </div>
  );
}
