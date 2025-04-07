import { useState } from "react";
import { FaUserAlt } from "react-icons/fa";
import { logout } from "../../util/AuthUtil";

export default function Navbar() {

  const [showDropdown, setShopDropdown] = useState(false)

  return (
    <nav className="flex w-full h-12 justify-between items-center bg-white p-2 fixed z-100 shadow-sm">
      <div className="text-lg font-bold ml-4">
        <a href="/admin" className="flex gap-2">
          <img src="/logo.svg"/>
          Sheepop
        </a>
      </div>
      <ul className="flex gap-4 text-xl">
        <li 
          className="cursor-pointer hover:text-gray-400"
          onMouseEnter={() => setShopDropdown(true)}
          onMouseLeave={() => setShopDropdown(false)}  
        >
          <FaUserAlt />
          {showDropdown && (
            <div className="absolute -mt-0.25 text-sm right-0 w-40 bg-white text-black shadow-lg rounded-md z-2">
                <a className="block px-4 py-2 hover:bg-gray-200 cursor-pointer" onClick={() => logout()}>Đăng xuất</a>    
            </div>
          )}
        </li>
      </ul>
    </nav>
  );
}