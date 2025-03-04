import { useState } from "react" 
import { ToastContainer, toast } from "react-toastify"
import { fetchWithAuth } from "../../../util/AuthUtil"
import { BASE_API_URL } from "../../../constants"

const restrictReasons = [
    "Nội dung đăng bán không hợp lệ",
    "Hàng giả, hàng nhái",
    "Spam",
    "Hình ảnh không phù hợp",
    "Cần bổ sung thông tin",
]

export default function RestrictProductForm({closeForm, productId, onSuccess}){

    const [otherReason, setOtherReason] = useState('')
    const [selectedReasonIdx, setselectedReasonIdx] = useState(null)

    const handleRestrictProduct = () => {
        const reason = selectedReasonIdx === restrictReasons.length ? otherReason : restrictReasons[selectedReasonIdx]
        if(selectedReasonIdx === null || (selectedReasonIdx == restrictReasons.length && !otherReason)){
            toast.warn("Vui lòng chọn lý do")
            return
        }
        const body = {
            productId,
            reason
        }
        fetchWithAuth(`${BASE_API_URL}/v1/admin/product/restrict`, "/", true, {
            method: "POST",
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify(body)
        })
            .then(() => {
                onSuccess()
            })
            .catch(err => {
                console.log("Err: ", err)
                toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
            })
    }

    return (
        <div className="fixed inset-0 z-10 flex justify-center items-center bg-gray-100/80">
            <div className="bg-white rounded-sm p-6">
                <h2 className="text-2xl font-bold mb-10"> Đình chỉ sản phẩm </h2>

                <div className="flex flex-col gap-5 mb-10">
                    {restrictReasons.map((reason, index) => (
                        <label 
                            key={index}
                            className="flex gap-2 items-center cursor-pointer"
                        >
                            <input
                                type="radio"
                                checked={selectedReasonIdx === index}
                                className="w-5 h-5 accent-blue-400"
                                onChange={() => setselectedReasonIdx(index)}
                            />
                            <span className="">{reason}</span>
                        </label>
                    ))}

                    <label className="flex gap-2 items-center cursor-pointer">
                        <input
                            type="radio"
                            checked={selectedReasonIdx === restrictReasons.length}
                            className="w-5 h-5 accent-blue-400"
                            onChange={() => setselectedReasonIdx(restrictReasons.length)}
                        />
                        <span>Lý do khác</span>
                    </label>
                    {selectedReasonIdx === restrictReasons.length && (
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
                        Hủy
                    </button>

                    <button 
                        className="cursor-pointer w-30 p-2 text-l font-semibold rounded-sm bg-white border border-red-500 hover:bg-gray-100"
                        onClick={handleRestrictProduct}
                    >
                        Đình chỉ
                    </button>
                </div>
            </div>
            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}