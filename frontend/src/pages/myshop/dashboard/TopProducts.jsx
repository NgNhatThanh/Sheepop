import React from 'react';
import { FiBox, FiArrowUp, FiArrowDown } from 'react-icons/fi';

const products = [
  {
    id: 1,
    name: 'iPhone 15 Pro',
    sales: 124,
    growth: '+12%',
    isPositive: true
  },
  {
    id: 2,
    name: 'MacBook Air M3',
    sales: 98,
    growth: '+9%',
    isPositive: true
  },
  {
    id: 3,
    name: 'iPad Air',
    sales: 67,
    growth: '+5%',
    isPositive: true
  },
  {
    id: 4,
    name: 'Apple Watch',
    sales: 45,
    growth: '-3%',
    isPositive: false
  }
];

const TopProducts = () => {
  return (
    <div className="rounded-sm border bg-white text-card-foreground shadow-sm h-full">
      <div className="flex flex-col space-y-1.5 p-6 pb-2">
        <div className="flex items-center gap-2">
          <FiBox className="text-blue-500" />
          <h3 className="text-2xl font-semibold leading-none tracking-tight">Top sản phẩm</h3>
        </div>
      </div>
      <div className="p-6 pt-0">
        <div className="space-y-4">
          {products.map((product) => (
            <div 
              key={product.id} 
              className="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 transition-colors"
            >
              <div className="flex flex-col">
                <span className="font-medium text-gray-800">{product.name}</span>
                <span className="text-sm text-gray-500">{product.sales} đã bán</span>
              </div>
              <div className="flex items-center">
                {product.isPositive ? 
                  <FiArrowUp className="text-green-500 mr-1" /> : 
                  <FiArrowDown className="text-red-500 mr-1" />
                }
                <span className={`text-sm font-medium ${product.isPositive ? 'text-green-600' : 'text-red-600'}`}>
                  {product.growth}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default TopProducts;
