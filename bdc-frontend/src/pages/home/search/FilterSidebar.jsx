import React, { useEffect, useState } from 'react';
import { RiFilterLine, RiPriceTag3Line } from 'react-icons/ri';
import { BASE_API_URL } from '../../../constants';
import { FaStar } from "react-icons/fa";

export default function FilterSidebar({filters, setFilters, resetFilters}){

  const [priceRange, setPriceRange] = useState({
    min: filters.minPrice || '',
    max: filters.maxPrice || ''
  });

  const [categories, setCategories] = useState([])
  const [locations, setLocations] = useState([])

  const [errorPriceRange, setErrorPriceRange] = useState(false)

  const handlePriceChange = () => {
    setErrorPriceRange(false)
    if(priceRange.min && priceRange.max && priceRange.min > priceRange.max){
      setErrorPriceRange(true)
      return
    }
    if(!priceRange.min && !priceRange.max){
      setErrorPriceRange(true)
      return
    }
    if(priceRange.min){
      setFilters(prev => ({
        ...prev,
        minPrice: priceRange.min
      }))
    }
    if(priceRange.max){
      setFilters(prev => ({
        ...prev,
        maxPrice: priceRange.max
      }))
    }
  }

  useEffect(() => {
    const fetchCategories = () => {
      fetch(`${BASE_API_URL}/v1/homepage/get_categories_filter`)
        .then(res => res.json())
        .then(res => setCategories(res))
    }

    const fetchLocations = () => {
      fetch(`${BASE_API_URL}/v1/homepage/get_locations_filter`)
        .then(res => res.json())
        .then(res => setLocations(res))
    }

    fetchLocations()
    fetchCategories()
  }, [])

  return (
    <div className="w-full bg-white rounded-lg shadow-sm border border-gray-100 p-4 animate-fade-in">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-medium flex items-center gap-2">
          <RiFilterLine size={18} className="text-orange-500" />
          BỘ LỌC TÌM KIẾM
        </h2>
      </div>

      <div className="py-4 border-b border-gray-200">
        <h2
          className="font-medium text-gray-800 mb-2 flex justify-between items-center w-full"
        >
          Theo danh mục
        </h2>
        <div className="mt-2 space-y-2 max-h-[200px] overflow-y-auto pr-2">
          {categories.length > 0 && categories.map((cat) => (
            <div 
              key={cat.id} 
              className="flex items-center"
            >
              <input
                type="checkbox"
                checked={filters.categoryIds?.includes(cat.id)}
                className="w-4 h-4 cursor-pointer rounded border-gray-300 text-orange-500 focus:ring-orange-500"
                onChange={e => {
                  setFilters(prev => ({
                    ...prev,
                    categoryIds: e.target.checked
                      ? prev.categoryIds ? [...prev.categoryIds, cat.id] : [cat.id] // Thêm nếu chưa có
                      : (prev.categoryIds || []).filter(id => id !== cat.id) // Xoá nếu đã có
                  }));
                }}
              />
              <label className="ml-2 text-sm font-medium text-gray-700 cursor-pointer select-none">
                {cat.name}
              </label>
            </div>
          ))}
        </div>
      </div>

      <div className="py-4 border-b border-gray-200">
        <h2
          className="font-medium text-gray-800 mb-2 flex justify-between items-center w-full"
        >
          Nơi bán
        </h2>
        <div className="mt-2 space-y-2 max-h-[200px] overflow-y-auto pr-2">
          {locations.length > 0 && locations.map((loc, index) => (
            <div 
              key={index} 
              className="flex items-center"
            >
              <input
                type="checkbox"
                checked={filters.locations?.includes(loc.name)}
                className="w-4 h-4 cursor-pointer rounded border-gray-300 text-orange-500 focus:ring-orange-500"
                onChange={e => {
                  setFilters(prev => ({
                    ...prev,
                    locations: e.target.checked
                      ? [...prev.locations, loc.name] 
                      : (prev.locations || []).filter(locName => locName !== loc.name) 
                  }));
                }}
              />
              <label className="ml-2 text-sm font-medium text-gray-700 cursor-pointer select-none">
                {loc.name}
              </label>
            </div>
          ))}
        </div>
      </div>

      <div className="py-4 border-b border-gray-200">
        <h3 className="font-medium text-gray-800 mb-2 flex items-center">
          <RiPriceTag3Line size={16} className="mr-2 text-orange-500" />
          Khoảng Giá
        </h3>
        <div className="mt-4 px-2">
          <div className="flex gap-2 mt-4">
            <div className="flex-1">
              <input
                type="number"
                value={priceRange.min}
                onChange={(e) => setPriceRange(prev => ({ ...prev, min: parseInt(e.target.value) || '' }))}
                className="w-full px-2 py-1 text-sm border border-gray-300 rounded-md"
                placeholder="₫ Từ"
              />
            </div>
            <div className="self-center">-</div>
            <div className="flex-1">
              <input
                type="number"
                value={priceRange.max}
                onChange={(e) => setPriceRange(prev => ({ ...prev, max: parseInt(e.target.value) || '' }))}
                className="w-full px-2 py-1 text-sm border border-gray-300 rounded-md"
                placeholder="₫ Đến"
              />
            </div>
          </div>
          {errorPriceRange && (
            <p className='text-red-500 text-sm'>Vui lòng chọn khoảng giá phù hợp</p>
          )}
          <button 
            className="cursor-pointer w-full mt-3 px-3 py-2 bg-blue-500 text-white rounded-md text-sm font-medium hover:bg-blue-600 transition-colors"
            onClick={handlePriceChange}  
          >
            Áp Dụng
          </button>
        </div>
      </div>

      <div className="py-4 border-b border-gray-200">
        <h2
          className="font-medium text-gray-800 mb-2 flex justify-between items-center w-full"
        >
          Đánh giá
        </h2>

        <div className="mt-4 px-2 flex flex-col gap-2">
          {[5, 4, 3, 2, 1].map(rating => (
            <div 
              key={rating} 
              className="cursor-pointer flex gap-3 items-center justify-left"
              onClick={() => {
                setFilters(prev => ({
                  ...prev, 
                  minRating: rating
                }))
              }}
            >
              {[...Array(5)].map((_, index) => (
                <FaStar
                  key={index}
                  className={index < rating ? "text-blue-500" : "text-gray-300"}
                />
              ))}
              {rating <= 4 && <p>trở lên</p>}
            </div>
          ))}
        </div>
      </div> 

      <button 
        className="cursor-pointer w-full mt-3 px-3 py-2 bg-blue-500 text-white rounded-md text-sm font-medium hover:bg-blue-600 transition-colors"
        onClick={() => {
          setPriceRange({
            max: '',
            min: ''
          })
          resetFilters()
        }}  
      >
        Xóa bộ lọc
      </button>

    </div>
  );
};