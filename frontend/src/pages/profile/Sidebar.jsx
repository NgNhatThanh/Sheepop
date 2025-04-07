import { NavLink } from "react-router-dom";
import { FaUser, FaAddressCard, FaClipboardList } from "react-icons/fa";

export default function ProfileSidebar() {

    const userData = JSON.parse(localStorage.getItem('userData'))

    return (
        <aside className="w-64 h-screen bg-white shadow-md flex flex-col p-4 rounded-md">
            <div className="flex flex-col items-center border-b pb-4 mb-4">
                <img 
                    src={userData.avatarUrl}
                    alt="Avatar" 
                    className="w-20 h-20 rounded-full mb-2"
                />
                <h2 className="text-lg font-semibold">{userData.username}</h2>
            </div>

            <nav className="flex flex-col space-y-2">
                <NavLink 
                    to="profile" 
                    className={({ isActive }) => `flex items-center p-2 rounded-md transition ${isActive ? 'bg-gray-200' : 'hover:bg-gray-100'}`}
                >
                    <FaUser className="w-5 h-5 mr-2" /> Hồ sơ
                </NavLink>

                <NavLink 
                    to="address" 
                    className={({ isActive }) => `flex items-center p-2 rounded-md transition ${isActive ? 'bg-gray-200' : 'hover:bg-gray-100'}`}
                >
                    <FaAddressCard className="w-5 h-5 mr-2" /> Địa chỉ
                </NavLink>

                <NavLink 
                    to="orders" 
                    className={({ isActive }) => `flex items-center p-2 rounded-md transition ${isActive ? 'bg-gray-200' : 'hover:bg-gray-100'}`}
                >
                    <FaClipboardList className="w-5 h-5 mr-2" /> Đơn hàng
                </NavLink>

            </nav>
        </aside>
    );
}
