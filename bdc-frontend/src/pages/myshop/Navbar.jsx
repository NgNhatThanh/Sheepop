import { IoIosNotifications } from "react-icons/io";
import { FaUserAlt } from "react-icons/fa";

export default function Navbar() {
  return (
    <nav className="flex w-full h-12 justify-between items-center bg-gray-800 text-white p-2 fixed z-100 shadow-sm">
      <div className="text-lg font-bold ml-4">
        <a href="/" className="flex gap-2">
          <img src="/logo.svg"/>
          Sheepop
        </a>
      </div>
      <ul className="flex gap-4 text-xl">
        <li className="cursor-pointer hover:text-gray-400">
          <IoIosNotifications />
        </li>
        <li className="cursor-pointer hover:text-gray-400">
          <FaUserAlt />
        </li>
      </ul>
    </nav>
  );
}