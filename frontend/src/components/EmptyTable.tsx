import { EmptyState, Table } from "@heroui/react";
import { GoInbox } from "react-icons/go";
import type { JSX } from "react";

export default function EmptyTable<T extends object>({
  data,
  renderRow,
  message = "No results found",
}: {
  data: T[];
  renderRow: (item: T) => JSX.Element;
  message?: string;
}) {
  return (
    <Table.Body
      items={data}
      renderEmptyState={() => (
        <EmptyState className="flex h-full w-full flex-col items-center justify-center gap-4 text-center">
          <GoInbox className="size-6 text-muted" />
          <span className="text-sm text-muted">{message}</span>
        </EmptyState>
      )}
    >
      {(item: T) => renderRow(item)}
    </Table.Body>
  );
}
