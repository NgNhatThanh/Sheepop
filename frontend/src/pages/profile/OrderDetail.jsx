import { useEffect, useState } from "react"
import { useNavigate, useParams, Link } from "react-router-dom"
import { fetchWithAuth } from "../../util/AuthUtil"
import { BASE_API_URL } from "../../constants"
import OrderTrackSection from './OrderTrackSection'

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

const paymentMethodMapping = {
    'cash_on_delivery': 'Thanh toán khi nhận hàng',
    'bank_transfer': 'Chuyển khoản ngân hàng'
}

const whoCancel = {
    1: "bạn",
    2: "cửa hàng",
    3: "admin"
}

export default function OrderDetail(){
    
    const navigate = useNavigate()
    const [shopOrder, setShopOrder] = useState(null)
    const { shopOrderId } = useParams()

    const fetchOrder = () => {
        console.log("ID: ", shopOrderId)
        fetchWithAuth(`${BASE_API_URL}/v1/order/detail?shopOrderId=${shopOrderId}`, null, true)
            .then(res => res.json())
            .then(res => {
                console.log(res)
                setShopOrder(res)
            })
            .catch(() => navigate('/error'))
    }

    useEffect(() => {
        fetchOrder()
    }, [])

    return (
        <div className="w-300 bg-white shadow-md rounded-md">
        {shopOrder && (
            <>
            <div className="flex justify-between border-b items-center h-15 p-2 ml-3 mr-3">
                <button 
                    className="text-gray-500 text-xl cursor-pointer"
                    onClick={() => window.history.back()}
                >
                    {"< Quay lại"}
                </button>
                <div className="flex gap-2">
                    <p>ID: {shopOrder.id}</p>
                    |
                    <p className={`${statusTextColor[shopOrder.status - 1]}`}>
                        {statusText[shopOrder.status - 1].toUpperCase()}
                    </p>
                </div>
            </div>
            {shopOrder.status !== 7 && (
                <div className="p-10 border-b ml-3 mr-3">
                    <OrderTrackSection tracks={shopOrder.tracks}/>
                </div>
            )}

            <div className="flex justify-between border-b ml-3 mr-3">
                <div className="m-7 flex flex-col gap-1">
                    <h3 className="font-bold text-2xl mb-4"> Địa chỉ </h3>
                    <p className="font-semibold">{shopOrder.address.receiverName}</p>
                    <p className="text-gray-500">{shopOrder.address.phoneNumber}</p>
                    <p className="text-gray-500">{shopOrder.address.detail}</p>
                    <p className="text-gray-500"> {shopOrder.address.ward.name}, {shopOrder.address.district.name}, {shopOrder.address.province.name} </p>
                </div>

                <div className="flex flex-col gap-2 m-7">
                    {shopOrder.payment.status === "PENDING" && (
                        <button 
                            className="bg-blue-600 w-50 rounded p-2 text-white cursor-pointer hover:bg-blue-700"
                        >
                            Thanh toán
                        </button>
                    )}
                    {shopOrder.status === 6 && (
                        <button 
                            className="bg-blue-600 w-50 rounded p-2 text-white cursor-pointer hover:bg-blue-700"
                        >
                            Mua lại
                        </button>
                    )}
                    {shopOrder.status === 5 && (
                        <button 
                            className="bg-blue-600 w-50 rounded p-2 text-white cursor-pointer hover:bg-blue-700"
                        >
                            Đánh giá
                        </button>
                    )}
                    {shopOrder.status === 4 || shopOrder.status === 3 && (
                        <button 
                            className="bg-blue-600 w-50 rounded p-2 text-white cursor-pointer hover:bg-blue-700"
                        >
                            Đã nhận hàng
                        </button>
                    )}
                    {shopOrder.status === 6 && (
                        <button 
                            className="bg-gray-50 w-50 rounded border-1 p-2 cursor-pointer hover:bg-gray-200"
                        >
                            Xem đánh giá
                        </button>
                    )}
                    {shopOrder.status <= 2 && (
                        <button 
                            className="bg-gray-50 w-50 rounded border-1 p-2 cursor-pointer hover:bg-gray-200"
                        >
                            Hủy đơn
                        </button>
                    )}
                </div>
            </div>

            <div className="m-7">
                <div className="flex items-center mb-3">
                    <Link to="#" className="text-xl font-semibold">🛒 {shopOrder.shopName}</Link>
                </div>
                <div className="border-b">
                    {shopOrder.items.map((item) => (
                    <div key={item.id} className="flex items-center py-3">
                        <img
                        src={item.product.thumbnailUrl}
                        alt={item.product.name}
                        className="w-16 h-16 object-cover rounded-lg"
                        />
                        <div className="ml-4 flex-1">
                        <p className="font-medium text-gray-800 line-clamp-2">
                            {item.product.name}
                        </p>
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
                </div>
                

                <table className="w-full border-b">
                    <tbody>
                        <tr className="border-b border-gray-300">
                            <td className="text-gray-500 text-right border-r pr-5 border-gray-300 w-3/4">Tổng tiền hàng</td>
                            <td className={`text-right font-medium mr-3 p-2`}>
                                {shopOrder.items.reduce((total, item) => total + item.price * item.quantity, 0)
                                    .toLocaleString()} VND
                            </td>
                        </tr>

                        <tr className="border-b border-gray-300">
                            <td className="text-gray-500 text-right border-r pr-5 border-gray-300 w-3/4">Phí vận chuyển</td>
                            <td className={`text-right font-medium mr-3 p-2`}>
                                {shopOrder.shippingFee.toLocaleString()} VND
                            </td>
                        </tr>

                        <tr className="border-b border-gray-300">
                            <td className="text-gray-500 text-right border-r pr-5 border-gray-300 w-3/4">Tổng</td>
                            <td className={`text-right text-blue-500 text-xl font-medium mr-3 p-2`}>
                                {shopOrder.items.reduce((total, item) => total + item.price * item.quantity, shopOrder.shippingFee)
                                    .toLocaleString()} VND
                            </td>
                        </tr>

                        <tr>
                            <td className="text-gray-500 text-right border-r pr-5 border-gray-300">Phương thức thanh toán</td>
                            <td className={`text-right font-medium mr-3 p-2`}>
                                {paymentMethodMapping[shopOrder.payment.type]}
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            {shopOrder.cancelReason && (
                <div className="flex flex-col gap-1 ml-10">
                    <p className="text-xl text-gray-600 font-semibold">Hủy bởi {whoCancel[shopOrder.canceledBy]}</p>
                    <p className="text-l">Lý do: {shopOrder.cancelReason}</p>
                </div>
            )}
            </>
        )} 
        </div>
    )
}