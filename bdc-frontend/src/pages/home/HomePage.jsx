import { useEffect, useState } from "react";
import { BASE_API_URL } from "../../constants";
import { useSearchParams, Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { FaStar } from "react-icons/fa";

export default function HomePage() {
    const [searchParams] = useSearchParams();
    const pageLimit = 60;
    const [productList, setProductList] = useState([]);

    useEffect(() => {
        const fetchProducts = async (page, limit) => {
            try {
                const res = await fetch(`${BASE_API_URL}/v1/homepage/get-items?page=${page}&limit=${limit}`);
                if (res.ok) {
                    const data = await res.json();
                    setProductList(data.content);
                }
            } catch (error) {
                toast.error('Có lỗi xảy ra, vui lòng thử lại sau!')
            }
        };

        const page = searchParams.get("page") || 0;
        fetchProducts(page, pageLimit);
    }, [searchParams]);

    return (
        <div className="container mx-auto px-4 py-6">
            <h2 className="text-2xl font-bold mb-4">Danh sách sản phẩm</h2>

            {productList.length === 0 ? (
                <p>Không có sản phẩm nào.</p>
            ) : (
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
                    {productList.map((product) => (
                        <Link to={`/product/${encodeURIComponent(product.name.replace(/\s+/g, "-"))}.${product.id}`}  
                            key={product.id} 
                            className="bg-white border p-2 rounded-lg shadow-sm transition-all duration-300 hover:shadow-lg hover:-translate-y-1 hover:border-blue-400"
                        >
                            <img 
                                src={product.thumbnailUrl} 
                                alt={product.name} 
                                className="w-full h-40 object-contain rounded-md" 
                            />
                            <p className="text-sm h-10 font-medium line-clamp-2 overflow-hidden mt-2">{product.name}</p>
                            <p className="text-red-500 font-bold">{product.price.toLocaleString()}₫</p>
                            <div className="flex">
                                <div className="flex gap-1 border-r border-gray-300 p-1 items-center">
                                    <FaStar className="text-sm text-blue-500" />
                                    <p className="text-sm ">{product.averageRating.toFixed(1)}</p>
                                </div>
                                <p className="ml-1 flex items-center text-sm items-center">{product.sold} đã bán</p>
                            </div>
                        </Link>
                    ))}
                </div>
            )}
            <ToastContainer 
                position="bottom-right"
            />
        </div>
    );
}
