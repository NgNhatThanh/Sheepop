import React from 'react';
import ProductCard from './ProductCard';

export default function ProductGrid(){
  // Sample product data based on the shared image
  const products = [
    {
      id: 'p1',
      title: 'Áo Thun Trơn Nam Nữ vải dày không xù lông mềm mịn form rộng unisex',
      image: '/placeholder.svg',
      price: 28500,
      originalPrice: 48000,
      discount: 40,
      rating: 4.5,
      soldCount: 140000,
      location: 'Hà Nội',
      badgeType: null,
      badgeText: '',
    },
    {
      id: 'p2',
      title: 'Áo Thun Protext Cotton Cao Cấp in Hình WARM TINT VINTAGE COFFE Unisex Nam Nữ Form Rộng',
      image: '/placeholder.svg',
      price: 92000,
      originalPrice: 170000,
      discount: 46,
      rating: 4.8,
      soldCount: 2900,
      location: 'Hà Nội',
      badgeType: 'hot',
      badgeText: 'Hot',
    },
    {
      id: 'p3',
      title: 'Áo Polo Tweety Local Brand Form Regular In Chữ Logo Unisex',
      image: '/placeholder.svg',
      price: 175000,
      originalPrice: 290000,
      discount: 40,
      rating: 4.9,
      soldCount: 2300,
      location: 'Hà Nội',
      badgeType: 'new',
      badgeText: 'New',
    },
    {
      id: 'p4',
      title: 'Áo Thun Nam,Nữ New York Yankees In Hình Cotton Form Rộng',
      image: '/placeholder.svg',
      price: 228000,
      originalPrice: 380000,
      discount: 40,
      rating: 5.0,
      soldCount: 112,
      location: 'Hà Nội',
      badgeType: null,
      badgeText: '',
    },
    {
      id: 'p5',
      title: 'Áo thun trơn nam nữ tay lỡ thời trang form rộng unisex',
      image: '/placeholder.svg',
      price: 95000,
      originalPrice: 180000,
      discount: 47,
      rating: 4.9,
      soldCount: 23300,
      location: 'Hà Nội',
      badgeType: null,
      badgeText: '',
    },
    {
      id: 'p6',
      title: 'Áo thun nữ dài tay Evalover có chuyện vải lông cừu form rộng',
      image: '/placeholder.svg',
      price: 209000,
      originalPrice: 299000,
      discount: 30,
      rating: 4.8,
      soldCount: 1400,
      location: 'Hà Nội',
      badgeType: 'hot',
      badgeText: 'Hot',
    },
    {
      id: 'p7',
      title: 'Áo Polo Nam Nữ Cộc Tay Chất Vải Mềm Mịn Thoáng Mát',
      image: '/placeholder.svg',
      price: 29000,
      originalPrice: 49000,
      discount: 40,
      rating: 4.8,
      soldCount: 67700,
      location: 'Hà Nội',
      badgeType: 'new',
      badgeText: 'New',
    },
    {
      id: 'p8',
      title: 'Áo Thun BOO Unisex Regular in Graphic Everyday',
      image: '/placeholder.svg',
      price: 219000,
      originalPrice: 399000,
      discount: 45,
      rating: 4.9,
      soldCount: 4400,
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
          {...product}
        />
      ))}
    </div>
  );
};