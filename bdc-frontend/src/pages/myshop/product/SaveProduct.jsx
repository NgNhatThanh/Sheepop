"use client"

import { useState, useEffect } from "react"
import { BASE_API_URL } from "../../../constants/index.js"
import { fetchWithAuth } from "../../../util/AuthUtil.js"
import { uploadImage } from "../../../util/UploadUtil.js"
import { FaUpload, FaTimes, FaPlus, FaTrash } from "react-icons/fa"
import { ToastContainer, toast } from "react-toastify"

export default function SaveProduct({ curProduct }) {

  const [productDetails, setProductDetails] = useState({
    name: curProduct ? curProduct.name : "",
    description: curProduct ? curProduct.description : "",
    thumbnailUrl: curProduct ? curProduct.thumbnailUrl : "",
    price: curProduct ? curProduct.price : "",
    quantity: curProduct ? curProduct.quantity : "",
    weight: curProduct ? curProduct.weight : "",
    visible: curProduct ? curProduct.visible : true,
  })

  const updateProductDetails = (field, value) => {
    setProductDetails((prevDetails) => ({
      ...prevDetails,
      [field]: value,
    }))
  }

  const [galleryImages, setGalleryImages] = useState(
    curProduct ? curProduct.mediaList.map(media => media.url) : []
  )
  const [variantAttributes, setVariantAttributes] = useState(
    curProduct ? extractAttributes(curProduct.skuList) : []
  )
  const [variants, setVariants] = useState(
    curProduct ? curProduct.skuList.map(sku => ({
      attributes: sku.attributes,
      price: sku.price,
      quantity: sku.quantity,
      sku: sku.sku
    })) : []
  )
  const [categorySearch, setCategorySearch] = useState("")
  const [categories, setCategories] = useState([])
  const [selectedCategory, setSelectedCategory] = useState(curProduct ? curProduct.category : null)
  const [isDropdownVisible, setDropdownVisible] = useState(false)

  function extractAttributes(skuList) {
    const attributeMap = new Map();

    skuList.forEach(sku => {
        sku.attributes.forEach(attr => {
            if (!attributeMap.has(attr.name)) {
                attributeMap.set(attr.name, new Set());
            }
            attributeMap.get(attr.name).add(attr.value);
        });
    });

    return Array.from(attributeMap, ([name, values]) => ({
        name,
        values: Array.from(values),
    }));
  } 

  useEffect(() => {
    const timeout = setTimeout(() => {
      if (categorySearch.trim() !== "") {
        fetchCategories(categorySearch)
      }
    }, 500)

    return () => clearTimeout(timeout)
  }, [categorySearch])

  const displayCategory = (category) => {
    let displayStr = category.name
    let tmp = category.parent
    while (tmp) {
      displayStr = tmp.name + " -> " + displayStr
      tmp = tmp.parent
    }
    return displayStr
  }

  const fetchCategories = async (query) => {
    try {
      const response = await fetch(`${BASE_API_URL}/v1/common/category/search?keyword=${query}`)
      const jsonResponse = await response.json()
      const categoriesList = jsonResponse.data || []
      setCategories(categoriesList)
      setDropdownVisible(true)
    } catch (error) {
      console.error("Error fetching categories:", error)
    }
  }

  const handleSelectCategory = (category) => {
    setSelectedCategory(category)
    setCategorySearch("")
    setDropdownVisible(false)
  }

  const handleAddAttribute = () => {
    setVariantAttributes([...variantAttributes, { name: "", values: [] }])
  }

  const handleAttributeNameChange = (index, name) => {
    const updatedAttributes = [...variantAttributes]
    updatedAttributes[index].name = name
    setVariantAttributes(updatedAttributes)
  }

  const handleAddAttributeValue = (index, value) => {
    if (!value.trim()) return
    const updatedAttributes = [...variantAttributes]
    if (!updatedAttributes[index].values.includes(value)) {
      updatedAttributes[index].values.push(value)
    }
    setVariantAttributes(updatedAttributes)
    generateVariantCombinations()
  }

  const handleDeleteAttribute = (index) => {
    const updatedAttributes = variantAttributes.filter((_, i) => i !== index)
    setVariantAttributes(updatedAttributes)
    generateVariantCombinations()
  }

  const handleDeleteAttributeValue = (attrIndex, valueIndex) => {
    const updatedAttributes = [...variantAttributes]
    updatedAttributes[attrIndex].values.splice(valueIndex, 1)
    setVariantAttributes(updatedAttributes)
    generateVariantCombinations()
  }

  const generateVariantCombinations = () => {
    const combinations = variantAttributes.reduce((acc, attribute) => {
      if (acc.length === 0) return attribute.values.map((value) => [{ name: attribute.name, value }])
      return acc.flatMap((combination) =>
        attribute.values.map((value) => [...combination, { name: attribute.name, value }]),
      )
    }, [])

    const updatedVariants = combinations.map((combination) => ({
      attributes: combination,
      price: "",
      quantity: "",
      sku: "",
    }))

    setVariants(updatedVariants)
  }

  const handleVariantChange = (index, field, value) => {
    const updatedVariants = [...variants]
    updatedVariants[index][field] = value
    setVariants(updatedVariants)
  }

  const handleAddGallery = async (galleries) => {
    if (galleries.length + galleryImages.length > 9) {
      alert("Max gallery images is 9")
      return
    }
    const tmp = [...galleryImages]
    for (const img of galleries) {
      const url = await uploadImage(img)
      tmp.push(url)
      setGalleryImages([...tmp])
    }
  }

  const handleRemoveGallery = (index) => {
    setGalleryImages(galleryImages.filter((_, i) => i !== index))
  }

  const handleAddProduct = () => {
    const mediaList = galleryImages.map((imgUrl) => ({
      url: imgUrl,
      type: "IMAGE",
    }))

    productDetails.price = variants.length === 0 ? productDetails.price : -1
    productDetails.quantity = variants.length === 0 ? productDetails.quantity : -1

    const product = {
      ...productDetails,
      productId: curProduct ? curProduct.id: null,
      mediaList: mediaList,
      category: selectedCategory,
      skuList: variants,
    }

    fetchWithAuth(`${BASE_API_URL}/v1/shop/product/${curProduct ? 'update' : 'add'}`, window.location, true, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(product),
    })
      .then((res) => {
        if (!res.ok) {
          toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
        } else {
          alert("Done!")
          window.location.assign("/myshop/product-list")
        }
      })
      .catch((err) => {
        console.log(err)
        toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
      })
  }

  return (
    <div className="container px-4 py-8 bg-white rounded-sm">

      <div className="mb-6">
        <h2 className="text-2xl font-bold">Thông tin sản phẩm</h2>
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-1">Tên sản phẩm</label>
        <input
          type="text"
          value={productDetails.name}
          onChange={(e) => updateProductDetails("name", e.target.value)}
          placeholder="Tên sản phẩm"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <div className="mb-4">
        <label className="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
        <textarea
          value={productDetails.description}
          onChange={(e) => updateProductDetails("description", e.target.value)}
          placeholder="Mô tả sản phẩm"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 h-32"
        ></textarea>
      </div>

      <div className="mb-4">
        <h3 className="text-lg font-semibold mb-2">Hình đại diện</h3>
        <input
          type="file"
          accept="image/*"
          onChange={async (e) => {
            const url = await uploadImage(e.target.files[0])
            updateProductDetails("thumbnailUrl", url)
          }}
          className="hidden"
          id="thumbnailUpload"
        />
        {productDetails.thumbnailUrl && (
          <img
            src={productDetails.thumbnailUrl}
            alt="Thumbnail"
            className="w-32 h-32 object-contain rounded-lg shadow-lg mb-2"
          />
        )}
        <label
          htmlFor="thumbnailUpload"
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded cursor-pointer inline-flex items-center"
        >
          <FaUpload className="mr-2" /> Tải lên
        </label>
      </div>

      <div className="mb-4">
        <h3 className="text-lg font-semibold mb-2">Hình trưng bày (Tối đa: 9)</h3>
        <input
          type="file"
          multiple
          accept="image/*"
          onChange={(e) => handleAddGallery([...e.target.files])}
          className="hidden"
          id="galleryUpload"
        />
        <div className="flex flex-wrap gap-4 mb-2">
          {galleryImages.map((imgUrl, index) => (
            <div key={index} className="relative">
              <img
                src={imgUrl}
                alt={`Gallery ${index + 1}`}
                className="w-24 h-24 object-contain rounded-lg shadow-lg"
              />
              <button
                className="absolute top-0 right-0 bg-red-500 text-white text-xs p-1 rounded-full cursor-pointer hover:bg-red-600"
                onClick={() => handleRemoveGallery(index)}
              >
                <FaTimes />
              </button>
            </div>
          ))}
        </div>
        {galleryImages.length < 9 && (
          <label
            htmlFor="galleryUpload"
            className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded cursor-pointer inline-flex items-center"
          >
            <FaUpload className="mr-2" /> Tải lên
          </label>
        )}
      </div>

      <div className="mb-4">
        <h3 className="text-lg font-semibold mb-2">Ngành hàng</h3>
        <input
          type="text"
          value={categorySearch}
          onChange={(e) => setCategorySearch(e.target.value)}
          placeholder="Tìm kiếm ngành hàng"
          onFocus={() => setDropdownVisible(true)}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
        {selectedCategory && (
          <p className="mt-2 text-sm text-gray-600">
            Đã chọn: <strong>{displayCategory(selectedCategory)}</strong>
          </p>
        )}
        {isDropdownVisible && categories.length > 0 && (
          <ul className="mt-1 max-h-60 overflow-auto border border-gray-300 rounded-md bg-white shadow-lg">
            {categories.map((category) => (
              <li
                key={category.id}
                onClick={() => handleSelectCategory(category)}
                className="px-3 py-2 font-semibold hover:bg-gray-100 cursor-pointer"
              >
                {displayCategory(category)}
              </li>
            ))}
          </ul>
        )}
      </div>

      <div className="mb-4">
        <h3 className="text-lg font-semibold mb-2">Phân loại</h3>
        {variantAttributes.map((attribute, index) => (
          <div key={index} className="mb-4 p-4 border border-gray-300 rounded-md">
            <div className="flex items-center mb-2">
              <input
                type="text"
                value={attribute.name}
                onChange={(e) => handleAttributeNameChange(index, e.target.value)}
                placeholder="Tên phân loại (VD: Size, Màu, ...)"
                className="flex-grow px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 mr-2"
              />
              <button
                onClick={() => handleDeleteAttribute(index)}
                className="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded"
              >
                <FaTrash />
              </button>
            </div>
            <input
              type="text"
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleAddAttributeValue(index, e.target.value)
                  e.target.value = ""
                }
              }}
              placeholder="Nhấn Enter để thêm phân loại"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 mb-2"
            />
            <div className="flex flex-wrap gap-2">
              {attribute.values.map((value, i) => (
                <span key={i} className="bg-gray-200 px-2 py-1 rounded-full text-sm flex items-center">
                  {value}
                  <button
                    onClick={() => handleDeleteAttributeValue(index, i)}
                    className="ml-1 text-red-500 hover:text-red-700"
                  >
                    <FaTimes />
                  </button>
                </span>
              ))}
            </div>
          </div>
        ))}
        <button
          onClick={handleAddAttribute}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center"
        >
          <FaPlus className="mr-2" /> Thêm phân loại
        </button>
      </div>

      {variants.length > 0 ? (
        <div className="mb-4 overflow-x-auto">
          <table className="min-w-full bg-white border border-gray-300">
            <thead>
              <tr className="bg-gray-100">
                {variantAttributes.map((attr) => (
                  <th key={attr.name} className="px-4 py-2 text-left">
                    {attr.name}
                  </th>
                ))}
                <th className="px-4 py-2 text-left">Giá</th>
                <th className="px-4 py-2 text-left">Số lượng</th>
                <th className="px-4 py-2 text-left">SKU</th>
              </tr>
            </thead>
            <tbody>
              {variants.map((variant, index) => (
                <tr key={index} className="border-t border-gray-300">
                  {variant.attributes.map((attr, i) => (
                    <td key={i} className="px-4 py-2">
                      {attr.value}
                    </td>
                  ))}
                  <td className="px-4 py-2">
                    <input
                      type="text"
                      value={variant.price.toLocaleString()}
                      onChange={(e) => handleVariantChange(index, "price", e.target.value)}
                      placeholder="Giá"
                      className="w-full px-2 py-1 border border-gray-300 rounded"
                    />
                  </td>
                  <td className="px-4 py-2">
                    <input
                      type="text"
                      value={variant.quantity}
                      onChange={(e) => handleVariantChange(index, "quantity", e.target.value)}
                      placeholder="Số lượng"
                      className="w-full px-2 py-1 border border-gray-300 rounded"
                    />
                  </td>
                  <td className="px-4 py-2">
                    <input
                      type="text"
                      value={variant.sku}
                      onChange={(e) => handleVariantChange(index, "sku", e.target.value)}
                      placeholder="SKU"
                      className="w-full px-2 py-1 border border-gray-300 rounded"
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="mb-4 p-4 border border-gray-300 rounded-md">
          <h3 className="text-lg font-semibold mb-2">Thông tin mặc định</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Price</label>
              <input
                type="number"
                value={productDetails.price.toLocaleString()}
                onChange={(e) => updateProductDetails("price", e.target.value)}
                placeholder="Giá"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Số lượng</label>
              <input
                type="number"
                value={productDetails.quantity}
                onChange={(e) => updateProductDetails("quantity", e.target.value)}
                placeholder="Số lượng"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </div>
      )}

      <div className="mb-4 p-4 border border-gray-300 rounded-md">
        <h3 className="text-lg font-semibold mb-2">Thông tin vận chuyển</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Cân nặng</label>
            <input
              type="number"
              value={productDetails.weight}
              onChange={(e) => updateProductDetails("weight", e.target.value)}
              placeholder="Cân nặng (gram)"
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      </div>

      <div className="mb-4">
        <h3 className="text-lg font-semibold mb-2">Chế độ hiển thị</h3>
        <div className="flex items-center space-x-4">
          <label className="inline-flex items-center">
            <input
              type="radio"
              className="form-radio text-blue-600"
              checked={productDetails.visible}
              onChange={() => updateProductDetails("visible", true)}
            />
            <span className="ml-2">Hiển thị</span>
          </label>
          <label className="inline-flex items-center">
            <input
              type="radio"
              className="form-radio text-blue-600"
              checked={!productDetails.visible}
              onChange={() => updateProductDetails("visible", false)}
            />
            <span className="ml-2">Ẩn</span>
          </label>
        </div>
      </div>

      <div className="flex justify-center">
        <button
          onClick={handleAddProduct}
          className="bg-blue-500 w-50 cursor-pointer hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
        >
          Lưu
        </button>
      </div>
      
      <ToastContainer
        position="bottom-right"
      />
    </div>
  )
}

