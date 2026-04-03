import { EmptyState, Table } from "@heroui/react";
import { GoInbox } from "react-icons/go";

export default function EmptyTable() {
  return (
    <Table.Body
      renderEmptyState={() => (
        <EmptyState className="flex h-full w-full flex-col items-center justify-center gap-4 text-center">
          <GoInbox className="size-6 text-muted" />
          <span className="text-sm text-muted">No results found</span>
        </EmptyState>
      )}
    >
      {[]}
    </Table.Body>
  );
}
