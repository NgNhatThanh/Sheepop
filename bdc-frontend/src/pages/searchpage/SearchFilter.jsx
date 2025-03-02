import React, { useState } from 'react';
import { RiArrowDownSLine, RiArrowLeftSLine, RiArrowRightSLine } from 'react-icons/ri';

export default function SearchFilter(){
  const sortOptions = [
    { id: 'relevant', label: 'Liên Quan' },
    { id: 'newest', label: 'Mới Nhất' },
    { id: 'bestselling', label: 'Bán Chạy' },
    { id: 'price', label: 'Giá' },
  ];

  const [activeSort, setActiveSort] = useState('relevant');
  const [currentPage, setCurrentPage] = useState(1);
  const totalPages = 17;

  const handleSortChange = (sortId) => {
    setActiveSort(sortId);
  };

  const handlePrevPage = () => {
    if (currentPage > 1) {
      setCurrentPage(prevPage => prevPage - 1);
    }
  };

  const handleNextPage = () => {
    if (currentPage < totalPages) {
      setCurrentPage(prevPage => prevPage + 1);
    }
  };

  return (
    <div className="flex flex-col gap-3 w-full animate-fade-in">
      <div className="flex items-center text-sm">
        <span className="text-gray-500 flex items-center">
          Kết quả tìm kiếm cho từ khóa '<span className="text-blue-500 font-medium">áo</span>'
        </span>
      </div>
      
      <div className="flex justify-between items-center">
        <div className="flex items-center">
          <span className="text-sm text-gray-500 mr-3">Sắp xếp theo</span>
          <div className="flex space-x-2">
            {sortOptions.map((option) => (
              <button
                key={option.id}
                onClick={() => handleSortChange(option.id)}
                className={`
                  px-4 py-2 text-sm font-medium border border-gray-200 rounded-md focus:outline-none transition-all
                  ${activeSort === option.id 
                    ? "bg-blue-500 text-white border-blue-500" 
                    : "hover:bg-gray-50"}
                `}
              >
                {option.label}
                {option.id === 'price' && (
                  <RiArrowDownSLine size={16} className="ml-1 inline-block" />
                )}
              </button>
            ))}
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <span className="text-sm text-gray-500">
            {currentPage}/{totalPages}
          </span>
          <div className="flex items-center">
            <button
              onClick={handlePrevPage}
              disabled={currentPage === 1}
              className={`
                "p-2 rounded-md border",
                currentPage === 1 
                  ? "text-gray-300 border-gray-200 cursor-not-allowed" 
                  : "text-gray-600 border-gray-200 hover:bg-gray-50"
              `}
            >
              <RiArrowLeftSLine size={16} />
            </button>
            <button
              onClick={handleNextPage}
              disabled={currentPage === totalPages}
              className={`
                "p-2 rounded-md border ml-1",
                currentPage === totalPages 
                  ? "text-gray-300 border-gray-200 cursor-not-allowed" 
                  : "text-gray-600 border-gray-200 hover:bg-gray-50"
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