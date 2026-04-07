export default function Footer() {
  const year = new Date().getFullYear();

  return (
    <footer className="border-t flex items-center px-4 h-16">
      <div className="page-wrap flex flex-col items-center justify-between gap-4 text-center sm:flex-row sm:text-left">
        <p className="m-0 text-sm">
          &copy; {year} Stilton's Store. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
