import { useLocation, Link } from "react-router-dom";
import React from "react";
import { useState, useEffect } from "react";
import { fetchWithAuth } from '../../../util/AuthUtil'
import { formatDate } from '../../../util/DateUtil'
import { BASE_API_URL } from "../../../constants";
import Pagination from "../../common/Pagination";
import { ToastContainer, toast } from "react-toastify";
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

const tabs = [
    "Tất cả",
    "Đang kích hoạt",
    "Vi phạm",
    "Chưa được đăng",
    "Hết hàng"
];

export default function ShopProducts(){

    const location = useLocation();
    const searchParams = new URLSearchParams(location.search);
    const currentType = parseInt(searchParams.get("type")) || 0;

    const [changeVisibleProductId, setChangeVisibleProductId] = useState(null)
    const [deleteProductId, setDeleteProductId] = useState(null)
    const [openOtherActionDropdowm, setOpenOtherActionDropdown] = useState({})
    const [page, setPage] = useState(1)
    const [limit, setLimit] = useState(10)
    const [products, setProducts] = useState([]);
    const [totalPages, setTotalPages] = useState(1)
    const [totalProducts, setTotalProducts] = useState(0)
    const [isLoadingProduct, setIsLoadingProduct] = useState(false)
    const [isShowingSKUList, setIsShowingSKUList] = useState({})
    
    const loadProduct = async () => {
        setIsLoadingProduct(true)
        const res = await fetchWithAuth(`${BASE_API_URL}/v1/shop/product/list?page=${page - 1}&limit=${limit}`)
        await res.json()
        .then(page => {
            setTotalPages(page.totalPages)
            setTotalProducts(page.totalElements)
            setProducts(page.content)
        })
        setIsLoadingProduct(false)
    }
    
    const toggleShowSKUList = (productId) => {
        setIsShowingSKUList((prev) => ({
        ...prev,
        [productId]: !prev[productId]
        }))
    }

    const changeProductVisible = (productId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/shop/product/change_visible?productId=${productId}`, window.location, true, {
            method: "POST"
        })
            .then(res => {
                if(!res.ok){
                    toast.error('Có lỗi xảy ra, vui lòng thử lại sau!')
                    return
                }
                const updateProd = [...products]
                updateProd.map(prod => {
                    if(prod.id === productId){
                        prod.visible = !prod.visible
                    }
                    return prod
                })
                setProducts(updateProd)
            })
            .catch(() => toast.error('Có lỗi xảy ra, vui lòng thử lại sau!'))
    }

    const handleDeleteProduct = (productId) => {
        fetchWithAuth(`${BASE_API_URL}/v1/shop/product/delete/${productId}`, window.location, true, {
            method: "POST"
        })
            .then(res => {
                if(!res.ok){
                    toast.error("Có lỗi xảy ra, vui lòng thử lại sau!")
                    return
                }
                toast.success("Xóa sản phẩm thành công")
                setProducts(prev => prev.filter(prod => prod.id !== productId))
            })
            .catch(() => toast.error("Có lỗi xảy ra, vui lòng thử lại sau!"))
    }
    
    useEffect(() => {
        loadProduct()
    }, [])
    
    return (
        <div>
            <div className="flex justify-between mb-5">
                <h2 className="text-2xl font-bold">Sản phẩm</h2>
                <Link 
                    to="../add-product"
                    className='p-2 bg-blue-500 rounded-sm text-white font-semibold text-l cursor-pointer hover:bg-blue-600'
                >
                    + Thêm sản phẩm
                </Link>
                
            </div>

            <div className="w-full bg-white shadow-md sticky top-12 z-1 flex justify-center rounded-sm">
                {tabs.map((tab, index) => (
                <button
                    key={index}
                    className={`cursor-pointer px-4 py-2 ${
                    currentType === index
                        ? "text-blue-600 border-b-2 border-blue-600"
                        : "text-gray-900"
                    } hover:text-blue-500`}
                    onClick={() => navigate(`/myshop/order-list?type=${index}`)}
                >
                    {tab}
                </button>
                ))}
            </div>

            <div className="flex items-center justify-center space-x-3 bg-white p-4 rounded mt-4 shadow-md">
                <input
                    type="text"
                    placeholder="Tìm tên sản phẩm"
                    className={`border border-gray-300 px-3 py-2 rounded w-60`}
                />

                <input
                    type="text"
                    placeholder="Tìm theo ngành hàng"
                    className={`border border-gray-300 px-3 py-2 rounded w-60`}
                />

                <button 
                    className="bg-blue-500 cursor-pointer text-white px-4 py-2 rounded hover:bg-blue-600 transition"
                    onClick={() => {
                        setIsEmpty(false)
                        setOrders([])
                        fetchShopOrders(currentType, true)
                    }}    
                >
                    Áp dụng
                </button>

                <button
                    className="border cursor-pointer border-gray-400 px-4 py-2 rounded hover:bg-gray-100 transition"
                    onClick={() => {
                        setSearchQuery("");
                        setFilterType(0);
                        resetFilter()
                    }}
                >
                    Đặt lại
                </button>
            </div>

            <p className="text-xl font-semibold mb-2 mt-4">{totalProducts} sản phẩm</p>
            <div className="container mx-auto bg-white rounded-sm ">
                <table className="table-auto w-full border-collapse border border-gray-300">
                    <thead>
                        <tr className="bg-white">
                            <th className="border border-gray-300 p-2">STT</th>
                            <th className="border border-gray-300 p-2">Tên</th>
                            <th className="border border-gray-300 p-2">Giá</th>
                            <th className="border border-gray-300 p-2">Kho hàng</th>
                            <th className="border border-gray-300 p-2">Doanh thu</th>
                            <th className="border border-gray-300 p-2">Đã bán</th>
                            <th className="border border-gray-300 p-2">Thời gian tạo</th>
                            <th className="border border-gray-300 p-2">Thao tác</th>
                        </tr>
                    </thead>

                    {isLoadingProduct ? (
                    <tbody>
                        <tr>
                        <td>Loading...</td>
                        </tr>
                        
                    </tbody>
                    ) : (
                    <tbody>
                    {products.map((product, index) => (
                        <React.Fragment key={product.id}>
                        <tr id={product.id}>
                            <td className="border border-gray-300 p-2 text-center">{index + 1}</td>
                            <td className="border border-gray-300 p-2 text-left">
                                {!product.visible && (
                                    <p 
                                        className="text-gray-600 w-fit text-sm p-1 bg-gray-200 font-semibold rounded-l mb-2"
                                    >
                                        Đã ẩn
                                    </p> 
                                )}  
                                <div className="flex justify-start gap-3">
                                    <img
                                    src={product.thumbnailUrl}
                                    alt={product.name}
                                    className="w-12 h-12 object-cover"
                                    />
                                    <p className="mr-auto">{product.name}</p>
                                </div>
                            </td>
                            <td className="border border-gray-300 p-2 text-center">{product.skuList.length > 0 ? '---' : product.price.toLocaleString() + ' VND'}</td>
                            <td className="border border-gray-300 p-2 text-center">{product.quantity}</td>
                            <td className="border border-gray-300 p-2 text-center">{product.revenue.toLocaleString()} VND</td>
                            <td className="border border-gray-300 p-2 text-center">{product.sold}</td>
                            <td className="border border-gray-300 p-2 text-center">{formatDate(product.createdAt)}</td>
                            <td className="border border-gray-300 p-2">
                            <div className="flex flex-col gap-1">
                                <Link 
                                    to={`../product/${product.id}`}
                                    className="text-white text-center cursor-pointer rounded-xs bg-blue-400 p-1 hover:bg-blue-500"
                                >
                                    Chỉnh sửa
                                </Link>
                                {product.skuList.length > 0 && 
                                <button
                                    className="cursor-pointer rounded-xs border-1 border-gray-700 p-1 hover:bg-gray-100"
                                    onClick={() => toggleShowSKUList(product.id)}
                                >
                                    {isShowingSKUList[product.id] ? "Thu gọn" : "Mở rộng"}  
                                </button>}

                                <div className="relative">
                                    <p 
                                        className="text-blue-500 cursor-pointer text-center"
                                        onClick={() => setOpenOtherActionDropdown(prev => ({
                                            ...prev,
                                            [index]: openOtherActionDropdowm[index] ? false : true
                                        }))} 
                                    >
                                        Khác ▼
                                    </p>

                                    {openOtherActionDropdowm[index] && (
                                        <ul
                                            className={`opacity-100 absolute right-0 z-10 top-full mb-1 w-24 bg-white border border-gray-300 rounded shadow-md overflow-hidden transition-all duration-300 max-h-40`}
                                        >
                                            <li
                                                className="px-2 py-1 cursor-pointer hover:bg-gray-200"
                                                onClick={() => {
                                                    window.open(`/preview/${product.id}`, '_blank')
                                                }}
                                            >
                                                Xem trước
                                            </li>

                                            <li
                                                className="px-2 py-1 cursor-pointer hover:bg-gray-200"
                                                onClick={() => setChangeVisibleProductId(product.id)}
                                            >
                                                {product.visible ? 'Ẩn' : 'Hiện'}
                                            </li>

                                            <li
                                                className="px-2 py-1 cursor-pointer text-red-500 hover:bg-gray-200"
                                                onClick={() => setDeleteProductId(product.id)}   
                                            >
                                                Xóa
                                            </li>
                                        </ul>
                                    )}
                                </div>

                                
                            </div>
                            </td>
                        </tr>
                        {isShowingSKUList[product.id] && (
                            product.skuList.map(skuProduct => (
                            <tr>
                            <td></td>
                            <td className="border border-gray-300 p-2 text-center">
                                {skuProduct.attributes.map(attribute => (
                                <p>{attribute.name}: {attribute.value}</p>
                                ))}
                                SKU: {skuProduct.sku}
                            </td>
                            <td className="border border-gray-300 p-2 text-center">{skuProduct.price.toLocaleString()} VND</td>
                            <td className="border border-gray-300 p-2 text-center">{skuProduct.quantity}</td>
                            </tr>
                            ))
                        )}

                        <Dialog
                            open={changeVisibleProductId !== null}
                            onclose={() => setChangeVisibleProductId(null)}
                            aria-labelledby="alert-dialog-title"
                            aria-describedby="alert-dialog-description"
                        >
                            <DialogTitle id="alert-dialog-title">
                                {"Thay đổi hiển thị sản phẩm?"}
                            </DialogTitle>
                            <DialogContent>
                                <DialogContentText id="alert-dialog-description">
                                    Người mua sẽ không nhìn thấy và mua được sản phẩm của bạn nếu bạn ẩn đi,
                                    và ngược lại
                                </DialogContentText>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={() => setChangeVisibleProductId(null)}>Hủy</Button>
                                <Button onClick={() => {
                                    changeProductVisible(changeVisibleProductId)
                                    setChangeVisibleProductId(null)
                                }} autoFocus>
                                Đồng ý
                                </Button>
                            </DialogActions>
                        </Dialog>

                        <Dialog
                            open={deleteProductId !== null}
                            onclose={() => setDeleteProductId(null)}
                            aria-labelledby="alert-dialog-title"
                            aria-describedby="alert-dialog-description"
                        >
                            <DialogTitle id="alert-dialog-title">
                                {"Xóa sản phẩm?"}
                            </DialogTitle>
                            <DialogContent>
                                <DialogContentText id="alert-dialog-description">
                                    Thao tác này sẽ xóa hoàn toàn sản phẩm của bạn, và không thể hoàn tác
                                </DialogContentText>
                            </DialogContent>
                            <DialogActions>
                                <Button onClick={() => setDeleteProductId(null)}>Hủy</Button>
                                <Button onClick={() => {
                                    handleDeleteProduct(deleteProductId)
                                    setDeleteProductId(null)
                                }} autoFocus>
                                Đồng ý
                                </Button>
                            </DialogActions>
                        </Dialog>

                        </React.Fragment>
                    ))}
                    </tbody>
                    )}
                </table>
                <Pagination
                    page={page}
                    setPage={setPage}
                    limit={limit}
                    setLimit={setLimit}
                    maxPage={totalPages}
                />
            </div>
            <ToastContainer
                position="bottom-right"
            />
        </div>
    )
}