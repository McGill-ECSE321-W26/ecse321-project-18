import { Skeleton } from "@heroui/react";

export default function CustomSkeleton() {
  return (
    <div className="skeleton--shimmer justify-self-center self-center relative grid w-full max-w-xl grid-cols-3 gap-4 overflow-hidden rounded-xl">
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
      <Skeleton animationType="none" className="h-24 rounded-xl" />
    </div>
  );
}
