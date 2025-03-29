import { useState } from "react"
import { useNavigate, useSearchParams } from "react-router-dom"
import { fetchWithAuth } from "../../../util/AuthUtil"
import { BASE_API_URL } from "../../../constants"
import { toast } from "react-toastify"
import { Button } from "@mui/material";
import { FaCheckCircle } from "react-icons/fa";
import { RxCrossCircled } from "react-icons/rx";
import Loading from "../../common/Loading"

export default function HandleReturn(){

    const [loading, setLoading] = useState(false)
    const [success, setSuccess] = useState(false)
    const [params] = useSearchParams()
    const navigate = useNavigate()
    
    const checkPayment = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/payment/check?gateway=vnpay&${params.toString()}`, '/', true, {
            method: "POST"
        })
            .then(res => res.json())
            .then(res => {
                if(res.message) toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
                else{
                    setSuccess(res.status === 'success')
                }
            })
            .finally(() => setLoading(false))
    }

    useState(() => {
        checkPayment()
    }, [])

    return (
        <div>
            {loading ? (
                <Loading/>
            ) : (
                <>
                {success ? (
                    <div className="flex flex-col items-center justify-center min-h-screen p-6 bg-gray-100">
                        <div className="bg-white p-8 rounded-2xl shadow-lg text-center max-w-md">
                            <FaCheckCircle className="text-green-500 w-16 h-16 mx-auto" fontSize="large" />
                            <h2 className="text-2xl font-bold mt-4">Thanh toán thành công!</h2>
                            <p className="text-gray-600 mt-2">Cảm ơn bạn đã mua sắm với chúng tôi. Đơn hàng của bạn sẽ sớm được xử lý.</p>
                            <div className="mt-6 flex gap-4">
                            <Button onClick={() => navigate("/")} variant="contained" color="primary" fullWidth>
                                Tiếp tục mua hàng
                            </Button>
                            <Button onClick={() => navigate("/account/orders")} variant="contained" color="secondary" fullWidth>
                                Xem các đơn hàng
                            </Button>
                            </div>
                        </div>
                    </div>
                ) : (
                    <div className="flex flex-col items-center justify-center min-h-screen p-6 bg-gray-100">
                        <div className="bg-white p-8 rounded-2xl shadow-lg text-center max-w-md">
                            <RxCrossCircled className="text-red-500 w-16 h-16 mx-auto" fontSize="large" />
                            <h2 className="text-2xl font-bold mt-4">Thanh toán không thành công!</h2>
                            <p className="text-gray-600 mt-2">Có lỗi xảy ra trong quá trình xử lý thanh toán, vui lòng thực hiện lại</p>
                            <div className="mt-6 flex gap-4">
                            <Button onClick={() => navigate("/account/orders?type=1")} variant="contained" color="primary" fullWidth>
                                Xem các đơn cần thanh toán
                            </Button>
                            </div>
                        </div>
                    </div>
                )}
                </>
            )}
        </div>
    )
}