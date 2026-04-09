export default function Title({
  pagename,
  className = "",
}: {
  pagename: string;
  className?: string;
}) {
  return (
    <h2
      className={"font-serif flex text-2xl font-bold items-center justify-center pt-4".concat(
        " ",
        className,
      )}
    >
      {pagename}
    </h2>
  );
}
