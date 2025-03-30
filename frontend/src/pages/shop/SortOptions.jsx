import React, { useState } from 'react';
import { RiArrowDownSLine, RiArrowLeftSLine, RiArrowRightSLine } from 'react-icons/ri';

export default function SortOptions({sort, setSort, page, setPage, totalPages}){
  const sortOptions = [
    { id: 'popular', label: 'Phổ biến' },
    { id: 'newest', label: 'Mới Nhất' },
    { id: 'sales', label: 'Bán Chạy' },
  ];

  const [openPriceSortDropdown, setOpenPriceSortDropdown] = useState(false)

  const handlePrevPage = () => {
    if (page > 0) {
      setPage(prevPage => prevPage - 1);
    }
  };

  const handleNextPage = () => {
    if (page < totalPages - 1) {
      setPage(prevPage => prevPage + 1);
    }
  };

  return (
    <div className="flex flex-col gap-3 w-full animate-fade-in">
      <div className="flex justify-between items-center">
        <div className="flex items-center">
          <span className="text-sm text-gray-500 mr-3">Sắp xếp theo</span>
          <div className="flex space-x-2">
            {sortOptions.map((option) => (
              <button
                key={option.id}
                onClick={() => {
                  setSort({
                    sortBy: option.id,
                    order: "desc"
                  })
                }}
                className={`
                  cursor-pointer px-4 py-2 text-sm font-medium border border-gray-200 rounded-md focus:outline-none transition-all
                  ${sort.sortBy === option.id 
                    ? "bg-blue-500 text-white border-blue-500" 
                    : "hover:bg-gray-50"}
                `}
              >
                {option.label}
              </button>
            ))}

            <div 
              className='relative'
              onMouseEnter={() => setOpenPriceSortDropdown(true)}
              onMouseLeave={() => setOpenPriceSortDropdown(false)}
            >
              <button
                className={`
                    w-50 flex justify-between px-4 py-2 text-sm font-medium border border-gray-200 rounded-md focus:outline-none transition-all
                    ${sort.sortBy === 'price' 
                      ? "bg-blue-500 text-white border-blue-500" 
                      : "hover:bg-gray-50"}
                  `}
                >
                  {sort.sortBy === 'price' ? ('Giá: ' + (sort.order === 'asc' ? 'Thấp đến Cao' : 'Cao đến Thấp')) : 'Giá'}
                  <RiArrowDownSLine size={16} className="ml-1 inline-block" />
              </button>
              {openPriceSortDropdown && (
                <div className='absolute top-full bg-white z-5 w-50'>
                  <button
                    onClick={() => {
                      setSort({
                        sortBy: 'price',
                        order: 'asc'
                      })
                      setOpenPriceSortDropdown(false)
                    }}
                    className={`
                      w-full text-left px-4 py-2 text-sm font-medium hover:text-blue-500 cursor-pointer
                    `}
                  >
                    Giá: Thấp đến Cao
                  </button>

                  <button
                    onClick={() => {
                      setSort({
                        sortBy: 'price',
                        order: 'desc'
                      })
                      setOpenPriceSortDropdown(false)
                    }}
                    className={`
                      w-full text-left px-4 py-2 text-sm font-medium hover:text-blue-500 cursor-pointer
                    `}
                  >
                    Giá: Cao đến Thấp
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <span className="text-gray-500">
            <span className='text-blue-700'>{Number(page) + 1}</span>/{totalPages}
          </span>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrevPage}
              disabled={page === 0}
              className={`
                p-2 rounded-md border border-blue-500
                ${page === 0 
                  ? "text-gray-300 cursor-not-allowed" 
                  : "text-gray-600 cursor-pointer hover:bg-blue-300"}
              `}
            >
              <RiArrowLeftSLine size={16} />
            </button>
            <button
              onClick={handleNextPage}
              disabled={page === totalPages - 1}
              className={`
                p-2 rounded-md border border-blue-500
                ${page === totalPages - 1 
                  ? "text-gray-300 cursor-not-allowed" 
                  : "text-gray-600 cursor-pointer hover:bg-blue-300"}
              `}
            >
              <RiArrowRightSLine size={16} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};