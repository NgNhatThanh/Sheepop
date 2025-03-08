import React from 'react';
import ProductCard from '../ProductCard';

export default function ProductGrid(){
  // Sample product data based on the shared thumbnailUrl
  const products = [
    {
      id: 'p1',
      name: 'Áo Thun Trơn Nam Nữ vải dày không xù lông mềm mịn form rộng unisex',
      thumbnailUrl: '/placeholder.svg',
      price: 28500,
      originalPrice: 48000,
      discount: 40,
      averageRating: 4.5,
      sold: 140000,
      location: 'Hà Nội',
      badgeType: null,
      badgeText: '',
    },
    {
      id: 'p2',
      name: 'Áo Thun Protext Cotton Cao Cấp in Hình WARM TINT VINTAGE COFFE Unisex Nam Nữ Form Rộng',
      thumbnailUrl: '/placeholder.svg',
      price: 92000,
      originalPrice: 170000,
      discount: 46,
      averageRating: 4.8,
      sold: 2900,
      location: 'Hà Nội',
      badgeType: 'hot',
      badgeText: 'Hot',
    },
    {
      id: 'p3',
      name: 'Áo Polo Tweety Local Brand Form Regular In Chữ Logo Unisex',
      thumbnailUrl: '/placeholder.svg',
      price: 175000,
      originalPrice: 290000,
      discount: 40,
      averageRating: 4.9,
      sold: 2300,
      location: 'Hà Nội',
      badgeType: 'new',
      badgeText: 'New',
    },
    {
      id: 'p4',
      name: 'Áo Thun Nam,Nữ New York Yankees In Hình Cotton Form Rộng',
      thumbnailUrl: '/placeholder.svg',
      price: 228000,
      originalPrice: 380000,
      discount: 40,
      averageRating: 5.0,
      sold: 112,
      location: 'Hà Nội',
      badgeType: null,
      badgeText: '',
    },
    {
      id: 'p5',
      name: 'Áo thun trơn nam nữ tay lỡ thời trang form rộng unisex',
      thumbnailUrl: '/placeholder.svg',
      price: 95000,
      originalPrice: 180000,
      discount: 47,
      averageRating: 4.9,
      sold: 23300,
      location: 'Hà Nội',
      badgeType: null,
      badgeText: '',
    },
    {
      id: 'p6',
      name: 'Áo thun nữ dài tay Evalover có chuyện vải lông cừu form rộng',
      thumbnailUrl: '/placeholder.svg',
      price: 209000,
      originalPrice: 299000,
      discount: 30,
      averageRating: 4.8,
      sold: 1400,
      location: 'Hà Nội',
      badgeType: 'hot',
      badgeText: 'Hot',
    },
    {
      id: 'p7',
      name: 'Áo Polo Nam Nữ Cộc Tay Chất Vải Mềm Mịn Thoáng Mát',
      thumbnailUrl: '/placeholder.svg',
      price: 29000,
      originalPrice: 49000,
      discount: 40,
      averageRating: 4.8,
      sold: 67700,
      location: 'Hà Nội',
      badgeType: 'new',
      badgeText: 'New',
    },
    {
      id: 'p8',
      name: 'Áo Thun BOO Unisex Regular in Graphic Everyday',
      thumbnailUrl: '/placeholder.svg',
      price: 219000,
      originalPrice: 399000,
      discount: 45,
      averageRating: 4.9,
      sold: 4400,
      location: 'Hà Nội',
      badgeType: 'hot',
      badgeText: 'Hot',
    },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-3 animate-fade-in">
      {products.map((product) => (
        <ProductCard
          key={product.id}
          product={product}
        />
      ))}
    </div>
  );
};