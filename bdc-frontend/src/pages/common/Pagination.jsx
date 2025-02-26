import { useState } from "react";

export default function Pagination({ page, setPage, limit, setLimit, maxPage }) {
  const [open, setOpen] = useState(false);

  const getPaginationNumbers = () => {
    if (maxPage <= 5) return [...Array(maxPage).keys()].map((i) => i + 1);
    if (page <= 3) return [1, 2, 3, "...", maxPage];
    if (page >= maxPage - 2) return [1, "...", maxPage - 2, maxPage - 1, maxPage];
    return [1, "...", page - 1, page, page + 1, "...", maxPage];
  };

  return (
    <div className="flex justify-end items-center space-x-4 p-4">
      <button
        className={`px-2 py-1 ${page === 1 ? "text-gray-400 cursor-not-allowed" : "text-gray-600"}`}
        disabled={page === 1}
        onClick={() => setPage(page - 1)}
      >
        &lt;
      </button>

      {getPaginationNumbers().map((num, index) => (
        <button
          key={index}
          className={`px-2 py-1 rounded ${num === page ? "text-red-500 font-semibold" : "text-gray-600"}`}
          onClick={() => num !== "..." && setPage(num)}
        >
          {num}
        </button>
      ))}

      <button
        className={`px-2 py-1 ${page === maxPage ? "text-gray-400 cursor-not-allowed" : "text-gray-600"}`}
        disabled={page === maxPage}
        onClick={() => setPage(page + 1)}
      >
        &gt;
      </button>

      <div className="relative">
        <button
          onClick={() => setOpen(!open)}
          className="border border-gray-300 p-2 rounded flex transtion-all duration-200 items-center gap-1 cursor-pointer hover:border-blue-600"
        >
          {limit}/trang
          <span className={`transform transition-transform ${open ? "rotate-180" : "rotate-0"}`}>
            ▲
          </span>
        </button>
        <ul
          className={`absolute right-0 bottom-full mb-1 w-24 bg-white border border-gray-300 rounded shadow-md overflow-hidden transition-all duration-300 ${
            open ? "opacity-100 max-h-40" : "opacity-0 max-h-0"
          }`}
        >
          {[10, 24, 50].map((num) => (
            <li
              key={num}
              className="px-2 py-1 cursor-pointer hover:bg-gray-200"
              onClick={() => {
                setLimit(num);
                setOpen(false);
              }}
            >
              {num}/trang
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
