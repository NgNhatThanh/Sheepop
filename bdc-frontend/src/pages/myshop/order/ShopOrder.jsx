import { useNavigate, useLocation, Link } from "react-router-dom";
import { useState, useEffect } from "react";
import { fetchWithAuth} from '../../../util/AuthUtil'
import { formatDate } from "../../../util/DateUtil";
import { BASE_API_URL } from "../../../constants";
import { BsClipboard2PlusFill } from "react-icons/bs";
import { FaChevronDown } from "react-icons/fa";
import { ToastContainer, toast } from "react-toastify";
import CancelOrderForm from '../../profile/CancelOrderForm.jsx'
import Pagination from "../../common/Pagination.jsx";

const tabs = [
    "Tất cả",
    "Chờ xác nhận",
    "Chờ vận chuyển",
    "Đang giao",
    "Đã giao",
    "Đã hủy",
];

const statusText = [
    "Chờ xác nhận",
    "Chuẩn bị hàng",
    "Đã gửi hàng",
    "Đang giao hàng",
    "Thành công",
    "Đã đánh giá",
    "Đã hủy",
];

const statusTextColor = [
    "text-gray-500",
    "text-gray-500",
    "text-orange-500",
    "text-orange-500",
    "text-green-500",
    "text-green-500",
    "text-red-500"
]

const cancelReasons = [
    "Spam",
    "Hết hàng"
]

const filters = [
    "Tất cả",
    "Mã đơn hàng",
    "Tên người mua",
    "Tên sản phẩm"
];

export default function ShopOrder(){

    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const [isLoading, setIsLoading] = useState(false)
    const [limit, setLimit] = useState(10)
    const [page, setPage] = useState(1)
    const [totalPage, setTotalPage] = useState(1)
    const [totalOrders, setTotalOrders] = useState(0)
    const [cancelOrder, setCancelOrder] = useState(null)
    const [isEmpty, setIsEmpty] = useState(false)
    const navigate = useNavigate()
    const currentType = parseInt(searchParams.get("type")) || 0;
    const [orders, setOrders] = useState([])

    const [filterType, setFilterType] = useState(0);
    const [searchQuery, setSearchQuery] = useState("");
    const [openFilter, setOpenFilter] = useState(false);

    const fetchShopOrders = (type, reset = false) => {
        setIsEmpty(false)
        setOrders([])
        setIsLoading(true)
        fetchWithAuth(`${BASE_API_URL}/v1/shop/order/get_list?type=${type}&page=${page - 1}&limit=${limit}&filterType=${filterType}&keyword=${searchQuery}`, 
            window.location, 
            true)
            .then(res => res.json())
            .then(res => {
                if(reset && res.content.length === 0) setIsEmpty(true)
                setTotalOrders(res.totalElements)
                setTotalPage(res.totalPages)
                setOrders(res.content)
                setIsLoading(false)
            })
            .catch(() => setIsLoading(false))
    }

    const updateOrderStatus = (shopOrderId, status) => {
        fetchWithAuth(`${BASE_API_URL}/v1/shop/order/update?shopOrderId=${shopOrderId}&currentStatus=${status}`, window.location, true, {
            method: "POST"
        })
            .then(res => {
                if(res.ok){
                    fetchShopOrders(currentType, true)
                }
            })
            .catch(() => {
                toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
            })
    }

    const resetFilter = () => {
        setPage(1)
        fetchShopOrders(currentType, true)
    }

    useEffect(() => {
        resetFilter()
    }, [currentType])

    useEffect(() => {
        fetchShopOrders(currentType, true)
    }, [page, limit])

    return (
        <div>
            <div>
                <h1 className="font-bold text-3xl mb-5">Danh sách đơn hàng</h1>
            </div>
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
                <div className="relative">
                    <button
                        onClick={() => setOpenFilter(!openFilter)}
                        className="flex cursor-pointer justify-between border border-gray-300 transtion-all duration-200 px-3 py-2 rounded flex items-center gap-2 w-44 hover:border-blue-500"
                    >
                    {filters[filterType]}
                    <FaChevronDown className={`transition-transform ${openFilter ? "rotate-180" : "rotate-0"}`} />
                    </button>
                    {openFilter && (
                    <ul className="absolute left-0 top-full mt-1 w-52 bg-white border border-gray-300 rounded shadow-lg overflow-hidden z-10">
                        {filters.map((item, index) => (
                        <li
                            key={item}
                            className={`px-3 py-2 cursor-pointer ${
                                item === filters[filterType] ? "text-blue-500 font-semibold" : "text-gray-700"
                            } hover:bg-gray-100`}
                            onClick={() => {
                                setSearchQuery("")
                                setFilterType(index);
                                setOpenFilter(false);
                            }}
                        >
                            {item}
                        </li>
                        ))}
                    </ul>
                    )}
                </div>

                <input
                    type="text"
                    disabled={filterType === 0}
                    placeholder={filterType === 0 ? 'Tìm kiếm...' : `Nhập ${filters[filterType].toLowerCase()}...`}
                    className={`border border-gray-300 px-3 py-2 rounded w-60 ${filterType === 0 && 'bg-gray-100 cursor-not-allowed'}`}
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />

                <button 
                    className="bg-blue-500 cursor-pointer text-white px-4 py-2 rounded hover:bg-blue-600 transition"
                    onClick={() => {
                        setIsEmpty(false)
                        setOrders([])
                        fetchShopOrders(currentType, true)
                    }}    
                >
                    Áp dụng
                </button>

                <button
                    className="border cursor-pointer border-gray-400 px-4 py-2 rounded hover:bg-gray-100 transition"
                    onClick={() => {
                        setSearchQuery("");
                        setFilterType(0);
                        resetFilter()
                    }}
                >
                    Đặt lại
                </button>
            </div>

            <p className="text-xl mt-4 font-semibold">{totalOrders} đơn hàng</p>

            <div className="bg-white mt-2">
                <table className="table-fixed w-full border-collapse border border-gray-300">
                    <thead>
                        <tr className=" w-full">
                            <th className="border border-gray-300 p-2 w-1/14">Ngày tạo đơn</th>
                            <th className="border border-gray-300 p-2 w-2/3">Sản phẩm</th>
                            <th className="border border-gray-300 p-2 w-2/15">Tổng đơn hàng</th>
                            <th className="border border-gray-300 p-2 w-1/15">Trạng thái</th>
                            <th className="border border-gray-300 p-2 w-1/15">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orders.map((shopOrder) => (
                            <tr key={shopOrder.id} className="border-b border-gray-300">
                                <td className="text-center border-r border-gray-300">
                                    {formatDate(shopOrder.createdAt)}
                                </td>
                                <td className="items-center border-r border-gray-300">
                                    {shopOrder.items.map((item) => (
                                        <div key={item.id} className="flex p-5">
                                            <img
                                                src={item.product.thumbnailUrl}
                                                alt={item.product.name}
                                                className="w-16 h-16 object-cover rounded-lg"
                                            />
                                            <div className="ml-4 flex-1">
                                                <Link to="#">
                                                    <p className="font-medium text-gray-800 line-clamp-2 hover:text-blue-500">
                                                        {item.product.name}
                                                    </p>
                                                </Link>
                                                
                                                <div className="flex gap-5">
                                                    <div>
                                                    {item.attributes?.map((attr) => (
                                                        <p key={attr.name} className="text-gray-500 text-sm">
                                                            {attr.name}: {attr.value} &nbsp;
                                                        </p>
                                                    ))}
                                                    </div>
                                                    <p className="text-gray-500 text-sm">
                                                    x {item.quantity}
                                                    </p>
                                                </div>
                                            </div>
                                            <p className="text-blue-600 font-semibold">
                                                {item.price.toLocaleString()} VND
                                            </p>
                                        </div>
                                    ))}
                                    <p className="text-sm text-gray-600 ml-5 mb-2">Mã đơn: {shopOrder.id.toUpperCase()}</p>
                                </td>
                                <td className="text-center border-r border-gray-300">
                                    <p className="text-blue-600 font-semibold">
                                        {(shopOrder.items.
                                            reduce((total, item) => total + item.price * item.quantity, 0) + shopOrder.shippingFee)
                                                .toLocaleString()} VND
                                    </p> 
                                </td>
                                <td className="text-center border-r border-gray-300">
                                    <p className={`text-l ${statusTextColor[shopOrder.status - 1]}`}>
                                        {statusText[shopOrder.status - 1]}
                                    </p>
                                    {!shopOrder.completedPayment && (
                                        <p className="text-red-500">
                                            Chưa thanh toán
                                        </p>
                                    )}
                                </td>
                                <td className="border-r border-gray-300">
                                    <div className="flex flex-col justify-center items-center h-full gap-2">
                                        {shopOrder.status === 1 && (
                                            <button 
                                                className={`p-2 rounded text-white ${shopOrder.completedPayment ? 'bg-blue-400 cursor-pointer hover:bg-blue-500' : 'bg-gray-300 cursor-not-allowed'}`}
                                                onClick={() => updateOrderStatus(shopOrder.id, shopOrder.status)}
                                            >
                                                Xác nhận
                                            </button>
                                        )}

                                        {shopOrder.status === 1 && (
                                            <button 
                                                className="bg-red-400 p-2 rounded cursor-pointer hover:bg-red-500"
                                                onClick={() => setCancelOrder(shopOrder)}
                                            >
                                                Hủy đơn
                                            </button>
                                        )}

                                        {shopOrder.status === 2 && (
                                            <button 
                                                className="bg-blue-400 text-white p-2 rounded cursor-pointer hover:bg-blue-500"
                                                onClick={() => updateOrderStatus(shopOrder.id, shopOrder.status)}
                                            >
                                                Bàn giao
                                            </button>
                                        )}

                                        {cancelOrder === shopOrder && (
                                            <CancelOrderForm
                                                reasons={cancelReasons}
                                                whoCancel={2}
                                                closeForm={() => setCancelOrder(null)}
                                                order={shopOrder}
                                            />
                                        )}
                                    </div>
                                </td>
                            </tr>
                        ))}

                        
                    </tbody>
                </table>
                {!isEmpty && (
                    <Pagination
                        page={page}
                        setPage={setPage}
                        limit={limit}
                        setLimit={setLimit}
                        maxPage={totalPage}
                    />
                )} 
            </div>

            {isEmpty && (
                <div className="w-full bg-white shadow-md rounded-sm h-100 mt-3 flex items-center justify-center">
                    <div className="text-center">
                    <BsClipboard2PlusFill className="text-blue-300 text-8xl mx-auto mb-2"/>
                    <p className="text-xl">Không có đơn hàng nào</p>
                    </div>
                </div>
            )}

            {isLoading && (
                <div role="status" className="flex justify-center mt-2">
                    <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/>
                        <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/>
                    </svg>
                    <span className="sr-only">Loading...</span>
                </div>
            )}
            <ToastContainer 
                position="bottom-right"
            />
        </div>
    )
}