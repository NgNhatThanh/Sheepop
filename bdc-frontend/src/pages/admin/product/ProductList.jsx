import { useLocation, Link } from "react-router-dom";

const tabs = [
    "Tất cả",
    "Bị đình chỉ",
    "Đã sửa đổi"
];

export default function ProductList(){

    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const currentType = parseInt(searchParams.get("type")) || 0;

    return (
        <div className="h-100">
            <div className="w-full bg-white shadow-md sticky top-12 z-1 flex justify-center rounded-sm">
                {tabs.map((tab, index) => (
                <button
                    key={index}
                    className={`cursor-pointer px-4 py-2 ${
                    currentType === index
                        ? "text-blue-600 border-b-2 border-blue-600"
                        : "text-gray-900"
                    } hover:text-blue-500`}
                    onClick={() => navigate(`/myshop/order-list?type=${index}`)}
                >
                    {tab}
                </button>
                ))}
            </div>

            <div className="flex items-center justify-center space-x-3 bg-white p-4 rounded mt-4 shadow-md">
                <input
                    type="text"
                    placeholder="Tìm tên sản phẩm"
                    className={`border border-gray-300 px-3 py-2 rounded w-60`}
                />

                <input
                    type="text"
                    placeholder="Tìm theo ngành hàng"
                    className={`border border-gray-300 px-3 py-2 rounded w-60`}
                />

                <button 
                    className="bg-blue-500 cursor-pointer text-white px-4 py-2 rounded hover:bg-blue-600 transition"
                    onClick={() => {
                        
                    }}    
                >
                    Áp dụng
                </button>

                <button
                    className="border cursor-pointer border-gray-400 px-4 py-2 rounded hover:bg-gray-100 transition"
                    onClick={() => {
                        
                    }}
                >
                    Đặt lại
                </button>
            </div>
        </div>
    )
}