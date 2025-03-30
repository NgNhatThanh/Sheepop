import React from 'react';
import { AiFillStar } from 'react-icons/ai';
import { Link } from 'react-router-dom'
import { CiLocationOn } from "react-icons/ci";

export default function ProductCard({product}){
  const formatPrice = (amount) => {
    return `₫${amount.toLocaleString()}`;
  };

  return (
    <Link to={`/product/${encodeURIComponent(product.name.replace(/\s+/g, "-"))}.${product.id}`}  
        key={product.id} 
        className="bg-white rounded-sm shadow-sm transition-transform duration-200 hover:shadow-lg hover:-translate-y-1 hover:border-1 hover:border-blue-500"
    >
      <div className="relative overflow-hidden">
        <img 
          src={product.thumbnailUrl} 
          className="w-full rounded-sm aspect-square object-cover object-center transition-transform duration-300 group-hover:scale-105"
        />
      </div>
      
      <div className="p-3">
        <h3 className="text-sm font-medium line-clamp-2 min-h-[2.5rem]" title={product.name}>
          {product.name}
        </h3>
        
        <div className="flex items-center mb-1 justify-between">
          <div>
            <span className="text-base font-bold text-blue-500">{formatPrice(product.price)}</span>
          </div>
        </div>
        
        <div className="flex flex-col gap-1 justify-center text-xs text-gray-500">
          <div className="flex items-center">
            <span className="text-yellow-400 mr-1">
              <AiFillStar size={12}/>
            </span>
            {product.averageRating.toFixed(1)}
            <span className="mx-1">|</span>
            <span>Đã bán {product.sold}</span>
          </div>
          <div className='flex gap-1 items-center'>
            <CiLocationOn size={12}/>
            <div>{product.location}</div>
          </div>
        </div>
      </div>
    </Link>
  );
};