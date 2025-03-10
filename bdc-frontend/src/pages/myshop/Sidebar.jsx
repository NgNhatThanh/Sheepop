import { NavLink, useLocation } from "react-router-dom";
import { useState, useEffect } from "react";
import { FiChevronLeft, FiChevronRight } from "react-icons/fi";
import { FaTachometerAlt, FaBox, FaClipboardList, FaChevronDown } from "react-icons/fa";
import { IoSettings } from "react-icons/io5";

const menuList = [
  {
    title: "Trang chủ",
    icon: <FaTachometerAlt/>,
    to: 'dashboard' 
  },
  {
    title: "Quản lý đơn hàng",
    icon: <FaClipboardList/>,
    subMenu:[
      {
        title: "Tất cả",
        to: "order-list"
      }
    ]
  },
  {
    title: "Quản lý sản phẩm",
    icon: <FaBox/>,
    subMenu: [
      {
        title: "Danh sách sản phẩm",
        to: "product-list"
      },
      {
        title: "Thêm sản phẩm",
        to: "add-product"
      }
    ]
  },
  {
    title: "Quản lý Shop",
    icon: <IoSettings />,
    subMenu: [
      {
        title: "Hồ sơ",
        to: "setting/profile"
      },
      {
        title: "Cài đặt vận chuyển",
        to: "setting/shipping"
      }
    ]
  }
]

export default function Sidebar({isOpen, toggle}) {

  const [openSubMenus, setOpenSubMenus] = useState({});
  const location = useLocation();

  useEffect(() => {
    if (!isOpen) {
      setOpenSubMenus({});
    }
  }, [isOpen]);

  const toggleSubMenu = (index) => {
    setOpenSubMenus((prev) => ({
      ...prev,
      [index]: !prev[index],
    }));
  };

  return (
    <div className="flex h-screen bottom-0 fixed">
      <div className={`mt-12 bg-gray-800 text-white transition-all duration-300 ${isOpen ? 'w-56' : 'w-16'} relative flex flex-col justify-between`}> 
        <nav 
          className="flex flex-col space-y-2 overflow-y-hidden hover:overflow-y-auto [&::-webkit-scrollbar]:w-1
          [&::-webkit-scrollbar-track]:rounded-full
          [&::-webkit-scrollbar-track]:bg-gray-100
          [&::-webkit-scrollbar-thumb]:rounded-full
          [&::-webkit-scrollbar-thumb]:bg-gray-500"
        >
            <ul className="p-2">
              {menuList.map((item, index) => {
                const isParentActive = item.subMenu && item.subMenu.some(sub => location.pathname.includes(sub.to));
                return (
                  <li key={index} className="mb-2">
                    {item.subMenu ? (
                      <>
                        <div
                          className={`flex items-center ${isOpen ? "justify-between" : "justify-center"} p-2 cursor-pointer rounded transition ${isParentActive ? "font-semibold text-blue-400" : "text-white hover:text-blue-300"}`}
                          onClick={() => toggleSubMenu(index)}
                        >
                          <div className="flex items-center gap-2">
                            {item.icon}
                            {isOpen && <span>{item.title}</span>}
                          </div>
                          {isOpen && <FaChevronDown className={`transform transition-transform duration-300 ${openSubMenus[index] ? "rotate-180" : ""}`} />}
                        </div>
                        <div
                          className={`transition-all duration-300 overflow-hidden ${
                            isOpen && openSubMenus[index] ? "max-h-40 opacity-100" : "max-h-0 opacity-0"
                          }`}
                        >
                          <ul className="ml-6 mt-1 border-gray-600">
                            {item.subMenu.map((subItem, subIndex) => (
                              <li key={subIndex}>
                                <NavLink
                                  to={subItem.to}
                                  className={({ isActive }) => `block p-2 text-sm rounded ${isActive ? "font-semibold text-blue-400" : "text-white hover:text-blue-300"}`}
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
                        className={({ isActive }) => `flex items-center ${isOpen ? "gap-2" : "justify-center"} p-2 rounded transition  ${isActive ? "font-semibold text-blue-400" : "text-white hover:text-blue-300"}`}
                      >
                        {item.icon}
                        {isOpen && <span>{item.title}</span>}
                      </NavLink>
                    )}
                  </li>
                );
              })}
            </ul>
        </nav>
        <button
          className="mb-4 mx-auto bg-gray-700 cursor-pointer hover:bg-gray-600 text-white p-2 rounded-full shadow-lg"
          onClick={toggle}
        >
          {isOpen ? <FiChevronLeft size={24} /> : <FiChevronRight size={24} />}
        </button>
      </div>
    </div>
  );
}