import { useState } from "react"
import { Link } from "react-router-dom"
import Loading from '../common/Loading'
import { BASE_API_URL } from "../../constants"

export default function ForgotPasswordPage(){

    const [email, setEmail] = useState('')
    const [error, setError] = useState('')
    const [processing, setProcessing] = useState(false)
    const [success, setSuccess] = useState(false)

    const handleSubmit = (e) => {
        e.preventDefault();
        setError('')
        setProcessing(true)
        document.activeElement.blur()

        fetch(`${BASE_API_URL}/v1/auth/forgot-password`, {
            method: "POST",
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify({
                email: email
            })
        })
            .then(res => res.json())
            .then(res => {
                if(res.message) setError(res.message)
                else setSuccess(true)
            })
            .finally(() => setProcessing(false))
    }

    return (
        <div className="flex items-center justify-center min-h-screen bg-[linear-gradient(120deg,#a1c4fd_0%,#c2e9fb_100%)]">
            <div className="w-full max-w-md p-8 bg-white rounded-2xl shadow-lg">
                <a href="/" className="flex justify-center mb-2">
                <img src="/logo.svg"/>
                </a>

                <h2 className="text-2xl font-bold text-center mb-6">Quên mật khẩu</h2>

                <p className="text-red-500">{error}</p>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <input
                            type="text" 
                            placeholder="Email"
                            className="w-full px-4 py-2 mt-1 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                            onChange={e => setEmail(e.target.value.trim())}
                            required
                        />
                    </div>

                    <button
                        type="submit"
                        className="w-full bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-lg transition duration-200 cursor-pointer"
                    >
                        Xác nhận
                    </button>
                </form>

                <p className="text-center text-gray-600 mt-4">
                    <Link to={`/login`} className="text-blue-500 hover:underline">
                        Trở về đăng nhập
                    </Link>
                </p>
                
            </div>

            {processing && <Loading/>}

            {success && (
                <div className="fixed inset-0 w-full z-10 flex flex-col items-center justify-center bg-gray-50/90">
                    <h2
                        className='text-2xl font-bold mb-3'
                    >
                        Xác nhận thành công
                    </h2>
                    <h3
                        className='text-xl mb-3'
                    >
                        Thư đã được gửi tới địa chỉ email của bạn, hãy kiểm tra để đặt lại mật khẩu
                    </h3>
                    <Link 
                        to='/login'
                        className="p-2 bg-blue-500 hover:bg-blue-600 text-white font-semibold py-2 rounded-lg transition duration-200 cursor-pointer"
                    >
                        Đồng ý
                    </Link>
                </div>
            )}
        </div>
    )
}