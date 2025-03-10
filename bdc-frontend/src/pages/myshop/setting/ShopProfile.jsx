import { useState, useEffect } from "react"
import { ToastContainer, toast } from "react-toastify"
import { fetchWithAuth } from "../../../util/AuthUtil"
import { BASE_API_URL } from "../../../constants"
import { uploadImage } from "../../../util/UploadUtil"

export default function ShopProfile(){
    
    const [profile, setProfile] = useState(null)

    const fetchProfile = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/shop/profile/get`, window.location, true)
            .then(res => res.json())
            .then(res => {
                setProfile(res)
            })
    }   

    useEffect(() => {
        fetchProfile()
    }, [])

    const handleChangeProfile = (field, value) => {
        setProfile({ ...profile, [field]: value });
    };

    const handleChangeAvatar = async (file) => {
        const url = await uploadImage(file)
        handleChangeProfile("avatarUrl", url)
    }

    const handleUpdateProfile = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/shop/profile/update`, window.location, true, {
            method: "POST",
            headers: {
                'content-type': 'application/json'
            },
            body: JSON.stringify(profile)
        })
            .then(res => res.json())
            .then(res => {
                if(res.message){
                    toast.error(res.message)
                }
                else{
                    setProfile(res)
                    toast.success("Cập nhật thành công")
                }
            })
            .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau"))
    }
    
    return (
        <div className="bg-white rounded-sm p-6">
            <div className="border-b border-gray-300 flex justify-between">
                <div>
                    <h2 className="text-xl font-semibold">Hồ sơ cửa hàng</h2>
                    <p className="text-gray-500 mb-4">Quản lý hồ sơ</p>
                </div>

                <a href="#" className="m-2 border rounded-sm p-2 h-fit hover:bg-gray-100">
                    Xem cửa hàng
                </a>
            </div>

            <div className="flex pt-10 gap-4">
                
            {profile && (
                <>
                <div className="grid grid-cols-3 gap-4 w-3/4 p-4 ">
                    <div className="col-span-2">
                        <div className="mb-3">
                            <label className="text-gray-700 font-medium">Tên cửa hàng</label>
                            <input
                                type="text"
                                name="name"
                                value={profile.shopName}
                                onChange={e => handleChangeProfile("shopName", e.target.value)}
                                className="border p-2 w-full rounded mt-1"
                            />
                        </div>

                        <div className="mb-3">
                            <label className="text-gray-700 font-medium">Mô tả</label>
                            <textarea
                                type="text"
                                name="name"
                                value={profile.description}
                                onChange={e => handleChangeProfile("description", e.target.value)}
                                className="border p-2 w-full rounded"
                            />
                        </div>

                        <div className="mb-3">
                            <label className="text-gray-700 font-medium">Email</label>
                            <p className="text-gray-600">
                                {profile.email} <a href="#" className="text-blue-500">Thay đổi</a>
                            </p>
                        </div>

                        <div className="mb-3">
                            <label className="text-gray-700 font-medium">SĐT</label>
                            <p className="text-gray-600">
                                {profile.phoneNumber} <a href="#" className="text-blue-500">Thay đổi</a>
                            </p>
                        </div>

                        <div className="mb-3">
                            <label className="text-gray-700 font-medium">Tham gia vào</label>
                            <p className="text-gray-600">
                                {new Date(profile.createdAt).toLocaleDateString()}
                            </p>
                        </div>

                        <div className="flex justify-center mt-10">
                            <button 
                                className="w-70 cursor-pointer bg-blue-500 text-white px-6 py-2 rounded-md hover:bg-blue-600"
                                onClick={handleUpdateProfile}
                            >
                                Lưu
                            </button>
                        </div>
                        
                    </div>
                </div>

                <div className="text-center p-4">
                    <img src={profile.avatarUrl} alt="Avatar" className="w-24 h-24 rounded-full mx-auto" />
                    <label className="block mt-3 bg-gray-200 text-gray-700 px-4 py-2 rounded cursor-pointer hover:bg-gray-300">
                        Tải lên ảnh
                        <input type="file" className="hidden" onChange={e => handleChangeAvatar(e.target.files[0])} />
                    </label>
                    <p className="text-xs text-gray-500 mt-2">File size: max 1MB<br />File extension: .JPEG, .PNG</p>
                </div>
                </>   
            )} 
            </div>
            <ToastContainer 
                position="bottom-right"
            />
        </div>
    )
}