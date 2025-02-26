import { useEffect, useState } from "react";
import { fetchWithAuth } from "../../util/AuthUtil";
import { BASE_API_URL } from "../../constants";
import { Link, useNavigate } from 'react-router-dom'
import { ToastContainer, toast } from "react-toastify";

export default function CartPage() {
    const [shopCarts, setShopCarts] = useState([]);
    const  navigate = useNavigate()

    useEffect(() => {
        localStorage.removeItem('cart')
        const fetchCart = async () => {
            const res = await fetchWithAuth(`${BASE_API_URL}/v1/cart/get`, window.location, true);
            const data = await res.json();
            setShopCarts(data.shopCarts);
        };
        fetchCart();
    }, []);

    const selectedTotal = shopCarts.reduce((total, shopCart) => {
        return total + shopCart.items.reduce((sum, item) => {
            return item.selected ? sum + item.price * item.quantity : sum;
        }, 0);
    }, 0);

    const updateSelectItems = (items, selected) => {
        items.map(item => {
            item.selected = selected
        })
        updateCart(items)
    }

    const updateItemQuantity = (item, newQuantity) => {
        if(newQuantity === item.quantity || newQuantity < 1) return
        item.quantity = newQuantity
        updateCart([item])
    }

    const deleteItem = async (item) => {
        try{
            fetchWithAuth(`${BASE_API_URL}/v1/cart/item/remove?itemId=${item.itemId}`, window.location, true, {
                method: "POST"
            })
                .then(res => {
                    if(!res.ok) toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
                    else {
                        res.json()
                            .then(data => {
                                setShopCarts(data.shopCarts)
                            })
                    }
                })
                .catch(err => {
                    console.log(err)
                    toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
                })
            
        }
        catch(err){
            console.log(err)
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
        }
    }

    const updateCart = async (updateItems) => {
        const lst = updateItems.map(item => ({
            itemId: item.itemId,
            selected: item.selected,
            quantity: item.quantity
        }))
        try{
            const res = await fetchWithAuth(`${BASE_API_URL}/v1/cart/update`, null, true,{
                method: "POST",
                headers:{
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(lst)
            })
            if(!res.ok) toast.error("Có lỗi xảy ra, vui lòng thử lại sau!");
            else{
                res.json()
                    .then(data => {
                        if(data.warnMsg) toast.warn(data.warnMsg);
                        setShopCarts(data.cart.shopCarts)
                    })
                    .catch(err => {
                        console.log(err)
                        toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
                    })
            }
        }
        catch(err){
            console.log(err)
            toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
        }
    }

    const handleCheckout = () => {
        
        navigate('/checkout')
    }

    return (
        <div className="max-w-6xl mx-auto p-4 flex gap-6">
            {/* Phần danh sách sản phẩm */}
            <div className="w-2/3">
                <h2 className="text-2xl font-bold mb-4">Giỏ hàng</h2>
                {shopCarts.length === 0 ? (
                    <p className="text-gray-500">Giỏ hàng của bạn đang trống.</p>
                ) : (
                    shopCarts.map((shopCart) => (
                        <div key={shopCart.shop.id} className="mb-6 border p-4 rounded-lg shadow-lg bg-white">
                            <div className="flex items-center mb-3">
                                <input 
                                    type="checkbox" 
                                    checked={shopCart.items.every(item => item.selected)} 
                                    className="mr-2" 
                                    onChange={(e) => updateSelectItems(shopCart.items, e.target.checked)}    
                                />
                                <Link to="#" className="text-xl font-semibold">🛒 {shopCart.shop.name}</Link>
                            </div>
                            {shopCart.items.map((item) => (
                                <div key={item.itemId} className="flex items-center border-b py-3">
                                    <input type="checkbox" 
                                        disabled={item.quantity > item.stock ? true : false}
                                        checked={item.selected} 
                                        className="mr-2" 
                                        onChange={(e) => updateSelectItems([item], e.target.checked)}
                                    />
                                    <Link to={`/product/${encodeURIComponent(item.name.replace(/\s+/g, "-"))}.${item.productId}`}>
                                        <img src={item.thumbnailUrl} alt={item.name} className="w-20 h-20 object-cover rounded-lg" />
                                    </Link>
                                    <div className="ml-4 flex-1">
                                        <Link to={`/product/${encodeURIComponent(item.name.replace(/\s+/g, "-"))}.${item.productId}`} 
                                            className="text-lg font-medium break-words w-full">
                                            {item.name}
                                        </Link>
                                        <div className="grid grid-cols-4 gap-5 text-gray-600 text-sm mt-2">
                                            <div>
                                                <p className="font-bold">Phân loại</p>
                                                {item.attributes && item.attributes.map((attr) => (
                                                    <p key={attr.id}>{attr.name}: {attr.value}</p>
                                                ))}
                                            </div>
                                            <div>
                                                <p className="font-bold">Giá</p>
                                                <p>{item.price.toLocaleString()} VND</p>
                                            </div>
                                            <div>
                                                <p className="font-bold">Số lượng</p>
                                                <div className="flex items-center gap-1">
                                                    <button 
                                                        className='px-2 py-1 text-sm font-bold rounded-lg transition bg-gray-200 hover:bg-gray-300'
                                                        onClick={() => updateItemQuantity(item, item.quantity - 1)}
                                                    >
                                                        -
                                                    </button>
                                                    <input 
                                                        type="number"
                                                        className="w-12 text-center border rounded-lg px-2 py-1 text-md" 
                                                        onChange={(e) => {
                                                            const newQuantity = e.target.value;
                                                            if(!newQuantity) return
                                                            if(newQuantity < 1) e.target.value = 1
                                                            else if(newQuantity > item.stock) e.target.value = item.stock
                                                        }}
                                                        defaultValue={item.quantity}
                                                        key={item.quantity}
                                                        onBlur={(e) => {
                                                            if(!e.target.value) e.target.value = 1
                                                            updateItemQuantity(item, Number(e.target.value))
                                                        }}
                                                    />
                                                    <button 
                                                        className='px-2 py-1 text-sm font-bold rounded-lg transition bg-gray-200 hover:bg-gray-300'
                                                        onClick={() => updateItemQuantity(item, item.quantity + 1)}
                                                    >
                                                        +
                                                    </button>
                                                </div>
                                                {item.stock <= 10 && <p className="text-xs text-red-500">Còn lại: {item.stock}</p>}
                                            </div>
                                            <div>
                                                <p className="font-bold justify-center">Tổng</p>
                                                <p>{(item.price * item.quantity).toLocaleString()} VND</p>
                                            </div>
                                        </div>
                                    </div>
                                    <button 
                                        className="bg-red-500 text-white px-3 py-1 rounded-lg hover:bg-red-600"
                                        onClick={() => deleteItem(item)}
                                    >
                                        Xóa
                                    </button>
                                </div>
                            ))}
                        </div>
                    ))
                )}
            </div>

            {selectedTotal > 0 && (
                <div className="w-1/3 p-6 bg-white shadow-lg rounded-lg h-fit sticky top-20">
                    <h3 className="text-xl font-bold mb-4">Tóm tắt đơn hàng</h3>
                    <div className="flex justify-between text-lg mb-4">
                        <span>Tổng:</span>
                        <span className="font-bold text-red-600">{selectedTotal.toLocaleString()} VND</span>
                    </div>
                    <button 
                        className="w-full bg-blue-500 text-white text-lg py-2 rounded-lg hover:bg-blue-600 cursor-pointer"
                        onClick={handleCheckout}
                    >
                        Tiến hành thanh toán
                    </button>
                </div>
            )}
            <ToastContainer 
                position="bottom-right"
            />
        </div>
    );
}
