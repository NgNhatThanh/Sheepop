import { useState, useEffect } from "react"
import ProductMedia from './ProductMedia'
import {BASE_API_URL} from '../../constants/index'
import {fetchWithAuth} from '../../util/AuthUtil'
import { toast, ToastContainer } from 'react-toastify';
import { FaStar, FaRegStar, FaStarHalfAlt } from "react-icons/fa";

export default function ProductSection({product, isPreview}){

    const minPrice = product.price
    const [variationDisplay, setVariationDisplay] = useState([])
    const [selectedAttributes, setSelectedAttributes] = useState([])
    const [selectedQuantity, setSelectedQuantity] = useState(1)
    const [productInfo, setProductInfo] = useState({
        price: product.price,
        stock: product.quantity
    })
    const [errorMessage, setErrorMessage] = useState("");

    const handleChangeProductInfo = (type, value) => {
        setProductInfo(prev => ({ ...prev, [type]: value }));
    }

    useEffect(() => {
        setVariationDisplay(product.variationDisplayIndicators)
    }, [product])

    const breadcrumb = []
    let catTmp = product.category
    while(catTmp){
        breadcrumb.unshift(catTmp)
        catTmp = catTmp.parent
    }

    const handleClickAttribute = async (name, value) => {
        setSelectedQuantity(1)
        setSelectedAttributes((prev) => {
            const newAttributes = [...prev];
            const index = newAttributes.findIndex((attr) => attr.name === name);

            if (index !== -1 && newAttributes[index].value === value) {
            newAttributes.splice(index, 1); // Nếu đã chọn, thì bỏ chọn
            } else {
            if (index !== -1) {
                newAttributes[index].value = value; // Nếu đã có thuộc tính này, cập nhật giá trị
            } else {
                newAttributes.push({ name, value }); // Nếu chưa có, thêm vào danh sách
            }
            }

            fetchNewVariationDisplay(newAttributes);
            return newAttributes;
        });
    };

    const fetchNewVariationDisplay = async (attributes) => {
        try {
            const response = await fetch(`${BASE_API_URL}/v1/product/select_variation`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ 
                    productId: product.id, 
                    quantity: selectedQuantity,
                    attributes
                }),
            });
            const data = await response.json();
            if(data.price !== -1) handleChangeProductInfo("price", data.price)
            else handleChangeProductInfo("price", minPrice)
            handleChangeProductInfo("stock", data.quantity)
            setVariationDisplay(data.variationDisplayIndicators);
        } catch (error) {
            console.error("Error fetching variations:", error);
        }
    };

    const isAllAttributesSelected =
            selectedAttributes.length === product.variationDisplayIndicators.length;

    const handleQuantityChange = (newQuantity) => {
        if (!isAllAttributesSelected) return;
        if (newQuantity < 1) newQuantity = 1;
        if (newQuantity > productInfo.stock) newQuantity = productInfo.stock;
        setSelectedQuantity(newQuantity);
    };

    const handleAddToCart = () => {
        if (!isAllAttributesSelected) {
            setErrorMessage("Vui lòng chọn đầy đủ thuộc tính trước khi tiếp tục!");
            return;
        }
        else setErrorMessage("")

        const itemDTO = {
            productId: product.id,
            quantity: selectedQuantity,
            attributes: selectedAttributes
        }
        
        fetchWithAuth(`${BASE_API_URL}/v1/cart/add-to-cart`, window.location, true, {
            method: "POST",
            headers:{
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(itemDTO)
        })
            .then(async res => {
                if(!res.ok){
                    res.json()
                        .catch(err => {
                            console.log(err)
                            toast.error("Có lỗi xảy ra, thử lại sau!")
                        })
                        .then(data => {
                            toast.error(data.message)
                        })
                } 
                else {
                    toast.success("Thêm vào giỏ hàng thành công!")
                    localStorage.removeItem('cart')
                    window.dispatchEvent(new Event("cartChange"));
                }
            })
    };
    
    const handleBuyNow = () => {
        if (!isAllAttributesSelected) {
            setErrorMessage("Vui lòng chọn đầy đủ thuộc tính trước khi tiếp tục!");
            return;
        }
        else setErrorMessage("")
        console.log("Mua ngay:", {
            productId: product.id,
            attributes: selectedAttributes,
            quantity: selectedQuantity,
        });
    };

    return(
        <div className="w-300 mx-auto md:flex-row mt-3">
            <div className="flex category-info mb-4">
                {breadcrumb.map((cat) => (
                    <span key={cat.id}>
                        <a href='#' className="text-blue-600 font-semibold hover:underline">
                            {cat.name}
                        </a>
                        <span className="mr-2">{" > "}</span>
                    </span>
                ))}
                <span className="line-clamp-1 overflow-hidden">
                    {product.name}
                </span>
            </div>

            <div className="product-intro rounded-sm bg-white flex mb-4">
                
                <ProductMedia mediaList={product.mediaList}/>

                <div className="p-10 pt-5 w-full items-center">
                    <p className="text-2xl font-semibold">{product.name}</p>
                    <div className="flex mt-2">
                        <div className="flex gap-2 justify-center items-center border-r border-gray-300 pr-5">
                            <p className="border-b">{product.averageRating.toFixed(1)}</p>
                            {[1, 2, 3, 4, 5].map((index) => {
                                if (index <= product.averageRating) {
                                    return <FaStar key={index} className="text-blue-700" />;
                                } else if (index - 0.5 <= product.averageRating) {
                                    return <FaStarHalfAlt key={index} className="text-blue-700" />;
                                } else {
                                    return <FaRegStar key={index} className="text-gray-400" />;
                                }
                            })}
                        </div>
                        <div className=" flex gap-2 pl-5 border-r border-gray-300 pr-5">
                            <p className="border-b">{product.totalReviews}</p>
                            <p className="text-gray-500">đánh giá</p>
                        </div>
                        <div className=" flex gap-2 pl-5">
                            <p className="border-b">{product.soldCount}</p>
                            <p className="text-gray-500">đã bán</p>
                        </div>
                    </div>

                    <div className="mt-2 w-full bg-gray-100 rounded-s p-4 mb-4 text-3xl font-semibold text-blue-500">
                        {productInfo.price.toLocaleString()} VND
                    </div>

                    {variationDisplay.length > 0 && (
                        <div className="p-4 border rounded-sm shadow-md">
                            {variationDisplay.map((attr) => (
                            <div key={attr.name} className="mb-4">
                                <span className="block text-lg font-semibold mb-2">
                                    {attr.name}:
                                </span>
                                <div className="flex gap-2 flex-wrap">
                                    {attr.variationOptions.map((option) => {
                                        const isSelected = selectedAttributes.some(
                                        (attrItem) => attrItem.name === attr.name && attrItem.value === option.value
                                        );
                                        return (
                                            <button
                                                key={option.value}
                                                disabled={!option.available}
                                                className={`px-4 py-2 border rounded-sm text-sm font-medium transition ${
                                                option.available
                                                    ?  isSelected
                                                    ? "bg-blue-500 text-white cursor-pointer"
                                                    : "bg-gray-100 hover:bg-gray-200 cursor-pointer"
                                                    : "bg-gray-300 text-gray-500 cursor-not-allowed"
                                                }`}
                                                onClick={() => option.available && handleClickAttribute(attr.name, option.value)}
                                            >
                                                {option.value}
                                            </button>
                                        );
                                    })}
                                </div>
                            </div>
                            ))}
                        </div>
                    )}

                    <div className="mt-4 p-4 border rounded-sm shadow-md">
                        <span className="block text-lg font-semibold mb-2">Số lượng:</span>
                        <div className="flex items-center gap-4">
                            <button
                                className={`px-4 py-2 text-lg font-bold rounded-sm transition ${
                                isAllAttributesSelected
                                    ? "bg-gray-200 hover:bg-gray-300"
                                    : "bg-gray-300 cursor-not-allowed opacity-50"
                                }`}
                                onClick={() => handleQuantityChange(selectedQuantity - 1)}
                                disabled={!isAllAttributesSelected || selectedQuantity <= 1}
                            >
                                -
                            </button>
                            <input
                                type="number"
                                value={selectedQuantity}
                                onChange={(e) => handleQuantityChange(Number(e.target.value))}
                                className="w-16 text-center border rounded-sm px-2 py-1 text-lg"
                                disabled={!isAllAttributesSelected}
                            />
                            <button
                                className={`px-4 py-2 text-lg font-bold rounded-sm transition ${
                                isAllAttributesSelected
                                    ? "bg-gray-200 hover:bg-gray-300"
                                    : "bg-gray-300 cursor-not-allowed opacity-50"
                                }`}
                                onClick={() => handleQuantityChange(selectedQuantity + 1)}
                                disabled={!isAllAttributesSelected || selectedQuantity >= productInfo.stock}
                            >
                                +
                            </button>
                        </div>
                        <p className="text-sm text-gray-500 mt-1">
                        {productInfo.stock} sản phẩm có sẵn
                        </p>
                    </div>

                    {errorMessage && (
                        <p className="text-red-500 text-sm mt-2">{errorMessage}</p>
                    )}

                    {!isPreview && (
                        <div className="flex gap-4 mt-6">
                            <button
                            className='px-6 py-3 text-lg font-bold rounded-sm transition bg-blue-100 text-blue-700 cursor-pointer border-1 border-blue-500 hover:bg-blue-200'
                            onClick={handleAddToCart}
                            >
                            🛒 Thêm vào giỏ hàng
                            </button>

                            <button
                            className='px-6 py-3 text-lg font-bold rounded-sm transition bg-blue-500 cursor-pointer hover:bg-blue-600 text-white'
                            onClick={handleBuyNow}
                            >
                            ⚡ Mua ngay
                            </button>
                        </div>
                    )} 

                </div>
            </div>
            <ToastContainer 
                position="bottom-right"
            />
        </div>
    )

}