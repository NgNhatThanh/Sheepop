import React, { useState, useEffect } from "react";

const ProductTable = () => {
  const [products, setProducts] = useState([
    {
      id: 1,
      thumbnailUrl: "https://via.placeholder.com/50",
      name: "Product A",
      price: 100,
      quantity: 10,
      visible: true,
    },
    {
      id: 2,
      thumbnailUrl: "https://via.placeholder.com/50",
      name: "Product B",
      price: 200,
      quantity: 5,
      visible: false,
    },
    {
      id: 3,
      thumbnailUrl: "https://via.placeholder.com/50",
      name: "Product C",
      price: 300,
      quantity: 8,
      visible: true,
    },
  ]);

  return (
    <div className="container mx-auto p-4">
      <h2 className="text-xl font-bold mb-4">Product List</h2>
      <table className="table-auto w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-gray-100">
            <th className="border border-gray-300 p-2">STT</th>
            <th className="border border-gray-300 p-2">Thumbnail</th>
            <th className="border border-gray-300 p-2">Name</th>
            <th className="border border-gray-300 p-2">Price</th>
            <th className="border border-gray-300 p-2">Quantity</th>
            <th className="border border-gray-300 p-2">Visible</th>
          </tr>
        </thead>
        <tbody>
          {products.map((product, index) => (
            <tr key={product.id}>
              <td className="border border-gray-300 p-2 text-center">{index + 1}</td>
              <td className="border border-gray-300 p-2 text-center">
                <img
                  src={product.thumbnailUrl}
                  alt={product.name}
                  className="w-12 h-12 object-cover mx-auto"
                />
              </td>
              <td className="border border-gray-300 p-2 text-center">{product.name}</td>
              <td className="border border-gray-300 p-2 text-center">${product.price}</td>
              <td className="border border-gray-300 p-2 text-center">{product.quantity}</td>
              <td className="border border-gray-300 p-2 text-center">
                {product.visible ? "Visible" : "Hidden"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default ProductTable;
