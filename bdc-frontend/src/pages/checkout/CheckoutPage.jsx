import { useEffect, useState } from "react"
import { fetchWithAuth } from "../../util/AuthUtil"
import { BASE_API_URL } from "../../constants"
import { ToastContainer, toast } from "react-toastify"
import { Link, useNavigate } from 'react-router-dom'
import Button from '@mui/material/Button';

export default function CheckoutPage(){
    const navigate = useNavigate()
    const [checkingOut, setCheckingOut] = useState(false)

    const [checkoutList, setCheckoutList] = useState([])
    const [addressList, setAddressList] = useState([])
    const [selectedAddress, setSelectedAddress] = useState(null)
    const [isLoading, setIsLoading] = useState(true)
    const [isOpenAddressList, setIsOpenAddressList] = useState(false)
    const [selectingAddressId, setSelectingAddressId] = useState();

    const fetchCheckoutList = async (body) => {
        try{
            const res = await fetchWithAuth(`${BASE_API_URL}/v1/checkout/get`, window.location, true, body ? 
                {
                    method: "POST",
                    headers: {
                        'Content-type': 'application/json'
                    },
                    body: JSON.stringify(body),
                } : {})
            if(!res.ok){
                alert("Có lỗi xảy ra, vui lòng thử lại sau")
                navigate('/cart')
            }
            else{
                res.json()
                    .then(data => {
                        setCheckoutList(data.shopCheckouts)
                    })
            }
        }
        catch(err){
            console.log("Error: " + err)
            alert("Có lỗi xảy ra, vui lòng thử lại sau")
            navigate('/cart')
        }
    }   

    const fetchAddressList = () => {
        fetchWithAuth(`${BASE_API_URL}/v1/user/address/get-list`, window.location, true)
            .then(res => res.json())
            .then(res => {
                setAddressList(res)
                for(const addr of res){
                    if(addr.primary) {
                        setSelectedAddress(addr)
                        break
                    }
                }
            })
    }

    const fetchCheckoutData = async () => {
        setIsLoading(true)
        await fetchCheckoutList()
        await fetchAddressList()
        setIsLoading(false)
    }

    useEffect(() => {
        fetchCheckoutData()
        
    }, [])

    const handleChangeAddress = async () => {
        setIsOpenAddressList(false)
        if(selectingAddressId === selectedAddress.id) return
        const body = {
            "addressId": selectingAddressId
        }
        setIsLoading(true)
        for(var addr of addressList){
            if(addr.id === selectingAddressId){
                setSelectedAddress(addr)
                break
            }
        }
        await fetchCheckoutList(body)
        setIsLoading(false)
    }

    const totalProductPrice = checkoutList.reduce((total, shopCheckout) => {
        return total + shopCheckout.items.reduce((subtotal, item) => subtotal + item.price * item.quantity, 0);
    }, 0);

    const totalShippingFee = checkoutList.reduce((total, shopCheckout) => total + shopCheckout.shipmentFee, 0);

    const totalOrderPrice = totalProductPrice + totalShippingFee;

    const handlePlaceOrder = () => {
        setCheckingOut(true)
        const body = {
            'address_id': selectedAddress.id,
            'shop_orders': [],
            'payment_type': 'cash_on_delivery'
        }
        checkoutList.map((shopCheckout) => {
            const totalPrice = shopCheckout.items.reduce((subtotal, item) => subtotal + item.price * item.quantity, 0)
            body['shop_orders'].push({
                'shop_id': shopCheckout.shop.id,
                'shipping_fee': shopCheckout.shipmentFee,
                'total_price': totalPrice
            })
        })
        try{
            fetchWithAuth(`${BASE_API_URL}/v1/order/place_order`, null, true, {
                method: "POST",
                headers: {
                    'Content-type': 'application/json'
                },
                body: JSON.stringify(body)
            })
                .then(res => {
                    if(res.ok) return res.json()
                    throw new Error()
                })
                .then(res => {
                    console.log(res)
                    window.location.assign('/checkout/success')
                })
                .catch(err => {
                    alert("Sản phẩm còn lại không đủ!")
                    window.location.assign('/cart')
                })
        }
        catch(err){
            console.log(err)
            alert("Có lỗi xảy ra")
        }
    }

    return (
        <div className="max-w-6xl mx-auto p-4 flex gap-6">
            {isLoading ? (
                <div role="status" className="fixed flex items-center justify-center bg-gray-50/75">
                    <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/><path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/></svg>
                    <span className="sr-only">Loading...</span>
                </div>
            ) : (
                <>
                <div className="w-2/3">
                    <h2 className="text-2xl font-bold mb-4">Thanh toán</h2>
                    {checkoutList.length === 0 ? (
                        <p className="text-gray-500"> Không có sản phẩm nào để thanh toán.</p>
                    ) : (
                        checkoutList.map((shopCheckout) => (
                            <div key={shopCheckout.shop.id} className="mb-6 border p-4 rounded-lg shadow-lg bg-white">
                                <div className="flex items-center mb-3">
                                    <Link to="#" className="text-xl font-semibold">🛒 {shopCheckout.shop.name}</Link>
                                </div>
                                {shopCheckout.items.map((item) => (
                                    <div key={item.itemId} className="flex items-center border-b py-3">
                                        <Link to={`/product/${encodeURIComponent(item.name.replace(/\s+/g, "-"))}.${item.productId}`}>
                                            <img src={item.thumbnailUrl} alt={item.name} className="w-20 h-20 object-cover rounded-lg" />
                                        </Link>
                                        <div className="ml-4 flex-1">
                                            <Link to={`/product/${encodeURIComponent(item.name.replace(/\s+/g, "-"))}.${item.productId}`} 
                                                className="text-lg font-medium line-clamp-2 overflow-hidden w-full">
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
                                                    <p>{item.quantity}</p>
                                                </div>
                                                <div>
                                                    <p className="font-bold justify-center">Tổng</p>
                                                    <p>{(item.price * item.quantity).toLocaleString()} VND</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                ))}

                                <div className="mt-4 p-3 flex justify-end">
                                    <div className="text-right ml-auto">
                                        <p className="font-bold">Phí vận chuyển</p>
                                        <p className="text-gray-700">{shopCheckout.shipmentFee.toLocaleString()} VND</p>
                                    </div>
                                    <div className="text-right ml-6">
                                        <p className="font-bold">Ngày giao dự kiến</p>
                                        <p className="text-gray-700">{new Date(shopCheckout.expectedDeliveryDate).toLocaleDateString("vi-VN")}</p>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                <div className="sticky top-20">
                    <div className="p-6 bg-white shadow-lg rounded-lg h-fit mb-6">
                        <div className="flex justify-between">
                            <h3 className="text-xl font-bold mb-4">Địa chỉ</h3>
                            <button className="cursor-pointer mb-2 text-blue-400" onClick={() => {
                                setSelectingAddressId(selectedAddress.id)
                                setIsOpenAddressList(true)
                            }}>
                                Thay đổi
                            </button>
                        </div>
                        {selectedAddress && (
                            <div key={selectedAddress.id} className="mb-4 p-4 border rounded-lg shadow">
                                <div className="flex justify-between">
                                    <div>
                                        <p className="font-semibold">{selectedAddress.receiverName} <span className="text-gray-500">({selectedAddress.phoneNumber})</span></p>
                                        <p>{selectedAddress.detail}</p>
                                        <p> {selectedAddress.ward.name}, {selectedAddress.district.name}, {selectedAddress.province.name} </p>
                                        {selectedAddress.primary && <span className="text-md text-red-500 text-sm font-semibold border-2 border-solid border-red-100">Chính</span>}
                                    </div>
                                </div>
                            </div>
                        )}
                    </div>

                    <div className="p-6 bg-white shadow-lg rounded-lg h-fit mb-6">
                        <h3 className="text-xl font-bold mb-4">Hình thức thanh toán</h3>
                        <div className="flex justify-between">
                            <h4>Thanh toán tiền mặt</h4>
                            <button className="text-blue-400 cursor-pointer">
                                Thay đổi
                            </button>
                        </div>
                    </div>

                    <div className="p-6 bg-white shadow-lg rounded-lg h-fit">
                        <h3 className="text-xl font-bold mb-4"> Đơn hàng</h3>
                        <div className="flex justify-between text-md mb-2">
                            <span>Tổng tiền sản phẩm:</span>
                            <span>{totalProductPrice.toLocaleString()} VND</span>
                        </div>
                        <div className="flex justify-between text-md mb-2">
                            <span>Phí vận chuyển:</span>
                            <span>{totalShippingFee.toLocaleString()} VND</span>
                        </div>
                        <div className="flex justify-between text-lg mb-4 border-t pt-2">
                            <span>Tổng cộng:</span>
                            <span className="font-bold text-red-600">{totalOrderPrice.toLocaleString()} VND</span>
                        </div>
                        <Button
                            onClick={handlePlaceOrder}
                            loading={checkingOut}
                            loadingPosition="end"
                            variant="contained"
                            className="w-full bg-blue-500 text-white text-lg py-2 rounded-lg hover:bg-blue-600 cursor-pointer"
                        >
                            Đặt hàng
                        </Button>
                    </div>
                </div>
                </>
            )}

            {isOpenAddressList && (
                <div className="fixed inset-0 flex items-center justify-center bg-gray-50/75">
                    <div className="bg-white p-6 rounded-lg shadow-lg w-150">
                        <h3 className="text-lg font-semibold mb-4">
                            Chọn địa chỉ
                        </h3>
                        {addressList.map((addr) => (
                            <div key={addr.id} className="flex items-center ps-4 border border-gray-200 rounded-sm dark:border-gray-700">
                                <input type="radio" 
                                    defaultChecked={addr.id === selectedAddress.id ? true : false}
                                    name="bordered-radio" 
                                    className="w-4 h-4 text-blue-600 bg-gray-100 border-gray-300 focus:ring-blue-500 dark:focus:ring-blue-600 dark:ring-offset-gray-800 focus:ring-2 dark:bg-gray-700 dark:border-gray-600"
                                    onClick={() => {
                                        setSelectingAddressId(addr.id)
                                    }}
                                />
                                <div className="flex justify-between">
                                    <div key={addr.id} className="mb-4 p-4">
                                        <div className="flex justify-between">
                                            <div>
                                                <p className="font-semibold">{addr.receiverName} <span className="text-gray-500">({addr.phoneNumber})</span></p>
                                                <p>{addr.detail}</p>
                                                <p> {addr.ward.name}, {addr.district.name}, {addr.province.name} </p>
                                                {addr.primary && <span className="text-md text-red-500 text-sm font-semibold border-2 border-solid border-red-100">Chính</span>}
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                        <div className="flex justify-between items-center mt-4">
                            <button className="text-gray-500 hover:underline" onClick={() => setIsOpenAddressList(false)}>Hủy</button>
                            <button 
                                className="px-4 py-2 bg-orange-500 text-white rounded hover:bg-orange-600 cursor-pointer" 
                                onClick={handleChangeAddress}
                            >
                                Xác nhận
                            </button>
                        </div>
                    </div>
                </div>
            )}

            <ToastContainer 
                position="bottom-right"
            />
        </div>
    )
}