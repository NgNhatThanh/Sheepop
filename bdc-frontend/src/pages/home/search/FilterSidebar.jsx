import React, { useState } from 'react';
import { RiArrowDownSLine, RiArrowUpSLine, RiFilterLine, RiPriceTag3Line } from 'react-icons/ri';

export default function FilterSidebar(){
  const [sections, setSections] = useState([
    {
      id: 'categories',
      title: 'Theo Danh Mục',
      isOpen: true,
      items: [
        { id: 'ao', name: 'Áo', count: 1250, checked: true },
        { id: 'thoi-trang-tre-em', name: 'Thời Trang Trẻ Em', count: 980 },
        { id: 'thoi-trang-nam', name: 'Thời Trang Nam', count: 1450 },
        { id: 'nha-cua', name: 'Nhà Cửa & Đời Sống', count: 756 },
      ]
    },
    {
      id: 'location',
      title: 'Nơi Bán',
      isOpen: true,
      items: [
        { id: 'ha-noi', name: 'Hà Nội', count: 852 },
        { id: 'ho-chi-minh', name: 'TP. Hồ Chí Minh', count: 1254 },
        { id: 'quan-9', name: 'Quận 9', count: 453 },
        { id: 'hoang-mai', name: 'Quận Hoàng Mai', count: 321 },
      ]
    },
    {
      id: 'shipping',
      title: 'Đơn Vị Vận Chuyển',
      isOpen: true,
      items: [
        { id: 'hoa-toc', name: 'Hỏa Tốc', count: 231 },
        { id: 'nhanh', name: 'Nhanh', count: 542 },
        { id: 'tiet-kiem', name: 'Tiết Kiệm', count: 876, checked: true },
      ]
    },
    {
      id: 'brand',
      title: 'Thương Hiệu',
      isOpen: true,
      items: [
        { id: 'mucho', name: 'Mucho', count: 124 },
        { id: 'misoul', name: 'MiSoul', count: 98 },
        { id: 'adam-store', name: 'ADAM STORE', count: 156 },
        { id: 'teelab', name: 'Teelab', count: 213 },
      ]
    },
  ]);

  const [priceRange, setPriceRange] = useState({
    min: 0,
    max: 500000
  });

  const toggleSection = (sectionId) => {
    setSections(prevSections =>
      prevSections.map(section =>
        section.id === sectionId ? { ...section, isOpen: !section.isOpen } : section
      )
    );
  };

  const toggleCheckbox = (sectionId, itemId) => {
    setSections(prevSections =>
      prevSections.map(section =>
        section.id === sectionId ? {
          ...section,
          items: section.items.map(item =>
            item.id === itemId ? { ...item, checked: !item.checked } : item
          )
        } : section
      )
    );
  };

  return (
    <div className="w-full bg-white rounded-lg shadow-sm border border-gray-100 p-4 animate-fade-in">
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-medium flex items-center gap-2">
          <RiFilterLine size={18} className="text-orange-500" />
          BỘ LỌC TÌM KIẾM
        </h2>
      </div>

      {sections.map((section) => (
        <div key={section.id} className="py-4 border-b border-gray-200">
          <button
            className="font-medium text-gray-800 mb-2 flex justify-between items-center w-full"
            onClick={() => toggleSection(section.id)}
          >
            {section.title}
            {section.isOpen ? (
              <RiArrowUpSLine size={16} className="text-gray-500" />
            ) : (
              <RiArrowDownSLine size={16} className="text-gray-500" />
            )}
          </button>
          
          {section.isOpen && (
            <div className="mt-2 space-y-2 max-h-[200px] overflow-y-auto pr-2">
              {section.items.map((item) => (
                <div key={item.id} className="flex items-center">
                  <input
                    type="checkbox"
                    id={`${section.id}-${item.id}`}
                    className="w-4 h-4 cursor-pointer rounded border-gray-300 text-orange-500 focus:ring-orange-500"
                    checked={item.checked || false}
                    onChange={() => toggleCheckbox(section.id, item.id)}
                  />
                  <label htmlFor={`${section.id}-${item.id}`} className="ml-2 text-sm font-medium text-gray-700 cursor-pointer select-none">
                    {item.name}
                  </label>
                  <span className="ml-auto text-xs text-gray-500">({item.count})</span>
                </div>
              ))}
            </div>
          )}
        </div>
      ))}

      <div className="py-4 border-b border-gray-200">
        <h3 className="font-medium text-gray-800 mb-2 flex items-center">
          <RiPriceTag3Line size={16} className="mr-2 text-orange-500" />
          Khoảng Giá
        </h3>
        <div className="mt-4 px-2">
          <div className="flex items-center justify-between mb-2">
            <span className="text-xs text-gray-500">₫{priceRange.min.toLocaleString()}</span>
            <span className="text-xs text-gray-500">₫{priceRange.max.toLocaleString()}</span>
          </div>
          <input
            type="range"
            min="0"
            max="1000000"
            step="10000"
            value={priceRange.max}
            onChange={(e) => setPriceRange(prev => ({ ...prev, max: parseInt(e.target.value) }))}
            className="h-2 bg-gray-200 rounded-full appearance-none cursor-pointer w-full"
          />
          <div className="flex gap-2 mt-4">
            <div className="flex-1">
              <input
                type="number"
                value={priceRange.min}
                onChange={(e) => setPriceRange(prev => ({ ...prev, min: parseInt(e.target.value) }))}
                className="w-full px-2 py-1 text-sm border border-gray-300 rounded-md"
                placeholder="₫ TỪ"
              />
            </div>
            <div className="self-center">-</div>
            <div className="flex-1">
              <input
                type="number"
                value={priceRange.max}
                onChange={(e) => setPriceRange(prev => ({ ...prev, max: parseInt(e.target.value) }))}
                className="w-full px-2 py-1 text-sm border border-gray-300 rounded-md"
                placeholder="₫ ĐẾN"
              />
            </div>
          </div>
          <button className="w-full mt-3 px-3 py-2 bg-blue-500 text-white rounded-md text-sm font-medium hover:bg-blue-600 transition-colors">
            Áp Dụng
          </button>
        </div>
      </div>
    </div>
  );
};