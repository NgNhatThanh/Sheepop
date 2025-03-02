import React from 'react';
import { AiFillStar } from 'react-icons/ai';

export default function ProductCard({
  id,
  title,
  image,
  price,
  originalPrice,
  discount,
  rating,
  soldCount,
  location,
  badgeType,
  badgeText,
}){
  const formatPrice = (amount) => {
    return `₫${amount.toLocaleString()}`;
  };

  return (
    <div className="relative bg-white rounded-lg overflow-hidden border border-gray-200 transition-all duration-300 hover:shadow-md animate-fade-up">
      {badgeType && badgeText && (
        <div className={`
          absolute top-2 left-2 text-xs font-bold uppercase px-2 py-1 rounded-sm text-white
          ${badgeType === 'new' && "bg-green-500"}
          ${badgeType === 'hot' && "bg-blue-500"}
          ${badgeType === 'mall' && "bg-blue-500"}
        `}>
          {badgeText}
        </div>
      )}
      
      {discount && (
        <div className="absolute top-2 right-2 bg-blue-500 text-white text-xs font-bold px-1.5 py-0.5 rounded">
          -{discount}%
        </div>
      )}
      
      <div className="relative overflow-hidden">
        <img 
          src={image} 
          className="w-full aspect-square object-cover object-center transition-transform duration-300 group-hover:scale-105"
        />
      </div>
      
      <div className="p-3">
        <h3 className="text-sm font-medium line-clamp-2 min-h-[2.5rem]" title={title}>
          {title}
        </h3>
        
        <div className="flex items-center mb-1 justify-between">
          <div>
            <span className="text-base font-bold text-blue-500">{formatPrice(price)}</span>
            {originalPrice && (
              <span className="text-xs text-gray-500 line-through ml-1">
                {formatPrice(originalPrice)}
              </span>
            )}
          </div>
        </div>
        
        <div className="flex items-center justify-between text-xs text-gray-500">
          <div className="flex items-center">
            <span className="text-yellow-400 mr-1">
              <AiFillStar size={12} className="inline"/>
            </span>
            {rating}
            <span className="mx-1">|</span>
            <span>Đã bán {soldCount}</span>
          </div>
          <div>{location}</div>
        </div>
      </div>
      
      <div className="absolute inset-0 opacity-0 group-hover:opacity-100 bg-black bg-opacity-5 transition-opacity duration-300 flex items-center justify-center">
        <div className="bg-white px-4 py-2 rounded-md shadow-md transform translate-y-4 group-hover:translate-y-0 transition-transform duration-300">
          <button className="hover:text-blue-500 transition-colors">
            Xem nhanh
          </button>
        </div>
      </div>
    </div>
  );
};