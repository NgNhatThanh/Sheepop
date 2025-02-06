import React, { useState } from "react";
import "./Sidebar.css";

export default function Sidebar(){
    const [isCollapsed, setIsCollapsed] = useState(false);

    const toggleSidebar = () => {
        setIsCollapsed(!isCollapsed);
    };

    return (
        <div style={{ display: "flex", minHeight: "100vh" }}>
            <div className={`sidebar ${isCollapsed ? "collapsed" : ""}`} style={{ width: isCollapsed ? "80px" : "250px" }}>
                <ul>
                <li>
                    <a href="dashboard">Dashboard</a>
                </li>
                <li>
                    <a href="products">Products</a>
                </li>
                </ul>

                <button className="toggle-button" onClick={toggleSidebar}>
                </button>
            </div>
        </div>
    );
}