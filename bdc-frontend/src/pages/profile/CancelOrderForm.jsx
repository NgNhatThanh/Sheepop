import { useState } from "react"
import { fetchWithAuth } from "../../util/AuthUtil"
import { BASE_API_URL } from "../../constants"
import { ToastContainer, toast } from "react-toastify"

export default function CancelOrderForm({ reasons, whoCancel, closeForm, order }){

    const [otherReason, setOtherReason] = useState('')
    const [selectedReason, setSelectedReason] = useState(null)

    console.log(order)

    const handleCancelOrder = () => {
        if(selectedReason === null || (selectedReason == reasons.length && !otherReason)){
            toast.warning("Vui lòng chọn lý do hủy đơn hàng")
            return
        }
        const body = {
            whoCancel,
            orderId: order.id,
            shopOrderIds: order.shopOrders ? order.shopOrders.map(shopOrder => shopOrder.id) : [order.id],
            cancelReason: selectedReason < reasons.length ? reasons[selectedReason] : otherReason
        }
        fetchWithAuth(`${BASE_API_URL}/v1/order/cancel`, window.location, true, {
            method: "POST",
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify(body)
        })
            .then(() => {
                window.location.reload()
            })
            .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau!"))
    }

    return (
        <div className="fixed inset-0 z-10 flex items-center justify-center bg-gray-100/60">
            <div className="bg-white w-120 rounded-sm p-6 border border-gray-600">
                <h2 className="text-2xl font-bold mb-10"> Hủy đơn hàng </h2>

                <div className="flex flex-col gap-5 mb-10">
                    {reasons.map((reason, index) => (
                        <label 
                            key={index}
                            className="flex gap-2 items-center cursor-pointer"
                        >
                            <input
                                type="radio"
                                checked={selectedReason === index}
                                className="w-5 h-5 accent-blue-400"
                                onChange={() => setSelectedReason(index)}
                            />
                            <span className="">{reason}</span>
                        </label>
                    ))}

                    <label className="flex gap-2 items-center cursor-pointer">
                        <input
                            type="radio"
                            checked={selectedReason === reasons.length}
                            className="w-5 h-5 accent-blue-400"
                            onChange={() => setSelectedReason(reasons.length)}
                        />
                        <span>Lý do khác</span>
                    </label>
                    {selectedReason === reasons.length && (
                        <input
                            type="text"
                            placeholder="Lý do khác..."
                            className="w-full px-2 py-1 border border-gray-500 rounded"
                            onChange={e => setOtherReason(e.target.value)}
                        />
                    )}
                </div>
                <div className="flex justify-evenly">
                    <button 
                        className="cursor-pointer w-30 p-2 text-l text-white font-semibold rounded-sm bg-blue-400 hover:bg-blue-500"
                        onClick={closeForm}
                    >
                        Thoát
                    </button>

                    <button 
                        className="cursor-pointer w-30 p-2 text-l font-semibold rounded-sm bg-white border border-red-500 hover:bg-gray-100"
                        onClick={handleCancelOrder}    
                    >
                        Hủy đơn
                    </button>
                </div>
            </div>
            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}