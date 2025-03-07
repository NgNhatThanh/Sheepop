import { useEffect, useState } from "react"
import { BASE_API_URL } from "../../../constants"
import { fetchWithAuth } from "../../../util/AuthUtil"

export default function UserInfoModal({ userId, closeModal }){

    const [user, setUser] = useState(null)
 
    useEffect(() => {
        const fetchUser = () => {
            fetchWithAuth(`${BASE_API_URL}/v1/admin/user/get/${userId}`, "/", true)
                .then(res => res.json())
                .then(res => setUser(res))
        }

        fetchUser()

    }, [])

    return(
        <div className="fixed inset-0 flex items-center justify-center z-10 bg-gray-50/80">
            <div className="bg-white p-4 rounded-sm border-1">
                <h2 className="font-bold text-2xl text-center"> Thông tin chủ cửa hàng </h2>
                {user && (
                    <div className="mt-10">
                        
                        <div className="flex gap-10">
                            <div className="flex flex-col gap-3">
                                <p className="text-xl"><span className="font-semibold">Họ tên: </span> {user.fullName} </p>
                                <p className="text-xl"><span className="font-semibold">Username: </span> {user.username} </p>
                                <p className="text-xl"><span className="font-semibold">SĐT: </span> {user.phoneNumber} </p>
                                <p className="text-xl"><span className="font-semibold">Email: </span> {user.email} </p>
                                <p className="text-xl"><span className="font-semibold">Ngày sinh: </span> {new Date(user.dob).toLocaleDateString()} </p>
                                <p className="text-xl"><span className="font-semibold">Tham gia vào: </span> {new Date(user.createdAt).toLocaleDateString()} </p>
                            </div>
                            <img src={user.avatarUrl} className="w-50 h-50 rounded-sm"/>
                        </div>

                        <div className="flex justify-center m-5">
                            <button
                                className="cursor-pointer bg-blue-400 p-2 w-20 rounded-sm text-white text-xl hover:bg-blue-500"
                                onClick={closeModal}
                            >
                                Oke
                            </button>
                        </div>
                        
                    </div>
                )} 
            </div>
        </div>
    )
}