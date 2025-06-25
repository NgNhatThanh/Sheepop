import { useState } from "react";
import { Link } from 'react-router-dom'
import { logout } from "../../util/AuthUtil";
import { WebsocketProvider } from "../common/WebsocketProvider";
import NotificationDropdown from "../home/NotificationDropdown";

export default function Navbar() {

  const [showDropdown, setShopDropdown] = useState(false)

  return (
    <nav className="flex w-full h-12 justify-between items-center bg-gray-800 text-white p-2 fixed shadow-sm">
      <div className="text-lg font-bold ml-4">
        <a href="/" className="flex gap-2">
          <img src="/logo.svg"/>
          Sheepop
        </a>
      </div>
      <ul className="flex gap-4 text-2xl">
        <li className="cursor-pointer hover:text-gray-400">
          <WebsocketProvider>
              <NotificationDropdown/>
          </WebsocketProvider>
        </li>
        <li 
          className="cursor-pointer hover:text-gray-400"
          onMouseEnter={() => setShopDropdown(true)}
          onMouseLeave={() => setShopDropdown(false)}
        >
          <img src={JSON.parse(localStorage.getItem('userData'))['avatarUrl']} className="w-5 h-5 rounded-full"/>
          {showDropdown && (
            <div className="absolute mt-1 text-sm right-0 w-40 bg-white text-black shadow-lg rounded-md z-2">
                <Link to="./setting/profile" className="block px-4 py-2 hover:bg-gray-200">Thông tin cửa hàng</Link>
                <a className="block px-4 py-2 hover:bg-gray-200 cursor-pointer" onClick={() => logout()}>Đăng xuất</a>    
            </div>
          )}
        </li>
      </ul>
    </nav>
  );
}