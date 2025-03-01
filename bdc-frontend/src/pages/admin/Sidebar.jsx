import { NavLink, useLocation } from "react-router-dom";
import { useState } from "react";
import { FaTachometerAlt, FaBox, FaClipboardList, FaChevronDown } from "react-icons/fa";
import { IoSettings } from "react-icons/io5";

const menuList = [
  {
    title: "Sản phẩm",
    icon: <FaClipboardList/>,
    subMenu:[
      {
        title: "Danh sách",
        to: "product"
      },
      {
        title: "Quản lý danh mục",
        to: "category"
      }
    ]
  }
]

export default function Sidebar() {

  const [openSubMenus, setOpenSubMenus] = useState({});
  const location = useLocation();

  const toggleSubMenu = (index) => {
    setOpenSubMenus((prev) => ({
      ...prev,
      [index]: !prev[index],
    }));
  };

  return (
    <div className="flex h-screen bottom-0 fixed">
      <div className={`mt-12 bg-white text-gray-800 transition-all duration-300 w-56 relative flex flex-col justify-between`}> 
        <nav 
          className="flex flex-col space-y-2 overflow-y-hidden hover:overflow-y-auto [&::-webkit-scrollbar]:w-1
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-gray-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-gray-600"
        >
            <ul className="p-2">
              {menuList.map((item, index) => {
                const isParentActive = item.subMenu && item.subMenu.some(sub => location.pathname.includes(sub.to));
                return (
                  <li key={index} className="mb-2">
                    {item.subMenu ? (
                      <>
                        <div
                          className={`flex items-center justify-between p-2 cursor-pointer rounded transition ${isParentActive ? "font-semibold text-blue-700" : "text-gray-600 hover:text-blue-500"}`}
                          onClick={() => toggleSubMenu(index)}
                        >
                          <div className="flex items-center gap-2">
                            {item.icon}
                            <span>{item.title}</span>
                          </div>
                          <FaChevronDown className={`transform transition-transform duration-300 ${openSubMenus[index] ? "rotate-180" : ""}`} />
                        </div>
                        <div
                          className={`transition-all duration-300 overflow-hidden ${
                            openSubMenus[index] ? "max-h-40 opacity-100" : "max-h-0 opacity-0"
                          }`}
                        >
                          <ul className="ml-6 mt-1 border-gray-600">
                            {item.subMenu.map((subItem, subIndex) => (
                              <li key={subIndex}>
                                <NavLink
                                  to={subItem.to}
                                  className={({ isActive }) => `block p-2 text-sm rounded ${isActive ? "font-semibold text-blue-700" : "text-gray-600 hover:text-blue-500"}`}
                                >
                                  {subItem.title}
                                </NavLink>
                              </li>
                            ))}
                          </ul>
                        </div>
                      </>
                    ) : (
                      <NavLink
                        to={item.to}
                        className={({ isActive }) => `flex items-center gap-2 p-2 rounded transition  ${isActive ? "font-semibold text-blue-700" : "text-gray-600 hover:text-blue-500"}`}
                      >
                        {item.icon}
                        <span>{item.title}</span>
                      </NavLink>
                    )}
                  </li>
                );
              })}
            </ul>
        </nav>
      </div>
    </div>
  );
}