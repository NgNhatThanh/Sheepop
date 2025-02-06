import React, { useState, useEffect } from "react";
import "./CreateProduct.css";
import {BASE_API_URL} from '../../../constants/index.js'
import {fetchWithAuth} from '../../../common/AuthUtil.js'

const AddProduct = () => {
  const [productDetails, setProductDetails] = useState({
    name: "",
    description: "",
    thumbnailUrl: "",
    price: -1,
    quantity: -1,
    visible: true
  });

  const updateProductDetails = (field, value) => {
    setProductDetails((prevDetails) => ({
      ...prevDetails,
      [field]: value,
    }));
  };
  const [galleryImages, setGalleryImages] = useState([]);
  const [video, setVideo] = useState(null);
  const [variantAttributes, setVariantAttributes] = useState([]);
  const [variants, setVariants] = useState([]);

  const [categorySearch, setCategorySearch] = useState(""); // Text người dùng nhập
  const [categories, setCategories] = useState([]); // Kết quả tìm kiếm trả về
  const [selectedCategory, setSelectedCategory] = useState(null); // Danh mục được chọn
  const [isDropdownVisible, setDropdownVisible] = useState(false); // Điều khiển dropdown

  // Debounce logic
  useEffect(() => {
    const timeout = setTimeout(() => {
      if (categorySearch.trim() !== "") {
        fetchCategories(categorySearch); // Gọi API tìm kiếm danh mục
      }
    }, 500); // Đợi 0.5s sau khi ngừng nhập

    return () => clearTimeout(timeout); // Xóa timeout khi giá trị thay đổi
  }, [categorySearch]);

  const displayCategory = (category) => {
    var displayStr = category.name
    var tmp = category.parent
    while(tmp){
      displayStr = tmp.name + ' -> ' + displayStr 
      tmp = tmp.parent
    }
    return displayStr
  }

  // Hàm gọi API (mô phỏng)
  const fetchCategories = async (query) => {
    try {
      // Thay bằng API thực tế của bạn
      const response = await fetch(`${BASE_API_URL}/common/category/search?keyword=${query}`);
      const jsonResponse = await response.json();
      const categoriesList = jsonResponse.data || []
      setCategories(categoriesList); // Lưu danh mục tìm được
      setDropdownVisible(true); // Hiển thị dropdown
    } catch (error) {
      console.error("Error fetching categories:", error);
    }
  };

  // Xử lý chọn danh mục
  const handleSelectCategory = (category) => {
    setSelectedCategory(category); // Lưu danh mục được chọn
    setCategorySearch(""); // Xóa text tìm kiếm
    setDropdownVisible(false); // Ẩn dropdown
  };

  const handleAddAttribute = () => {
    setVariantAttributes([...variantAttributes, { name: "", values: [] }]);
  };

  const handleAttributeNameChange = (index, name) => {
    const updatedAttributes = [...variantAttributes];
    updatedAttributes[index].name = name;
    setVariantAttributes(updatedAttributes);
  };

  const handleAddAttributeValue = (index, value) => {
    if (!value.trim()) return;
    const updatedAttributes = [...variantAttributes];
    if (!updatedAttributes[index].values.includes(value)) {
      updatedAttributes[index].values.push(value);
    }
    setVariantAttributes(updatedAttributes);
    generateVariantCombinations();
  };

  const handleDeleteAttribute = (index) => {
    const updatedAttributes = variantAttributes.filter((_, i) => i !== index);
    setVariantAttributes(updatedAttributes);
    generateVariantCombinations();
  };

  const handleDeleteAttributeValue = (attrIndex, valueIndex) => {
    const updatedAttributes = [...variantAttributes];
    updatedAttributes[attrIndex].values.splice(valueIndex, 1);
    setVariantAttributes(updatedAttributes);
    generateVariantCombinations();
  };

  const generateVariantCombinations = () => {
    const combinations = variantAttributes.reduce((acc, attribute) => {
      if (acc.length === 0) return attribute.values.map((value) => [{ name: attribute.name, value }]);
      return acc.flatMap((combination) =>
        attribute.values.map((value) => [...combination, { name: attribute.name, value }])
      );
    }, []);

    const updatedVariants = combinations.map((combination) => ({
      attributes: combination,
      price: "",
      quantity: "",
      sku: "",
    }));

    setVariants(updatedVariants);
  };

  const handleVariantChange = (index, field, value) => {
    const updatedVariants = [...variants];
    updatedVariants[index][field] = value;
    setVariants(updatedVariants);
  };

  const handleAddGalery = (galeries) => {
    console.log(galeries);
    setGalleryImages(galeries)
  }

  const handleAddProduct = () => {

    const mediaList = galleryImages.map(img => (
      {
        url: img ? img.name : null,
        type: "IMAGE"
      })
    )
    mediaList.push({
      url: video ? video.name : null,
      type: "VIDEO"
    })

    productDetails.price = variants.length === 0 ? productDetails.price : -1;
    productDetails.quantity = variants.length === 0 ? productDetails.quantity : -1;

    const product = {
      ...productDetails,
      mediaList: mediaList,
      category: selectedCategory,
      skuList: variants
    };

    fetchWithAuth(`${BASE_API_URL}/shop/product/add`, {
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(product)
    })
    .then(res => console.log(res))
    .catch(err => console.log(err))
  };

  return (
    <div className="container">
      <div className="header">
        <h2>Create New Product</h2>
      </div>

      <div className="form-group">
        <label>Product Name</label>
        <input
          type="text"
          value={productDetails.name}
          onChange={(e) => updateProductDetails("name", e.target.value)}
          placeholder="Enter product name"
        />
      </div>

      <div className="form-group">
        <label>Description</label>
        <textarea
          value={productDetails.description}
          onChange={(e) => updateProductDetails("description", e.target.value)}
          placeholder="Enter product description"
        ></textarea>
      </div>

      <div className="form-group">
        <label>Thumbnail</label>
        <input
          type="file"
          // value={productDetails.thumbnailUrl}
          onChange={(e) => updateProductDetails("thumbnailUrl" ,e.target.files[0].name)}
        />
      </div>

      <div className="form-group">
        <label>Gallery Images</label>
        <input
          type="file"
          multiple
          onChange={(e) => handleAddGalery([...e.target.files])}
        />
      </div>

      <div className="form-group">
        <label>Product Video</label>
        <input
          type="file"
          onChange={(e) => setVideo(e.target.files[0])}
        />
      </div>

      <div className="form-group">
      <h3>Select Category</h3>
      <input
        type="text"
        value={categorySearch}
        onChange={(e) => setCategorySearch(e.target.value)}
        placeholder="Search category"
        onFocus={() => setDropdownVisible(true)} // Hiển thị dropdown khi focus
      />
      {selectedCategory && (
        <p className="selected-category">
          Selected: <strong>{displayCategory(selectedCategory)}</strong>
        </p>
      )}

      {/* Dropdown danh sách kết quả */}
      {isDropdownVisible && categories.length > 0 && (
        <ul className="dropdown">
          {categories.map((category) => (
            <li
              onClick={() => handleSelectCategory(category)}
              className="dropdown-item"
            >
              {displayCategory(category)}
            </li>
          ))}
        </ul>
      )}
    </div>
      <div className="form-group">
        <h3>Product Variants</h3>
        {variantAttributes.map((attribute, index) => (
          <div key={index} className="attribute">
            <div className="attribute-header">
              <input
                type="text"
                value={attribute.name}
                onChange={(e) => handleAttributeNameChange(index, e.target.value)}
                placeholder="Variant Name (e.g., Size, Color)"
              />
              <button onClick={() => handleDeleteAttribute(index)}>Delete</button>
            </div>
            <input
              type="text"
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  handleAddAttributeValue(index, e.target.value);
                  e.target.value = "";
                }
              }}
              placeholder="Press Enter to Add Variant Value"
            />
            <div className="attribute-values">
              {attribute.values.map((value, i) => (
                <span key={i} className="value">
                  {value}
                  <button onClick={() => handleDeleteAttributeValue(index, i)}>×</button>
                </span>
              ))}
            </div>
          </div>
        ))}

        <button onClick={handleAddAttribute}>Add Classification</button>
      </div>

      {/* Variant Table */}
      {variants.length > 0 ? (
        <table className="variant-table">
          <thead>
            <tr>
              {variantAttributes.map((attr) => (
                <th key={attr.name}>{attr.name}</th>
              ))}
              <th>Price</th>
              <th>Quantity</th>
              <th>SKU</th>
            </tr>
          </thead>
          <tbody>
            {variants.map((variant, index) => (
              <tr key={index}>
                {variant.attributes.map((attr, i) => (
                  <td key={i}>{attr.value}</td>
                ))}
                <td>
                  <input
                    type="text"
                    value={variant.price}
                    onChange={(e) => handleVariantChange(index, "price", e.target.value)}
                    placeholder="Price"
                  />
                </td>
                <td>
                  <input
                    type="text"
                    value={variant.quantity}
                    onChange={(e) => handleVariantChange(index, "quantity", e.target.value)}
                    placeholder="Quantity"
                  />
                </td>
                <td>
                  <input
                    type="text"
                    value={variant.sku}
                    onChange={(e) => handleVariantChange(index, "sku", e.target.value)}
                    placeholder="SKU"
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <div className="default-info">
          <h3>Default Product Info</h3>
          <div className="grid">
            <div>
              <label>Price</label>
              <input type="text" 
              value={productDetails.price}
              onChange={e => updateProductDetails("price", e.target.value)}
              placeholder="Enter Price" />
            </div>
            <div>
              <label>Quantity</label>
              <input type="text" 
              value={productDetails.quantity}
              onChange={e => updateProductDetails("quantity", e.target.value)}
              placeholder="Enter Quantity" />
            </div>
          </div>
        </div>
      )}

      <div>
        <h3>Mode</h3>
        <label>
          Visible
          <input
          type="checkbox"
          checked={productDetails.visible}
          onClick={() => updateProductDetails("visible", true)}
          />
        </label>
        <label>
          Hide
          <input
          type="checkbox"
          checked={!productDetails.visible}
          onClick={() => updateProductDetails("visible", false)}
          />
        </label>
      </div>

      <button onClick={handleAddProduct}>Add Product</button>
    </div>
  );
};

export default AddProduct;
