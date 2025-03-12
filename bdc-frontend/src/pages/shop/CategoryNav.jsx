import React, { useState, useRef, useEffect } from 'react';
import { FaSearch, FaChevronDown } from 'react-icons/fa';

export default function CategoryNav({ categories, selectedCat, setCategory }) {
  const [showDropdown, setShowDropdown] = useState(false);
  const visibleCount = 4;

  const visibleCategories = categories.slice(0, visibleCount);
  const overflowCategories = categories.slice(visibleCount);

  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setShowDropdown(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleCategoryClick = (id) => {
    setCategory(id);
    setShowDropdown(false);
  };

  return (
    <div className="w-full border-b border-gray-200 bg-white sticky top-0 z-10 shadow-sm">
      <div className="container px-4 mx-auto flex flex-col md:flex-row items-center justify-between">
        <div className="flex py-1 md:py-0 w-full">
            <button
              className={`cursor-pointer whitespace-nowrap px-4 py-3 font-medium text-gray-700 border-b-2 transition-all duration-200 hover:text-blue-600 truncate w-1/5 ${
                !selectedCat ? 'text-blue-600 border-blue-500' : 'border-transparent'
              }`}
              onClick={() => handleCategoryClick(null)}
            >
              Tất cả
            </button>
          {visibleCategories.map((category) => (
            <button
              key={category.id}
              className={`cursor-pointer whitespace-nowrap px-4 py-3 font-medium text-gray-700 border-b-2 transition-all duration-200 hover:text-blue-600 truncate w-1/5 ${
                selectedCat === category.id ? 'text-blue-600 border-blue-500' : 'border-transparent'
              }`}
              onClick={() => handleCategoryClick(category.id)}
            >
              {category.name}
            </button>
          ))}
          
          {overflowCategories.length > 0 && (
            <div className="relative" ref={dropdownRef} onMouseEnter={() => setShowDropdown(true)} onMouseLeave={() => setShowDropdown(false)}>
              <button className="whitespace-nowrap px-4 py-3 font-medium text-gray-700 border-b-2 border-transparent transition-all duration-200 hover:text-blue-600 flex items-center">
                Thêm <FaChevronDown className="ml-1 text-xs" />
              </button>
              {showDropdown && (
                <div className="absolute top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-lg py-1 z-20 min-w-40">
                  {overflowCategories.map((category) => (
                    <button
                      key={category.id}
                      className={`cursor-pointer block w-full text-left px-4 py-2 text-sm hover:bg-gray-100 ${
                        selectedCat === category.id ? 'text-blue-600 font-medium' : 'text-gray-700'
                      }`}
                      onClick={() => handleCategoryClick(category.id)}
                    >
                      {category.name}
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};