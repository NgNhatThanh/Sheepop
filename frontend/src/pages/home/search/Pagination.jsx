export default function Pagination({page, totalPages, setPage}){

    const getPaginationNumbers = () => {
        if (totalPages <= 5) return [...Array(totalPages).keys()].map((i) => i + 1);
        if (page <= 3) return [1, 2, 3, "...", totalPages];
        if (page >= totalPages - 2) return [1, "...", totalPages - 2, totalPages - 1, totalPages];
        return [1, "...", page - 1, page, page + 1, "...", totalPages];
    };

    return (
        <div className='flex gap-3 justify-center items-center mt-5'>
            <button
                className={`px-2 py-1 text-3xl ${page === 0 ? "text-gray-400 cursor-not-allowed" : "cursor-pointer text-gray-600"}`}
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
            >
                &lt;
            </button>

            {getPaginationNumbers().map((num, index) => (
                <button
                    key={index}
                    className={`cursor-pointer px-2 py-1 rounded-xs text-xl ${num === page + 1 ? "bg-blue-500 text-white font-semibold" : "text-gray-600"}`}
                    onClick={() => num !== "..." && setPage(num - 1)}
                >
                {num}
                </button>
            ))}

            <button
                className={`px-2 py-1 text-3xl ${page === totalPages - 1 ? "text-gray-400 cursor-not-allowed" : "cursor-pointer text-gray-600"}`}
                disabled={page === totalPages - 1}
                onClick={() => setPage(page + 1)}
            >
                &gt;
            </button>
        </div>  
    )
}