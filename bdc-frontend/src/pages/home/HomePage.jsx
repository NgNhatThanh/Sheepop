import { useEffect, useState } from "react";
import { BASE_API_URL } from "../../constants";
import { useSearchParams, Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { FaStar } from "react-icons/fa";
import BannerSlider from "./BannerSlider";
import ProductCard from "./ProductCard";

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

            <BannerSlider/>

            <div className="flex justify-center">
                <h2 className="text-center w-full text-2xl font-bold mb-4 mt-5 p-4 bg-white border-1 border-blue-500 text-blue-600">Gợi ý hôm nay</h2>
            </div>

            {productList.length === 0 ? (
                <p>Không có sản phẩm nào.</p>
            ) : (
                <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
                    {productList.map((product) => (
                        <ProductCard product={product}/>
                    ))}
                </div>
            )}
            <ToastContainer 
                position="bottom-right"
            />
        </div>
    );
}
