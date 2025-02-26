import { useEffect, useState } from "react"
import { FaSearch, FaShoppingCart } from "react-icons/fa"
import { fetchWithAuth } from "../../util/AuthUtil"
import { BASE_API_URL } from "../../constants"
import { Link, useLocation } from 'react-router-dom'

export default function Header({isAuthenticated}){
    const maxMiniCartDisplay = 5;
    const [isCartOpen, setIsCartOpen] = useState(false);
    const [cartItems, setCartItems] = useState([]);
    const location = useLocation()
    const showMiniCart = !['/cart', '/checkout'].includes(location.pathname)

    const fetchMiniCart = async () => {
        const res = await fetchWithAuth(`${BASE_API_URL}/v1/cart/get-mini`, null, false)
        const cart = await res.json()
        localStorage.setItem('cart', JSON.stringify(cart))
        return cart
    }

    // if(isAuthenticated){

    // }

    useEffect(() => {
        setIsCartOpen(false)
        const updateCartCount = async () => {
            let cart = JSON.parse(localStorage.getItem('cart'))
            if(!cart){
                try{
                    cart = await fetchMiniCart()
                }
                catch(err){}
            }
            setCartItems(cart.items)
        }

        if(isAuthenticated && showMiniCart) {
            updateCartCount()
            window.addEventListener("cartChange", updateCartCount); 
            return () => window.removeEventListener("cartChange", updateCartCount);
        }
    }, [location.pathname])

    return (
        <div className="bg-gradient-to-r from-blue-500 to-blue-700 text-white">
            <div className="container mx-auto flex justify-between items-center py-4">
                <Link to="/" className="text-2xl font-bold flex gap-2">
                    <img src="/logo.svg"/>
                    Sheepop
                </Link>
                
                <div className="flex flex-1 mx-4">
                    <input
                        type="text"
                        placeholder="Mua Hàng Xuyên Tết"
                        className="w-full p-2 rounded-l-md border-none focus:ring-0 text-black bg-white"
                    />
                    <button className="p-2 bg-red-600 rounded-r-md cursor-pointer">
                        <FaSearch className="text-white" />
                    </button>
                </div>

                {/* Cart Icon */}
                {showMiniCart && (
                    <div 
                        className="relative"
                        onMouseEnter={() => setIsCartOpen(true)}
                        onMouseLeave={() => setIsCartOpen(false)}
                    >
                        <Link to="/cart">
                            <FaShoppingCart size={24} />
                            {cartItems.length > 0 && (
                                <span className="absolute -top-2 -right-2 bg-red-600 text-white text-[10px] font-bold px-1.5 py-0.5 rounded-full">
                                    {cartItems.length}
                                </span>
                            )}
                        </Link>

                        {/* Mini Cart Dropdown */}
                        {isCartOpen && (
                            <div className="absolute right-0 -mt-1 w-64 bg-white text-black shadow-lg rounded-lg p-4 z-2">
                                <h4 className="font-semibold text-md mb-2">Giỏ hàng của bạn</h4>
                                {cartItems.length > 0 ? (
                                    <ul>
                                        {cartItems.map((item, index) => {
                                            if(index > 4) return
                                            return (
                                                <li key={index} className="flex items-center justify-between border-b py-2">
                                                    <div className="flex items-center">
                                                        <img src={item.thumbnailUrl} alt={item.name} className="w-12 h-12 rounded-md mr-2" />
                                                        <div>
                                                            <p className="text-sm font-medium line-clamp-1 overflow-hidden mt-2">{item.name}</p>
                                                            <p className="text-xs text-gray-600">
                                                                {item.attributes.map(attr => `${attr.name}: ${attr.value}`).join(', ')}
                                                            </p>
                                                            <p className="text-xs text-gray-600">{item.quantity} x {item.price} VND</p>
                                                        </div>
                                                    </div>
                                                </li>
                                            )
                                        })}
                                        {cartItems.length > maxMiniCartDisplay && (
                                            <li>
                                                <p> Và {cartItems.length - maxMiniCartDisplay} sản phẩm khác trong giỏ hàng </p>
                                            </li>
                                        )}
                                    </ul>
                                ) : (
                                    <p className="text-gray-500 text-sm">Giỏ hàng trống</p>
                                )}
                                <div className="mt-3">
                                    <Link to="/cart" className="block text-center bg-blue-600 text-white py-2 rounded-md font-medium hover:bg-blue-700">
                                        Xem giỏ hàng
                                    </Link>
                                </div>
                            </div>
                        )}
                    </div>
                )} 
            </div>
        </div>
    )
}