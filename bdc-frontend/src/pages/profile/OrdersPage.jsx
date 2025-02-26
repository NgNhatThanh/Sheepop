import { useNavigate, useLocation, Link } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import { fetchWithAuth } from "../../util/AuthUtil";
import { BASE_API_URL } from "../../constants";
import { BsClipboard2PlusFill } from "react-icons/bs";
import { ToastContainer, toast } from "react-toastify";
import ReviewForm from "./ReviewForm";
import CancelOrderForm from "./CancelOrderForm";

const tabs = [
  "Tất cả",
  "Chờ thanh toán",
  "Chờ xác nhận",
  "Chờ vận chuyển",
  "Nhận hàng",
  "Hoàn thành",
  "Đã hủy",
];

const statusText = [
  "Chờ xác nhận",
  "Chuẩn bị hàng",
  "Đã gửi hàng",
  "Đang giao hàng",
  "Thành công",
  "Đã đánh giá",
  "Đã hủy",
];

const statusTextColor = [
  "text-gray-500",
  "text-gray-500",
  "text-orange-500",
  "text-orange-500",
  "text-green-500",
  "text-green-500",
  "text-red-500"
]

const cancelReason = [
  "Thay đổi địa chỉ nhận hàng",
  "Điều chỉnh đơn hàng (phân loại, số lượng, ...)",
  "Quy trình thanh toán quá phức tạp",
  "Tìm được chỗ khác rẻ hơn",
  "Không muốn mua nữa"
]

export default function OrdersPage() {
  const limit = 5;
  const [cancelOrder, setCancelOrder] = useState(null)
  const [offset, setOffset] = useState(0);
  const [isEndOfOrders, setIsEndOfOrders] = useState(false)
  const [isEmptyList, setIsEmptyList] = useState(false)
  const [isFetchingOrders, setIsFetchingOrders] = useState(false)
  const [openReviewForm, setOpenReviewForm] = useState({})
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const currentType = parseInt(searchParams.get("type")) || 0;
  const [orders, setOrders] = useState({});

  const fetchOrders = (type, reset = false) => {
    console.log("fetch more")
    setIsFetchingOrders(true)
    fetchWithAuth(
      `${BASE_API_URL}/v1/order/get_order_list?type=${type}&offset=${reset ? 0 : offset}&limit=${limit}`,
      window.location,
      true
    )
      .then((res) => res.json())
      .then((res) => {
        setOrders(prev => ({
          ...prev,
          [type]: {
            'list': reset ? res.detailList : (prev[type].list ? prev[type].list.concat(res.detailList) : res.detailList),
            'orderCount' : res.nextOffset
          }
        }))
        setOffset(res.nextOffset)
        if(reset && res.detailList.length === 0) setIsEmptyList(true)
        if(res.detailList.length < limit) setIsEndOfOrders(true)
        setIsFetchingOrders(false)
      })
      .catch(() => setIsFetchingOrders(false))
  };

  const handleMarkOrderAsReceived = (shopOrderId) => {
    fetchWithAuth(`${BASE_API_URL}/v1/order/mark_as_received?shopOrderId=${shopOrderId}`, window.location, true, { 
      method: "POST"
    })
      .then(res => {
        if(res.ok) fetchOrders(currentType, true)
      })
      .catch(() => {
        toast.error("Có lỗi xảy ra, vui lòng thử lại sau")
      })
  }

  const handleSuccessReview = (orderId, shopOrderId) => {
    toast.success("Đánh giá thành công")
    setOrders(prevOrders => {
      const updatedOrders = { ...prevOrders };
      if (!updatedOrders[currentType] || !updatedOrders[currentType].list) {
          console.log("No orders found");
          return prevOrders;
      }
      const updatedList = updatedOrders[currentType].list.map(order => {
          if (order.id === orderId && order.shopOrders[0].id === shopOrderId) {
              return {
                  ...order,
                  rated: true
              };
          }
          return order;
      });
      return {
          ...updatedOrders,
          [currentType]: {
              ...updatedOrders[currentType],
              list: updatedList
          }
      };
    });
  }



  useEffect(() => {
    if(!orders[currentType]){
      setOffset(0)
      setIsEmptyList(false)
      setIsEndOfOrders(false)
      fetchOrders(currentType, true);
    }
    else{
      setIsEmptyList(orders[currentType].list.length === 0)
      setOffset(orders[currentType].list.length)
      setIsEndOfOrders(orders[currentType].list.length % limit !== 0)
    }
  }, [currentType]);

  const loadMoreRef = useRef(null);

  useEffect(() => {
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && !isFetchingOrders && !isEndOfOrders) {
          fetchOrders(currentType);
        }
      },
      { threshold: 1 }
    );
    if (loadMoreRef.current) observer.observe(loadMoreRef.current);

    return () => observer.disconnect();
  }, [isFetchingOrders, isEndOfOrders, currentType]);

  return (
    <div className="w-300 flex flex-col items-center min-h-screen">
      <div className="w-full bg-white shadow-md sticky top-0 z-1 flex justify-center border-b rounded-xs">
        {tabs.map((tab, index) => (
          <button
            key={index}
            className={`cursor-pointer px-4 py-2 ${
              currentType === index
                ? "text-blue-600 border-b-2 border-blue-600"
                : "text-gray-500"
            } hover:text-blue-500`}
            onClick={() => navigate(`/account/orders?type=${index}`)}
          >
            {tab}
          </button>
        ))}
      </div>

      {orders[currentType] && orders[currentType].list.map((order, index) => (
          <div
            key={index}
            className="w-full bg-white shadow-md rounded-lg p-4 mt-3 overflow-auto"
          >
            <div className={`text-right text-sm ${statusTextColor[order.status - 1]}`}>
              {statusText[order.status - 1]}
            </div>
            {order.shopOrders.map((shopOrder) => (
              <div key={shopOrder.id} className="mt-2">
                <Link to="#" className="text-lg font-bold flex items-center">
                  🛒 {shopOrder.name}
                </Link>
                {shopOrder.items.map((item) => (
                  <div key={item.id} className="flex items-center border-b py-3">
                    <img
                      src={item.product.thumbnailUrl}
                      alt={item.product.name}
                      className="w-16 h-16 object-cover rounded-lg"
                    />
                    <div className="ml-4 flex-1">
                      <p className="font-medium text-gray-800 line-clamp-2">
                        {item.product.name}
                      </p>
                      <div className="flex gap-5">
                        <div>
                          {item.attributes?.map((attr) => (
                            <p key={attr.name} className="text-gray-500 text-sm">
                                {attr.name}: {attr.value} &nbsp;
                            </p>
                          ))}
                        </div>
                        <p className="text-gray-500 text-sm">
                          x {item.quantity}
                        </p>
                      </div>
                      
                    </div>
                    <p className="text-blue-600 font-semibold">
                      {item.price.toLocaleString()} VND
                    </p>
                  </div>
                ))}
                {openReviewForm[shopOrder.id] && ( <ReviewForm 
                                                      shopOrder={shopOrder} 
                                                      closeForm={() => setOpenReviewForm(prev => ({
                                                                        ...prev,
                                                                        [shopOrder.id]: false
                                                                      }))}
                                                      onSuccess={() => handleSuccessReview(order.id, shopOrder.id)}
                                                    /> )}

                {cancelOrder === order && (<CancelOrderForm 
                    reasons={cancelReason}
                    whoCancel={1}
                    closeForm={() => setCancelOrder(null)} 
                    onSuccess={null} 
                    order={cancelOrder}
                />)}                                    
              </div>
            ))}

            <div className="flex justify-end mt-2">
              <p className="text-xl font-bold mr-3">Tổng: </p>
              <p className="text-xl text-blue-600 font-semibold">
                  {order.shopOrders.length > 1 
                      ? order.payment.amount.toLocaleString()
                      : (order.shopOrders[0].items.reduce((total, item) => total + item.price * item.quantity, 0) + order.shopOrders[0].shippingFee).toLocaleString()
                  } VND
              </p>
            </div>
  
            <div className="flex justify-end gap-2 mt-4">
              {order.pending && (
                <button className="px-4 text-white py-2 bg-blue-400 text-white rounded cursor-pointer hover:bg-blue-500">
                  Thanh toán
                </button>
              )}
              {order.shopOrders[0].status === 3 || order.shopOrders[0].status === 4 && (
                <button 
                  className="px-4 text-white py-2 bg-blue-400 rounded cursor-pointer hover:bg-blue-500"
                  onClick={() => handleMarkOrderAsReceived(order.shopOrders[0].id)}
                >
                  Đã nhận hàng
                </button>
              )}
              {order.rated && (
                <button className="px-4 text-white py-2 bg-blue-400 rounded cursor-pointer hover:bg-blue-500">
                  Mua lại
                </button>
              )}
              {}
              {order.completed && !order.rated && (
                <button 
                  className="px-4 text-white py-2 bg-blue-400 text-white rounded cursor-pointer hover:bg-blue-500"
                  onClick={() => setOpenReviewForm(prev => ({
                    ...prev,
                    [order.shopOrders[0].id]: true
                  }))}
                >
                  Đánh giá
                </button>
              )}
              {!order.pending && (
                <button className="px-4 py-2 border border-gray-400 rounded cursor-pointer hover:bg-gray-100">
                  <Link to={`${order.shopOrders[0].id}`}>
                    Chi tiết
                  </Link>
                </button>
              )}
              {order.cancelable && (
                <button 
                  className="px-4 py-2 border border-red-500 text-red-500 rounded cursor-pointer hover:bg-gray-100"
                  onClick={() => {
                    setCancelOrder(order)
                  }}  
                >
                  Hủy đơn
                </button>
              )}

            </div>
            <div ref={loadMoreRef} ></div>
          </div>))}

      {isEmptyList && (
        <div className="w-full bg-white shadow-md rounded-sm h-screen mt-3 flex items-center justify-center">
          <div className="text-center">
            <BsClipboard2PlusFill className="text-blue-300 text-8xl mx-auto mb-2"/>
            <p className="text-xl">Không có đơn hàng nào</p>
          </div>
        </div>
      )}

      {isFetchingOrders && (
        <div role="status">
          <svg aria-hidden="true" className="w-8 h-8 text-gray-200 animate-spin dark:text-gray-600 fill-blue-600" viewBox="0 0 100 101" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z" fill="currentColor"/>
              <path d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z" fill="currentFill"/>
          </svg>
          <span className="sr-only">Loading...</span>
        </div>
      )}

      
      <ToastContainer 
        position="bottom-right"
      />
    </div>
  );
}
