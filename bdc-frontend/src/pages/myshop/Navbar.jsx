import React from "react";
import "./Navbar.css";
import { IoIosNotifications } from "react-icons/io";
import { FaUserAlt } from "react-icons/fa";

export default function Navbar(){
    return (
        <nav>
          <div className="logo">
            <a href="/">MyApp</a>
          </div>
          <ul className="navbar-links">
            <li>
                <IoIosNotifications />
            </li>
            <li>
                <FaUserAlt />
            </li>
          </ul>
        </nav>
      );
}