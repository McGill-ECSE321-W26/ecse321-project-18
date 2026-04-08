import { Skeleton } from "@heroui/react";

export default function CustomSkeleton() {
  return (
    <div className="skeleton--shimmer mx-auto justify-self-center self-center relative grid w-full max-w-2xl grid-cols-3 gap-4 overflow-hidden rounded-xl">
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
      <Skeleton animationType="none" className="h-32 rounded-xl" />
    </div>
  );
}
